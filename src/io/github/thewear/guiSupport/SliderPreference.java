package io.github.thewear.guiSupport;

import io.github.thewear.thewearandroid.R;
import io.github.thewear.thewearandroidClientAPP.PreferenceConvertor;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SliderPreference extends ExtendedDialogPreference {

	/**
	 * SliderPreference is a Preference with a SeekBar slider which can be used
	 * to select integer values for preferences.
	 * 
	 * @attr ref R.styleable#SliderPreference_description
	 * @attr ref R.styleable#SliderPreference_unitType
	 * @attr ref R.styleable#SliderPreference_defaultUnit
	 * @attr ref R.styleable#SliderPreference_minimumValue
	 * @attr ref R.styleable#SliderPreference_maximumValue
	 * @attr ref R.styleable#SliderPreference_contentDescription
	 * @attr ref R.styleable#SliderPreference_src
	 */

	private int mValue;
	private String preferenceDescription;
	private int unitType = -1;
	private int unitPreferenceDefault = -1;
	private PreferenceConvertor myPreferenceConvertor;
	private int minimumPreferenceValue;
	private int maximumPreferenceValue;
	private SeekBar mSeekBar;
	private TextView preferenceValueTextView;
	private int preferenceDefaultValue;
	private String mImageContentDescription;
	private Drawable mDrawable;

	/**
	 * the constructor SliderPreference(Context context, AttributeSet attrs)
	 * sets the positiveButtonText and negativeButtonText, and imports all
	 * values from custom fields.
	 */

	public SliderPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		// get custom values
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.SliderPreference, 0, 0);

		preferenceDescription = a
				.getString(R.styleable.SliderPreference_description);
		if (preferenceDescription == null) {
			throw new IllegalArgumentException(
					"SliderPreference: error - Description is not specified");
		}

		unitType = a.getInt(R.styleable.SliderPreference_unitType, unitType);
		if (unitType == -1) {
			throw new IllegalArgumentException(
					"SliderPreference: error - UnitType is not specified");
		}

		unitPreferenceDefault = a
				.getInt(R.styleable.SliderPreference_defaultUnit,
						unitPreferenceDefault);
		if (unitPreferenceDefault == -1) {
			throw new IllegalArgumentException(
					"SliderPreference: error - unitPreferenceDefault is not specified");
		}

		minimumPreferenceValue = a.getInt(
				R.styleable.SliderPreference_minimumValue,
				minimumPreferenceValue);
		if (minimumPreferenceValue == -1) {
			throw new IllegalArgumentException(
					"SliderPreference: error - minimumPreferenceValue is not specified");
		}
		maximumPreferenceValue = a.getInt(
				R.styleable.SliderPreference_maximumValue,
				maximumPreferenceValue);
		if (maximumPreferenceValue == -1) {
			throw new IllegalArgumentException(
					"SliderPreference: error - maximumPreferenceValue is not specified");
		}

		mImageContentDescription = a
				.getString(R.styleable.SliderPreference_contentDescription);
		if (mImageContentDescription == null) {
			throw new IllegalArgumentException(
					"SliderPreference: error - mImageContentDescription is not specified");
		}

		mDrawable = a.getDrawable(R.styleable.SliderPreference_src);
		if (mDrawable == null) {
			throw new IllegalArgumentException(
					"SliderPreference: error - mDrawable is not specified");
		}

		a.recycle();
	}

	/**
	 * onCreateDialogView() executes all the code we want to have executed when
	 * the dialog is created:
	 * 
	 * * Set the dialog layout (using a LayoutInflater) and content
	 * 
	 * * Set the onChangeListeners for the SeekBar to change the EditText values
	 * when the seekBar is changed
	 * 
	 * * Set a positive button (OK button) for saving of the preferences and to
	 * close the window.
	 * 
	 * * Set a negative button (Cancel button) to cancel a change of preferences
	 * and to close the window
	 * 
	 * The temperatures are shown as °C or °F according to the user preference.
	 * 
	 * The Wind Speed is shown in m/s, Beaufort or Knots according to the user
	 * preference.
	 */

	@Override
	protected View onCreateDialogView() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.slider_preference_layout, null);

		TextView preferenceDescriptionTextView = (TextView) view
				.findViewById(R.id.SliderPreferenceDescription);
		preferenceDescriptionTextView.setText(preferenceDescription);

		ImageView descriptiveImage = (ImageView) view
				.findViewById(R.id.SliderPreferenceImageView);
		descriptiveImage.setImageDrawable(mDrawable);
		descriptiveImage.setContentDescription(mImageContentDescription);

		TextView preferenceUnitTextView = (TextView) view
				.findViewById(R.id.SliderPreferenceUnit);
		SharedPreferences sharedPref = getSharedPreferences();
		Resources res = getContext().getResources();
		String[] units = null;
		int unitPreference = -1;
		switch (unitType) {
		case 0: // Temperature notation
			units = res.getStringArray(R.array.temperatureNotationShort);
			unitPreference = sharedPref
					.getInt(res
							.getString(R.string.temperature_notation_preference_key),
							unitPreferenceDefault);
			break;
		case 1: // WindSpeed notation
			units = res.getStringArray(R.array.windspeedNotationPreferenceText);
			unitPreference = sharedPref.getInt(
					res.getString(R.string.windspeed_notation_preference_key),
					unitPreferenceDefault);
			String unit = units[unitPreference];
			preferenceUnitTextView.setText(unit);
			break;
		default:
			throw new IllegalArgumentException(
					"SliderPreference: error - No such UnitPreferenceKey");
		}
		String unit = units[unitPreference];
		preferenceUnitTextView.setText(unit);

		mSeekBar = (SeekBar) view.findViewById(R.id.sliderPreferenceSeekBar);
		myPreferenceConvertor = new PreferenceConvertor(unitType,
				unitPreference);
		myPreferenceConvertor.initiatePreferenceConvertor(
				minimumPreferenceValue, maximumPreferenceValue);
		mSeekBar.setMax(myPreferenceConvertor.prefMax
				- myPreferenceConvertor.prefMin);

		mValue = myPreferenceConvertor.convertValueForDisplaying(mValue);
		int mAdjustedValue = myPreferenceConvertor.NormalToAdjusted(mValue);
		mSeekBar.setProgress(mAdjustedValue);

		preferenceValueTextView = (TextView) view
				.findViewById(R.id.SliderPreferenceValue);
		String valueText = "" + mValue;
		preferenceValueTextView.setText(valueText);

		TextView preferenceLeftMarginTextView = (TextView) view
				.findViewById(R.id.SliderPreferenceLeftMargin);
		String prefMinText = "" + myPreferenceConvertor.prefMin;
		preferenceLeftMarginTextView.setText(prefMinText);
		TextView preferenceRightMarginTextView = (TextView) view
				.findViewById(R.id.SliderPreferenceRightMargin);
		String prefMaxText = "" + myPreferenceConvertor.prefMax;
		preferenceRightMarginTextView.setText(prefMaxText);

		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser == true) {
					int mChangedValue = myPreferenceConvertor
							.AdjustedToNormal(progress);
					String setPreferenceValue = "" + mChangedValue;
					preferenceValueTextView.setText(setPreferenceValue);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// Auto-generated method stub
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// Auto-generated method stub
			}
		});

		return view;
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return preferenceDefaultValue = a.getInteger(index, 0);
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue,
			Object defaultValue) {
		if (restorePersistedValue) {
			// Restore existing state
			mValue = this.getPersistedInt(mValue);
		} else {
			// Set default state from the XML attribute
			mValue = (Integer) defaultValue;
			persistInt(mValue);
		}
	}

	/**
	 * showDialog(Bundle state) with super.showDialog(state) to get the normal
	 * behavior and then overwrite the onClick behavior of the neutral button.
	 */

	@Override
	protected void showDialog(Bundle state) {
		super.showDialog(state);

		// Overwrite neutral button
		AlertDialog d = (AlertDialog) getDialog();
		Button neutralButton = d.getButton(DialogInterface.BUTTON_NEUTRAL);
		neutralButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int mDefaultValue = myPreferenceConvertor
						.convertValueForDisplaying(preferenceDefaultValue);
				int mAdjustedValue = myPreferenceConvertor
						.NormalToAdjusted(mDefaultValue);
				mSeekBar.setProgress(mAdjustedValue);
				String mValueText = "" + mDefaultValue;
				preferenceValueTextView.setText(mValueText);
			}
		});
	}

	/**
	 * onDialogClosed(boolean positiveResult) converts the preference value back
	 * to the default value (°C or m/s) for saving, and saves the value when the
	 * user had clicked the positive button.
	 */

	@Override
	protected void onDialogClosed(int whichButtonClicked) {
		// When the user selects "OK", persist the new value
		if (DialogInterface.BUTTON_POSITIVE == whichButtonClicked) {
			int seekBarProgress = mSeekBar.getProgress();
			mValue = myPreferenceConvertor.AdjustedToNormal(seekBarProgress);
			mValue = myPreferenceConvertor.convertValueForPersisting(mValue);
			persistInt(mValue);
		} else if (DialogInterface.BUTTON_NEUTRAL == whichButtonClicked) {
			// This should not be called
		} else {
			mValue = myPreferenceConvertor.convertValueForPersisting(mValue);
		}
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		// Check whether this Preference is persistent (continually saved)
		if (isPersistent()) {
			// No need to save instance state since it's persistent, use
			// superclass state
			return superState;
		}

		// Create instance of custom BaseSavedState
		final SavedState myState = new SavedState(superState);
		// Set the state's value with the class member that holds current
		// setting value
		myState.value = mValue;
		return myState;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		// Check whether we saved the state in onSaveInstanceState
		if (state == null || !state.getClass().equals(SavedState.class)) {
			// Didn't save the state, so call superclass
			super.onRestoreInstanceState(state);
			return;
		}

		// Cast state to custom BaseSavedState and pass to superclass
		SavedState myState = (SavedState) state;
		super.onRestoreInstanceState(myState.getSuperState());

		// Set this Preference's widget to reflect the restored state
		mSeekBar.setProgress(myState.value);
	}

	private static class SavedState extends BaseSavedState {
		// Member that holds the setting's value
		// Change this data type to match the type saved by your Preference
		int value;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		public SavedState(Parcel source) {
			super(source);
			// Get the current preference's value
			value = source.readInt(); // Change this to read the appropriate
										// data type
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			// Write the preference's value
			dest.writeInt(value); // Change this to write the appropriate data
									// type
		}

		// Standard creator object using an instance of this class
		@SuppressWarnings("unused")
		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
}