
package com.evancharlton.mileage;

import android.os.Bundle;

public class SettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_host);
        initToolbar();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new SettingsFragment())
                    .commit();
        }
    }
}
