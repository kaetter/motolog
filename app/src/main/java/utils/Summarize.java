package utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.kaetter.motorcyclemaintenancelog.MyListFragment;
import com.kaetter.motorcyclemaintenancelog.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Summarize extends AsyncTask<Object, String, Bundle> {
	View confview;
	MyListFragment activity;
	private int firstOdo,lastOdo,days,entries;
	private double cash, cashperelem;
	private double cashperday, kmperday;
	private TextView totalcashView,totalkmView,cashperkmView,cashperdayView,entriesView,cashperelementvalue,countperelementvalue,countperelementLabel;
	private Spinner maintElemSpinner;
	private String dateMinTxt;
	private Date bikedate ,cursordateMin,cursordateMax,selectedMinDate,dateMin, dateMax;
	int daysBetween;
	SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
	
	@Override
	protected Bundle doInBackground(Object... params) {
		
		Bundle b = new Bundle();
		// inital odo and date should be entered! 
		
		Integer action = (Integer) params[0];
		Cursor cursor = (Cursor)  params[1];
		String from = (String) params[2];
		confview = (View) params[3];
		activity = (MyListFragment) params[4];
		//check for null of cursor before sending to async.	
		
		if(action==1) {
			cash=0;firstOdo=0;lastOdo=0;cashperday=0;kmperday=0;
			cursor.moveToFirst();
			lastOdo = cursor.getInt(7);
			try {
				selectedMinDate = sdf.parse(from);
				cursordateMax = sdf.parse(cursor.getString(6));
			} catch (ParseException e) {
				e.printStackTrace();
			}
				entries =cursor.getCount();
				
				Set<String> mType = new HashSet<String>();
				
				while (!cursor.isAfterLast()) {				
					
					mType.add(cursor.getString(2));		
					
					cash+=cursor.getDouble(10);
					if(cursor.getInt(7)>lastOdo) {
						lastOdo=cursor.getInt(7);
					} else {
						if(cursor.getInt(7)>firstOdo) {
							if(firstOdo==0)
							firstOdo=cursor.getInt(7);
						} else {
							firstOdo=cursor.getInt(7);
						}
					}
					if(cursor.isLast()) {
						try {
							cursordateMin=sdf.parse(cursor.getString(6));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					cursor.moveToNext();			
				}

				SharedPreferences generalPref = activity.getActivity().getSharedPreferences(
						activity.getActivity().getString(R.string.general_preference_file_key),
						Context.MODE_PRIVATE);
				
				dateMinTxt = generalPref.getString("dateofpurchaset", "");
				try {
				if(!dateMinTxt.equals("")) {
						bikedate=sdf.parse(dateMinTxt);
				} else {
						bikedate=sdf.parse("1799-01-01");
				}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
				Button  toView = (Button) confview.findViewById(R.id.to);
				 
					try {
						daysBetween = daysBetween( cursordateMin, sdf.parse(toView.getText().toString()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
					b.putDouble("totalcashView", Math.round((cash * 100.0) / 100.0));
					b.putInt("totalkm", lastOdo - firstOdo);
					if(daysBetween==0) {
						b.putDouble("cashperday", Math.round((cash * 100.0) / 100.0));
					} else {
						b.putDouble("cashperday", Math.round((cash/daysBetween) * 100.0) / 100.0);
					}
					b.putDouble("cashperkm", Math.round((cash/(lastOdo - firstOdo)) * 100.0) / 100.0);
					b.putInt("action", action);
					mType.add("...");
					b.putStringArrayList("elements",  new ArrayList<String>(mType));
						
		}
		
		
		if (action==2) {
			cashperelem=0;
			 maintElemSpinner = (Spinner)  confview.findViewById(R.id.maintelemspinner);
			String elem = maintElemSpinner.getSelectedItem().toString();
			
			
			cursor.moveToFirst();
			int i=0;
			while(!cursor.isAfterLast()) {
				if(cursor.getString(2).equals(elem)) {
					cashperelem+=cursor.getDouble(10);
					i++;
				}
				
				cursor.moveToNext();
			}
			b.putInt("countperelement", i);
			b.putInt("action", action);
		}
		
		if (action==0) {
			b.putInt("action", action);
		}
		//calculus ; we have cash, days,odomin and max
		return b;
		
	}

	@Override
	protected void onPostExecute(Bundle b) {
		
		if (b.getInt("action")==1) {
		Button  fromView = (Button) confview.findViewById(R.id.from);
		fromView.setText(sdf.format(cursordateMin).toString());	
		totalcashView = (TextView) confview.findViewById(R.id.totalcash);
		totalcashView.setText(String.valueOf(b.getDouble("totalcashView")));
		totalkmView = (TextView) confview.findViewById(R.id.totalkm);
		totalkmView.setText(String.valueOf(b.getInt("totalkm")));
		cashperkmView= (TextView) confview.findViewById(R.id.cashperkm);
		cashperkmView.setText(String.valueOf(b.getDouble("cashperkm")));
		cashperdayView = (TextView) confview.findViewById(R.id.cashperday);
		cashperdayView.setText(String.valueOf(b.getDouble("cashperday")));
		
		 maintElemSpinner = (Spinner)  confview.findViewById(R.id.maintelemspinner);
		 
		 ArrayAdapter<String> maintElemAdapter = (ArrayAdapter<String>) maintElemSpinner.getAdapter();
		 maintElemSpinner.setSelection(0);
		 maintElemAdapter.clear();
		 
		 List<String> maintElemArrayList=b.getStringArrayList("elements");
		 java.util.Collections.sort(maintElemArrayList);
		 for(String s:maintElemArrayList) {
			 maintElemAdapter.add(s);
		 }
	 
		 maintElemAdapter.notifyDataSetChanged();
		 
		 cashperelementvalue = (TextView)  confview.findViewById(R.id.cashperelementvalue);
		 cashperelementvalue.setText("0");
		 countperelementvalue = (TextView)  confview.findViewById(R.id.countperelementvalue);
			countperelementvalue.setText("0");
		 entriesView = (TextView) confview.findViewById(R.id.entries);
		 entriesView.setText(String.valueOf(entries));
		
		} 

		if (b.getInt("action")==0 &&totalcashView!=null) {
			totalcashView.setText("0.00");
			totalkmView.setText("0");
			cashperkmView.setText("0.00");
			cashperdayView.setText("0.00");
			cashperelementvalue.setText("0.00");
			countperelementvalue.setText("0");
		} else {
			if (b.getInt("action")==0) {
				totalcashView = (TextView) confview.findViewById(R.id.totalcash);
				totalcashView.setText("0.00");
				totalkmView = (TextView) confview.findViewById(R.id.totalkm);
				totalkmView.setText("0.00");
				cashperkmView= (TextView) confview.findViewById(R.id.cashperkm);
				cashperkmView.setText("0.00");
				cashperdayView = (TextView) confview.findViewById(R.id.cashperday);
				cashperdayView.setText("0.00");
				cashperelementvalue = (TextView)  confview.findViewById(R.id.cashperelementvalue);
				cashperelementvalue.setText("0.00");
				countperelementvalue = (TextView)  confview.findViewById(R.id.countperelementvalue);
				countperelementvalue.setText("0");
			}
		}
		
		if(b.getInt("action")==2) {
			cashperelementvalue = (TextView)  confview.findViewById(R.id.cashperelementvalue);
			cashperelementvalue.setText(String.valueOf(Math.round((cashperelem *100.0 ) /100.0)));
			countperelementvalue = (TextView)  confview.findViewById(R.id.countperelementvalue);
			countperelementvalue.setText(String.valueOf(b.getInt("countperelement")));
			
			countperelementLabel = (TextView)  confview.findViewById(R.id.countperelementLabel);
			if(b.getInt("countperelement")==1) {
				countperelementLabel.setText("entry");
			}  else {
				countperelementLabel.setText("entries");
			}
		}
	}
	
	public static int daysBetween(Date startDate, Date endDate) {
		  Calendar sDate = getDatePart(startDate);
		  Calendar eDate = getDatePart(endDate);

		  int daysBetween = 0;
		  while (sDate.before(eDate)) {
		      sDate.add(Calendar.DAY_OF_MONTH, 1);
		      daysBetween++;
		  }
		  return daysBetween;
		}
	
	public static Calendar getDatePart(Date date){
	    Calendar cal = Calendar.getInstance();       // get calendar instance
	    cal.setTime(date);      
	    cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
	    cal.set(Calendar.MINUTE, 0);                 // set minute in hour
	    cal.set(Calendar.SECOND, 0);                 // set second in minute
	    cal.set(Calendar.MILLISECOND, 0);            // set millisecond in second

	    return cal;                                  // return the date part
	}
	

}
