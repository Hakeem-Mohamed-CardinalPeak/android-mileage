
package com.evancharlton.mileage.preferences;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.Preference;

/**
 * androidx.preference has no RingtonePreference equivalent; this is a thin marker
 * Preference whose click is handled (ringtone-picker launch + persistence) by SettingsFragment.
 */
public class RingtonePreference extends Preference {
    public RingtonePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
