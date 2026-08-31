
package com.evancharlton.mileage;

import com.evancharlton.mileage.io.CsvExportActivity;
import com.evancharlton.mileage.io.DbExportActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class ExportActivity extends BaseActivity {
    public static final String FILE_URI = "file_uri";

    private static final String BASE_NAME = "mileage-export";

    private static final String[] FILE_TYPES = new String[] {
            ".db", ".csv"
    };

    private static final String[] MIME_TYPES = new String[] {
            "application/octet-stream", "text/csv"
    };

    @SuppressWarnings("rawtypes")
    private static final Class[] EXPORTERS = new Class[] {
            DbExportActivity.class, CsvExportActivity.class
    };

    private Spinner mFileTypes;

    private ActivityResultLauncher<String> mDbPicker;

    private ActivityResultLauncher<String> mCsvPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.export_form);
        initToolbar();

        mDbPicker = registerForActivityResult(
                new ActivityResultContracts.CreateDocument(MIME_TYPES[0]),
                uri -> launchExporter(0, uri));
        mCsvPicker = registerForActivityResult(
                new ActivityResultContracts.CreateDocument(MIME_TYPES[1]),
                uri -> launchExporter(1, uri));

        mFileTypes = (Spinner) findViewById(R.id.exporter);

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mFileTypes.getSelectedItemPosition();
                String suggestedName = BASE_NAME + FILE_TYPES[position];
                if (position == 0) {
                    mDbPicker.launch(suggestedName);
                } else {
                    mCsvPicker.launch(suggestedName);
                }
            }
        });
    }

    private void launchExporter(int position, Uri uri) {
        if (uri == null) {
            // user cancelled the picker
            return;
        }
        Intent intent = new Intent(this, EXPORTERS[position]);
        intent.putExtra(FILE_URI, uri);
        startActivity(intent);
        finish();
    }
}
