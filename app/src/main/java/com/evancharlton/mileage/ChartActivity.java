
package com.evancharlton.mileage;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ZoomControls;

import java.util.Date;

public abstract class ChartActivity extends BaseActivity implements DialogInterface.OnCancelListener {
    public static final String VEHICLE_ID = "vehicle_id";

    /** Chart x-values are days-since-epoch (float-safe), not raw millisecond timestamps. */
    public static final long MS_PER_DAY = 86400000L;

    private static final int PROGRESS_DIALOG = 1;

    private LineChart mChart;
    private ZoomControls mZoomControls;
    private ChartGenerator mChartGenerator;
    private ProgressDialog mProgressDialog;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart);
        initToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mChart = (LineChart) findViewById(R.id.chart);
        mZoomControls = (ZoomControls) findViewById(R.id.zoom_controls);

        mChart.getDescription().setEnabled(false);
        mChart.getAxisRight().setEnabled(false);
        mChart.getLegend().setEnabled(false);
        mChart.setScaleYEnabled(false);
        mChart.setPinchZoom(false);

        final XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return DateFormat.getDateFormat(ChartActivity.this).format(
                        new Date((long) (value * MS_PER_DAY)));
            }
        });

        restoreLastNonConfigurationInstance();

        mZoomControls.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mChart.zoomIn();
            }
        });

        mZoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mChart.zoomOut();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return new Object[] {
                mChartGenerator,
                serializeData()
        };
    }

    private void restoreLastNonConfigurationInstance() {
        Object saved = getLastCustomNonConfigurationInstance();
        if (saved != null) {
            Object[] array = (Object[]) saved;
            mChartGenerator = (ChartGenerator) array[0];
            unserializeData(array[1]);
        } else {
            mChartGenerator = createChartGenerator();
        }
        mChartGenerator.attach(this);
        if (mChartGenerator.getStatus() == AsyncTask.Status.PENDING) {
            mChartGenerator.execute(getExecuteParameters());
        }
    }

    protected abstract Object serializeData();

    protected abstract void unserializeData(Object saved);

    protected final LineChart getChart() {
        return mChart;
    }

    protected abstract ChartGenerator createChartGenerator();

    protected Object[] getExecuteParameters() {
        return null;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case PROGRESS_DIALOG:
                if (mProgressDialog != null) {
                    removeDialog(PROGRESS_DIALOG);
                }
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setTitle(R.string.creating_chart);
                mProgressDialog.setOnCancelListener(this);
                mProgressDialog.setCancelable(true);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                return mProgressDialog;
        }
        return super.onCreateDialog(id);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mChartGenerator != null && mChartGenerator.getStatus() == AsyncTask.Status.RUNNING) {
            mChartGenerator.cancel(true);
        }
        finish();
    }

    protected ProgressDialog getProgressDialog() {
        return mProgressDialog;
    }

    protected void addChartSeries(ChartData data) {
        LineDataSet dataSet = new LineDataSet(data.points, data.label);
        dataSet.setDrawValues(false);
        mChart.setData(new LineData(dataSet));
        mChart.invalidate();
    }

    public abstract static class ChartGenerator extends AsyncTask<Object, Integer, ChartData[]> {
        private ChartActivity mActivity;
        private ProgressDialog mCachedProgressDialog;

        public final void attach(ChartActivity activity) {
            mActivity = activity;
            mCachedProgressDialog = mActivity.getProgressDialog();
        }

        @Override
        protected void onPreExecute() {
            mActivity.showDialog(PROGRESS_DIALOG);
            mCachedProgressDialog = mActivity.getProgressDialog();
        }

        @Override
        protected void onProgressUpdate(Integer... updates) {
            mCachedProgressDialog.setProgress(updates[0]);
            if (updates.length > 1) {
                mCachedProgressDialog.setMax(updates[1]);
            }
        }

        @Override
        protected void onPostExecute(ChartData[] series) {
            if (isCancelled()) {
                return;
            }
            mActivity.removeDialog(PROGRESS_DIALOG);
            final int length = series.length;
            for (int i = 0; i < length; i++) {
                mActivity.addChartSeries(series[i]);
            }
        }

        protected final ChartActivity getActivity() {
            return mActivity;
        }
    }
}
