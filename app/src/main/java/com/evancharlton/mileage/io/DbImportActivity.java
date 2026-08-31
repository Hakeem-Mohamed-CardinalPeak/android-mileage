
package com.evancharlton.mileage.io;

import com.evancharlton.mileage.BaseActivity;
import com.evancharlton.mileage.ImportActivity;
import com.evancharlton.mileage.R;
import com.evancharlton.mileage.tasks.DbImportTask;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.IntentCompat;

public class DbImportActivity extends BaseActivity {
    private DbImportTask mTask;

    private TextView mLog;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_csv_progress);
        initToolbar();

        mLog = (TextView) findViewById(R.id.log);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);

        restoreTask();
    }

    private void restoreTask() {
        mTask = (DbImportTask) getLastNonConfigurationInstance();

        if (mTask == null) {
            Uri uri = IntentCompat.getParcelableExtra(getIntent(), ImportActivity.FILE_URI, Uri.class);
            mTask = new DbImportTask(uri);
        }
        mTask.attach(this);

        if (mTask.getStatus() == AsyncTask.Status.PENDING) {
            mTask.execute();
        }
    }

    public void log(String msg) {
        mLog.append(msg + "\n");
    }

    public void setWorking(boolean isWorking) {
        mProgressBar.setIndeterminate(isWorking);
        if (!isWorking) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
