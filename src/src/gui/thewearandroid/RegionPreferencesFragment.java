package src.gui.thewearandroid;

import java.util.ArrayList;
import clientAPP.ForecastInfo;
import clientAPP.MergeImage;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

public class RegionPreferencesFragment extends DialogFragment {

	/**
	 * The Java code linked with region_preference_dialog.xml layout. These two
	 * files together display a dialog in which the user can chose their own
	 * preferred region for the forecast.
	 * 
	 * The preferred region is stored in SharedPreferences.
	 */

	private String defaultRegion;
	private String regionPreference;
	private SharedPreferences sharedPref;
	private View dialogView;
	private ImageView[] myImageViews = { null, null, null };
	ForecastInfo myForecastInfo;
	private Context applicationContext;
	private ProgressBar myProgressBar;
	private Dialog myDialog;
	private String[] ccTLDCodes;
	private Spinner spinner;

	/**
	 * onCreateDialog executes all the code we want to have executed when the
	 * dialog is created:
	 * 
	 * * Set the dialog layout and content
	 * 
	 * Set the region displayed by the spinner as the user preference-region or,
	 * if there isn't a user preference, set the default region
	 * 
	 * * Set an OnItemSelectedListener for the Spinner to register if the user
	 * chooses an other region
	 * 
	 * * Set a positive button (OK button) for saving of the region preference,
	 * to close the window and to change the forecast images according to the
	 * new preferences values. A progressBar is shown to notify the user that
	 * the images are changing.
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
		dialogView = inflater.inflate(R.layout.region_preference_dialog, null);
		builder.setView(dialogView);
		Log.d("TheWearDebug", "Region preference Dialog Created");

		// Get default Preference Values
		defaultRegion = getString(R.string.default_region);

		// Read the preference values from Shared Preferences
		sharedPref = getActivity().getSharedPreferences(
				getString(R.string.TheWear_preference_key),
				Context.MODE_PRIVATE);
		regionPreference = sharedPref.getString(
				getString(R.string.region_preference), defaultRegion);

		// Set Spinner
		spinner = (Spinner) dialogView.findViewById(R.id.spinner1);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				applicationContext, R.array.ccTLDCountries,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		// set the preferenced item in the spinner
		Resources res = getResources();
		ccTLDCodes = res.getStringArray(R.array.ccTLDCodes);
		int preferencePosition = -1;
		for (int i = 0; i < ccTLDCodes.length; i++) {
			int equalLocationPreference = ccTLDCodes[i].trim().compareTo(
					regionPreference.trim());
			if (equalLocationPreference == 0) {
				preferencePosition = i;
				break;
			}
		}
		if (preferencePosition == -1) {
			Log.e("TheWearDebug", "No PreferencePosition");
		} else {
			spinner.setSelection(preferencePosition);
		}

		// Set listener for the Spinner
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				regionPreference = ccTLDCodes[parent.getSelectedItemPosition()];
				Log.d("TheWearDebug", "regionPreference clicked: "
						+ regionPreference);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// Nothing Happens
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
						editor.putString(getString(R.string.region_preference),
								regionPreference);
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
	 * When Clicked the neutral button sets the region preference back to the
	 * default region.
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
					regionPreference = defaultRegion;
					// set the preferenced item in the spinner
					Resources res = getResources();
					ccTLDCodes = res.getStringArray(R.array.ccTLDCodes);
					int preferencePosition = -1;
					for (int i = 0; i < ccTLDCodes.length; i++) {
						int equalLocationPreference = ccTLDCodes[i].trim()
								.compareTo(regionPreference.trim());
						if (equalLocationPreference == 0) {
							preferencePosition = i;
							break;
						}
					}
					if (preferencePosition == -1) {
						Log.e("TheWearDebug", "No PreferencePosition");
					} else {
						spinner.setSelection(preferencePosition);
					}

					Log.d("TheWearDebug", "Region preference set to default");
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