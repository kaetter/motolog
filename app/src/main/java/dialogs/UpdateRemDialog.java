package dialogs;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import beans.MaintenanceItem;
import beans.ReminderItem;

import com.kaetter.motorcyclemaintenancelog.MyListFragment;
import com.kaetter.motorcyclemaintenancelog.NewLog;
import com.kaetter.motorcyclemaintenancelog.NewRem;
import com.kaetter.motorcyclemaintenancelog.R;

import dbcontrollers.RemLogSource;

public class UpdateRemDialog extends DialogFragment {
	ReminderItem remItem;
	private int START_NEW_LOG = 0;

	public UpdateRemDialog() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		remItem = (ReminderItem) getArguments().getSerializable("ReminderItem");

		// getDialog().setTitle("Update entry for " + remItem.getVehicle());

		getDialog().getWindow().requestFeature(STYLE_NO_TITLE);

		View view = inflater.inflate(R.layout.dialogupdaterem, container);

		TextView remElemStringView = (TextView) view
				.findViewById(R.id.remElemString);

		remElemStringView.setText(getArguments().getString("Description"));

		TextView remElemDetailsView = (TextView) view
				.findViewById(R.id.remElemDetails);

		remElemDetailsView.setText(remItem.getDetails());

		Button updateEntry = (Button) view.findViewById(R.id.updateButton);
		Button deleteEntry = (Button) view.findViewById(R.id.deleteButton);
		Button cancelEntry = (Button) view.findViewById(R.id.cancelButton);
		Button doneEntry = (Button) view.findViewById(R.id.doneButton);

		deleteEntry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new RemLogSource(getActivity()).deleteEntry(remItem);

				MyListFragment activity2 = (MyListFragment) getActivity()
						.getSupportFragmentManager().findFragmentById(
								R.id.tab_2);

				activity2.onItemChanged();

				Toast.makeText(getActivity(), "Entry was deleted",
						Toast.LENGTH_SHORT).show();
				dismiss();

			}
		});

		updateEntry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getActivity(), NewRem.class);
				intent.putExtra("ReminderItem", remItem);
				intent.putExtra("isModification", true);
				dismiss();
				startActivity(intent);
			}
		});

		cancelEntry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.i("UpdateDialog", "dismiss dialogue");
				dismiss();
			}
		});

		doneEntry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final Calendar cal = Calendar.getInstance();

				String today = new StringBuilder()
						.append(cal.get(Calendar.YEAR)).append("-")
						.append(cal.get(Calendar.MONTH) + 1).append("-")
						.append(cal.get(Calendar.DAY_OF_MONTH)).toString();

				MaintenanceItem item = new MaintenanceItem(
						remItem.getVehicle(), remItem.getMaintElem(), remItem
								.getMaintType(), 0, 0, today, 0, remItem
								.getDetails(), MyListFragment.mileageType);

				Intent intent = new Intent(getActivity(), NewLog.class);
				intent.putExtra("Maintenanceitem", item);
				intent.putExtra("isModification", false);
				intent.putExtra("ReminderItem", remItem);

				startActivityForResult(intent, START_NEW_LOG);

			}
		});

		return view;
	}

	@Override
	public void dismiss() {

		super.dismiss();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == START_NEW_LOG && resultCode == Activity.RESULT_OK) {
			MyListFragment activity2 = (MyListFragment) getActivity()
					.getSupportFragmentManager().findFragmentById(R.id.tab_2);

			activity2.onItemChanged();

		}

		dismiss();
	}

}
