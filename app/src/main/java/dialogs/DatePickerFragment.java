package dialogs;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	
	public interface EditDateDialogListener {
	    void onFinishEditDialog(String b,String inputText);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {	
		String b= getArguments().getString("button");		
		EditDateDialogListener activity = (EditDateDialogListener) getParentFragment();
		String days;
		if (day<10) {
			days = "0"+day;
			
		} else {
			days = ""+ day;
		}
		
		
		if (month<9) {
		activity.onFinishEditDialog(b,new String(year+ "-0"+ (month +1) + "-"+ days));
		} else {
			activity.onFinishEditDialog(b,new String(year+ "-"+ (month +1) + "-"+ days));
		}
	}
}