package dbcontrollers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import beans.ReminderItem;

public class RemLogSource {
	String tag = "RemLogSource";
	static SQLiteDatabase database;
	MainHelper dbh;
	public static RemLogSource singleton;
	String[] allColumns = { MainHelper.KEY, MainHelper.FIELD1R,
			MainHelper.FIELD2R, MainHelper.FIELD3R, MainHelper.FIELD4R,
			MainHelper.FIELD5R, MainHelper.FIELD5Ra, MainHelper.FIELD6R, MainHelper.FIELD7R,
			MainHelper.FIELD8R,MainHelper.FIELD9R };

	public RemLogSource(Context context) {
		this.dbh = new MainHelper(context);
		open();

	}

	public void open() throws SQLException {
		if (database==null || !database.isOpen() ) {  
			database = dbh.getWritableDatabase(); 
			} 
			
		

	}

	public void addMaintenanceItem(ReminderItem item) {
		ContentValues values = new ContentValues();
		values.put(MainHelper.FIELD1R, item.getVehicle());
		values.put(MainHelper.FIELD2R, item.getMaintElem());
		values.put(MainHelper.FIELD3R, item.getMaintType());
		values.put(MainHelper.FIELD4R, item.getReminderType());
		values.put(MainHelper.FIELD5R, item.getInterval());
		values.put(MainHelper.FIELD5Ra, item.getIntervalSize());
		values.put(MainHelper.FIELD6R, item.getLastInterval());
		values.put(MainHelper.FIELD7R, item.getNextInterval());
		values.put(MainHelper.FIELD8R, item.getDetails());
		values.put(MainHelper.FIELD9R, item.getDateInserted());

		database.insert(MainHelper.DATABASE_TABLER, null, values);

	}

	public Cursor getCursor() {

		Cursor cursor = database.query(MainHelper.DATABASE_TABLER,
				allColumns, null, null, null, null, null);

		return cursor;

	}

	public int getItemsCount() {

		return (int) DatabaseUtils.queryNumEntries(database,
				MainHelper.DATABASE_TABLER);

	}

	public void updateEntry(ReminderItem item) {
		System.out.println(item.getKey());
		String whereClause = MainHelper.KEY + "= ?";
		String[] whereArgs = { Integer.toString(item.getKey()) };
		ContentValues valuesToPut = new ContentValues();
		valuesToPut.put(MainHelper.FIELD2R, item.getMaintElem());
		valuesToPut.put(MainHelper.FIELD3R, item.getMaintType());
		valuesToPut.put(MainHelper.FIELD4R, item.getReminderType());
		valuesToPut.put(MainHelper.FIELD5R, item.getInterval());
		valuesToPut.put(MainHelper.FIELD5Ra, item.getIntervalSize());
		valuesToPut.put(MainHelper.FIELD6R, item.getLastInterval());
		valuesToPut.put(MainHelper.FIELD7R, item.getNextInterval());
		valuesToPut.put(MainHelper.FIELD8R, item.getDetails());
		valuesToPut.put(MainHelper.FIELD9R, item.getDateInserted());

		database.update(MainHelper.DATABASE_TABLER, valuesToPut, whereClause,
				whereArgs);

	}

	public void deleteEntry(ReminderItem item) {

		database.delete(MainHelper.DATABASE_TABLER, MainHelper.KEY + "= "
				+ item.getKey(), null);

	}

	public int getLastItem() {
		Cursor cursor = database.query(MainHelper.DATABASE_TABLER,
				allColumns, null, null, null, null, null);
		cursor.moveToLast();
		return (cursor.getInt(cursor.getColumnIndex(MainHelper.KEY)));

	}

//	public Cursor getLastItem(String vehicle, String maintItem) {
//
//		String whereClause = MainHelper.FIELD1 + "= ? and "
//				+ MainHelper.FIELD2 + " =?";
//
//		String[] whereArgs = { vehicle, maintItem };
//		String orderBy = MainHelper.FIELD7 + " desc";
//
//		Cursor cursor = database.query(MainHelper.DATABASE_TABLE,
//				allColumns, whereClause, whereArgs, null, null, orderBy);
//
//		boolean isEmpty = cursor.moveToFirst();
//
//		if (isEmpty) {
//			// System.out.println("cursor is empty " + isEmpty);
//			return (cursor);
//		} else {
//			return null;
//		}
//
//	}

}
