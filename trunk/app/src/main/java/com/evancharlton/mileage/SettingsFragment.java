
package com.evancharlton.mileage;

import com.evancharlton.mileage.dao.Field;
import com.evancharlton.mileage.provider.Settings;
import com.evancharlton.mileage.provider.tables.FieldsTable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceClickListener {
    private ActivityResultLauncher<Intent> mRingtonePickerLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRingtonePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getParcelableExtra(
                                RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                        SharedPreferences.Editor editor =
                                getPreferenceManager().getSharedPreferences().edit();
                        editor.putString(Settings.NOTIFICATIONS_RINGTONE,
                                uri == null ? "" : uri.toString());
                        editor.commit();
                    }
                });
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        Preference about = findPreference("about");
        String version;
        try {
            version = requireContext().getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(),
                            PackageManager.GET_ACTIVITIES).versionName;
        } catch (NameNotFoundException e) {
            version = "<unknown version>";
        }
        about.setSummary(getString(R.string.settings_about_summary, version));
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(requireContext(), AboutActivity.class));
                return true;
            }
        });

        findPreference("units").setOnPreferenceClickListener(this);
        // findPreference(Settings.META_FIELD).setOnPreferenceClickListener(this);

        Preference ringtone = findPreference(Settings.NOTIFICATIONS_RINGTONE);
        ringtone.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String existing = getPreferenceManager().getSharedPreferences().getString(
                        Settings.NOTIFICATIONS_RINGTONE,
                        "content://settings/system/notification_sound");
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
                        RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                        existing.length() > 0 ? Uri.parse(existing) : null);
                mRingtonePickerLauncher.launch(intent);
                return true;
            }
        });
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if ("units".equals(preference.getKey())) {
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.units_title)
                    .setMessage(R.string.units_description)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return true;
        } else if (Settings.META_FIELD.equals(preference.getKey())) {
            showMetaFieldDialog();
            return true;
        }
        return false;
    }

    private void showMetaFieldDialog() {
        final Cursor c = requireContext().getContentResolver().query(FieldsTable.URI,
                FieldsTable.PROJECTION, null, null, null);
        final SharedPreferences prefs =
                requireContext().getSharedPreferences(Settings.NAME, android.content.Context.MODE_PRIVATE);
        new AlertDialog.Builder(requireContext())
                .setSingleChoiceItems(c, -1, Field.TITLE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long id = -1;
                        if (c.moveToPosition(which)) {
                            id = c.getLong(c.getColumnIndex(Field._ID));
                        }
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putLong(Settings.META_FIELD, id);
                        editor.commit();
                    }
                })
                .setPositiveButton(android.R.string.ok, null)
                .setTitle(R.string.dialog_title_meta_fields)
                .show();
    }
}
