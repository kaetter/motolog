package dialogs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import beans.ReminderItem;

import com.kaetter.motorcyclemaintenancelog.MyListFragment;
import com.kaetter.motorcyclemaintenancelog.R;

import dbcontrollers.RemLogSource;

public class NewRemDialog extends DialogFragment {
	ReminderItem remItem;
	String remDescription;
	Activity activity;
	public NewRemDialog() {

	}

	@Override
	public void onAttach(Activity activity) {
		 super.onAttach(activity);
	}

	public interface onRemElementListener {
		public void onRemElementModify();

	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		getDialog().getWindow().requestFeature(STYLE_NO_TITLE);

		remDescription = getArguments().getString("remDescription");
		remItem = (ReminderItem) getArguments().getSerializable("reminderItem");

		final boolean isModification = getArguments().getBoolean(
				"isModification");

		View view = inflater.inflate(R.layout.dialognewrem, container);

		setNextInterval(remItem);

		TextView nextIntervalView = (TextView) view
				.findViewById(R.id.nextInterval);

		nextIntervalView.setText(remItem.getNextInterval());

		if (remItem.getReminderType() == 0) {
			if (MyListFragment.mileageType == 0||MyListFragment.mileageType == 2)
				nextIntervalView.append(" km's");
			else
				nextIntervalView.append(" miles");
		}

		TextView reminderDescription = (TextView) view
				.findViewById(R.id.reminderdescription);

		reminderDescription.setText(remDescription);

		TextView descriptionView = (TextView) view
				.findViewById(R.id.description);

		descriptionView.setText(remItem.getDetails());

		Button updateEntry = (Button) view.findViewById(R.id.updateButton);

		if (isModification) {
			updateEntry.setText("Update Entry");

		}

		Button cancelEntry = (Button) view.findViewById(R.id.cancelButton);

		updateEntry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				RemLogSource rml = new RemLogSource(getActivity());
				if (isModification) {

					rml.updateEntry(remItem);

				} else {
					rml.addMaintenanceItem(remItem);
				}

				dismiss();

				getActivity().finish();

			}
		});

		cancelEntry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.i("UpdateDialog", "dismiss dialogue");
				dismiss();
			}
		});

		return view;
	}

	public ReminderItem setNextInterval(ReminderItem remItem) {

		if (remItem.getReminderType() == 0) { // mileage

			int nextInterval = Integer.parseInt(remItem.getLastInterval())
					+ Integer.parseInt(remItem.getInterval());

			remItem.setNextInterval(String.valueOf(nextInterval));

		} else { // date

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			String lastIntervalDateString = String.valueOf(remItem
					.getLastInterval());

			Calendar c = Calendar.getInstance();

			try {
				c.setTime(sdf.parse(lastIntervalDateString));
			} catch (ParseException e) {

				e.printStackTrace();
			}
			
			String[] intervalSizeArray = getArguments().getStringArray("intervalSizeArray");
			
			String[] intervalSizeArrayP = getArguments().getStringArray("intervalSizeArrayP");

			if (remItem.getIntervalSize().equals(intervalSizeArray[0])
					|| remItem.getIntervalSize().equals(intervalSizeArrayP[0])) {
				// weeks

				c.add(Calendar.WEEK_OF_YEAR,
						Integer.parseInt(remItem.getInterval()));

			}

			if (remItem.getIntervalSize().equals(intervalSizeArray[1])
					|| remItem.getIntervalSize().equals(intervalSizeArrayP[1])) {
				// months

				c.add(Calendar.MONTH, Integer.parseInt(remItem.getInterval()));

			}

			if (remItem.getIntervalSize().equals(intervalSizeArray[2])
					|| remItem.getIntervalSize().equals(intervalSizeArrayP[2])) {
				// years

				c.add(Calendar.YEAR, Integer.parseInt(remItem.getInterval()));

			}

			Date d = c.getTime();
			remItem.setNextInterval(sdf.format(d));

		}

		return remItem;

	}

	@Override
	public void dismiss() {

		super.dismiss();
	}

}
