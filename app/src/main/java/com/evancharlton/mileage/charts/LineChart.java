
package com.evancharlton.mileage.charts;

import com.evancharlton.mileage.ChartActivity;
import com.evancharlton.mileage.ChartData;
import com.evancharlton.mileage.dao.Fillup;
import com.evancharlton.mileage.dao.Vehicle;
import com.evancharlton.mileage.provider.tables.FillupsTable;
import com.evancharlton.mileage.provider.tables.VehiclesTable;
import com.github.mikephil.charting.data.Entry;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class LineChart extends ChartActivity {
    private List<Entry> mPoints;

    protected abstract String getAxisTitle();

    protected abstract ChartGenerator createChartGenerator();

    protected final void createSeries(LineChartGenerator generator, List<Entry> points,
            Cursor cursor, Vehicle vehicle) {
        mPoints = points;
        // TODO(3.1) - consolidate this while loop
        processCursor(generator, cursor, vehicle);
    }

    protected final void addPoint(Date date, double value) {
        float day = (float) (date.getTime() / MS_PER_DAY);
        mPoints.add(new Entry(day, (float) value));
    }

    protected final void addPoint(long timestamp, double value) {
        addPoint(new Date(timestamp), value);
    }

    protected abstract void processCursor(LineChartGenerator generator, Cursor cursor,
            Vehicle vehicle);

    protected final Vehicle getVehicle() {
        Cursor cursor = managedQuery(VehiclesTable.BASE_URI, VehiclesTable.PROJECTION, Vehicle._ID
                + " = ?", new String[] {
                getIntent().getStringExtra(VEHICLE_ID)
        }, null);
        return new Vehicle(cursor);
    }

    @Override
    protected final Object serializeData() {
        return mPoints;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected final void unserializeData(Object saved) {
        List<Entry> savedData = (List<Entry>) saved;
        if (savedData != null) {
            addChartSeries(new ChartData(getAxisTitle(), savedData));
        }
    }

    @Override
    protected final Object[] getExecuteParameters() {
        return null;
    }

    protected static class LineChartGenerator extends ChartGenerator {
        private final LineChart mActivity;
        private final Vehicle mVehicle;
        private final String[] mProjection;

        public LineChartGenerator(LineChart chartActivity, Vehicle vehicle, String[] projection) {
            mActivity = chartActivity;
            mVehicle = vehicle;
            mProjection = projection;
        }

        @Override
        protected ChartData[] doInBackground(Object... params) {
            List<Entry> points = new ArrayList<Entry>();

            Cursor cursor = getActivity().getContentResolver().query(FillupsTable.BASE_URI,
                    mProjection, Fillup.VEHICLE_ID + " = ?", new String[] {
                        String.valueOf(mVehicle.getId())
                    }, Fillup.ODOMETER + " asc");
            publishProgress(0, cursor.getCount());
            cursor.moveToFirst();
            mActivity.createSeries(this, points, cursor, mVehicle);
            cursor.close();

            if (isCancelled()) {
                return null;
            }
            return new ChartData[] {
                    new ChartData(mActivity.getAxisTitle(), points)
            };
        }

        public final void update(int update) {
            publishProgress(update);
        }
    }
}
