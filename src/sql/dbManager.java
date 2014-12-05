package sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.style.SuperscriptSpan;

public class dbManager extends SQLiteOpenHelper {
	
	public final static String DATABASE_NAME = "baza.db";
	
	private static String CreateConcertTable =
		"CREATE TABLE Concerts(" +
				"ORD INTEGER PRIMARY KEY AUTOINCREMENT,"+
				"ARTIST TEXT,"+
				"CITY TEXT,"+
				"SPOT TEXT,"+
				"DAY INTEGER,"+
				"MONTH INTEGER,"+
				"YEAR INTEGER,"+
				"AGENCY TEXT,"+
				"URL TEXT)";
	
	//tablica hash odpowiada za hashcode najnowszego eventu danej agencji
	private static String CreateHashcodeTable =
			"CREATE TABLE Hashcodes("+
					"AGENCY TEXT PRIMARY KEY,"+
					"HASH INTEGER)";
	
	public dbManager(Context context){
		super(context,DATABASE_NAME,null,1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CreateConcertTable);
		db.execSQL(CreateHashcodeTable);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub	
	}
	
	public void addConcert(String artistName,String city,String spot,
			int day,int month,int year,String agency,String url){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("ARTIST",artistName);
		cv.put("CITY", city);
		cv.put("SPOT", spot);
		cv.put("DAY",day);
		cv.put("MONTH", month);
		cv.put("YEAR",year);	 
		cv.put("AGENCY", agency);
		cv.put("URL", url);
		db.insertOrThrow("Concerts",null,cv);
	}
	
	public Cursor getData(){
		String[] columns = {"ARTIST","CITY","SPOT","DAY","MONTH","YEAR","AGENCY","URL"};
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query("Concerts", columns, null,null,null,null,null);
		return c;
	}
	
	public boolean agencyHashCodeExists (String agencyName){
		String column[] = {"AGENCY"};
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query("Hashcodes", column, null,null,null,null,null);
		boolean check = false;
		while(c.moveToNext() && !check){
			if(c.getString(0).equals(agencyName))
				check = true;
		}
		c.close();
		return check;
	}
	
	public int getHash(String agencyName){
		int res = 0;
		if(agencyHashCodeExists(agencyName)){
			SQLiteDatabase db = getReadableDatabase();
			String [] column = {"AGENCY","HASH"};
			Cursor c = db.query("Hashcodes", column, null,null,null,null,null);
			while(c.moveToNext()){
				if (c.getString(0).equals(agencyName)){
						res = c.getInt(1);
						break;
				}
			}
			c.close();
		}
		return res;
	}
	
	public void updateHash(String agencyName,int hash){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		if(!agencyHashCodeExists(agencyName)){
			cv.put("AGENCY",agencyName);
			cv.put("HASH", hash);
			db.insertOrThrow("Hashcodes",null,cv);
		}
		else{
			String update = "UPDATE Hashcodes SET HASH = '"+hash+"' WHERE AGENCY = '"+agencyName+"'";
			db.execSQL(update);
		}
	}
}