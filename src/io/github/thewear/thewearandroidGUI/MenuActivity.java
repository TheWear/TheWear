package io.github.thewear.thewearandroidGUI;

import java.util.NoSuchElementException;

import io.github.thewear.thewearandroid.R;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MenuActivity extends ListActivity {

	/**
	 * MenuFragment contains a ListView to display the options for the menu.
	 */

	/**
	 * onCreateDialog() sets the content of the ListView.
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ListView optionsListView = getListView();
		String[] optionsList = getResources().getStringArray(
				R.array.optionsMenuList);
		ArrayAdapter<String> optionsAdapter = new ArrayAdapter<String>(this,
				R.layout.list_view_row, R.id.listText, optionsList);
		optionsListView.setAdapter(optionsAdapter);
	}

	/**
	 * onListItemClick(ListView listView, View view, int position, long id) is
	 * the method that is called when a list item is clicked. The corresponding
	 * intent is started when an option is clicked.
	 */

	@Override
	protected void onListItemClick(ListView listView, View view, int position,
			long id) {
		switch (position) {
		case 0: // Option 1
			Intent intent0 = new Intent(getBaseContext(),
					ForecastPreferenceActivity.class);
			startActivity(intent0);
			break;
		case 1: // Option 2
			// TODO add locations history thingy
			break;
		case 2: // Option 3
			Intent intent1 = new Intent(getBaseContext(),
					TheWearPreferenceActivity.class);
			startActivity(intent1);
			break;
		case 3: // Option 4
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			String aboutText = getString(R.string.about_text) + "\nVersion: "
					+ getString(R.string.testVersion);
			builder.setMessage(aboutText).setTitle(R.string.action_about);
			builder.setPositiveButton(R.string.close,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							// User clicked OK button
						}
					});
			// Get the AlertDialog from create()
			AlertDialog dialog = builder.create();
			dialog.show();
			break;
		default: // ERROR
			throw new NoSuchElementException(
					"MenuFragment: error - This position is incorrect.");
		}
	}
}