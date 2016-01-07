package com.kaetter.motorcyclemaintenancelog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import utils.FuellyScraper;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import beans.MaintenanceItem;
import dbcontrollers.MainHelper;
import dbcontrollers.MainLogSource;

public class RefuelActivity extends FragmentActivity {

	private MainLogSource mainLogSource;
	private double fuelAmount, cash, cashPerLitre;
	private int odometer;
	private int mileageType;
	private SharedPreferences sharedPrefs;
	private String username, pass;

	@Override
	protected void onCreate(Bundle arg0) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(arg0);

		setContentView(R.layout.refuelactivity);
		final EditText fuelAmountView = (EditText) findViewById(R.id.fueltext);
		final EditText odoTextView = (EditText) findViewById(R.id.odoText);
		final EditText cashTextView = (EditText) findViewById(R.id.cash);

		sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		mileageType = Integer.parseInt(sharedPrefs.getString(
				"pref_MileageType", "0"));

		if (mileageType == 1) {
			TextView litres = (TextView) findViewById(R.id.litres);
			litres.setText("gallons");
			TextView odoLabel = (TextView) findViewById(R.id.odoLabel);
			odoLabel.setText("miles");

		}

		if (!sharedPrefs.getString("email", "").equals("")) {

			findViewById(R.id.notification).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.notification).setVisibility(View.INVISIBLE);
		}

		final int cashType = Integer.parseInt(sharedPrefs.getString(
				"pref_CashType", "0"));

		String unit = (mileageType == 1) ? "gallon" : "litre";

		TextView cashTypeTextView = (TextView) findViewById(R.id.cashType);
		String cashTypeText = (cashType == 0) ? "price/" + unit
				: "price per fill-up!";

		cashTypeTextView.setText(cashTypeText);

		mainLogSource = new MainLogSource(getApplication()
				.getApplicationContext());

		odoTextView.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {

				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
						|| (actionId == EditorInfo.IME_ACTION_DONE)) {

					Date date = new Date();

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

					String today = sdf.format(date);

					String vehicle = "default";
					String[] maintElemArray = getResources().getStringArray(
							R.array.maintElemArray);
					String maintElem = maintElemArray[0];
					String[] maintTypeArray = getResources().getStringArray(
							R.array.maintTypeArray);
					String maintType = maintTypeArray[0];

					if (!fuelAmountView.getText().toString().equals("")) {

						fuelAmount = Double.parseDouble(fuelAmountView
								.getText().toString());
					} else {
						Toast.makeText(getApplicationContext(),
								"Fuel amount must be entered!",
								Toast.LENGTH_LONG).show();
						return false;
					}

					if (!odoTextView.getText().toString().equals("")) {
						odometer = Integer.parseInt(odoTextView.getText()
								.toString());

					} else {
						Toast.makeText(getApplicationContext(),
								"ODO must be entered!", Toast.LENGTH_LONG)
								.show();
						return false;
					}

					if (cashTextView.getText().toString().equals("")) {
						cash = 0;
						cashPerLitre = 0;
					}

					try {
						cash = Double.parseDouble(cashTextView.getText()
								.toString());
					} catch (NumberFormatException e) {

						Toast.makeText(getApplicationContext(),
								"Invalid number for price!", Toast.LENGTH_LONG)
								.show();
						return false;
					}

					if (cashType == 0) {
						cashPerLitre = cash;
						cash = cash * fuelAmount;
					} else {
						cashPerLitre = cash / fuelAmount;
					}
					cashPerLitre = Math.round(cashPerLitre * 100.0) / 100.0;
					cash = Math.round(cash * 100.0) / 100.0;
					double consumption = getConsumption(vehicle, maintElem,
							fuelAmount, odometer);

					String details = "quick entry";

					MaintenanceItem mItem = new MaintenanceItem(vehicle,
							maintElem, maintType, fuelAmount, consumption,
							today, odometer, details, mileageType, cash);

					mainLogSource.addMaintenanceItem(mItem);

					if (mileageType == 0) {

						Toast.makeText(
								getApplicationContext(),
								"New fuel entry added!\r\n\r\nFuel consumption is :"
										+ consumption
										+ "l/100km\r\n\r\nHave a safe trip!",
								Toast.LENGTH_LONG).show();
						finish();

					} else {
						if (mileageType == 2) {

							Toast.makeText(
									getApplicationContext(),
									"New fuel entry added!\r\n\r\nFuel consumption is :"
											+ consumption
											+ "l/km\r\n\r\nHave a safe trip!",
									Toast.LENGTH_LONG).show();
							finish();
						} else {
							Toast.makeText(
									getApplicationContext(),
									"New fuel entry added!\r\n\r\nFuel consumption is :"
											+ consumption
											+ "MPG\r\n\r\nHave a safe trip!",
									Toast.LENGTH_LONG).show();
							finish();
						}

					}

					sendToFuelly();
				}
				return false;
			}
		});
		cashTextView.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {

				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
						|| (actionId == EditorInfo.IME_ACTION_DONE)) {

					Date date = new Date();

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

					String today = sdf.format(date);

					String vehicle = "default";
					String[] maintElemArray = getResources().getStringArray(
							R.array.maintElemArray);
					String maintElem = maintElemArray[0];
					String[] maintTypeArray = getResources().getStringArray(
							R.array.maintTypeArray);
					String maintType = maintTypeArray[0];

					if (!fuelAmountView.getText().toString().equals("")) {

						fuelAmount = Double.parseDouble(fuelAmountView
								.getText().toString());
					} else {
						Toast.makeText(getApplicationContext(),
								"Fuel amount must be entered!",
								Toast.LENGTH_LONG).show();
						return false;
					}

					if (!odoTextView.getText().toString().equals("")) {
						odometer = Integer.parseInt(odoTextView.getText()
								.toString());

					} else {
						Toast.makeText(getApplicationContext(),
								"ODO must be entered!", Toast.LENGTH_LONG)
								.show();
						return false;
					}
					if (cashTextView.getText().toString().equals("")) {
						cash = 0;
						cashPerLitre = 0;
					} else {
						try {
							cash = Double.parseDouble(cashTextView.getText()
									.toString());
						} catch (NumberFormatException e) {

							Toast.makeText(getApplicationContext(),
									"Invalid number for price!",
									Toast.LENGTH_LONG).show();
							return false;
						}
					}

					if (cashType == 0) {
						cashPerLitre = cash;
						cash = cash * fuelAmount;

					} else {
						cashPerLitre = cash / fuelAmount;
					}
					cashPerLitre = Math.round(cashPerLitre * 100.0) / 100.0;
					cash = Math.round(cash * 100.0) / 100.0;

					// if value is correct then cash

					double consumption = getConsumption(vehicle, maintElem,
							fuelAmount, odometer);

					String details = "quick entry";

					MaintenanceItem mItem = new MaintenanceItem(vehicle,
							maintElem, maintType, fuelAmount, consumption,
							today, odometer, details, mileageType, cash);

					mainLogSource.addMaintenanceItem(mItem);

					if (mileageType == 0) {

						Toast.makeText(
								getApplicationContext(),
								"New fuel entry added!\r\n\r\nFuel consumption is :"
										+ consumption
										+ "l/100km\r\n\r\nHave a safe trip!",
								Toast.LENGTH_LONG).show();
						finish();

					} else {
						if (mileageType == 2) {

							Toast.makeText(
									getApplicationContext(),
									"New fuel entry added!\r\n\r\nFuel consumption is :"
											+ consumption
											+ "l/km\r\n\r\nHave a safe trip!",
									Toast.LENGTH_LONG).show();
							finish();
						} else {
							Toast.makeText(
									getApplicationContext(),
									"New fuel entry added!\r\n\r\nFuel consumption is :"
											+ consumption
											+ "MPG\r\n\r\nHave a safe trip!",
									Toast.LENGTH_LONG).show();
							finish();
						}

					}

					sendToFuelly();

				}
				return false;
			}
		});

	}

	public void sendToFuelly() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		FuellyScraper scraper = new FuellyScraper(getApplicationContext());
		Calendar cal = Calendar.getInstance();

		if (networkInfo == null) {
			Toast.makeText(getApplicationContext(),
					"No internet connection! Fuelly submission postponed! ",
					Toast.LENGTH_LONG).show();
			sharedPrefs.edit().putString("Submit", "1").commit();
			scraper.setFuellySharedPrefs(odometer, fuelAmount, cashPerLitre,
					cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
					cal.get(Calendar.DAY_OF_MONTH));
		} else {

			scraper.setFuellySharedPrefs(odometer, fuelAmount, cashPerLitre,
					cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
					cal.get(Calendar.DAY_OF_MONTH));
			scraper.scrapeAndSubmit();

		}

	}

	public double getConsumption(String vehicle, String maintElem, double fuel,
			int odometer) {

		Cursor cursor = mainLogSource.getLastItem(vehicle, maintElem);

		if (cursor != null) {

			double consumption;
			if (mileageType == 0) {
				consumption = 100
						* fuel
						/ (odometer - cursor.getDouble(cursor
								.getColumnIndex(MainHelper.FIELD7)));

			} else {
				if (mileageType == 2) {
					consumption = fuel
							/ (odometer - cursor.getDouble(cursor
									.getColumnIndex(MainHelper.FIELD7)));

				} else {

					consumption = (odometer - cursor.getDouble(cursor
							.getColumnIndex(MainHelper.FIELD7))) / fuel;
				}

			}

			consumption = Math.round(consumption * 100.0) / 100.0;

			if (consumption < 0) {
				return 0;
			} else {
				return consumption;
			}

		} else

			return 0;

	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

}
