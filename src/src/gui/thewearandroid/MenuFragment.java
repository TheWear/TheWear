package src.gui.thewearandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MenuFragment extends DialogFragment {

	/**
	 * MenuFragment contains a ListView to display the options for the menu.
	 */

	private Context applicationContext;
	private ListView optionsListView;
	MainActivity mainActivity;

	/**
	 * onCreateDialog() sets the layout for the ListView, the content of the
	 * ListView and the onClickListener for the items in the ListView. When an
	 * option is clicked, a corresponding method is called in MainActivity.
	 */

	@Override
	public Dialog onCreateDialog(Bundle SavedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// get the LayoutInflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because the dialog is going in the
		// dialog layout
		View dialogView = inflater.inflate(R.layout.options_menu, null);
		builder.setView(dialogView);
		Log.d("TheWearDebug", "Region preference Dialog Created");

		// Set dialog Title
		builder.setTitle(R.string.options);

		optionsListView = (ListView) dialogView
				.findViewById(R.id.optionsListView);
		String[] optionsList = applicationContext.getResources()
				.getStringArray(R.array.optionsMenuList);
		ArrayAdapter<String> optionsAdapter = new ArrayAdapter<String>(
				applicationContext, R.layout.list_view_row, R.id.listText,
				optionsList);
		optionsListView.setAdapter(optionsAdapter);

		optionsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0: // Option 1
					mainActivity.showMenuItem1();
					break;
				case 1: // Option 2
					mainActivity.showMenuItem2();
					break;
				case 2: // Option 3
					mainActivity.showMenuItem3();
					break;
				case 3: // Option 4
					mainActivity.showMenuItem4();
				default: // ERROR
					Log.e("TheWearDebug", "This position is incorrect.");
				}
			}
		});

		return builder.create();
	}

	/**
	 * passNecessaryInformation() is used to set the applicationContext.
	 */

	public void passNecessaryInformation(Context applicationContext,
			MainActivity mainActivity) {
		this.applicationContext = applicationContext;
		this.mainActivity = mainActivity;
	}
}