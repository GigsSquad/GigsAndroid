package sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbManager extends SQLiteOpenHelper {

	private static String CreateTable =
		"CREATE TABLE Concerts(" +
				"ORD INTEGER PRIMARY KEY AUTOINCREMENT,"+
				"ARTIST TEXT,"+
				"DAY INTEGER,"+
				"MONTH INTEGER,"+
				"YEAR INTEGER,"+
				"CITY TEXT,"+
				"SPOT TEXT,"+
				"AGENCY TEXT,"+
				"URL TEXT)";
	
	public dbManager(Context context){
		super(context,"baza.db",null,1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CreateTable);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub	
	}
	
	public void addConcert(String artistName,int day,int month,int year,
			String city,String spot,String agency,String url){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("ARTIST",artistName);
		cv.put("DAY",day);
		cv.put("MONTH", month);
		cv.put("YEAR",year);	 
		cv.put("CITY", city);
		cv.put("SPOT", spot);
		cv.put("AGENCY", agency);
		cv.put("URL", url);
		db.insertOrThrow("Concerts",null,cv);
		db.close();
	}
	
	public Cursor getData(){
		String[] columns = {"ARTIST","DAY","MONTH","YEAR","CITY","SPOT","AGENCY","URL"};
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query("Concerts", columns, null,null,null,null,null);
		return c;
	}
	
	
}