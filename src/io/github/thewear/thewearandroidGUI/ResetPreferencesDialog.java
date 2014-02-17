package io.github.thewear.thewearandroidGUI;

import io.github.thewear.thewearandroid.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class ResetPreferencesDialog extends DialogPreference {

	private String[] preferenceKeys;

	public ResetPreferencesDialog(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.PreferenceKeys, 0, 0);

		preferenceKeys = (String[]) a.getTextArray(R.styleable.PreferenceKeys_all);
		if (preferenceKeys == null) {
			throw new IllegalArgumentException(
					"IntegerListPreference: error - entryList is not specified");
		}
		a.recycle();
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		// When the user selects "OK", persist the new value
		if (positiveResult) {
			SharedPreferences sharedPref = getSharedPreferences();
			Editor editor = sharedPref.edit();
			for (int i = 0; i < preferenceKeys.length; i++) {
				editor.remove(preferenceKeys[i]);
			}
			editor.commit();
		}
	}
}