package src.gui.thewearandroid;

import java.util.ArrayList;

import clientAPP.ForecastInfo;
import clientAPP.MergeImage;
import clientAPP.PreferenceConvertor;
import clientAPP.WeatherEnumHandler;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

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
		
		// Set dialog Title
		builder.setTitle(R.string.preference_dialog_Title);

		// Get default Preference Values
		defaultPreference1Value = Integer
				.parseInt(getString(R.string.preference1_defaultValue));
		defaultPreference2Value = Integer
				.parseInt(getString(R.string.preference2_defaultValue));
		defaultPreference3Value = Integer
				.parseInt(getString(R.string.preference3_defaultValue));
		Log.d("TheWearDebug", "Got default Vales");

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
		Log.d("TheWearDebug", "Red the preference values");

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
		myPreference1Convertor = new PreferenceConvertor();
		myPreference1Convertor.initiatePreferenceConvertor(getActivity(),
				"preference1");
		myPreference2Convertor = new PreferenceConvertor();
		myPreference2Convertor.initiatePreferenceConvertor(getActivity(),
				"preference2");
		myPreference3Convertor = new PreferenceConvertor();
		myPreference3Convertor.initiatePreferenceConvertor(getActivity(),
				"preference3");
		Log.d("TheWearDebug", "PreferenceConvertors initiated");

		// Set max of the SeekBars
		final Resources res = getResources();
		int preference1SeekBarMax = (res.getInteger(R.integer.preference1_max))
				- (res.getInteger(R.integer.preference1_min));
		Log.d("TheWearAndroid", "preference1SeekBarMax = "
				+ preference1SeekBarMax);
		int preference2SeekBarMax = (res.getInteger(R.integer.preference2_max))
				- (res.getInteger(R.integer.preference2_min));
		Log.d("TheWearAndroid", "preference2SeekBarMax = "
				+ preference2SeekBarMax);
		int preference3SeekBarMax = (res.getInteger(R.integer.preference3_max))
				- (res.getInteger(R.integer.preference3_min));
		Log.d("TheWearAndroid", "preference3SeekBarMax = "
				+ preference3SeekBarMax);
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

					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {

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
							preference2Value = myPreference2Convertor
									.AdjustedToNormal(progress);
							String setPreference2Value = preference2Value + "";
							preference2EditText.setText(setPreference2Value);
						}
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {

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

					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {

					}
				});

		preference1EditText
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView editedTextView,
							int actionId, KeyEvent keyEvent) {
						boolean handled = false;
						if (actionId == EditorInfo.IME_ACTION_SEND) {
							String submittedPreference = editedTextView
									.getText().toString();
							int parsedIntPreference = Integer
									.parseInt(submittedPreference);
							// Correct too high and too low values
							int maxPref = res
									.getInteger(R.integer.preference1_max);
							int minPref = res
									.getInteger(R.integer.preference1_min);
							if (parsedIntPreference > maxPref) {
								parsedIntPreference = maxPref;
								String parsedStringPrefrence = parsedIntPreference
										+ "";
								editedTextView.setText(parsedStringPrefrence);
							} else if (parsedIntPreference < minPref) {
								parsedIntPreference = minPref;
								String parsedStringPrefrence = parsedIntPreference
										+ "";
								editedTextView.setText(parsedStringPrefrence);
							}
							int adjustedparsedIntPreference = myPreference1Convertor
									.NormalToAdjusted(parsedIntPreference);
							preference1SeekBar
									.setProgress(adjustedparsedIntPreference);

							preference1Value = parsedIntPreference;

							handled = true;
						}
						return handled;
					}
				});

		preference2EditText
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView editedTextView,
							int actionId, KeyEvent keyEvent) {
						boolean handled = false;
						if (actionId == EditorInfo.IME_ACTION_SEND) {
							String submittedPreference = editedTextView
									.getText().toString();
							int parsedIntPreference = Integer
									.parseInt(submittedPreference);
							// Correct too high and too low values
							int maxPref = res
									.getInteger(R.integer.preference2_max);
							int minPref = res
									.getInteger(R.integer.preference2_min);
							if (parsedIntPreference > maxPref) {
								parsedIntPreference = maxPref;
								String parsedStringPrefrence = parsedIntPreference
										+ "";
								editedTextView.setText(parsedStringPrefrence);
							} else if (parsedIntPreference < minPref) {
								parsedIntPreference = minPref;
								String parsedStringPrefrence = parsedIntPreference
										+ "";
								editedTextView.setText(parsedStringPrefrence);
							}
							int adjustedparsedIntPreference = myPreference2Convertor
									.NormalToAdjusted(parsedIntPreference);
							preference2SeekBar
									.setProgress(adjustedparsedIntPreference);

							preference2Value = parsedIntPreference;

							handled = true;
						}
						return handled;
					}
				});

		preference3EditText
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView editedTextView,
							int actionId, KeyEvent keyEvent) {
						boolean handled = false;
						if (actionId == EditorInfo.IME_ACTION_SEND) {
							String submittedPreference = editedTextView
									.getText().toString();
							int parsedIntPreference = Integer
									.parseInt(submittedPreference);
							// Correct too high and too low values
							int maxPref = res
									.getInteger(R.integer.preference3_max);
							int minPref = res
									.getInteger(R.integer.preference3_min);
							if (parsedIntPreference > maxPref) {
								parsedIntPreference = maxPref;
								String parsedStringPrefrence = parsedIntPreference
										+ "";
								editedTextView.setText(parsedStringPrefrence);
							} else if (parsedIntPreference < minPref) {
								parsedIntPreference = minPref;
								String parsedStringPrefrence = parsedIntPreference
										+ "";
								editedTextView.setText(parsedStringPrefrence);
							}
							int adjustedparsedIntPreference = myPreference3Convertor
									.NormalToAdjusted(parsedIntPreference);
							preference3SeekBar
									.setProgress(adjustedparsedIntPreference);

							preference3Value = parsedIntPreference;

							handled = true;
						}
						return handled;
					}
				});

		// Add action Buttons
		builder.setPositiveButton(R.string.positive_button,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						myProgressBar.setIndeterminate(true);
						myProgressBar.setVisibility(0);

						// Saves the changed preference values and closes the
						// dialog
						SharedPreferences.Editor editor = sharedPref.edit();
						editor.putInt(getString(R.string.forecast_preference1),
								preference1Value);
						editor.putInt(getString(R.string.forecast_preference2),
								preference2Value);
						editor.putInt(getString(R.string.forecast_preference3),
								preference3Value);
						editor.commit();
						Log.d("TheWearDebug", "Saved the changed Preferences");

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
								// Recreate the Bitmaps and set them into the
								// imageViews
								Bitmap[] myBitmap = { null, null, null };
								for (int i = 0; i <= 2; i++) {
									WeatherEnumHandler weather_data;
									weather_data = new WeatherEnumHandler();
									weather_data.handleWeatherEnum(
											datasets.get(i), applicationContext);
									Log.d("TheWearDebug", "handled WeatherEnum");

									boolean[] advice = weather_data.weathertype.show_imgs;

									MergeImage myMergeImage = new MergeImage();
									myBitmap[i] = myMergeImage
											.MergeForecastImage(advice,
													applicationContext);
									myImageViews[i].setImageBitmap(myBitmap[i]);
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
		super.onStart(); // super.onStart() is where dialog.show() is actually
							// called on the underlying dialog, so we have to do
							// it after this point
		AlertDialog d = (AlertDialog) getDialog();
		if (d != null) {
			Log.d("TheWearDebug", "Custom onClickDialog set");
			Button positiveButton = (Button) d.getButton(Dialog.BUTTON_NEUTRAL);
			positiveButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// Set preference values to default
					preference1Value = defaultPreference1Value;
					preference2Value = defaultPreference2Value;
					preference3Value = defaultPreference3Value;
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
	}
}