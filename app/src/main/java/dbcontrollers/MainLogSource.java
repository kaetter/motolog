package dbcontrollers;

import java.io.IOException;
import java.sql.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import beans.MaintenanceItem;

public class MainLogSource {
	String tag = "MainLogSource";
	static SQLiteDatabase database;
	MainHelper dbh;
	public static MainLogSource singleton;
	String[] allColumns = { MainHelper.KEY, MainHelper.FIELD1,
			MainHelper.FIELD2, MainHelper.FIELD3, MainHelper.FIELD4,
			MainHelper.FIELD5, MainHelper.FIELD6, MainHelper.FIELD7,
			MainHelper.FIELD8, MainHelper.FIELD9, MainHelper.FIELD10 };

	public MainLogSource(Context context) {
		this.dbh = new MainHelper(context);

		open();

	}

	public void open() throws SQLException {
		if (database == null || !database.isOpen()) {
			database = dbh.getWritableDatabase();
		}

	}

	public void addMaintenanceItem(MaintenanceItem item) {
		ContentValues values = new ContentValues();
		values.put(MainHelper.FIELD1, item.getVehicle());
		values.put(MainHelper.FIELD2, item.getMaintElem());
		values.put(MainHelper.FIELD3, item.getMaintType());
		values.put(MainHelper.FIELD4, item.getFuelAmount());
		values.put(MainHelper.FIELD5, item.getConsumption());
		String formatDate =  item.getDate();
		
		if(formatDate.length()==9) {
			formatDate=formatDate.substring(0, 5) + "0" + formatDate.substring(5);
		} 
		formatDate = formatDate.replace("/", "-");
		values.put(MainHelper.FIELD6, formatDate);
		values.put(MainHelper.FIELD7, item.getOdometer());
		values.put(MainHelper.FIELD8, item.getDetails());
		values.put(MainHelper.FIELD9, item.getMileageType());
		values.put(MainHelper.FIELD10, item.getCash());
		Log.d(tag, "MainHelper.FIELD9=" + item.getMileageType());
		database.insert(MainHelper.DATABASE_TABLE, null, values);

	}

	public Cursor getCursor() {

		Cursor cursor = database.query(MainHelper.DATABASE_TABLE, allColumns,
				null, null, null, null, null);

		return cursor;

	}
	
	public Cursor getConfCursor(String dateFrom, String dateTo) {
		
		Cursor cursor=database.rawQuery("select * from mainlog where date(date) between  date(?) and date(?) order by date desc;", new String[] {dateFrom.replace("/", "-"),dateTo.replace("/", "-")});
		
		return cursor;
	}

	public Cursor getCursor(String maintElem) {

		String whereClause = MainHelper.FIELD2 + "= ?";

		String[] whereArgs = { maintElem };
		String orderBy = MainHelper.FIELD7;

		Cursor cursor = database.query(MainHelper.DATABASE_TABLE, allColumns,
				whereClause, whereArgs, null, null, orderBy);

		boolean isNotEmpty = cursor.moveToFirst();

		if (isNotEmpty) {

			return (cursor);
		} else {
			return null;
		}

	}

	public int getItemsCount() {

		return (int) DatabaseUtils.queryNumEntries(database,
				MainHelper.DATABASE_TABLE);

	}

	public void updateEntry(MaintenanceItem item) {

		String whereClause = MainHelper.KEY + "= ?";
		String[] whereArgs = { Integer.toString(item.getKey()) };
		ContentValues valuesToPut = new ContentValues();
		valuesToPut.put(MainHelper.FIELD2, item.getMaintElem());
		valuesToPut.put(MainHelper.FIELD3, item.getMaintType());
		valuesToPut.put(MainHelper.FIELD4, item.getFuelAmount());
		String formatDate =  item.getDate();
		
		if(formatDate.length()==9) {
			formatDate=formatDate.substring(0, 5) + "0" + formatDate.substring(5);
		} 
		formatDate = formatDate.replace("/", "-");
		valuesToPut.put(MainHelper.FIELD6, formatDate);
		valuesToPut.put(MainHelper.FIELD7, item.getOdometer());
		valuesToPut.put(MainHelper.FIELD8, item.getDetails());
		valuesToPut.put(MainHelper.FIELD5, item.getConsumption());
		valuesToPut.put(MainHelper.FIELD9, item.getMileageType());
		valuesToPut.put(MainHelper.FIELD10, item.getCash());

		Log.d(tag, "MainHelper.FIELD9=" + item.getMileageType());

		database.update(MainHelper.DATABASE_TABLE, valuesToPut, whereClause,
				whereArgs);

	}

	public void deleteEntry(MaintenanceItem item) {

		database.delete(MainHelper.DATABASE_TABLE,
				MainHelper.KEY + "= " + item.getKey(), null);

	}

	public int getLastItem() {
		Cursor cursor = database.query(MainHelper.DATABASE_TABLE, allColumns,
				null, null, null, null, null);
		cursor.moveToLast();
		return (cursor.getInt(cursor.getColumnIndex(MainHelper.KEY)));

	}

	public Cursor getLastItem(String vehicle, String maintItem) {

		String whereClause = MainHelper.FIELD1 + "= ? and " + MainHelper.FIELD2
				+ " =?";

		String[] whereArgs = { vehicle, maintItem };
		String orderBy = MainHelper.FIELD7 + " desc";

		Cursor cursor = database.query(MainHelper.DATABASE_TABLE, allColumns,
				whereClause, whereArgs, null, null, orderBy);

		boolean isNotEmpty = cursor.moveToFirst();

		if (isNotEmpty) {

			return (cursor);
		} else {
			return null;
		}

	}

	public Cursor getItemAtKey(String key, String maintItem) {

		String whereClause = MainHelper.KEY + "<= ? and " + MainHelper.FIELD2
				+ " =?";

		String[] whereArgs = { key, maintItem };
		String orderBy = MainHelper.FIELD7 + " desc";

		Cursor cursor = database.query(MainHelper.DATABASE_TABLE, allColumns,
				whereClause, whereArgs, null, null, orderBy);

		boolean isNotEmpty = cursor.moveToFirst();

		if (isNotEmpty) {

			return (cursor);
		} else {
			return null;
		}

	}

	public int getLastOdometer(String vehicle) {

		String whereClause = MainHelper.FIELD1 + "= ?";

		String[] whereArgs = { vehicle };
		String orderBy = MainHelper.FIELD7 + " desc";

		Cursor cursor = database.query(MainHelper.DATABASE_TABLE, allColumns,
				whereClause, whereArgs, null, null, orderBy);

		boolean isNotEmpty = cursor.moveToFirst();

		if (isNotEmpty) {
			return (cursor.getInt(cursor.getColumnIndex(MainHelper.FIELD7)));
		} else {
			return 0;
		}

	}

	public boolean copyDatabase(String fromDbPath, String toDbPath)
			throws IOException {

		return dbh.copyDatabase(fromDbPath, toDbPath);

	}

}
