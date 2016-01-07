package utils;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class FuellyScraper {
	Context context;
	SharedPreferences sharedPrefs;
	Map<String, ?> allPrefs;
	Set<String> allKeys;
	String year, month, day;

	int odometer;
	double fuelAmount, cashPerLitre;

	public FuellyScraper(Context context) {
		this.context = context;
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

	}

	public void scrapeAndSubmit() {

		allPrefs = sharedPrefs.getAll();
		allKeys = allPrefs.keySet();
		String[] allKeysString = allKeys.toArray(new String[0]);
		for (String key : allKeysString) {
			if (key.startsWith("Fuelly")) {
				submitPost(key, sharedPrefs.getString(key, ""));

			}

		}

	}

	private void submitPost(String key, String data) {

		year = key.split("\\|")[1];
		month = key.split("\\|")[2];
		day = key.split("\\|")[3];

		odometer = Integer.parseInt(data.split("\\|")[0]);
		fuelAmount = Double.parseDouble(data.split("\\|")[1]);
		cashPerLitre = Double.parseDouble(data.split("\\|")[2]);

		String username = sharedPrefs.getString("email", "");
		String pass = sharedPrefs.getString("pwd", "");

/*		if (!username.equals("") && !pass.equals("")) {
			SendPostReqAsyncTask sendPostReqAsyndTask = new SendPostReqAsyncTask();
			sendPostReqAsyndTask.execute(username, pass);
		}*/

		sharedPrefs.edit().remove(key).commit();
		

	}

	public void setFuellySharedPrefs(int odometer, double fuel,
			double cashPerLitre, int year, int month, int day ) {

		if (cashPerLitre > 15) {
			cashPerLitre = 0;
		}

		Calendar cal = Calendar.getInstance();

		StringBuilder sharedPrefsKey = new StringBuilder();
		sharedPrefsKey.append("Fuelly").append("|")
				.append(year).append("|")
				.append(month).append("|")
				.append(day).append("|")
				.append(cal.get(Calendar.HOUR))
				.append(cal.get(Calendar.MINUTE))
				.append(cal.get(Calendar.SECOND));

		StringBuilder sharedPrefsEntry = new StringBuilder();
		sharedPrefsEntry.append(Integer.toString(odometer)).append("|")
				.append(Double.toString(fuel)).append("|")
				.append(Double.toString(cashPerLitre));

		System.out.println(sharedPrefsKey.toString());
		System.out.println(sharedPrefsEntry.toString());

		sharedPrefs.edit().putString(sharedPrefsKey.toString(),
				sharedPrefsEntry.toString()).commit();
	 
		System.out.println("The returned key" + sharedPrefs.getString(sharedPrefsKey.toString(), ""));

	}

	/*class SendPostReqAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {

			*//*if (FuellyPost.postEntry(context, params, odometer, fuelAmount,
					cashPerLitre, year, month, day))

				return "Fuelly submit Succesfull!";
			else {
				return "Fuelly submit UNSuccesfull!";

			}*//*
		}

		@Override
		protected void onPostExecute(String result) {

			Toast.makeText(context, result, Toast.LENGTH_LONG).show();
		}

	}
*/
}
