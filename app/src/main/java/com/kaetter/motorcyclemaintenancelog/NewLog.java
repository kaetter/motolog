package com.kaetter.motorcyclemaintenancelog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import beans.MaintenanceItem;
import beans.ReminderItem;
import dbcontrollers.MainHelper;
import dbcontrollers.MainLogSource;
import dbcontrollers.RemLogSource;
import dialogs.NewElementDialog;
import dialogs.NewElementDialog.OnNewElementListener;
import dialogs.NewRemDialog;
import dialogs.NewRemDialog.onRemElementListener;
import listeners.OnItemChangedListener;
import utils.FuellyScraper;
import utils.Helper;

public class NewLog extends FragmentActivity implements OnNewElementListener,
		OnItemChangedListener,onRemElementListener {
	public static String tag = "newLog";
	private boolean notonloadmaintelemspinner=false, notonloadmainttypeelemspinner=false;
	private DatePicker datepick;
	private String dialogElem;
	private int day, month, year;
	private final int NEWELEMDIALOG = 10,DELETEELEM=1;
	private Spinner maintElemSpinner;
	private Spinner maintTypeSpinner, dateIntervalNumberView,
			dateIntervalTypeView;
	private ArrayAdapter<String> maintElemAdapter, maintTypeAdapter,
			intervalDateAdapter, intervalNumberAdapter, intervalDateAdapterP;
	private String[] maintElemList, maintTypeList;
	private MaintenanceItem mItem;
	private TextView fuelView, forDateView, kmLabelView, perLitreView;
	private EditText fuelTextView, odoEditText, memEditText, odoInterval,
			cashEditText;
	private View blankView;
	private CheckBox checkBox2View, checkBox1View;
	private MainLogSource mainlogSource;
	private NewElementDialog elemDialog;
	private ReminderItem remItem, remItemSent;
	private ArrayList<String> intervalNumberArrayList, maintElemArrayList,
			maintTypeArrayList;

	private boolean isModification;

	private SharedPreferences intervalPref, elemTypePref, elemPref,
			sharedPrefs;

	public static final int INTERVALCOUNT = 50, ELEMCOUNT = 50,
			ELEMTYPECOUNT = 50;

	private int intervalCount, elemCount, elemTypeCount;

	public static final String INTERVALCOUNTSTRING = "intervalCount";
	public static final String ELEMCOUNTSTRING = "elemCountString";
	public static final String ELEMTYPECOUNTSTRING = "elemTypeCount";

	public static final String INTERVALVAL = "intervalVal_";
	public static final String ELEMVAL = "elemVal_";
	public static final String ELEMTYPEVAL = "elemtypeVal_";

	Editor intervalEditor, elemTypeEditor, elemEditor;

	String intervalValues[], elemValues[], elemTypeValues[];
	double cashPerLitre;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		setContentView(R.layout.activity_new_log);

		mItem = (MaintenanceItem) getIntent().getSerializableExtra(
				"Maintenanceitem");

		isModification = (boolean) getIntent().getBooleanExtra(
				"isModification", false);

		remItemSent = (ReminderItem) getIntent().getSerializableExtra(
				"ReminderItem");

		setPreferences();
		setMaintElemSpinner(-1);

		setMaintTypeSpinner(-1);
		setCurrentDateOnView();
		setCheckboxModification();

		Toast.makeText(getApplicationContext(), "Press menu for more options!",
				Toast.LENGTH_LONG).show();

		if (mItem != null) {

			setValues();

		}

	}

	public void setPreferences() {
		
		
		
		

		intervalPref = getSharedPreferences(
				getString(R.string.interval_preference_file_key),
				getApplicationContext().MODE_PRIVATE);
		elemTypePref = getSharedPreferences(
				getString(R.string.elemtype_preference_file_key),
				getApplicationContext().MODE_PRIVATE);
		elemPref = getSharedPreferences(
				getString(R.string.elem_preference_file_key),
				getApplicationContext().MODE_PRIVATE);

		intervalEditor = intervalPref.edit();
		elemTypeEditor = elemTypePref.edit();
		elemEditor = elemPref.edit();

		intervalCount = intervalPref.getInt(INTERVALCOUNTSTRING, 0);

		// set intervals defined by user from preferences
		// these are used to populate spinners

		String[] intervalNumberList = getResources().getStringArray(
				R.array.intervalNumberArray);

		intervalNumberArrayList = new ArrayList<String>(
				Arrays.asList(intervalNumberList));

		if (intervalCount == 0) { // populate

			intervalCount = intervalNumberList.length - 1;

			intervalEditor.putInt(INTERVALCOUNTSTRING, intervalCount);
			intervalEditor.commit();

			for (int i = 0; i <= intervalCount; i++) {
				intervalEditor
						.putString(INTERVALVAL + i, intervalNumberList[i]);
				intervalEditor.commit();
			}
		}

		if (intervalCount > intervalNumberList.length - 1) {

			for (int i = intervalNumberList.length; i <= intervalCount; i++) {

				intervalNumberArrayList.add(intervalPref.getString(INTERVALVAL
						+ i, " "));
			}
		}
		
		
		
		// Set maintElemList from preferences
		// populate maintSpiner
		String[] maintElemList = getResources().getStringArray(
				R.array.maintElemArray);
		maintElemArrayList = new ArrayList<String>(Arrays.asList(maintElemList)); 
		elemCount = elemPref.getInt(ELEMCOUNTSTRING, 0);
		if (elemCount == 0) { // populate preferences
			elemCount = maintElemList.length;
			elemEditor.putInt(ELEMCOUNTSTRING, elemCount);
			elemEditor.commit();

			for (int i = 0; i < elemCount; i++) {
				elemEditor.putString(ELEMVAL + i, maintElemList[i]);
				elemEditor.commit();
			}

		}
		
		// populate from preferences array that fills elemTypespinner

		String[] maintTypeList = getResources().getStringArray(
				R.array.maintTypeArray);

		maintTypeArrayList = new ArrayList<String>(Arrays.asList(maintTypeList));
		elemTypeCount = elemTypePref.getInt(ELEMTYPECOUNTSTRING, 0);

		if (elemTypeCount == 0) {
			elemTypeCount = maintTypeList.length ;
			elemTypeEditor.putInt(ELEMTYPECOUNTSTRING, elemTypeCount);
			elemTypeEditor.commit();

			for (int i = 0; i < elemTypeCount; i++) {
				elemTypeEditor.putString(ELEMTYPEVAL + i, maintTypeList[i]);
				elemTypeEditor.commit();
			}
		}
		
		
		boolean onFirstRun= elemPref.getBoolean("firstrun", true);
		if(onFirstRun) {
			//let's recount elemcount and set it to the new value. 
			  int elemcount = 0;
			  Editor ed = elemPref.edit();
			  Map<String, ?> m =elemPref.getAll();
			  Iterator it = m.entrySet().iterator();
			  while(it.hasNext()) {
					Map.Entry<String, ?> me= (Entry<String, ?>) it.next();
					if (me.getKey().startsWith(NewLog.ELEMVAL.toString())) {
						elemcount++;
					}
				}
			  elemCount=elemcount;
			  ed.putInt(ELEMCOUNTSTRING,elemcount).commit();
				
			  int elemtypecount = 0;
			  Editor edType = elemTypePref.edit();
			  Map<String, ?> mt =elemTypePref.getAll();
				
			  Iterator itt = mt.entrySet().iterator();
			  while(itt.hasNext()) {
					Map.Entry<String, ?> mte= (Entry<String, ?>) itt.next();
					if (mte.getKey().startsWith(NewLog.ELEMTYPEVAL.toString())) {
						elemtypecount++;
					}
				}	
			  elemTypeCount=elemtypecount;
			 edType.putInt(ELEMTYPECOUNTSTRING,elemtypecount).commit();
			elemPref.edit().putBoolean("firstrun", false).commit();
		}



		if (elemCount > maintElemList.length ) { 
			for (int i = maintElemList.length; i < elemCount; i++) {
				maintElemArrayList.add(elemPref.getString(ELEMVAL + i, " "));
			}
		}

		if (elemTypeCount > maintTypeList.length ) {
			for (int i = maintTypeList.length; i < elemTypeCount; i++) {
				maintTypeArrayList.add(elemTypePref.getString(ELEMTYPEVAL + i,
						" "));
			}
		}

	}

 

	public void setElemType() {

		elemTypeCount = elemTypePref.getInt(ELEMTYPECOUNTSTRING, 0);

		maintTypeArrayList.clear();
		for (int i = 0; i < elemTypeCount; i++) {
			maintTypeArrayList
					.add(elemTypePref.getString(ELEMTYPEVAL + i, " "));
		}

	}

	public void setCheckboxModification() {

		checkBox1View = (CheckBox) findViewById(R.id.checkBox1);

		checkBox2View = (CheckBox) findViewById(R.id.checkBox2);
		forDateView = (TextView) findViewById(R.id.forDate);
		odoInterval = (EditText) findViewById(R.id.odoInterval);
		dateIntervalTypeView = (Spinner) findViewById(R.id.dateIntervalType);
		dateIntervalNumberView = (Spinner) findViewById(R.id.dateIntervalNumber);
		kmLabelView = (TextView) findViewById(R.id.kmlabel);
		kmLabelView.setVisibility(View.GONE);
		blankView = (View) findViewById(R.id.blankspace);
		blankView.setVisibility(View.GONE);
		dateIntervalTypeView.setVisibility(View.GONE);
		dateIntervalNumberView.setVisibility(View.GONE);
		checkBox2View.setVisibility(View.GONE);
		forDateView.setVisibility(View.GONE);
		odoInterval.setVisibility(View.GONE);

		checkBox1View.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {
					blankView.setVisibility(View.VISIBLE);
					checkBox2View.setVisibility(View.VISIBLE);
					forDateView.setVisibility(View.VISIBLE);
					odoInterval.setVisibility(View.VISIBLE);
					kmLabelView.setVisibility(View.VISIBLE);

				} else {
					blankView.setVisibility(View.GONE);
					checkBox2View.setVisibility(View.GONE);
					forDateView.setVisibility(View.GONE);
					odoInterval.setVisibility(View.GONE);
					kmLabelView.setVisibility(View.GONE);
					dateIntervalNumberView.setVisibility(View.GONE);
					dateIntervalTypeView.setVisibility(View.GONE);
					checkBox2View.setChecked(false);

				}

			}
		});

		checkBox2View.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {

					dateIntervalNumberView.setVisibility(View.VISIBLE);
					dateIntervalTypeView.setVisibility(View.VISIBLE);
					odoInterval.setVisibility(View.GONE);
					kmLabelView.setVisibility(View.GONE);

				} else {
					if (checkBox1View.isChecked()) {
						dateIntervalNumberView.setVisibility(View.GONE);
						dateIntervalTypeView.setVisibility(View.GONE);
						odoInterval.setVisibility(View.VISIBLE);
						kmLabelView.setVisibility(View.VISIBLE);
					}
				}

			}
		}

		);

		// String[] intervalNumberList = getResources().getStringArray(
		// R.array.intervalNumberArray);
		//
		// ArrayList<String> intervalNumberArrayList = new ArrayList<String>(
		// Arrays.asList(intervalNumberList));
		intervalNumberAdapter = new ArrayAdapter<String>(
				getApplicationContext(), R.layout.generalspinner,
				intervalNumberArrayList);
		intervalNumberAdapter
				.setDropDownViewResource(R.layout.generalspinnerdropdown);

		dateIntervalNumberView.setAdapter(intervalNumberAdapter);

		// load adapter for type list singular
		String[] intervalTypeList = getResources().getStringArray(
				R.array.intervalTypeArray);

		ArrayList<String> intervalTypeArrayList = new ArrayList<String>(
				Arrays.asList(intervalTypeList));
		intervalDateAdapter = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.generalspinner, intervalTypeArrayList);
		// load adapter for type list plural
		String[] intervalTypeListP = getResources().getStringArray(
				R.array.intervalTypeArrayP);

		ArrayList<String> intervalTypeArrayListP = new ArrayList<String>(
				Arrays.asList(intervalTypeListP));
		intervalDateAdapterP = new ArrayAdapter<String>(
				getApplicationContext(), R.layout.generalspinner,
				intervalTypeArrayListP);
		// set singular first then change in listener to plural if necessary
		intervalDateAdapterP
				.setDropDownViewResource(R.layout.generalspinnerdropdown);
		dateIntervalTypeView.setAdapter(intervalDateAdapter);

		dateIntervalNumberView
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int position, long id) {

						if (!dateIntervalNumberView.getItemAtPosition(position)
								.equals("1")) {
							int i = dateIntervalTypeView
									.getSelectedItemPosition();
							dateIntervalTypeView
									.setAdapter(intervalDateAdapterP);
							dateIntervalTypeView.setSelection(i);

						} else {
							int i = dateIntervalTypeView
									.getSelectedItemPosition();
							dateIntervalTypeView
									.setAdapter(intervalDateAdapter);
							dateIntervalTypeView.setSelection(i);
						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

	}

	public void setMaintElemSpinner(int pos) {

		maintElemSpinner = (Spinner) findViewById(R.id.maintelemspinner);

		maintElemAdapter = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.generalspinner, maintElemArrayList);
		maintElemAdapter
				.setDropDownViewResource(R.layout.generalspinnerdropdown);
		maintElemSpinner.setAdapter(maintElemAdapter);

		maintElemSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int position, long id) {
						
						String select = maintElemSpinner.getItemAtPosition(position).toString() ;
						if(select.equals("Other") && notonloadmaintelemspinner) {
							
							elemDialog = new NewElementDialog();
							Bundle b = new Bundle();
							FragmentManager fm = getSupportFragmentManager();
							b.putString("newelementdialog", ELEMVAL);
							b.putString("callingActivity", tag);
							elemDialog.setArguments(b);
							elemDialog.show(fm, tag);
						} else {
							notonloadmaintelemspinner=true;
							if (mItem == null) {
								setMaintTypeSpinner(-1);
							}
	
							if (position != 0) {
								fuelView.setVisibility(View.GONE);
								fuelTextView.setVisibility(View.GONE);
								fuelTextView.setText("0");
								perLitreView.setVisibility(View.GONE);
							} else {
								fuelView.setVisibility(View.VISIBLE);
								fuelTextView.setVisibility(View.VISIBLE);
								perLitreView.setVisibility(View.VISIBLE);
	
							}
						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}

				});
		

		if (pos >-1 ) {
			maintElemSpinner.setSelection(pos-1);
		}
		

	}

	public void setMaintTypeSpinner(int pos) {
		fuelView = (TextView) findViewById(R.id.fuel);
		fuelTextView = (EditText) findViewById(R.id.fueltext);
		perLitreView = (TextView) findViewById(R.id.perLitre);

		if (sharedPrefs.getString("pref_CashType", "0").equals("0")) {
			if (sharedPrefs.getString("pref_MileageType", "0").equals("1")) {
				perLitreView.setText("/g");
			} else {
				perLitreView.setText("/l");
			}
		} else {
			perLitreView.setText("");
		}

		maintTypeSpinner = (Spinner) findViewById(R.id.mainttypespinner);

		String maintElemSelected = (String) maintElemSpinner.getSelectedItem();

		if (mItem == null) {

			if (maintElemSelected.equals("Fuel")) {

				maintTypeArrayList.clear();
				maintTypeArrayList.add("Refuel");
				maintTypeArrayList.add("Other");

				fuelView.setVisibility(View.VISIBLE);
				fuelTextView.setVisibility(View.VISIBLE);
				perLitreView.setVisibility(View.VISIBLE);
				// fuelTextView.setText("");

			} else {

				fuelView.setVisibility(View.GONE);
				fuelTextView.setVisibility(View.GONE);
				perLitreView.setVisibility(View.GONE);
			}

			if (maintElemSelected.equals("Oil")) {
				// maintTypeArrayList.remove("Maintain");
				// maintTypeArrayList.remove("Refuel");
				//
				maintTypeArrayList.clear();
				maintTypeArrayList.add("Replace");
				maintTypeArrayList.add("Other");

			} else {
				if (!maintElemSelected.equals("Fuel")) {

					setElemType();

					maintTypeArrayList.remove("Refuel");
				}

			}

		}

		maintTypeAdapter = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.generalspinner, maintTypeArrayList);
		maintTypeAdapter
				.setDropDownViewResource(R.layout.generalspinnerdropdown);
		maintTypeSpinner.setAdapter(maintTypeAdapter);

		maintTypeSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent, View view,
							int pos, long arg3) {
						String select = maintTypeSpinner.getItemAtPosition(pos).toString() ;
						if(select.equals("Other")&& !maintElemSpinner.getSelectedItem().toString().equals("Fuel") && !maintElemSpinner.getSelectedItem().toString().equals("Oil") &&notonloadmainttypeelemspinner) {
							
							elemDialog = new NewElementDialog();
							Bundle b = new Bundle();
								FragmentManager fm = getSupportFragmentManager();
								b.putString("newelementdialog", ELEMTYPEVAL);
								b.putString("callingActivity", tag);
								elemDialog.setArguments(b);
								elemDialog.show(fm, tag);
						} else {
							notonloadmainttypeelemspinner=true;
						} 
						
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});
		
		maintTypeSpinner.setOnLongClickListener(new AdapterView.OnLongClickListener() { 
	        public boolean onLongClick(View v) { 
	        throw new RuntimeException("You long clicked an item!");
		            
		        } 
		     }); 
		
		maintTypeSpinner.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				if (pos<4 ) {
				String value = (String) maintTypeSpinner.getItemAtPosition(pos);
					deleteSharedPreference(NewLog.ELEMTYPEVAL, value);
				}
				else {
					Toast.makeText(getApplicationContext(), "Can't delete main types! ", Toast.LENGTH_LONG).show();
					}
				
				return false;
			}
		
		
		});
		
		if (pos >-1 ) {
			maintTypeSpinner.setSelection(pos-2);
		}
	}

	public void	deleteSharedPreference(String type, String value) {
		if (type.equals(NewLog.ELEMVAL)) {

			SharedPreferences elemPref = getSharedPreferences(
					getString(R.string.elem_preference_file_key),
					Context.MODE_PRIVATE);

			Editor ed = elemPref.edit();

			int elemCount = elemPref.getInt(NewLog.ELEMCOUNTSTRING, 0);

			if (elemCount == 0) {

				Toast.makeText(getApplicationContext(),
						"somethign wrong with shared pref", Toast.LENGTH_LONG)
						.show();

			} else {
				
				
				String [] values = new String[elemCount-1];
				
				for (int i=0 ;i< elemCount;i++) {
					if (i<5 || (i>=5 && elemPref.getString(NewLog.ELEMVAL+elemCount,"")!=value))
						values[i]=elemPref.getString(NewLog.ELEMVAL+i,"");
				}
				ed.clear();
				elemCount--;
				
				for (int i=0 ;i< elemCount;i++) {
						ed.putString(NewLog.ELEMVAL+i, values[i]);
				}
				
				ed.putInt(NewLog.ELEMCOUNTSTRING, elemCount);
				ed.commit();
				
				setPreferences();
				setMaintElemSpinner(-1);
				setMaintTypeSpinner(-1);

			}
		}

		if (type.equals(NewLog.ELEMTYPEVAL)) {

			SharedPreferences elemTypePref = getSharedPreferences(
					getString(R.string.elemtype_preference_file_key),
					getApplicationContext().MODE_PRIVATE);

			Editor edType = elemTypePref.edit();

			int elemTypeCount = elemTypePref.getInt(NewLog.ELEMTYPECOUNTSTRING,
					0);
			if (elemTypeCount == 0) {

				Toast.makeText(
						getApplicationContext(),
						"somethign wrong with shared pref on "
								+ NewLog.ELEMTYPECOUNTSTRING, Toast.LENGTH_LONG)
						.show();

			} else {
				String [] values = new String[elemTypeCount-1];
				
				for (int i=0 ;i< elemTypeCount;i++) {
					if (i<5 || (i>=5 && elemPref.getString(NewLog.ELEMTYPEVAL+elemTypeCount,"")!=value))
						values[i]=elemPref.getString(NewLog.ELEMTYPEVAL+i,"");
				}
				edType.clear();
				elemTypeCount--;
				
				for (int i=0 ;i< elemTypeCount;i++) {
						edType.putString(NewLog.ELEMTYPEVAL+i, values[i]);
				}
				
				edType.putInt(NewLog.ELEMTYPECOUNTSTRING, elemTypeCount);
				edType.commit();
				
				setPreferences();
				setMaintElemSpinner(-1);
				setMaintTypeSpinner(-1);


			}
		}

		setPreferences();
		
		
	}
	
	public void setCurrentDateOnView() {

		datepick = (DatePicker) findViewById(R.id.datepick);

		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		// set current date into datepicker
		datepick.init(year, month, day, null);

	}

	public void cancelLog(View view) {

		setResult(Activity.RESULT_CANCELED);
		this.onBackPressed();

	}

	public void createLog(View view) {

		String vehicle = new String("default"); // vehicle

		String maintElem = (String) maintElemSpinner.getSelectedItem(); // maintelem
		String maintType = (String) maintTypeSpinner.getSelectedItem(); // mainttype
		double fuelAmount;

		String fuelAmountTxt = fuelTextView.getText().toString();

		if (maintElem.equals("Fuel") && !fuelAmountTxt.equals("")) { // fuel

			try {
				fuelAmount = Math
						.round(Double.parseDouble(fuelAmountTxt) * 100.0) / 100.0;
			} catch (NumberFormatException e) {
				Toast.makeText(getApplicationContext(), "Value "
						+ fuelAmountTxt + " not a number!", Toast.LENGTH_LONG);
				return;
			}

		} else {
			fuelAmount = 0;

		}

		String date = Helper.formatDate(datepick.getYear(), datepick.getMonth(), datepick.getDayOfMonth());

		odoEditText = (EditText) findViewById(R.id.odotext); //
		cashEditText = (EditText) findViewById(R.id.cash);
		cashEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if(cashEditText.getText().toString().equals("0.0")){
						cashEditText.setText("");
					} 
				}
				
			}
		});
		

		double cash;
		if (cashEditText.getText().toString().equals("")) {
			cash = 0;
		} else {
			try {
				DecimalFormat df = new DecimalFormat("####.00");
				cash = Double.parseDouble(cashEditText.getText().toString());
			} catch (NumberFormatException e) {

				Toast.makeText(getApplicationContext(),
						"Invalid number for price!", Toast.LENGTH_LONG).show();
				return;
			}
		}

		int cashType = Integer.parseInt(sharedPrefs.getString("pref_CashType",
				"0"));

		if (cashType == 0 && maintElem.equals("Fuel")) {
			cashPerLitre = cash;
			cash = cash * fuelAmount;
			cash = Math.round(cash * 100.0) / 100.0;
		} else {
			cashPerLitre = cash / fuelAmount;
			cashPerLitre = Math.round(cashPerLitre * 100.0) / 100.0;
		}

		memEditText = (EditText) findViewById(R.id.memotext); // details
		int odometer;

		if (!odoEditText.getText().toString().equals("")) {
			odometer = Integer.parseInt(odoEditText.getText().toString());
			if (odometer > 999999) {
				Toast.makeText(getApplicationContext(),
						"Odo can not be > 999999", Toast.LENGTH_LONG).show();
				return;

			}
		} else {
			odometer = 0;
		}

		String details = memEditText.getText().toString();
		mainlogSource = new MainLogSource(getApplicationContext());

		double consumption;

		if (fuelAmount == 0) {

			consumption = 0;

		} else {

			consumption = getConsumption(vehicle,
					(String) maintElemSpinner.getItemAtPosition(0), fuelAmount,
					odometer);

			consumption = Math.round(consumption * 100.0) / 100.0;

		}

		if (mItem != null && isModification) { // modification

			mItem = new MaintenanceItem(mItem.getKey(), vehicle, maintElem,
					maintType, fuelAmount, consumption, date, odometer,
					details, MyListFragment.mileageType, cash);
			mainlogSource.updateEntry(mItem);

			setResult(Activity.RESULT_OK);

			Toast.makeText(getApplicationContext(), "Entry was modified!",
					Toast.LENGTH_SHORT).show();

		} else { // creation

			mItem = new MaintenanceItem(vehicle, maintElem, maintType,
					fuelAmount, consumption, date, odometer, details,
					MyListFragment.mileageType, cash);

			mainlogSource.addMaintenanceItem(mItem);

			Toast.makeText(getApplicationContext(), "Entry was created!",
					Toast.LENGTH_SHORT).show();
			sendToFuelly();
			setResult(Activity.RESULT_OK);

		}

		// reminder item

		if (checkBox1View.isChecked()) { // create reminder

			Date dateInserted = new Date();

			if (!checkBox2View.isChecked()) {

				EditText odoIntervalView = (EditText) findViewById(R.id.odoInterval);

				String interval = odoIntervalView.getText().toString();

				remItem = new ReminderItem(vehicle, maintElem, maintType, 0,
						interval, "km", String.valueOf(odometer), details,
						dateInserted.toString());

			} else {

				remItem = new ReminderItem(vehicle, maintElem, maintType, 1,
						dateIntervalNumberView.getSelectedItem().toString(),
						dateIntervalTypeView.getSelectedItem().toString(),
						String.valueOf(odometer), details,
						dateInserted.toString());

			}

			Intent intent = new Intent(getApplicationContext(), NewRem.class);
			intent.putExtra("ReminderItem", remItem);
			intent.putExtra("isModification", false);
			startActivity(intent);

			// if checkbox is checked ( new reminder ) and isModification is
			// false and
			// we have remItem <> null
			// delete other reminder
			if (!isModification && remItemSent != null) {

				RemLogSource rLS = new RemLogSource(getApplicationContext());
				rLS.deleteEntry(remItemSent);
			}

		} else {

			// if checkbox is not checked ( no new reminder ) and isModification
			// is false and
			// we have remItem <> null
			// regen other reminder with new interval
			if (!isModification && remItemSent != null) {

				RemLogSource rLS = new RemLogSource(getApplicationContext());
				
				Bundle b = new Bundle();
				b.putStringArray("intervalSizeArray", getResources().getStringArray(
					R.array.intervalTypeArray));
				b.putStringArray("intervalSizeArrayP", getResources().getStringArray(
					R.array.intervalTypeArrayP));
				
				NewRemDialog newRemDialog = new NewRemDialog();
				newRemDialog.setArguments(b);
				if (remItemSent.getReminderType() == 0) {

					remItemSent.setLastInterval(String.valueOf(odometer));

				} else {
					remItemSent.setLastInterval(date);

				}

				remItemSent = newRemDialog.setNextInterval(remItemSent);

				rLS.updateEntry(remItemSent);

				Toast.makeText(getApplicationContext(),
						"Reminder was regenerated! ", Toast.LENGTH_SHORT)
						.show();

			}

		}

		this.onBackPressed();

	}

	public void setValues() {

		maintElemSpinner.setSelection(maintElemAdapter.getPosition(mItem
				.getMaintElem()));

		maintTypeSpinner.setSelection(maintTypeAdapter.getPosition(mItem
				.getMaintType()));

		DecimalFormat df = new DecimalFormat("####.00");

		fuelTextView.setText(String.valueOf(df.format(mItem.getFuelAmount())));

		odoEditText = (EditText) findViewById(R.id.odotext); //
		memEditText = (EditText) findViewById(R.id.memotext); // details
		cashEditText = (EditText) findViewById(R.id.cash);
		cashEditText.setText(df.format(mItem.getCash()));
		cashEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if(cashEditText.getText().toString().equals("0.0")){
						cashEditText.setText("");
					} 
				}
				
			}
		});
		int cashType = Integer.parseInt(sharedPrefs.getString("pref_CashType",
				"0"));

		if (cashType == 0 && mItem.getMaintElem().equals("Fuel")) {

			double cash = mItem.getCash() / mItem.getFuelAmount();
			cash = Math.round(cash * 100.0) / 100.0;

			cashEditText.setText(String.valueOf(cash));

		}

		odoEditText.setText(String.valueOf(mItem.getOdometer()));
		odoEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if(odoEditText.getText().toString().equals("0")) {
						odoEditText.setText("");
					} 
				}
				
			}
		});
		memEditText.setText(String.valueOf(mItem.getDetails()));

		Integer indexOfSecondSlash = new String(mItem.getDate())
				.indexOf("-", 5);

		int yearInt = Integer.parseInt(new String(mItem.getDate()).substring(0,
				4));
		int monthInt = Integer.parseInt(new String(mItem.getDate()).substring(
				5, indexOfSecondSlash));
		int dayInt = Integer.parseInt(new String(mItem.getDate()).substring(
				indexOfSecondSlash + 1, mItem.getDate().length()));

		datepick.updateDate(yearInt, monthInt - 1, dayInt);

		if (isModification) {

			Button createButtonView = (Button) findViewById(R.id.create);
			createButtonView.setText("Modify");
			TextView titleView = (TextView) findViewById(R.id.title);
			titleView.setText("Modify existing entry! ");

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_new_log, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		FragmentManager fm = getSupportFragmentManager();
		elemDialog = new NewElementDialog();
		Bundle b = new Bundle();
		if (item.getItemId() == R.id.menu_elem) {
			Intent i = new Intent(getApplicationContext(),DeleteElem.class);
			i.putExtra("type", NewRem.ELEMVAL);
			startActivityForResult(i, DELETEELEM);
		}

		if (item.getItemId() == R.id.menu_add_maintenance_type) {
			Intent i = new Intent(getApplicationContext(),DeleteElem.class);
			i.putExtra("type", NewRem.ELEMTYPEVAL);
			startActivityForResult(i,DELETEELEM);
			 
		}
		
		if (item.getItemId() == R.id.menu_clear_preferences) {

			intervalEditor.clear();
			intervalEditor.commit();
			elemTypeEditor.clear();
			elemTypeEditor.commit();
			elemEditor.clear();
			elemEditor.commit();

			setPreferences();
			setMaintElemSpinner(-1);
			setMaintTypeSpinner(-1);

		}
		return super.onOptionsItemSelected(item);
	}

	public double getConsumption(String vehicle, String maintElem, double fuel,
			int odometer) {
		Cursor cursor;
		if (!isModification) {
			cursor = mainlogSource.getLastItem(vehicle, maintElem);
		} else {
			cursor = mainlogSource.getItemAtKey(
					Integer.toString(mItem.getKey()), maintElem);
			if (cursor.getCount() > 1) { // modification

				cursor.moveToNext();
			} else {
				return 0;
			}
		}

		if (cursor != null) {

			double consumption;
			if (MyListFragment.mileageType == 0) {
				consumption = 100
						* fuel
						/ (odometer - cursor.getDouble(cursor
								.getColumnIndex(MainHelper.FIELD7)));

			} else {

				if (MyListFragment.mileageType == 2) {

					consumption = fuel
							/ (odometer - cursor.getDouble(cursor
									.getColumnIndex(MainHelper.FIELD7)));

				} else { // adica si pe 1 ( mpg ) si pe 3 (km/l)

					consumption = (odometer - cursor.getDouble(cursor
							.getColumnIndex(MainHelper.FIELD7))) / fuel;
				}
			}

			if (consumption < 0) {
				return 0;
			} else {
				return consumption;
			}

		} else
			return 0;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==DELETEELEM) {
			
			if(resultCode==1){
				setPreferences();
				setMaintElemSpinner(-1);
				setMaintTypeSpinner(-1);
			}
		}
	}

	@Override
	public void addNewSharedPreference(String preferenceType, String value) {

		if (preferenceType.equals(NewLog.ELEMVAL)) {

			SharedPreferences elemPref = getSharedPreferences(
					getString(R.string.elem_preference_file_key),
					Context.MODE_PRIVATE);

			Editor ed = elemPref.edit();

			int elemCount = elemPref.getInt(NewLog.ELEMCOUNTSTRING, 0);

			if (elemCount == 0) {

				Toast.makeText(getApplicationContext(),
						"somethign wrong with shared pref", Toast.LENGTH_LONG)
						.show();

			} else {
				ed.putString(NewLog.ELEMVAL + elemCount, value);
				elemCount++;		
				ed.putInt(NewLog.ELEMCOUNTSTRING, elemCount);
				ed.commit();
				
				setPreferences();
				setMaintElemSpinner(elemCount);
				setMaintTypeSpinner(-1);

			}
		}

		if (preferenceType.equals(NewLog.ELEMTYPEVAL)) {

			SharedPreferences elemTypePref = getSharedPreferences(
					getString(R.string.elemtype_preference_file_key),
					getApplicationContext().MODE_PRIVATE);

			Editor edType = elemTypePref.edit();

			int elemTypeCount = elemTypePref.getInt(NewLog.ELEMTYPECOUNTSTRING,
					0);
			if (elemTypeCount == 0) {

				Toast.makeText(
						getApplicationContext(),
						"somethign wrong with shared pref on "
								+ NewLog.ELEMTYPECOUNTSTRING, Toast.LENGTH_LONG)
						.show();

			} else {
				edType.putString(NewLog.ELEMTYPEVAL + elemTypeCount, value);
				elemTypeCount++;
				edType.putInt(NewLog.ELEMTYPECOUNTSTRING, elemTypeCount);
				edType.commit();
				setPreferences();
				setMaintTypeSpinner(elemTypeCount);
				

			}
		}

		setPreferences();
		

	}

	@Override
	public void onItemChanged() {

	}

	public void sendToFuelly() {

		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		FuellyScraper scraper = new FuellyScraper(getApplicationContext());

		if (networkInfo == null) {
			Toast.makeText(getApplicationContext(),
					"No internet connection! Fuelly submission postponed! ",
					Toast.LENGTH_LONG).show();
			sharedPrefs.edit().putString("Submit", "1").commit();
			scraper.setFuellySharedPrefs(mItem.getOdometer(),
					mItem.getFuelAmount(), cashPerLitre, datepick.getYear(),
					datepick.getMonth() + 1, datepick.getDayOfMonth());
		} else {

			scraper.setFuellySharedPrefs(mItem.getOdometer(),
					mItem.getFuelAmount(), cashPerLitre, datepick.getYear(),
					datepick.getMonth() + 1, datepick.getDayOfMonth());
			scraper.scrapeAndSubmit();

		}
	}

	@Override
	public void onRemElementModify() {
		
		
	}

}
