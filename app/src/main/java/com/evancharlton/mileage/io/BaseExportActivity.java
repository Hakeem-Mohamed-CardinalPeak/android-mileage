
package com.evancharlton.mileage.io;

import com.evancharlton.mileage.BaseActivity;
import com.evancharlton.mileage.ExportActivity;
import com.evancharlton.mileage.R;
import com.evancharlton.mileage.provider.FillUpsProvider;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.IntentCompat;

public abstract class BaseExportActivity extends BaseActivity {
    private ProgressBar mProgressBar;

    private TextView mLog;

    private ExportTask mExportTask;

    private String mDisplayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.export_progress);
        initToolbar();

        final Uri uri = IntentCompat.getParcelableExtra(getIntent(), ExportActivity.FILE_URI, Uri.class);
        mDisplayName = uri.getLastPathSegment();
        try (Cursor c = getContentResolver().query(uri, null, null, null, null)) {
            if (c != null && c.moveToFirst()) {
                int idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx >= 0) {
                    mDisplayName = c.getString(idx);
                }
            }
        }
        setTitle(getString(R.string.exporting, mDisplayName));

        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mLog = (TextView) findViewById(R.id.log);

        mProgressBar.setIndeterminate(true);

        mExportTask = (ExportTask) getLastCustomNonConfigurationInstance();
        if (mExportTask == null) {
            mExportTask = createExportTask();
        }
        mExportTask.attach(this);

        if (mExportTask.getStatus() == AsyncTask.Status.PENDING) {
            String dbPath = getDatabasePath(FillUpsProvider.DATABASE_NAME).getAbsolutePath();
            mExportTask.execute(dbPath, uri.toString());
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mExportTask;
    }

    public void update(Update update) {
        if (update.message != null && update.message.length() > 0) {
            mLog.append(update.message + "\n");
        }
        if (update.progress > 0) {
            mProgressBar.setIndeterminate(false);
            mProgressBar.setProgress(update.progress);
        }
        if (update.max > 0) {
            mProgressBar.setMax(update.max);
        }
    }

    public void completed(String message) {
        update(new Update(message, mProgressBar.getMax()));
    }

    abstract protected ExportTask createExportTask();

    protected static abstract class ExportTask extends AsyncTask<String, Update, String> {
        protected BaseExportActivity mActivity = null;

        public final void attach(BaseExportActivity activity) {
            mActivity = activity;
        }

        @Override
        protected final void onPreExecute() {
            mActivity.update(new Update(mActivity.getTitle().toString(), 0));
        }

        @Override
        protected final String doInBackground(String... params) {
            final String inputFile = params[0];
            final String outputFile = params[1];
            return performExport(inputFile, outputFile);
        }

        @Override
        protected final void onProgressUpdate(Update... updates) {
            mActivity.update(updates[0]);
        }

        @Override
        protected final void onPostExecute(String result) {
            final String msg;
            if (result != null) {
                msg = mActivity.getString(R.string.exported, mActivity.mDisplayName);
            } else {
                msg = mActivity.getString(R.string.export_error);
            }
            mActivity.completed(msg);
        }

        abstract public String performExport(final String inputFile, final String outputFile);
    }

    protected static final class Update {
        public final String message;

        public final int progress;

        public final int max;

        public Update(String message, int progress) {
            this(message, progress, 100);
        }

        public Update(int progress, int max) {
            this(null, progress, max);
        }

        public Update(int progress) {
            this(null, progress, 0);
        }

        private Update(String message, int progress, int max) {
            this.message = message;
            this.progress = progress;
            this.max = max;
        }
    }
}
