
package com.evancharlton.mileage.tasks;

import com.evancharlton.mileage.R;
import com.evancharlton.mileage.io.DbImportActivity;
import com.evancharlton.mileage.provider.DatabaseUpgrader;
import com.evancharlton.mileage.provider.FillUpsProvider;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

public class DbImportTask extends AttachableAsyncTask<DbImportActivity, Void, String, Boolean> {
    private static final String TAG = "DbImportTask";

    private final Uri mInput;

    private File mTempFile;

    public DbImportTask(Uri input) {
        mInput = input;
    }

    @Override
    public void attach(DbImportActivity activity) {
        super.attach(activity);
        mTempFile = new File(activity.getCacheDir(), "import.db");
    }

    @Override
    protected void onPreExecute() {
        getParent().setWorking(true);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            publishProgress(getParent().getString(R.string.update_starting_import));
            makeBackup();
            publishProgress(getParent().getString(R.string.update_made_backup));
            publishProgress(getParent().getString(R.string.update_upgrading_database));
            upgradeDatabase();
            publishProgress(getParent().getString(R.string.update_upgraded_database));
            publishProgress(getParent().getString(R.string.update_cleaning_up));
            cleanUp();
            publishProgress(getParent().getString(R.string.update_finished_importing));
            return true;
        } catch (IOException e) {
            publishProgress(e.getMessage());
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(String... updates) {
        getParent().log(updates[0]);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        getParent().setWorking(false);
    }

    private void makeBackup() throws IOException {
        try (InputStream in = getParent().getContentResolver().openInputStream(mInput);
                FileOutputStream out = new FileOutputStream(mTempFile)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
    }

    private void upgradeDatabase() {
        Log.d(TAG, "Upgrading " + mTempFile.getAbsolutePath());
        SQLiteDatabase db =
                SQLiteDatabase.openDatabase(mTempFile.getAbsolutePath(), null,
                        SQLiteDatabase.OPEN_READWRITE);
        DatabaseUpgrader.upgradeDatabase(db);
        db.close();
    }

    private void cleanUp() throws IOException {
        File database = getParent().getDatabasePath(FillUpsProvider.DATABASE_NAME);
        FileChannel input = new FileInputStream(mTempFile).getChannel();
        FileChannel output = new FileOutputStream(database).getChannel();
        long bytes = input.transferTo(0, input.size(), output);
        input.close();
        output.close();
        Log.d(TAG, "Wrote " + bytes + " bytes to " + database.getAbsolutePath() + " from "
                + mTempFile.getAbsolutePath());

        mTempFile.delete();

        getParent().getContentResolver().getType(
                Uri.withAppendedPath(FillUpsProvider.BASE_URI, "reset"));
    }
}
