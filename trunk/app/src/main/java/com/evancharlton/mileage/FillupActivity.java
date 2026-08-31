
package com.evancharlton.mileage;

import android.os.Bundle;

public class FillupActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_host);
        initToolbar();

        if (savedInstanceState == null) {
            FillupFragment fragment = new FillupFragment();
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
    }
}
