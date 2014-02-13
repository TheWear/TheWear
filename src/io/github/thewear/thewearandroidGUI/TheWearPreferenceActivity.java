package io.github.thewear.thewearandroidGUI;

import io.github.thewear.thewearandroid.R;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

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
		setUpInActionBar();
		
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	// The new API is only used if the device has API level 11 or higher
	public void setUpInActionBar() {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			ActionBar actionBar = getActionBar();
		    actionBar.setDisplayHomeAsUpEnabled(true);
		    Log.i("BUG","Done");
		}
	}

	// TODO: Move Forecast Preferences here if we think this won't be changed a
	// lot

	// TODO set to defaults
}
