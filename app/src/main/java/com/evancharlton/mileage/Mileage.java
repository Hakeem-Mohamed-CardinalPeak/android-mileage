
package com.evancharlton.mileage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Mileage extends BaseActivity implements FillupFragment.OnFillupSavedListener {
    private static final String TAG_FILLUP = "fillups";
    private static final String TAG_HISTORY = "history";
    private static final String TAG_STATISTICS = "statistics";
    private static final String TAG_VEHICLES = "vehicles";

    private Fragment mFillupFragment;
    private Fragment mHistoryFragment;
    private Fragment mStatisticsFragment;
    private Fragment mVehiclesFragment;
    private Fragment mActiveFragment;

    private BottomNavigationView mBottomNavigation;

    private final ActivityResultLauncher<String> mNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        requestNotificationPermissionIfNeeded();

        FragmentManager fm = getSupportFragmentManager();
        if (savedInstanceState == null) {
            mFillupFragment = new FillupFragment();
            mHistoryFragment = new FillupListFragment();
            mStatisticsFragment = new VehicleStatisticsFragment();
            mVehiclesFragment = new VehicleListFragment();

            fm.beginTransaction()
                    .add(R.id.content_frame, mVehiclesFragment, TAG_VEHICLES)
                    .hide(mVehiclesFragment)
                    .add(R.id.content_frame, mStatisticsFragment, TAG_STATISTICS)
                    .hide(mStatisticsFragment)
                    .add(R.id.content_frame, mHistoryFragment, TAG_HISTORY)
                    .hide(mHistoryFragment)
                    .add(R.id.content_frame, mFillupFragment, TAG_FILLUP)
                    .commit();
        } else {
            mFillupFragment = fm.findFragmentByTag(TAG_FILLUP);
            mHistoryFragment = fm.findFragmentByTag(TAG_HISTORY);
            mStatisticsFragment = fm.findFragmentByTag(TAG_STATISTICS);
            mVehiclesFragment = fm.findFragmentByTag(TAG_VEHICLES);
        }
        mActiveFragment = mFillupFragment;

        mBottomNavigation = findViewById(R.id.bottom_navigation);
        mBottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_tab_fillup) {
                switchTo(mFillupFragment);
            } else if (id == R.id.menu_tab_history) {
                switchTo(mHistoryFragment);
            } else if (id == R.id.menu_tab_statistics) {
                switchTo(mStatisticsFragment);
            } else if (id == R.id.menu_tab_vehicles) {
                switchTo(mVehiclesFragment);
            }
            return true;
        });
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            mNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void switchTo(Fragment target) {
        if (target == mActiveFragment) {
            return;
        }

        // hide the virtual keyboard when switching tabs
        InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        getSupportFragmentManager().beginTransaction()
                .hide(mActiveFragment)
                .show(target)
                .commit();
        mActiveFragment = target;
    }

    public void switchToHistoryTab() {
        mBottomNavigation.setSelectedItemId(R.id.menu_tab_history);
    }

    @Override
    public void onFillupSaved() {
        switchToHistoryTab();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mileage, menu);
        menu.findItem(R.id.menu_service_intervals).setIntent(
                new Intent(this, ServiceIntervalsListActivity.class));
        menu.findItem(R.id.menu_import_export).setIntent(
                new Intent(this, ImportExportActivity.class));
        menu.findItem(R.id.menu_settings).setIntent(new Intent(this, SettingsActivity.class));
        return super.onCreateOptionsMenu(menu);
    }
}
