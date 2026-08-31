
package com.evancharlton.mileage.tasks;

import com.evancharlton.mileage.io.CsvDateFormatActivity;

import android.net.Uri;
import android.util.Log;

import au.com.bytecode.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CsvDateReaderTask extends
        AttachableAsyncTask<CsvDateFormatActivity, String, String, String> {
    private static final String TAG = "CsvDateReaderTask";
    private final int mIndex;

    public CsvDateReaderTask(int index) {
        mIndex = index;
        Log.d(TAG, "Parsing date from column #" + index);
    }

    @Override
    protected String doInBackground(String... params) {
        final String inputFile = params[0];
        final Uri uri = Uri.parse(inputFile);
        try {
            InputStream is = getParent().getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            CSVReader csvReader = new CSVReader(reader);
            // skip the first row of headers
            csvReader.readNext();

            String[] data = csvReader.readNext();
            csvReader.close();
            return data[mIndex];
        } catch (IOException e) {
            Log.e(TAG, "Could not get columns!", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        getParent().setRawDate(result);
    }
}
