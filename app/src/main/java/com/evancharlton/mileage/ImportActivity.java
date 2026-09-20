
package com.evancharlton.mileage;

import com.evancharlton.mileage.io.CsvColumnMappingActivity;
import com.evancharlton.mileage.io.DbImportActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class ImportActivity extends BaseActivity {
    public static final String FILE_URI = "file_uri";

    public static final String WIPE_DATA = "wipe data";

    private static final String[] MIME_TYPES = new String[] {
            "*/*", "text/csv"
    };

    private static final Class<?>[] IMPORTERS = new Class[] {
            DbImportActivity.class, CsvColumnMappingActivity.class
    };

    private Spinner mFileTypes;

    private CheckBox mWipeData;

    private ActivityResultLauncher<String[]> mFilePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.import_form);
        initToolbar();

        mFilePicker = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(), this::launchImporter);

        mFileTypes = (Spinner) findViewById(R.id.importer);
        mWipeData = (CheckBox) findViewById(R.id.erase_database);

        mFileTypes.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view,
                    int position, long id) {
                mWipeData.setChecked(position == 0);
                mWipeData.setEnabled(position != 0);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        View submit = findViewById(R.id.submit);
        applyBottomInset(submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilePicker.launch(new String[] {
                        MIME_TYPES[mFileTypes.getSelectedItemPosition()]
                });
            }
        });
    }

    private void launchImporter(Uri uri) {
        if (uri == null) {
            // user cancelled the picker
            return;
        }
        Intent intent = new Intent(this, IMPORTERS[mFileTypes.getSelectedItemPosition()]);
        intent.putExtra(ImportActivity.FILE_URI, uri);
        intent.putExtra(ImportActivity.WIPE_DATA, mWipeData.isChecked());
        startActivity(intent);
        finish();
    }
}
