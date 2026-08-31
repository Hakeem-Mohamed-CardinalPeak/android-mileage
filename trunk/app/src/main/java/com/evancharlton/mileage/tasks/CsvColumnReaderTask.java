
package com.evancharlton.mileage.tasks;

import com.evancharlton.mileage.io.CsvColumnMappingActivity;

import android.net.Uri;
import android.util.Log;

import au.com.bytecode.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CsvColumnReaderTask extends
        AttachableAsyncTask<CsvColumnMappingActivity, String, Void, String[]> {
    @Override
    protected String[] doInBackground(String... inputFiles) {
        final String inputFile = inputFiles[0];
        final Uri uri = Uri.parse(inputFile);
        try {
            InputStream is = getParent().getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            CSVReader csvReader = new CSVReader(reader);
            // Note: it's assumed that the first row is the column names.
            String[] columnNames = csvReader.readNext();
            csvReader.close();
            return columnNames;
        } catch (IOException e) {
            Log.e("ColumnReaderTask", "Could not get columns!", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] columnNames) {
        getParent().setColumns(columnNames);
    }
}
