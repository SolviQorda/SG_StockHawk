package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.sam_chordas.android.stockhawk.R;

/**
 * Created by sorengoard on 05/10/16.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);
    }
}
