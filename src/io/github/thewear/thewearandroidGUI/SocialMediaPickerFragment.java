package io.github.thewear.thewearandroidGUI;

import src.gui.thewearandroid.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

public class SocialMediaPickerFragment extends DialogFragment {

	/**
	 * SocialMediaPickerFragment is the Java code linked with the layout in
	 * social_media_dialog.xml.
	 * 
	 * SocialMediaPiverFragment shows buttons to the different social media the
	 * user can choose to share their forecast.
	 */

	/**
	 * onCreateDialog() contains all the code that has to be executed when the
	 * Dialog is created: it sets social_media_dialog.xlm as layout; and sets
	 * the negative button of the dialog to cancel sharing.
	 */

	public Dialog onCreateDialog(Bundle SavedInstance) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// get the LayoutInflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because the dialog is going in the
		// dialog layout
		View dialogView = inflater.inflate(R.layout.social_media_dialog, null);
		builder.setView(dialogView);
		Log.d("TheWearDebug", "Share Dialog Created");

		// Set dialog Title
		builder.setTitle(R.string.social_media_dialog_Title);

		// set Cancel button
		builder.setNegativeButton(R.string.negative_button,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Cancels changes to the Preferences and
						// closes the dialog
					}
				});

		return builder.create();
	}
}