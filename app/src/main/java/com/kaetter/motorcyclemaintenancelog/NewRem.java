package com.kaetter.motorcyclemaintenancelog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import utils.Helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import beans.ReminderItem;
import dbcontrollers.MainLogSource;
import dialogs.NewElementDialog;
import dialogs.NewElementDialog.OnNewElementListener;
import dialogs.NewRemDialog;

public class NewRem extends FragmentActivity implements OnNewElementListener {
	public static String tag = "NewRem";
	private boolean notonloadmaintelemspinner=false, notonloadmainttypeelemspinner=false;
	private Spinner maintElemSpinner;
	private Spinner maintTypeSpinner, intervalSpinner, intervalTypeSpinner;
	private ArrayAdapter<String> maintElemAdapter, maintTypeAdapter,
			intervalNumberAdapter, intervalTypeAdapter, intervalTypeAdapterP;
	private ArrayList<String> intervalNumberArrayList, maintElemArrayList,
			maintTypeArrayList;
	private DatePicker datepick;
	private EditText odometerView, intervalo2;
	private TextView header;
	private int day, year, month, reminderType;
	private CheckBox dateCheckBox;
	public String vehicle;
	public static int DELETEELEM=1;
	private SharedPreferences intervalPref, elemTypePref, elemPref;

	public static final int INTERVALCOUNT = 50, ELEMCOUNT = 50,
			ELEMTYPECOUNT = 50;
	

	private int intervalCount, elemCount, elemTypeCount;

	public static final String INTERVALCOUNTSTRING = "intervalCount";
	public static final String ELEMCOUNTSTRING = "elemCountString";
	public static final String ELEMTYPECOUNTSTRING = "elemTypeCount";

	public static final String INTERVALVAL = "intervalVal_";
	public static final String ELEMVAL = "elemVal_";
	public static final String ELEMTYPEVAL = "elemtypeVal_";
	private NewElementDialog elemDialog;

	private ReminderItem remItem;
	View mainView;
	Editor intervalEditor, elemTypeEditor, elemEditor;

	String intervalValues[], elemValues[], elemTypeValues[];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_rem);

		vehicle = "default";
		remItem = (ReminderItem) getIntent().getSerializableExtra(
				"ReminderItem");

		setPreferences();
		setMaintTypeSpinner(-1);
		setMaintElemSpinner(-1);
		setIntervalSpinner();
		setElemType();
		setOdometer(vehicle);
		setDate();
		setView();
		setCreateButton();

		if (remItem != null) {

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

	public void setMaintTypeSpinner(int pos) {

		maintTypeArrayList.add(0, "...");

		maintTypeAdapter = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.generalspinner, maintTypeArrayList);
		maintTypeAdapter
				.setDropDownViewResource(R.layout.generalspinnerdropdown);

		maintTypeSpinner = (Spinner) findViewById(R.id.mainttypespinner);

		maintTypeSpinner.setAdapter(maintTypeAdapter);

		maintTypeSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent, View view,
							int pos, long id) {		
						String select = maintTypeSpinner.getItemAtPosition(pos).toString() ;
						if(select.equals("Other")&& !maintElemSpinner.getSelectedItem().toString().equals("Fuel") && !maintElemSpinner.getSelectedItem().toString().equals("Oil") && notonloadmainttypeelemspinner) {
							
							elemDialog = new NewElementDialog();
							Bundle b = new Bundle();
								FragmentManager fm = getSupportFragmentManager();
								b.putString("newelementdialog", ELEMTYPEVAL);
								b.putString("callingActivity", tag);
								elemDialog.setArguments(b);
								elemDialog.show(fm, tag);
						} else {
							notonloadmainttypeelemspinner=true;
							setTextHeader();
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

		if (pos >-1 ) {
			maintTypeSpinner.setSelection(pos);
		}
	}

	public void setMaintElemSpinner(int pos) {

		maintElemArrayList.add(0, "...");

		maintElemAdapter = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.generalspinner, maintElemArrayList);
		maintElemAdapter
				.setDropDownViewResource(R.layout.generalspinnerdropdown);

		maintElemSpinner = (Spinner) findViewById(R.id.maintelemspinner);
		maintElemSpinner.setAdapter(maintElemAdapter);

		maintElemSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view,
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
							setTextHeader();
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}

				});
		if (pos >-1 ) {
			maintElemSpinner.setSelection(pos);
		}

	}

	public void setIntervalSpinner() {
		intervalNumberArrayList.add(0, "...");
		intervalNumberAdapter = new ArrayAdapter<String>(
				getApplicationContext(), R.layout.generalspinner,
				intervalNumberArrayList);
		intervalSpinner = (Spinner) findViewById(R.id.interval);
		intervalNumberAdapter
				.setDropDownViewResource(R.layout.generalspinnerdropdown);
		intervalSpinner.setAdapter(intervalNumberAdapter);

		// load adapter for type list singular
		String[] intervalTypeList = getResources().getStringArray(
				R.array.intervalTypeArray);

		ArrayList<String> intervalTypeArrayList = new ArrayList<String>(
				Arrays.asList(intervalTypeList));
		intervalTypeAdapter = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.generalspinner, intervalTypeArrayList);
		// load adapter for type list plural
		String[] intervalTypeListP = getResources().getStringArray(
				R.array.intervalTypeArrayP);

		ArrayList<String> intervalTypeArrayListP = new ArrayList<String>(
				Arrays.asList(intervalTypeListP));
		intervalTypeAdapterP = new ArrayAdapter<String>(
				getApplicationContext(), R.layout.generalspinner,
				intervalTypeArrayListP);
		// set singular first then change in listener to plural if necessary
		intervalTypeSpinner = (Spinner) findViewById(R.id.intervaltype);
		intervalTypeAdapterP
				.setDropDownViewResource(R.layout.generalspinnerdropdown);
		intervalTypeSpinner.setAdapter(intervalTypeAdapter);

		intervalSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long id) {

				if (!intervalSpinner.getItemAtPosition(position).equals("1")) {
					int i = intervalTypeSpinner.getSelectedItemPosition();
					intervalTypeSpinner.setAdapter(intervalTypeAdapterP);
					intervalTypeSpinner.setSelection(i);

				} else {
					int i = intervalTypeSpinner.getSelectedItemPosition();
					intervalTypeSpinner.setAdapter(intervalTypeAdapter);
					intervalTypeSpinner.setSelection(i);
				}

				setTextHeader();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		intervalTypeSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						setTextHeader();

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

	}

	public void setOdometer(String vehicle) {

		MainLogSource mainLogSource = new MainLogSource(getApplicationContext());
		int odometer = mainLogSource.getLastOdometer(vehicle);
		odometerView = (EditText) findViewById(R.id.odometer);
		odometerView.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if (!hasFocus)
					setTextHeader();

			}
		});

		odometerView.setText(odometer + "");

		intervalo2 = (EditText) findViewById(R.id.intervalo);
		intervalo2.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if (!hasFocus)
					if (String.valueOf(intervalo2.getText()) == "") {
						intervalo2.setText("0");
					}

				setTextHeader();

			}
		});

	}

	public void setView() {
		datepick.setVisibility(View.GONE);

		intervalSpinner.setVisibility(View.GONE);

		intervalTypeSpinner.setVisibility(View.GONE);

		dateCheckBox = (CheckBox) findViewById(R.id.datecheckbox);
		dateCheckBox.setChecked(false);

		TextView kmLabelrem = (TextView) findViewById(R.id.kmLabelrem);
		EditText intervalo = (EditText) findViewById(R.id.intervalo);
		if (MyListFragment.mileageType == 1) {
			kmLabelrem.setText("miles");
			intervalo.setHint("miles interval");

		}

		final EditText intervaloFinal = intervalo;
		final TextView kmLabelremFinal = kmLabelrem;

		dateCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			final EditText odometer = (EditText) findViewById(R.id.odometer);

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {

					intervaloFinal.setVisibility(View.GONE);
					odometer.setVisibility(View.GONE);
					kmLabelremFinal.setVisibility(View.GONE);

					intervalSpinner.setVisibility(View.VISIBLE);
					intervalTypeSpinner.setVisibility(View.VISIBLE);
					datepick.setVisibility(View.VISIBLE);
					reminderType = 1;
					setTextHeader();

				} else {
					intervaloFinal.setVisibility(View.VISIBLE);
					odometer.setVisibility(View.VISIBLE);

					kmLabelremFinal.setVisibility(View.VISIBLE);

					intervalSpinner.setVisibility(View.GONE);
					intervalTypeSpinner.setVisibility(View.GONE);
					datepick.setVisibility(View.GONE);
					reminderType = 0;
					setTextHeader();
				}

			}
		});

	}

	public void setDate() {

		datepick = (DatePicker) findViewById(R.id.datepickrem);

		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		// set current date into datepicker
		datepick.init(year, month, day, new OnDateChangedListener() {

			@Override
			public void onDateChanged(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {

				setTextHeader();

			}
		});

	}

	public void getDate() {

	}

	public void setElemType() {

		elemTypeCount = elemTypePref.getInt(ELEMTYPECOUNTSTRING, 0);
		maintTypeArrayList.clear();
		maintTypeArrayList.add("...");
		for (int i = 0; i < elemTypeCount; i++) {

			maintTypeArrayList
					.add(elemTypePref.getString(ELEMTYPEVAL + i, " "));
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_new_rem, menu);
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


		return super.onOptionsItemSelected(item);
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

//		setElemType();

	}

	public void setTextHeader() {

		header = (TextView) findViewById(R.id.reminderDescription);

		EditText odometer2 = (EditText) findViewById(R.id.odometer);

		String maintType = (String) maintTypeSpinner.getSelectedItem();

		if (!maintType.equals("...")) {

			header.setText(new StringBuilder("This will remind you to").append(
					" ").append(maintType.toLowerCase()));

			String maintElement = (String) maintElemSpinner.getSelectedItem();

			if (!maintElement.equals("...")) {

				header.setText(new StringBuilder(header.getText()).append(" ")
						.append(maintElement.toLowerCase()));

				if (dateCheckBox.isChecked()) {

					if (!String.valueOf(intervalSpinner.getSelectedItem())
							.equals("...")) {

						String date = Helper.formatDate(datepick.getYear(), datepick.getMonth(), datepick.getDayOfMonth());

						header.setText(new StringBuilder(header.getText()
								+ " every ")
								.append(String.valueOf(intervalSpinner
										.getSelectedItem()))
								.append(" ")
								.append(String.valueOf(intervalTypeSpinner
										.getSelectedItem()))
								.append(" starting with ").append(date));
					}

				} else {
					if (!String.valueOf(intervalo2.getText()).equals("")) {
						if (MyListFragment.mileageType == 0
								|| MyListFragment.mileageType == 2) {

							header.setText(new StringBuilder(header.getText()
									+ " every ")
									.append(String.valueOf(intervalo2.getText()))
									.append(" km's starting with km ")
									.append(String.valueOf(odometer2.getText())));
						} else {
							header.setText(new StringBuilder(header.getText()
									+ " every ")
									.append(String.valueOf(intervalo2.getText()))
									.append(" miles starting with  ")
									.append(String.valueOf(odometer2.getText())));

						}
					}

				}

			}
		}

	}

	public void setCreateButton() {

		Button createButton = (Button) findViewById(R.id.CreateRemButton);

		final boolean isModification = getIntent().getBooleanExtra(
				"isModification", false);

		if (remItem != null && isModification) {

			createButton.setText("Modify Rem");

		}

		createButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				setTextHeader();
				String interval, intervalSize, lastInterval;
				String maintElem = (String) maintElemSpinner.getSelectedItem();
				String maintType = (String) maintTypeSpinner.getSelectedItem();
				EditText odometerView = (EditText) findViewById(R.id.odometer);

				if (maintElem.equals("...") || maintType.equals("...")) {

					Toast.makeText(getApplicationContext(),
							"You must select an element!", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				if (reminderType == 1) {

					interval = (String) intervalSpinner.getSelectedItem();

					if (interval.equals("...")) {

						Toast.makeText(getApplicationContext(),
								"You must select an interval!",
								Toast.LENGTH_SHORT).show();
						return;
					}

					intervalSize = (String) intervalTypeSpinner
							.getSelectedItem();

					lastInterval =  Helper.formatDate(datepick.getYear(), datepick.getMonth(), datepick.getDayOfMonth());
				} else {

					interval = String.valueOf(intervalo2.getText());

					if (interval.equals("0") || interval.equals("")) {

						Toast.makeText(getApplicationContext(),
								"You must insert a number!", Toast.LENGTH_SHORT)
								.show();
						return;
					}

					intervalSize = (String) "km";
					lastInterval = String.valueOf(odometerView.getText());

					if (lastInterval.equals("0") || lastInterval.equals("")) {

						Toast.makeText(getApplicationContext(),
								"You must insert a starting point!",
								Toast.LENGTH_SHORT).show();
						return;
					}

				}

				EditText detailsView = (EditText) findViewById(R.id.editdetails);

				String details = String.valueOf(detailsView.getText());

				final Calendar cal = Calendar.getInstance();

				String dateInserted =  new StringBuilder()
						.append(cal.get(Calendar.YEAR)).append("/")
						.append(cal.get(Calendar.MONTH) + 1).append("/")
						.append(cal.get(Calendar.DAY_OF_MONTH)).toString();

				if (remItem != null && isModification) {

					remItem = new ReminderItem(remItem.getKey(), vehicle,
							maintElem, maintType, reminderType, interval,
							intervalSize, lastInterval, remItem
									.getNextInterval(), details, dateInserted);

				} else {

					remItem = new ReminderItem(vehicle, maintElem, maintType,
							reminderType, interval, intervalSize, lastInterval,
							details, dateInserted);

				}

				FragmentManager fm = getSupportFragmentManager();
				NewRemDialog newRemDialog = new NewRemDialog();
				Bundle b = new Bundle();
				b.putString("remDescription", String.valueOf(header.getText()));
				b.putSerializable("reminderItem", remItem);
				b.putBoolean("isModification", isModification);
				b.putStringArray("intervalSizeArray", getResources().getStringArray(
						R.array.intervalTypeArray));
					b.putStringArray("intervalSizeArrayP", getResources().getStringArray(
						R.array.intervalTypeArrayP));
				newRemDialog.setArguments(b);
				newRemDialog.show(fm, tag);

			}
		});

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

	
	public void setValues() {

		maintElemSpinner.setSelection(maintElemAdapter.getPosition(remItem
				.getMaintElem()));
		maintTypeSpinner.setSelection(maintTypeAdapter.getPosition(remItem
				.getMaintType()));

		if (remItem.getReminderType() == 0) {

			dateCheckBox.setChecked(false);
			intervalo2.setText(remItem.getInterval());
			odometerView.setText(remItem.getLastInterval());

		} else {

			dateCheckBox.setChecked(true);
			intervalSpinner.setSelection(intervalNumberAdapter
					.getPosition(remItem.getInterval()));
			if (Integer.parseInt(remItem.getInterval()) > 1) {
				intervalTypeSpinner.setSelection(intervalTypeAdapterP
						.getPosition(remItem.getIntervalSize()));
			} else {
				intervalTypeSpinner.setSelection(intervalTypeAdapter
						.getPosition(remItem.getIntervalSize()));

			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			String lastInterval = String.valueOf(remItem.getLastInterval());
			try {
				c.setTime(sdf.parse(lastInterval));

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			datepick.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
					c.get(Calendar.DAY_OF_MONTH));
		}
		EditText detailsView = (EditText) findViewById(R.id.editdetails);
		detailsView.setText(remItem.getDetails());

	}

}