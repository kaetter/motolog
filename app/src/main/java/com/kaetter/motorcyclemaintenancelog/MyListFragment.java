package com.kaetter.motorcyclemaintenancelog;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import listeners.OnItemChangedListener;
import utils.FuellyScraper;
import utils.Summarize;
import adapter.MainLogCursorAdapter;
import adapter.RemLogCursorAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayout;
import android.text.format.Time;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import beans.MaintenanceItem;
import beans.ReminderItem;
import dbcontrollers.MainHelper;
import dbcontrollers.MainLogSource;
import dbcontrollers.RemLogSource;
import dialogs.DatePickerFragment;
import dialogs.DatePickerFragment.EditDateDialogListener;
import dialogs.NewRemDialog.onRemElementListener;
import dialogs.UpdateDialog;
import dialogs.UpdateRemDialog;
import com.kaetter.motorcyclemaintenancelog.R;

@SuppressLint("ValidFragment")
public class MyListFragment extends Fragment implements
		LoaderCallbacks<Cursor>, OnItemChangedListener, onRemElementListener,
		EditDateDialogListener {

	private static final String TAG = "MyListFragment";

	private String mTag;
	private MainLogCursorAdapter mainAdapter;
	private RemLogCursorAdapter remAdapter;
	private Cursor cursor, remCursor;
	private MainLogSource mainLogSource;
	private RemLogSource remLogSource;
	private SharedPreferences sharedPrefs;

	private ListView mainLogListView, remLogListView;
	private Spinner filter;

	private View mainLogView, remLogView;
	private final int START_NEW_LOG = 0;
	private final int START_SETTINGS = 1;
	private Date today;
	private int odometer;
	private boolean showToast;
	public static int mileageType;
	private  Button from,to ;
	private View confView;
	private Summarize sum;
	private Cursor currentCursor ;
	   private static final int SWIPE_MIN_DISTANCE = 120;
	    private static final int SWIPE_MAX_OFF_PATH = 250;
	    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	    private GestureDetector gestureDetector;
	    View.OnTouchListener gestureListener;
	    

	public MyListFragment() {
	}

	public MyListFragment(String tag) {
		mTag = tag;
		Log.d(TAG, "Constructor: tag=" + tag);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setRetainInstance(true);

		if (mTag.equals(TabsFragment.TAB_LOG)) {

			if (showToast) {
				Toast.makeText(getActivity(), "Press menu for filter!",
						Toast.LENGTH_LONG).show();
				showToast = false;
			}

			loadMainLog();

		} else {
			if (mTag.equals(TabsFragment.TAB_REM)) {

			} else {
				loadConfig();
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View returnView = null;
		
		
		
		 gestureDetector = new GestureDetector(getActivity(), new MyGestureDetector());
	        gestureListener = new View.OnTouchListener() {
	            public boolean onTouch(View v, MotionEvent event) {
	                return gestureDetector.onTouchEvent(event);
	            }
	        };
		
		

		sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		mileageType = Integer.parseInt(sharedPrefs.getString(
				"pref_MileageType", "0"));
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (!sharedPrefs.getString("email", "").equals("")
				&& !sharedPrefs.getString("pwd", "").equals("")
				&& networkInfo != null
				&& sharedPrefs.getString("Submit", "0").equals("1")) {
			
			sharedPrefs.edit().putString("email", "");
			sharedPrefs.edit().putString("pwd", "");
			
//			Toast.makeText(getActivity(), "Starting submit of Fuelly entries!",
//					Toast.LENGTH_LONG).show();
//			sharedPrefs.edit().putString("Submit", "0").commit();
//			startFuellyScraper();
		}

		if (mTag == null)

			mTag = new String(TabsFragment.TAB_LOG);
		
		if (mTag.equals(TabsFragment.TAB_LOG)) {
			setHasOptionsMenu(true);
		}

		if (mTag.equals(TabsFragment.TAB_LOG)) {

			mainLogSource = new MainLogSource(getActivity()
					.getApplicationContext());

			// cursor = mainLogSource.getCursor();
			//
			// mainAdapter = new MainLogCursorAdapter(getActivity()
			// .getApplicationContext(), cursor);

			mainAdapter = new MainLogCursorAdapter(getActivity()
					.getApplicationContext(), null);

			mainLogView = inflater.inflate(R.layout.logmainlist, container,
					false);

			mainLogListView = (ListView) mainLogView
					.findViewById(R.id.mainList);

			mainLogListView.setEmptyView(mainLogView
					.findViewById(R.id.nologsinfo));

			mainLogListView.setAdapter(mainAdapter);

			mainLogListView.setSelection(mainAdapter.getCount() - 1);
			
			mainLogListView.setOnTouchListener(gestureListener);
			
			setMainLogListeners();
			setSpinnerFilter(false);
			
			 getLoaderManager().initLoader(1, null, this);
			returnView = mainLogView;

		}

		if (mTag.equals(TabsFragment.TAB_REM)) {

			remLogSource = new RemLogSource(getActivity()
					.getApplicationContext());
			remCursor = remLogSource.getCursor();

			Date today = new Date();

			mainLogSource = new MainLogSource(getActivity()
					.getApplicationContext());

			int odometer = mainLogSource.getLastOdometer("default");

			remAdapter = new RemLogCursorAdapter(getActivity()
					.getApplicationContext(), remCursor, odometer, today);

			remLogView = inflater
					.inflate(R.layout.logremlist, container, false);
			remLogListView = (ListView) remLogView.findViewById(R.id.remList);
			remLogListView.setEmptyView(remLogView
					.findViewById(R.id.nologsinfo));
			remLogListView.setAdapter(remAdapter);
			remLogListView.setOnTouchListener(gestureListener);
			remLogListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long id) {

					Cursor cursorAt = (Cursor) remLogListView
							.getItemAtPosition(position);

					int key = cursorAt.getInt(cursorAt
							.getColumnIndex(MainHelper.KEY));
					String vehicle = cursorAt.getString(cursorAt
							.getColumnIndex(MainHelper.FIELD1R));
					String maintElem = cursorAt.getString(cursorAt
							.getColumnIndex(MainHelper.FIELD2R));
					String maintType = cursorAt.getString(cursorAt
							.getColumnIndex(MainHelper.FIELD3R));
					int reminderType = cursorAt.getInt(cursorAt
							.getColumnIndex(MainHelper.FIELD4R));
					String interval = cursorAt.getString(cursorAt
							.getColumnIndex(MainHelper.FIELD5R));
					String intervalSize = cursorAt.getString(cursorAt
							.getColumnIndex(MainHelper.FIELD5Ra));
					String lastInterval = cursorAt.getString(cursorAt
							.getColumnIndex(MainHelper.FIELD6R));
					String nextInterval = cursorAt.getString(cursorAt
							.getColumnIndex(MainHelper.FIELD7R));
					String details = cursorAt.getString(cursorAt
							.getColumnIndex(MainHelper.FIELD8R));
					String dateInserted = cursorAt.getString(cursorAt
							.getColumnIndex(MainHelper.FIELD9R));

					ReminderItem item = new ReminderItem(key, vehicle,
							maintElem, maintType, reminderType, interval,
							intervalSize, lastInterval, nextInterval, details,
							dateInserted);

					Bundle b = new Bundle();

					b.putSerializable("ReminderItem", item);

					TextView descriptionView = (TextView) arg1
							.findViewById(R.id.maintInfo);

					b.putString("Description", descriptionView.getText()
							.toString());

					UpdateRemDialog updateRemDialog = new UpdateRemDialog();

					updateRemDialog.setArguments(b);

					updateRemDialog.show(getActivity()
							.getSupportFragmentManager(), "UpdateRemDialog");
				}
			});

			Button remLogButton = (Button) remLogView
					.findViewById(R.id.NewRemButton);

			remLogButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent(getActivity(), NewRem.class);

					startActivity(intent);
				}
			});
			returnView = remLogView;
		}

		if (mTag.equals(TabsFragment.TAB_CONF)) {

			  confView = inflater
					.inflate(R.layout.logconf, container, false);
			  confView.setOnTouchListener(gestureListener);
			  getGeneralBikeData();
			  
			  Calendar cal =Calendar.getInstance();
			 from = (Button) confView.findViewById(R.id.from);
			 from.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					DialogFragment newFragment = new DatePickerFragment();
					Bundle b = new Bundle();
					b.putString("button", "from");
					newFragment.setArguments(b);			
				    newFragment.show(getChildFragmentManager(), "datePicker");

				}
			});
			 
			 String days;
			 
				if (cal.get(Calendar.DAY_OF_MONTH)<10) {
					days = "0"+cal.get(Calendar.DAY_OF_MONTH);
					
				} else {
					days = ""+ cal.get(Calendar.DAY_OF_MONTH);
				}
			 
			 
			 if(cal.get(Calendar.MONTH)<9) {
				 from.setText(cal.get(Calendar.YEAR)+"-0"+(cal.get(Calendar.MONTH)+1)+"-"+days);
			 } else {
				 from.setText(cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+days);
			 }
			 
			 
			 
			 to = (Button) confView.findViewById(R.id.to);
			 to.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					DialogFragment newFragment = new DatePickerFragment();
					Bundle b = new Bundle();
					b.putString("button", "to");
					newFragment.setArguments(b);			
				    newFragment.show(getChildFragmentManager(), "datePicker");

				}
			});	
			 
			 
			 ArrayList<String> list = new ArrayList<String>();
			 list.add("...");
			 
			 Spinner maintelemspinner = (Spinner) confView.findViewById(R.id.maintelemspinner) ;
			 
			 ArrayAdapter<String> maintElemAdapter = new ArrayAdapter<String>(
						 getActivity().getApplicationContext(), R.layout.generalspinner,list);
			 
			 maintelemspinner.setAdapter(maintElemAdapter);
			 
			 maintelemspinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				
				 
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int pos , long id) {
					if(pos!=0) {
						sum = new Summarize();						
						
						sum.execute(2,currentCursor,from.getText().toString(), confView,MyListFragment.this);
					} else {
						TextView cashperelementvalue = (TextView)  confView.findViewById(R.id.cashperelementvalue);
						 cashperelementvalue.setText("0");
					}
				}
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
			 
			 
			 
				if (cal.get(Calendar.DAY_OF_MONTH)<10) {
					days = "0"+cal.get(Calendar.DAY_OF_MONTH);
					
				} else {
					days = ""+ cal.get(Calendar.DAY_OF_MONTH);
				}
			 
			 if(cal.get(Calendar.MONTH)<9) {
				 to.setText(cal.get(Calendar.YEAR)+"-0"+(cal.get(Calendar.MONTH)+1)+"-"+days);
				 } else {
					 to.setText(cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+days);
				 }
			 
				Bundle b = new Bundle();
				b.putString("from", "1800-01-01");
				b.putString("to", "2200-01-01");


			 getLoaderManager().initLoader(2, b, this);
			 
			returnView = confView;

		}

		return returnView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == START_NEW_LOG && resultCode == Activity.RESULT_OK) {
			onItemChanged();

			MyListFragment activity = (MyListFragment) getActivity()
					.getSupportFragmentManager().findFragmentById(R.id.tab_2);

			if (activity != null) {

				activity.loadReminderLog();
			}
		}

		if (requestCode == START_SETTINGS) {

			mileageType = Integer.parseInt(sharedPrefs.getString(
					"pref_MileageType", "0"));
		}
	}

	private void setMainLogListeners() {

		Button newLogButton = (Button) mainLogView
				.findViewById(R.id.NewLogButton);

		newLogButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getActivity(), NewLog.class);

				startActivityForResult(intent, START_NEW_LOG);

			}
		});

		mainLogListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				
				
//				TextView rowDetails = (TextView ) arg1.findViewById(R.id.rowDetails);
//				TextView rowodometer= (TextView ) arg1.findViewById(R.id.rowOdometer);
//				
//				System.out.println(rowDetails.getText().toString());
//				System.out.println(rowodometer.getText().toString());
				
				
				FragmentManager fm = getActivity().getSupportFragmentManager();
				Cursor cursorAt = (Cursor) mainLogListView
						.getItemAtPosition(position);
				int key = cursorAt.getInt(cursorAt
						.getColumnIndex(MainHelper.KEY));

				String vehicle = cursorAt.getString(cursorAt
						.getColumnIndex(MainHelper.FIELD1));

				String maintElem = cursorAt.getString(cursorAt
						.getColumnIndex(MainHelper.FIELD2));

				String maintType = cursorAt.getString(cursorAt
						.getColumnIndex(MainHelper.FIELD3));

				double fuelAmount = cursorAt.getFloat(cursorAt
						.getColumnIndex(MainHelper.FIELD4));
				double consumption = cursorAt.getFloat(cursorAt
						.getColumnIndex(MainHelper.FIELD5));
				String date = cursorAt.getString(cursorAt
						.getColumnIndex(MainHelper.FIELD6));
				int odometer = cursorAt.getInt(cursorAt
						.getColumnIndex(MainHelper.FIELD7));
				String details = cursorAt.getString(cursorAt
						.getColumnIndex(MainHelper.FIELD8));
				double cash = cursorAt.getFloat(cursorAt
						.getColumnIndex(MainHelper.FIELD10));

				MaintenanceItem item = new MaintenanceItem(key, vehicle,
						maintElem, maintType, fuelAmount, consumption, date,
						odometer, details, mileageType, cash);

				// UpdateDialog updateDialog = new UpdateDialog(item);

				UpdateDialog updateDialog1 = new UpdateDialog();

				Bundle args = new Bundle();
				args.putSerializable("MaintItem", item);
				updateDialog1.setArguments(args);

				updateDialog1.show(fm, "fragment_edit_name");

			}

		});

	}

	private void loadConfig() {

	}

	private void loadReminderLog() {
		System.out.println("loadReminderLog");
		if (remLogListView != null)

			remLogSource = new RemLogSource(getActivity()
					.getApplicationContext());
		remCursor = remLogSource.getCursor();

		today = new Date();

		odometer = mainLogSource.getLastOdometer("default");

		remAdapter = new RemLogCursorAdapter(getActivity()
				.getApplicationContext(), remCursor, odometer, today);

		remLogListView = (ListView) remLogView.findViewById(R.id.remList);
		remLogListView.setAdapter(remAdapter);

	}

	private void loadMainLog() {

	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, final Bundle b) {

		AsyncTaskLoader<Cursor> loader=null;
		if (loaderId==1) { //mainlog
			loader = new AsyncTaskLoader<Cursor>(
				getActivity()) {

				@Override
				public Cursor loadInBackground() {
					if (mainLogSource == null) {
						mainLogSource = new MainLogSource(getContext());
					}
					Cursor cursor;
					if (b==null) {
						cursor = mainLogSource.getCursor();
					}
					else  {
						cursor = mainLogSource.getCursor(b.getString("filter"));
					}
					return cursor;
				}
			};			
			loader.forceLoad();
			
		}
		
		
		if (loaderId==2) { //confreport
			loader = new AsyncTaskLoader<Cursor>(getActivity()) {
					@Override
					public Cursor loadInBackground() {
						if (mainLogSource == null) {
							mainLogSource = new MainLogSource(getContext());
						}
						Cursor cursor = mainLogSource.getConfCursor(b.getString("from"), b.getString("to"));
						return cursor;
					}
				};				
				loader.forceLoad();
				
		}
		return loader;

	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader , Cursor cursor) {

		if (loader.getId()==1) {
			mainAdapter.swapCursor(cursor);
			mainLogListView.setSelection(mainAdapter.getCount() - 1);
		}
		
		if(loader.getId()==2) { 
			
			currentCursor =cursor;
			if(!cursor.isAfterLast()) {
				sum = new Summarize();
				sum.execute(1,cursor,from.getText().toString(), confView,this);
			} else {
				sum = new Summarize();
				sum.execute(0,cursor,from.getText().toString(), confView,this);
			}
			
		}

	}

	

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	}

	@Override
	public void onItemChanged() {

		if (mTag.equals(TabsFragment.TAB_LOG)) {
			
			
			if (getLoaderManager().getLoader(1)!=null) {
				getLoaderManager().restartLoader(1, null, this);
			} else {
				getLoaderManager().initLoader(1, null, this);
			}

			// if (filter != null && filter.getVisibility() == View.VISIBLE) {
			// mainAdapter.changeCursor(mainLogSource.getCursor(filter
			// .getSelectedItem().toString()));
			//
			// } else {
			// mainAdapter.changeCursor(mainLogSource.getCursor());
			//
			// }
			// mainAdapter.notifyDataSetChanged();
			// mainLogListView.setSelection(mainAdapter.getCount() - 1);

		}

		if (mTag.equals(TabsFragment.TAB_REM)) {
			loadReminderLog();
		 
				MyListFragment activity1 = (MyListFragment) getActivity()
						.getSupportFragmentManager().findFragmentById(R.id.tab_1);
				
				if (activity1!=null) {
					activity1.getLoaderManager().destroyLoader(1);
					
					
					activity1.getLoaderManager().restartLoader(1, null, activity1);

				}
			

		}
	}

	@Override
	public void onResume() {

		super.onResume();

		if (mTag.equals(TabsFragment.TAB_LOG)) {
			Bundle b =new Bundle();
			if (filter != null && filter.getVisibility() == View.VISIBLE) {
				b.putString("filter", filter
						.getSelectedItem().toString());
				getLoaderManager().restartLoader(1, b, MyListFragment.this);
				
			} else {

				
			}
			mainLogListView.setSelection(mainAdapter.getCount() - 1);
		}

		if (mTag.equals(TabsFragment.TAB_REM)) {
			remAdapter.changeCursor(remLogSource.getCursor());
			remAdapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onRemElementModify() {

		if (mTag.equals("Rem")) {
			remAdapter.changeCursor(remLogSource.getCursor());
			remAdapter.notifyDataSetChanged();
		}
	}

	public void setSpinnerFilter(boolean show) {

		SharedPreferences elemPref = getActivity().getSharedPreferences(
				getString(R.string.elem_preference_file_key),
				getActivity().getApplicationContext().MODE_PRIVATE);

		Editor elemEditor = elemPref.edit();

		String[] maintElemList = getResources().getStringArray(
				R.array.maintElemArray);
		ArrayList<String> maintElemArrayList = new ArrayList<String>(
				Arrays.asList(maintElemList));

		int elemCount = elemPref.getInt(NewLog.ELEMCOUNTSTRING, 0);

		if (elemCount == 0) {
			elemCount = maintElemList.length - 1;
			elemEditor.putInt(NewLog.ELEMCOUNTSTRING, elemCount);
			elemEditor.commit();

			for (int i = 0; i <= elemCount; i++) {
				elemEditor.putString(NewLog.ELEMVAL + i, maintElemList[i]);
				elemEditor.commit();
			}
		}

		if (elemCount > maintElemList.length - 1) {

			for (int i = maintElemList.length; i <= elemCount; i++) {
				maintElemArrayList.add(elemPref.getString(NewLog.ELEMVAL + i,
						" "));
			}
		}

		ArrayAdapter<String> maintElemAdapter = new ArrayAdapter<String>(
				getActivity().getApplicationContext(), R.layout.generalspinner,
				maintElemArrayList);
		maintElemAdapter
				.setDropDownViewResource(R.layout.generalspinnerdropdown);

		filter = (Spinner) mainLogView.findViewById(R.id.filter);
		filter.setAdapter(maintElemAdapter);
		filter.setVisibility(View.GONE);
		filter.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long id) {
				Bundle b = new Bundle();
				b.putString("filter", filter
						.getSelectedItem().toString());
				getLoaderManager().restartLoader(1, b, MyListFragment.this);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.menu_filter) {
			Bundle b =new Bundle();
			if (filter.getVisibility() == View.VISIBLE) {
				filter.setVisibility(View.GONE);
				setSpinnerFilter(false);
				 
				getLoaderManager().restartLoader(1, null, this);
//				mainAdapter.changeCursor(mainLogSource.getCursor());
//				mainAdapter.notifyDataSetChanged();
//				mainLogListView.setSelection(mainAdapter.getCount() - 1);
			} else {
				filter.setVisibility(View.VISIBLE);
				 
				b.putString("filter", filter
						.getSelectedItem().toString());
				getLoaderManager().restartLoader(1, b, this);
				
//				mainAdapter.changeCursor(mainLogSource.getCursor(filter
//						.getSelectedItem().toString()));
//				mainAdapter.notifyDataSetChanged();
//				mainLogListView.setSelection(mainAdapter.getCount() - 1);
			}
		}

		if (item.getItemId() == R.id.menu_exportdb) {

			File extSd = Environment.getExternalStorageDirectory();

			Log.d(TAG, "media is " + Environment.getExternalStorageState());
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				Time now = new Time();
				now.setToNow();
				final String exportPath = extSd.toString() + "/MotoLog"
						+ now.year + Integer.toString(now.month + 1)
						+ now.monthDay + now.hour + now.minute + now.second
						+ ".db";
				builder.setMessage("File will be exported to: " + exportPath)

				.setTitle("Export Database!");

				builder.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								File dbFile = getActivity().getDatabasePath(
										MainHelper.DATABASE_NAME);
								try {
									mainLogSource.copyDatabase(
											dbFile.toString(), exportPath);
								} catch (IOException e) {
									Toast.makeText(getActivity(),
											"Export did not work! ",
											Toast.LENGTH_LONG).show();
								}
							}
						});

				builder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});

				builder.show();
			}
		}

		if (item.getItemId() == R.id.menu_importdb) {

			final File extSdImport = Environment.getExternalStorageDirectory();
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {

				File[] listOfFiles = extSdImport
						.listFiles(new FilenameFilter() {

							@Override
							public boolean accept(File dir, String filename) {

								return filename.toLowerCase()
										.startsWith("motolog");
							}
						});

				final ArrayAdapter<File> arrayAdapter = new ArrayAdapter<File>(
						getActivity(),
						android.R.layout.select_dialog_singlechoice,
						listOfFiles);

				AlertDialog.Builder builderImport = new AlertDialog.Builder(
						getActivity());

				builderImport.setTitle("Choose import DB!");

				builderImport.setAdapter(arrayAdapter,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0,
									int position) {

								AlertDialog.Builder builderInner = new AlertDialog.Builder(
										getActivity());

								builderInner.setMessage("Your selected db is:"
										+ arrayAdapter.getItem(position)
										+ "\n Your database will be overwritten!!!");
								final String fromDbPath = arrayAdapter.getItem(
										position).toString();

								Log.d(TAG, fromDbPath);
								builderInner.setPositiveButton("Import!",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {

												File toDbPath = getActivity()
														.getDatabasePath(
																MainHelper.DATABASE_NAME);
												Log.d(TAG, toDbPath.toString()
														+ " " + fromDbPath);
												boolean failed = false;
												try {
													mainLogSource
															.copyDatabase(
																	fromDbPath,
																	toDbPath.toString());
												} catch (IOException e) {
													Toast.makeText(
															getActivity(),
															"Database was not imported !",
															Toast.LENGTH_LONG)
															.show();
													failed = true;
												}
												if (!failed) {

													Intent intent = getActivity()
															.getIntent();
													getActivity().finish();
													startActivity(intent);
												}
											}
										});
								builderInner.setNegativeButton("Cancel!", null);

								builderInner.show();
							}
						});
				builderImport.show();
			}
		}

		if (item.getItemId() == R.id.menu_settings) {

			Intent intent = new Intent(getActivity(), SettingsActivity.class);
			startActivityForResult(intent, START_SETTINGS);

		}

		return true;
	}
	
	public void getGeneralBikeData() {
		
		SharedPreferences generalPref = getActivity().getSharedPreferences(
				getString(R.string.general_preference_file_key),
				Context.MODE_PRIVATE);
		
		final Editor generalEditor = generalPref.edit();

		final EditText bikeName = (EditText) confView
				.findViewById(R.id.bikenametext);

		bikeName.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if (!hasFocus) {

					generalEditor.putString("bikenametext", bikeName
							.getText().toString());
					generalEditor.commit();
				}
			}
		});

		bikeName.setText(generalPref.getString("bikenametext", ""));
		
		
		
		
		final TextView bikeDate = (TextView) confView
				.findViewById(R.id.dateofpurchaset);
		bikeDate.setClickable(true);
		bikeDate.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new DatePickerFragment();
				Bundle b = new Bundle();
				b.putString("button", "bikedate");
				newFragment.setArguments(b);			
			    newFragment.show(getChildFragmentManager(), "datePicker");
			}
		});		
		

		bikeDate.setText(generalPref.getString("dateofpurchaset", ""));

		final EditText bikeOdo = (EditText) confView
				.findViewById(R.id.initialodometert);

		bikeOdo.setText(generalPref.getString("initialodometert", ""));

		bikeOdo.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if (!hasFocus) {

					generalEditor.putString("initialodometert", bikeOdo
							.getText().toString());
					generalEditor.commit();
				}
			}
		});

		final EditText bikeDetails = (EditText) confView
				.findViewById(R.id.otherdetails);

		bikeDetails.setText(generalPref.getString("otherdetails", ""));

		bikeDetails.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if (!hasFocus) {

					generalEditor.putString("otherdetails", bikeDetails
							.getText().toString());
					generalEditor.commit();
				}
			}
		});
		
		
	}


	public void startFuellyScraper() {

		class StartFuellyScraperClass extends AsyncTask<String, String, String> {

			@Override
			protected String doInBackground(String... params) {
				FuellyScraper fuellyScraper = new FuellyScraper(getActivity());
				fuellyScraper.scrapeAndSubmit();
				return null;
			}
		}
		StartFuellyScraperClass sFScraperClass = new StartFuellyScraperClass();
		sFScraperClass.execute();

	}

	@Override
	public void onFinishEditDialog(String who, String inputText) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
		
		try {
			if(who.equals("from")){
				if(  (sdf.parse(inputText).before(sdf.parse(to.getText().toString()))||0==sdf.parse(inputText).compareTo(sdf.parse(to.getText().toString())))) {
					from.setText(inputText);
				} else {
					from.setText(to.getText());
				} 
			}
			if(who.equals("to") ){	
				if( (sdf.parse(inputText).after(sdf.parse(from.getText().toString()))||0==sdf.parse(inputText).compareTo(sdf.parse(from.getText().toString())))) {
					to.setText(inputText);
				} else {
					to.setText(from.getText());
				}
			}
		} catch (ParseException e) {
			
			e.printStackTrace();
		} 
		
		
		if(who.equals("bikedate")) {
			
			SharedPreferences generalPref = getActivity().getSharedPreferences(
					getString(R.string.general_preference_file_key),
					Context.MODE_PRIVATE);
			Editor generalEditor=generalPref.edit();
			generalEditor.putString("dateofpurchaset", inputText);
			generalEditor.commit();
			TextView bikeDate = (TextView) confView.findViewById(R.id.dateofpurchaset);
			bikeDate.setText(generalPref.getString("dateofpurchaset", ""));
		}
		

		Bundle b = new Bundle();
		b.putString("from", from.getText().toString());
		b.putString("to", to.getText().toString());
		getLoaderManager().restartLoader(2, b, this);
		
	}
	
	
	class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                	
                    return false;
                }
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	
                	TabsFragment.changeTab(true);
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	
                	
                	TabsFragment.changeTab(false);

                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

            @Override
        public boolean onDown(MotionEvent e) {
              return true;
        }
    }
}
