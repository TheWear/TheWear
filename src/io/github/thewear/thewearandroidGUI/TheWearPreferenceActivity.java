package io.github.thewear.thewearandroidGUI;

import io.github.thewear.thewearandroid.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class TheWearPreferenceActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	// addPreferencesFromResource and getPreferenceManager are deprecated in API
	// level 11; but we are using API level 10
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(
				getString(R.string.TheWear_preference_key));
		getPreferenceManager().setSharedPreferencesMode(MODE_PRIVATE);
		addPreferencesFromResource(R.xml.preferences);
	}

	// TODO: Move Forecast Preferences here if we think this won't be changed a
	// lot

	// ListPreference for the region thingy

	// TODO set to defaults
}
