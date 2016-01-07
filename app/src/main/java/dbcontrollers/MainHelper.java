package dbcontrollers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import utils.FileUtils;

public class MainHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "MainDB";
	public static final String DATABASE_TABLE = "MainLog";
	public static final int DATABASE_VERSION = 12;

	public static final String KEY = "_id";
	public static final String FIELD1 = "Vehicle";
	public static final String FIELD2 = "MaintElem";// FUEL ...
	public static final String FIELD3 = "MaintType"; // REPLACE ; Maintain ;
														// OTHER ;
	public static final String FIELD4 = "FuelAmount";
	public static final String FIELD5 = "Consumption";
	public static final String FIELD6 = "Date";
	public static final String FIELD7 = "Odometer";
	public static final String FIELD8 = "Details";
	public static final String FIELD9 = "MileageType";
	public static final String FIELD10 = "Cash";
	public final String Tag = "MainLogHelper";

	// fields of database_table remlog :

	public static final String DATABASE_TABLER = "RemLog";
	public static final String FIELD1R = "Vehicle";
	public static final String FIELD2R = "MaintElem";// FUEL ...
	public static final String FIELD3R = "MaintType"; // REPLACE ; Maintain ;
														// OTHER ;
	public static final String FIELD4R = "ReminderType";
	public static final String FIELD5R = "Interval";
	public static final String FIELD5Ra = "IntervalSize";
	public static final String FIELD6R = "LastInterval";
	public static final String FIELD7R = "NextInterval";
	public static final String FIELD8R = "Details";
	public static final String FIELD9R = "DateInserted";

	public MainHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + DATABASE_TABLE + " ( " + KEY
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + FIELD1
				+ " TEXT not null,  " + FIELD2 + " TEXT not null," + FIELD3
				+ " TEXT not null," + FIELD4 + " REAL not null DEFAULT 0,"
				+ FIELD5 + " REAL not null DEFAULT 0 , " + FIELD6
				+ " TEXT not null, " + FIELD7 + " INT not null DEFAULT 0, "
				+ FIELD8 + " TEXT not null, " + FIELD9
				+ " INT not null DEFAULT 0," + FIELD10
				+ " REAL not null DEFAULT 0 );");

		db.execSQL("CREATE TABLE " + DATABASE_TABLER + " ( " + KEY
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + FIELD1R
				+ " TEXT not null,  " + FIELD2R + " TEXT not null," + FIELD3R
				+ " TEXT not null," + FIELD4R + " int not null DEFAULT 0,"
				+ FIELD5R + " TEXT not null , " + FIELD5Ra
				+ " TEXT not null , " + FIELD6R + " TEXT not null, " + FIELD7R
				+ " TEXT not null , " + FIELD8R + " TEXT not null ," + FIELD9R
				+ " TEXT not null);");

	}

	/**
	 * Copies the database file at the specified location over the current
	 * internal application database.
	 * */
	public boolean copyDatabase(String fromDbPath, String toDbPath)
			throws IOException {

		// Close the SQLiteOpenHelper so it will commit the created empty
		// database to internal storage.
		close();
		File newDb = new File(fromDbPath);
		File oldDb = new File(toDbPath);
		if (newDb.exists()) { // import
			FileUtils.copyFile(new FileInputStream(newDb),
					new FileOutputStream(oldDb));
			// Access the copied database so SQLiteHelper will cache it and mark
			// it as created.
			getWritableDatabase().close();
			return true;
		} else { // export
			FileUtils.copyFile(new FileInputStream(newDb),
					new FileOutputStream(oldDb));

			return false;

		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		Log.w(DATABASE_NAME, "Database will be updated from verision "
				+ oldVersion + " to " + newVersion + ".");
		
		db.execSQL("update MainLog set date= case when substr(date,7,1)='/' then substr(date,1,5)||'0'||substr(date,6,length(date) -5) else date end  where length(date)<10;"  );
		db.execSQL("update MainLog set date =  substr(date,1,8)||'0'||substr(date,9,length(date) -8) where length(date)<10;");		
		db.execSQL("UPDATE  " + DATABASE_TABLE + "  set date = replace(date,'/','-'); ");		
		
	}

}
