
package com.evancharlton.mileage.io;

import android.net.Uri;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DbExportActivity extends BaseExportActivity {
    @Override
    protected ExportTask createExportTask() {
        return new DbExportTask();
    }

    private static final class DbExportTask extends ExportTask {
        @Override
        public String performExport(String inputFile, String outputFile) {
            Uri uri = Uri.parse(outputFile);
            try (InputStream in = new FileInputStream(inputFile);
                    OutputStream out = mActivity.getContentResolver().openOutputStream(uri)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                return outputFile;
            } catch (IOException e) {
            }
            return null;
        }
    }
}
