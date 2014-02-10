package io.github.thewear.thewearandroidGUI;

import io.github.thewear.thewearandroidClientAPP.ForecastInfo;
import io.github.thewear.thewearandroidClientAPP.MergeImage;
import io.github.thewear.thewearandroidClientAPP.PreferenceConvertor;
import io.github.thewear.thewearandroidClientAPP.SettingsConvertor;
import io.github.thewear.thewearandroidClientAPP.WeatherEnumHandler;

import java.util.ArrayList;
import java.util.Arrays;

import src.gui.thewearandroid.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ForecastPreferencesFragment extends DialogFragment {

	/**
	 * The Java code linked with preference_dialog.xml layout. These two files
	 * together display a dialog in which the user can chose their own preferred
	 * weather limits.
	 * 
	 * The preferred values are stored in SharedPreferences.
	 */

	private SharedPreferences sharedPref;
	private View dialogView;
	private int defaultPreference1Value;
	private int defaultPreference2Value;
	private int defaultPreference3Value;
	private EditText preference1EditText;
	private EditText preference2EditText;
	private EditText preference3EditText;
	private SeekBar preference1SeekBar;
	private SeekBar preference2SeekBar;
	private SeekBar preference3SeekBar;
	private int preference1Value;
	private int preference2Value;
	private int preference3Value;
	private int adjustedPreference1Value;
	private int adjustedPreference2Value;
	private int adjustedPreference3Value;
	private PreferenceConvertor myPreference1Convertor;
	private PreferenceConvertor myPreference2Convertor;
	private PreferenceConvertor myPreference3Convertor;
	private ImageView[] myImageViews = { null, null, null };
	ForecastInfo myForecastInfo;
	private Context applicationContext;
	private ProgressBar myProgressBar;
	private Dialog myDialog;
	private int temperatureNotation;
	private int windSpeedNotation;
	private int preference1_min;
	private int preference1_max;
	private int preference2_min;
	private int preference2_max;
	private int preference3_min;
	private int preference3_max;
	private boolean forecastInfo = false;

	/**
	 * onCreateDialog executes all the code we want to have executed when the
	 * dialog is created:
	 * 
	 * * Set the dialog layout and content
	 * 
	 * * Set the onChangeListeners for the SeekBars to change the EditText
	 * values when the seekBar is changed
	 * 
	 * * Set the onEditorActionListeners for the EditText to let the user change
	 * the preferences through the EditTexsts. if the user chooses a too small
	 * or too big value, the minimum or maximum value for the preference is used
	 * respectively.
	 * 
	 * * Set a positive button (OK button) for saving of the preferences, to
	 * close the window and to change the forecast images according to the new
	 * preferences values. A progressBar is shown to notify the user that the
	 * images are changing.
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
	public Dialog onCreateDialog(Bundle SavedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// get the LayoutInflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because the dialog is going in the
		// dialog layout
		dialogView = inflater.inflate(R.layout.preference_dialog, null);
		builder.setView(dialogView);
		Log.d("TheWearDebug", "Preferences Dialog Created");

		// Get default Preference Values
		defaultPreference1Value = Integer
				.parseInt(getString(R.string.preference1_defaultValue));
		defaultPreference2Value = Integer
				.parseInt(getString(R.string.preference2_defaultValue));
		defaultPreference3Value = Integer
				.parseInt(getString(R.string.preference3_defaultValue));
		final Resources res = getResources();
		final int defaultTemperatureNotation = res
				.getInteger(R.integer.defaultTemperatureNotation);
		final int defaultWindSpeedNotation = res
				.getInteger(R.integer.defaultWindSpeedNotation);
		Log.d("TheWearDebug", "Got default Values");

		// Read the preference values from Shared Preferences
		sharedPref = getActivity().getSharedPreferences(
				getString(R.string.TheWear_preference_key),
				Context.MODE_PRIVATE);
		preference1Value = sharedPref.getInt(
				getString(R.string.forecast_preference1),
				defaultPreference1Value);
		preference2Value = sharedPref.getInt(
				getString(R.string.forecast_preference2),
				defaultPreference2Value);
		preference3Value = sharedPref.getInt(
				getString(R.string.forecast_preference3),
				defaultPreference3Value);
		temperatureNotation = sharedPref.getInt(
				getString(R.string.temperature_notation_preference),
				defaultTemperatureNotation);
		windSpeedNotation = sharedPref.getInt(
				getString(R.string.windspeed_notation_preference),
				defaultWindSpeedNotation);
		Log.d("TheWearDebug", "Red the preference values");
		Log.d("BUG", "Preference2Value: " + preference2Value);

		// Set dialog Title
		builder.setTitle(R.string.preference_dialog_Title);

		// Set Preference Unit °C or °F
		TextView Preference1UnitTextView = (TextView) dialogView
				.findViewById(R.id.preference1_preferenceUnit);
		TextView Preference3UnitTextView = (TextView) dialogView
				.findViewById(R.id.preference3_preferenceUnit);
		switch (temperatureNotation) {
		case 0: // °C
			Preference1UnitTextView.setText(R.string.setting2RadioButton1);
			Preference3UnitTextView.setText(R.string.setting2RadioButton1);
			break;
		case 1: // °F
			Preference1UnitTextView.setText(R.string.setting2RadioButton2);
			Preference3UnitTextView.setText(R.string.setting2RadioButton2);
			break;
		default:
			Log.e("TheWearDebug", "No such temperature notation");
		}
		// Set Preference Unit m/s, Beaufort or Knots
		TextView Preference2UnitTextView = (TextView) dialogView
				.findViewById(R.id.preference2_preferenceUnit);
		switch (windSpeedNotation) {
		case 0:
			Preference2UnitTextView.setText(R.string.setting3RadioButton1);
			break;
		case 1:
			Preference2UnitTextView.setText(R.string.setting3RadioButton2);
			break;
		case 2:
			Preference2UnitTextView.setText(R.string.setting3RadioButton3);
			break;
		default:
			Log.e("TheWearDebug", "No such Wind Speed notation");
		}

		// °C or °F
		switch (temperatureNotation) {
		case 0: // °C, no conversion needed
			// Nothing
			break;
		case 1: // °F, conversion needed.
			preference1Value = SettingsConvertor
					.celsiusToFahrenheit(preference1Value);
			preference3Value = SettingsConvertor
					.celsiusToFahrenheit(preference3Value);
			break;
		default:
			Log.e("TheWearDebug", "No such temperature notation");
		}

		// m/s, Beaufort or Knots
		switch (windSpeedNotation) {
		case 0: // m/s, no conversion needed
			// Nothing
			break;
		case 1: // Beaufort, conversion needed
			preference2Value = SettingsConvertor
					.metersToBeaufort(preference2Value);
			break;
		case 2: // Knots, conversion needed
			preference2Value = (int) SettingsConvertor
					.metersToKnots(preference2Value);
			break;
		default:
			Log.e("TheWearDebug", "No such wind speed notation");
		}
		Log.d("BUG", "new preference2Value: " + preference2Value);

		// Initiate EditTexts and SeekBars
		preference1EditText = (EditText) dialogView
				.findViewById(R.id.preference1_preferenceNumber);
		preference2EditText = (EditText) dialogView
				.findViewById(R.id.preference2_preferenceNumber);
		preference3EditText = (EditText) dialogView
				.findViewById(R.id.preference3_preferenceNumber);
		preference1SeekBar = (SeekBar) dialogView.findViewById(R.id.seekBar1);
		preference2SeekBar = (SeekBar) dialogView.findViewById(R.id.seekBar2);
		preference3SeekBar = (SeekBar) dialogView.findViewById(R.id.seekBar3);
		Log.d("TheWearDebug", "EditTexts and SeekBars initiated");

		// Initiate PreferenceConvertor
		// Temperature
		myPreference1Convertor = new PreferenceConvertor();
		myPreference1Convertor.initiatePreferenceConvertor(getActivity(),
				"preference1", temperatureNotation, false, 0);
		// Wind (windSpeedNotation + 2 because of switch in PreferenceConvertor)
		myPreference2Convertor = new PreferenceConvertor();
		myPreference2Convertor
				.initiatePreferenceConvertor(getActivity(), "preference2",
						(windSpeedNotation + 2), true, windSpeedNotation);
		// Temperature
		myPreference3Convertor = new PreferenceConvertor();
		myPreference3Convertor.initiatePreferenceConvertor(getActivity(),
				"preference3", temperatureNotation, false, 0);
		Log.d("TheWearDebug", "PreferenceConvertors initiated");

		// Max. and Min of the preferences:
		preference1_max = -1;
		preference3_max = -1;
		preference1_min = -1;
		preference3_min = -1;
		switch (temperatureNotation) {
		case 0: // °C, no conversion needed
			preference1_max = res.getInteger(R.integer.preference1_max);
			preference3_max = res.getInteger(R.integer.preference3_max);
			preference1_min = res.getInteger(R.integer.preference1_min);
			preference3_min = res.getInteger(R.integer.preference3_min);
			break;
		case 1: // °F, conversion needed.
			preference1_max = SettingsConvertor.celsiusToFahrenheit(res
					.getInteger(R.integer.preference1_max));
			preference3_max = SettingsConvertor.celsiusToFahrenheit(res
					.getInteger(R.integer.preference3_max));
			preference1_min = SettingsConvertor.celsiusToFahrenheit(res
					.getInteger(R.integer.preference1_min));
			preference3_min = SettingsConvertor.celsiusToFahrenheit(res
					.getInteger(R.integer.preference3_min));
			break;
		default:
			Log.e("TheWearDebug", "No such temperature notation");
		}
		switch (windSpeedNotation) {
		case 0: // m/s, no conversion needed
			preference2_max = res.getInteger(R.integer.preference2_max1);
			preference2_min = res.getInteger(R.integer.preference2_min1);
			break;
		case 1: // Beaufort, conversion needed
			preference2_max = res.getInteger(R.integer.preference2_max2);
			preference2_min = res.getInteger(R.integer.preference2_min2);
			break;
		case 2: // Knots, conversion needed
			preference2_max = (int) res.getInteger(R.integer.preference2_max3);
			preference2_min = (int) res.getInteger(R.integer.preference2_min3);
			break;
		default:
			Log.e("TheWearDebug", "No such wind speed notation");
		}

		// Set max of the SeekBars
		int preference1SeekBarMax = preference1_max - preference1_min;
		Log.d("TheWearAndroid", "preference1_max = " + preference1_max
				+ " & preference1_min = " + preference1_min);
		Log.d("TheWearAndroid", "preference1SeekBarMax = "
				+ preference1SeekBarMax);
		int preference3SeekBarMax = preference3_max - preference3_min;
		Log.d("TheWearAndroid", "preference3_max = " + preference3_max
				+ " & preference3_min = " + preference3_min);
		Log.d("TheWearAndroid", "preference3SeekBarMax = "
				+ preference3SeekBarMax);
		int preference2SeekBarMax = preference2_max - preference2_min;
		Log.d("TheWearAndroid", "preference2_max = " + preference2_max
				+ " & preference2_min = " + preference2_min);
		Log.d("TheWearAndroid", "preference2SeekBarMax = "
				+ preference2SeekBarMax);
		preference1SeekBar.setMax(preference1SeekBarMax);
		preference2SeekBar.setMax(preference2SeekBarMax);
		preference3SeekBar.setMax(preference3SeekBarMax);
		Log.d("TheWeardebug", "Seekbars max set");
		// Adjust values for SeekBar
		adjustedPreference1Value = myPreference1Convertor
				.NormalToAdjusted(preference1Value);
		adjustedPreference2Value = myPreference2Convertor
				.NormalToAdjusted(preference2Value);
		adjustedPreference3Value = myPreference3Convertor
				.NormalToAdjusted(preference3Value);
		Log.d("TheWearDebug", "Values converted for SeekBars");
		// set the preference values
		String setPreference1Value = preference1Value + "";
		String setPreference2Value = preference2Value + "";
		String setPreference3Value = preference3Value + "";
		preference1EditText.setText(setPreference1Value);
		preference2EditText.setText(setPreference2Value);
		preference3EditText.setText(setPreference3Value);
		preference1SeekBar.setProgress(adjustedPreference1Value);
		preference2SeekBar.setProgress(adjustedPreference2Value);
		preference3SeekBar.setProgress(adjustedPreference3Value);
		Log.d("TheWearDebug",
				"Preference values set on the editTexsts and SeekBars");

		// Set the OnChangeListeners for the SeekBars
		// SeekBar Preference 1
		preference1SeekBar
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						if (fromUser == true) {
							// set the preference values
							preference1Value = myPreference1Convertor
									.AdjustedToNormal(progress);
							;
							String setPreference1Value = preference1Value + "";
							preference1EditText.setText(setPreference1Value);
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

		// SeekBar Preference 2
		preference2SeekBar
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						if (fromUser == true) {
							// set the preference values
							Log.d("BUG", "changed preference2Value: "
									+ progress);
							preference2Value = myPreference2Convertor
									.AdjustedToNormal(progress);
							Log.d("BUG", "adjusted changed preference2Value: "
									+ progress);
							String setPreference2Value = preference2Value + "";
							preference2EditText.setText(setPreference2Value);
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

		// SeekBar Preference 3
		preference3SeekBar
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						if (fromUser == true) {
							// set the preference values
							preference3Value = myPreference3Convertor
									.AdjustedToNormal(progress);
							String setPreference3Value = preference3Value + "";
							preference3EditText.setText(setPreference3Value);
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

		// Listener for preference 1 EditText
		preference1EditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				String submittedPreference = preference1EditText.getText()
						.toString().trim();
				if (!submittedPreference.equals("")) {
					int parsedIntPreference = Integer
							.parseInt(submittedPreference);
					// Correct too high and too low values
					if (parsedIntPreference > preference1_max) {
						parsedIntPreference = preference1_max;
						String parsedStringPrefrence = parsedIntPreference + "";
						preference1EditText.setText(parsedStringPrefrence);
					} else if (parsedIntPreference < preference1_min) {
						parsedIntPreference = preference1_min;
						String parsedStringPrefrence = parsedIntPreference + "";
						preference1EditText.setText(parsedStringPrefrence);
					}
					int adjustedparsedIntPreference = myPreference1Convertor
							.NormalToAdjusted(parsedIntPreference);
					preference1SeekBar.setProgress(adjustedparsedIntPreference);
					preference1Value = parsedIntPreference;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// Auto-generated method stub
			}

		});

		// Listener for preference 2 EditText
		preference2EditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				String submittedPreference = preference2EditText.getText()
						.toString().trim();
				if (!submittedPreference.equals("")) {
					int parsedIntPreference = Integer
							.parseInt(submittedPreference);
					// Correct too high and too low values
					if (parsedIntPreference > preference2_max) {
						parsedIntPreference = preference2_max;
						String parsedStringPrefrence = parsedIntPreference + "";
						preference2EditText.setText(parsedStringPrefrence);
					} else if (parsedIntPreference < preference2_min) {
						parsedIntPreference = preference2_min;
						String parsedStringPrefrence = parsedIntPreference + "";
						preference2EditText.setText(parsedStringPrefrence);
					}
					int adjustedparsedIntPreference = myPreference2Convertor
							.NormalToAdjusted(parsedIntPreference);
					preference2SeekBar.setProgress(adjustedparsedIntPreference);
					preference2Value = parsedIntPreference;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// Auto-generated method stub
			}

		});

		// Listener for preference 3 EditText
		preference3EditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				String submittedPreference = preference3EditText.getText()
						.toString().trim();
				if (!submittedPreference.equals("")) {
					int parsedIntPreference = Integer
							.parseInt(submittedPreference);
					// Correct too high and too low values
					if (parsedIntPreference > preference3_max) {
						parsedIntPreference = preference3_max;
						String parsedStringPrefrence = parsedIntPreference + "";
						preference3EditText.setText(parsedStringPrefrence);
					} else if (parsedIntPreference < preference3_min) {
						parsedIntPreference = preference3_min;
						String parsedStringPrefrence = parsedIntPreference + "";
						preference3EditText.setText(parsedStringPrefrence);
					}
					int adjustedparsedIntPreference = myPreference3Convertor
							.NormalToAdjusted(parsedIntPreference);
					preference3SeekBar.setProgress(adjustedparsedIntPreference);
					preference3Value = parsedIntPreference;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// Auto-generated method stub
			}

		});

		// Add action Buttons
		builder.setPositiveButton(R.string.positive_button,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						myProgressBar.setIndeterminate(true);
						myProgressBar.setVisibility(0);

						// Convert back to °C for saving purposes if necessary.
						int savingPreference1Value = -1;
						int savingPreference3Value = -1;
						switch (temperatureNotation) {
						case 0: // °C
							savingPreference1Value = preference1Value;
							savingPreference3Value = preference3Value;
							break;
						case 1: // °F
							savingPreference1Value = Math.round(SettingsConvertor
									.fahrenheitToCelsius(preference1Value));
							savingPreference3Value = Math.round(SettingsConvertor
									.fahrenheitToCelsius(preference3Value));
							break;
						default:
							Log.e("TheWearDebug",
									"No such temperature notation");
						}

						// Converting back to m/s for saving purposes if
						// necessary
						Log.d("BUG", "new preference2Value for saving: "
								+ preference2Value);
						int savingPreference2Value = -1;
						switch (windSpeedNotation) {
						case 0:
							savingPreference2Value = preference2Value;
							break;
						case 1:
							savingPreference2Value = Math.round(SettingsConvertor
									.beaufortToMeters(preference2Value));
							break;
						case 2:
							savingPreference2Value = Math
									.round(SettingsConvertor
											.knotsToMeters(preference2Value));
							break;
						default:
							Log.e("TheWearDebug", "No such wind speed notation");
						}
						Log.d("BUG", "new savingPreference2Value: "
								+ savingPreference2Value);

						// Saves the changed preference values and closes the
						// dialog
						SharedPreferences.Editor editor = sharedPref.edit();
						editor.putInt(getString(R.string.forecast_preference1),
								savingPreference1Value);
						editor.putInt(getString(R.string.forecast_preference2),
								savingPreference2Value);
						editor.putInt(getString(R.string.forecast_preference3),
								savingPreference3Value);
						editor.commit();
						Log.d("TheWearDebug", "Saved the changed Preferences");

						// Check if there is a forecast to change
						if (forecastInfo == true) {

							// Set the new Bitmaps in the ImageViews
							if (myForecastInfo == null) {
								Log.d("TheWearDebug",
										"No Dataset available, so the forecast image isn't changed.");
							} else {
								ArrayList<String[]> datasets = myForecastInfo.dataset;
								if (datasets == null) {
									Log.d("TheWearDebug",
											"No Dataset available, so the forecast image isn't changed.");
								} else {
									// Recreate the Bitmaps and set them into
									// the
									// imageViews
									Bitmap[] myBitmap = { null, null, null };
									for (int i = 0; i <= 2; i++) {
										WeatherEnumHandler weather_data;
										weather_data = new WeatherEnumHandler();
										weather_data.handleWeatherEnum(
												datasets.get(i),
												applicationContext);
										Log.d("TheWearDebug",
												"handled WeatherEnum");

										boolean[] advice = weather_data.weatherType.show_imgs;
										advice = Arrays.copyOf(advice,
												advice.length + 1);
										advice[advice.length - 1] = weather_data.sunglasses;

										MergeImage myMergeImage = new MergeImage();
										myBitmap[i] = myMergeImage
												.MergeForecastImage(advice,
														applicationContext);
										myImageViews[i]
												.setImageBitmap(myBitmap[i]);
									}
								}
							}
						}
					}
				})
				.setNeutralButton(R.string.neutral_button,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Initiating original onClickListener
								// is overwritten at OnStart()
							}
						})
				.setNegativeButton(R.string.negative_button,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Cancels changes to the Preferences and
								// closes the dialog
							}
						});
		Log.d("TheWearDebug", "Created buttons and listeners of those buttons");

		// Set the ProgressBar invisible
		myProgressBar = (ProgressBar) dialogView
				.findViewById(R.id.progressBar1);
		myProgressBar.setVisibility(4);

		myDialog = builder.create();

		return myDialog;
	}

	/**
	 * onStart() sets an onClickListener for the neutral button that doesn't
	 * close the dialog when clicked unlike the original onClickListener
	 * 
	 * When Clicked the neutral button sets the preference values back to the
	 * default ones.
	 */

	@Override
	public void onStart() {
		super.onStart();
		AlertDialog d = (AlertDialog) getDialog();
		if (d != null) {
			Log.d("TheWearDebug", "Custom onClickDialog set");
			Button neutralButton = (Button) d.getButton(Dialog.BUTTON_NEUTRAL);
			neutralButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// °C or °F
					switch (temperatureNotation) {
					case 0: // °C, no conversion needed
						preference1Value = defaultPreference1Value;
						preference3Value = defaultPreference3Value;
						break;
					case 1: // °F, conversion needed.
						preference1Value = SettingsConvertor
								.celsiusToFahrenheit(defaultPreference1Value);
						preference3Value = SettingsConvertor
								.celsiusToFahrenheit(defaultPreference3Value);
						break;
					default:
						Log.e("TheWearDebug", "No such temperature notation");
					}

					// m/s, Beaufort or Knots
					switch (windSpeedNotation) {
					case 0: // m/s, no conversion needed
						preference2Value = defaultPreference2Value;
						break;
					case 1: // Beaufort, conversion needed.
						preference2Value = SettingsConvertor
								.metersToBeaufort(defaultPreference2Value);
						break;
					case 2: // Knots, conversion needed.
						preference2Value = (int) SettingsConvertor
								.metersToKnots(defaultPreference2Value);
						break;
					default:
						Log.e("TheWearDebug", "No such wind speed notation");
					}

					// set the EditText to the new value
					String setPreference1Value = preference1Value + "";
					String setPreference2Value = preference2Value + "";
					String setPreference3Value = preference3Value + "";
					preference1EditText.setText(setPreference1Value);
					preference2EditText.setText(setPreference2Value);
					preference3EditText.setText(setPreference3Value);
					// set the SeekBar to the new value
					adjustedPreference1Value = myPreference1Convertor
							.NormalToAdjusted(preference1Value);
					adjustedPreference2Value = myPreference2Convertor
							.NormalToAdjusted(preference2Value);
					adjustedPreference3Value = myPreference3Convertor
							.NormalToAdjusted(preference3Value);
					preference1SeekBar.setProgress(adjustedPreference1Value);
					preference2SeekBar.setProgress(adjustedPreference2Value);
					preference3SeekBar.setProgress(adjustedPreference3Value);
					Log.d("TheWearDebug", "Preferences set to default");
				}
			});
		} else {
			Log.e("TheWearDebug", "No Dialog yet");
		}
	}

	/**
	 * passNecessaryInformation() is used to set the ImageViews, the datasets
	 * and the applicationContext used to change the forecastImage when the user
	 * changes their preferences.
	 */

	public void passNecessaryInformation(ImageView[] myImageViews,
			ForecastInfo myForecastInfo, Context applicationContext) {
		this.myImageViews = myImageViews;
		this.myForecastInfo = myForecastInfo;
		this.applicationContext = applicationContext;
		forecastInfo = true;
	}
}