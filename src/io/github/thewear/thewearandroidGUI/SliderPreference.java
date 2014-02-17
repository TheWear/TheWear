package io.github.thewear.thewearandroidGUI;

import io.github.thewear.thewearandroid.R;
import io.github.thewear.thewearandroidClientAPP.PreferenceConvertor;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SliderPreference extends DialogPreference {

	private int mValue;
	private int mAdjustedValue;
	private String preferenceDescription;
	private int unitType = -1;
	private int unitPreferenceDefault = -1;
	private PreferenceConvertor myPreferenceConvertor;
	private int minimumPreferenceValue;
	private int maximumPreferenceValue;
	SeekBar mSeekBar;
	TextView preferenceValueTextView;

	public SliderPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);

		// get custom values
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.SliderPreference, 0, 0);

		preferenceDescription = a
				.getString(R.styleable.SliderPreference_description);
		if (preferenceDescription == null) {
			throw new IllegalArgumentException(
					"IntegerListPreference: error - Description is not specified");
		}

		unitType = a.getInt(R.styleable.SliderPreference_unitType, unitType);
		if (unitType == -1) {
			throw new IllegalArgumentException(
					"IntegerListPreference: error - UnitType is not specified");
		}

		unitPreferenceDefault = a
				.getInt(R.styleable.SliderPreference_defaultUnit,
						unitPreferenceDefault);
		if (unitPreferenceDefault == -1) {
			throw new IllegalArgumentException(
					"IntegerListPreference: error - unitPreferenceDefault is not specified");
		}

		minimumPreferenceValue = a.getInt(
				R.styleable.SliderPreference_minimumValue,
				minimumPreferenceValue);
		if (minimumPreferenceValue == -1) {
			throw new IllegalArgumentException(
					"IntegerListPreference: error - minimumPreferenceValue is not specified");
		}
		maximumPreferenceValue = a.getInt(
				R.styleable.SliderPreference_maximumValue,
				maximumPreferenceValue);
		if (maximumPreferenceValue == -1) {
			throw new IllegalArgumentException(
					"IntegerListPreference: error - maximumPreferenceValue is not specified");
		}

		a.recycle();
	}

	protected View onCreateDialogView() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.slider_preference_layout, null);

		TextView preferenceDescriptionTextView = (TextView) view
				.findViewById(R.id.SliderPreferenceDescription);
		preferenceDescriptionTextView.setText(preferenceDescription);

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
			Log.e("TheWearDebug", "No such UnitPreferenceKey");
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

		mValue = myPreferenceConvertor
				.convertValueForDisplaying(mValue);
		mAdjustedValue = myPreferenceConvertor
				.NormalToAdjusted(mValue);
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
					// set the preference values
					mValue = myPreferenceConvertor.AdjustedToNormal(progress);
					String setPreferenceValue = "" + mValue;
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
		return a.getInteger(index, 0);
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

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		// When the user selects "OK", persist the new value
		mValue = myPreferenceConvertor.convertValueForPersisting(mValue);
		if (positiveResult) {
			persistInt(mValue);
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

			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
}