package dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kaetter.motorcyclemaintenancelog.NewLog;
import com.kaetter.motorcyclemaintenancelog.NewRem;
import com.kaetter.motorcyclemaintenancelog.R;

public class NewElementDialog extends DialogFragment {
	String type;
	String callingActivity;

	public NewElementDialog() {

	}

	public interface OnNewElementListener {
		void addNewSharedPreference(String sharedPreference, String value);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		getDialog().getWindow().requestFeature(STYLE_NO_TITLE);

		type = getArguments().getString("newelementdialog");
		callingActivity = getArguments().getString("callingActivity");

		View view = inflater.inflate(R.layout.dialognewelement, null);

		TextView title = (TextView) view.findViewById(R.id.title);

		if (type.equals(NewLog.ELEMVAL)) {

			title.setText("Add new element!");
		}
		if (type.equals(NewLog.ELEMTYPEVAL)) {

			title.setText("Add new element type!");
		}

		final EditText et = (EditText) view.findViewById(R.id.newelem);
		
		Button ok = (Button) view.findViewById(R.id.ok);
	

		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				
				if (String.valueOf(et.getText()).trim().equals("")) {
					Toast.makeText(getActivity(), "A text must be entered! ",  Toast.LENGTH_SHORT).show();
					return;
				}

				if (type.equals(NewLog.ELEMVAL)) {

					String etString = et.getText().toString();

					if (callingActivity.equals(NewLog.tag)) {

						NewLog activity = (NewLog) getActivity();
						if (activity!=null) {
								activity.addNewSharedPreference(NewLog.ELEMVAL,
										String.valueOf(et.getText()));
								dismiss();

						}
					}

					if (callingActivity.equals(NewRem.tag)) {

						NewRem activity = (NewRem) getActivity();
						if (activity!=null) {

								activity.addNewSharedPreference(NewLog.ELEMVAL,
										String.valueOf(et.getText()));
								dismiss();
							
						}

					}

				}

				if (type.equals(NewLog.ELEMTYPEVAL)) {

					String etString = et.getText().toString();

					if (callingActivity.equals(NewRem.tag)) {

						NewRem activity = (NewRem) getActivity();

						if (activity!=null) {
							activity.addNewSharedPreference(NewLog.ELEMTYPEVAL,
									String.valueOf(et.getText()));
							dismiss();
						}
					}

					if (callingActivity.equals(NewLog.tag)) {

						NewLog activity = (NewLog) getActivity();

						if (activity!=null) {
	
								activity.addNewSharedPreference(NewLog.ELEMTYPEVAL,
										String.valueOf(et.getText()));
	
								dismiss();
								} 
						}
					}
			}
			
		});

		Button nok = (Button) view.findViewById(R.id.nok);

		nok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				getDialog().dismiss();
			}
		});

		return view;
	}

	@Override
	public void dismiss() {

		super.dismiss();
	}

}
