package pl.javaparty.sql;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;

import pl.javaparty.concertmanager.Concert;
import pl.javaparty.concertmanager.Concert.AgencyName;
import pl.javaparty.jsoup.JSoupDownloader;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class dbManager extends SQLiteOpenHelper implements Serializable{

	private static final long serialVersionUID = -1641692594177112448L;
	public final static String DATABASE_NAME = "baza.db";
	private static SQLiteDatabase database;

	private static String CreateConcertTable =
			"CREATE TABLE Concerts(" +
					"ORD INTEGER PRIMARY KEY AUTOINCREMENT," +
					"ARTIST TEXT," +
					"CITY TEXT," +
					"SPOT TEXT," +
					"DAY INTEGER," +
					"MONTH INTEGER," +
					"YEAR INTEGER," +
					"AGENCY TEXT," +
					"URL TEXT)";

	// tablica hash odpowiada za hashcode najnowszego eventu danej agencji
	private static String CreateHashcodeTable =
			"CREATE TABLE Hashcodes(" +
					"AGENCY TEXT PRIMARY KEY," +
					"HASH INTEGER)"; 
	
	public Thread download;

	public dbManager(Context context) {
		super(context, DATABASE_NAME, null, 1);
		database = getWritableDatabase();
	}
	
	public void close(){
		database.close();
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
	
	public void updateDatabase()
	{
		download = new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				downloadConcerts();
			}
		});
		download.start();
	}
	
	private synchronized void downloadConcerts()
	{
		JSoupDownloader js = new JSoupDownloader(this);
		try
		{
			js.getData();
			deleteOldConcerts();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addConcert(String artistName,String city,String spot,
			int day,int month,int year,String agency,String url){
		if(!contains(artistName,city,spot,day,month,year)){
			ContentValues cv = new ContentValues();
			cv.put("ARTIST",artistName);
			cv.put("CITY", city);
			cv.put("SPOT", spot);
			cv.put("DAY",day);
			cv.put("MONTH", month);
			cv.put("YEAR",year);	 
			cv.put("AGENCY", agency);
			cv.put("URL", url);
			database.insertOrThrow("Concerts",null,cv);
		}
		else
			System.out.println("Nie dodano "+artistName+city);
	}
	
	public boolean contains(String artistName,String city,String spot,int day,int month,int year){
		boolean contains = false;
		int h1 = (artistName+city+spot+String.valueOf(day)+String.valueOf(month)+String.valueOf(year)).hashCode();
		String[] columns = {"ARTIST","CITY","SPOT","DAY","MONTH","YEAR"};
		Cursor c = database.query("Concerts",columns,null,null,null,null,null);
		int h2;
		while(c.moveToNext()&&!contains){
			h2 = (c.getString(0)+c.getString(1)+c.getString(2)+String.valueOf(c.getInt(3))+
					String.valueOf(c.getInt(4))+String.valueOf(c.getInt(5))).hashCode();
			contains = h1==h2;
		}
		c.close();
		return contains;
	}

	public Cursor getData() {
		String[] columns = { "ORD", "ARTIST", "CITY", "SPOT", "DAY", "MONTH", "YEAR", "AGENCY", "URL" };
		// dodane pobieranie ID na pocz¹tku
		Cursor c = database.query("Concerts", columns, null, null, null, null, null);
		return c;
	}

	public int getSize() //pobiera ilosc rekordow w bazie
	{
		int count = -1;
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT count(*) FROM Concerts", null);
		cursor.moveToFirst();
		count = cursor.getInt(0);
		cursor.close();
		return count;
	}

	public ArrayList<String> getArtist()
	{
		ArrayList<String> artist = new ArrayList<String>();
		Cursor c = getData();
		while (c.moveToNext())
			artist.add(c.getString(1));
		return artist;
	}

	public boolean agencyHashCodeExists(String agencyName) {
		String column[] = { "AGENCY" };
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query("Hashcodes", column, null, null, null, null, null);
		boolean check = false;
		while (c.moveToNext() && !check) {
			if (c.getString(0).equals(agencyName))
				check = true;
		}
		c.close();
		return check;
	}

	public int getHash(String agencyName) {
		int res = 0;
		if (agencyHashCodeExists(agencyName)) {
			SQLiteDatabase db = getReadableDatabase();
			String[] column = { "AGENCY", "HASH" };
			Cursor c = db.query("Hashcodes", column, null, null, null, null, null);
			while (c.moveToNext()) {
				if (c.getString(0).equals(agencyName)) {
					res = c.getInt(1);
					break;
				}
			}
			c.close();
		}
		return res;
	}

	public void updateHash(String agencyName, int hash) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		if (!agencyHashCodeExists(agencyName)) {
			cv.put("AGENCY", agencyName);
			cv.put("HASH", hash);
			db.insertOrThrow("Hashcodes", null, cv);
		}
		else {
			String update = "UPDATE Hashcodes SET HASH = '" + hash + "' WHERE AGENCY = '" + agencyName + "'";
			db.execSQL(update);
		}
	}
	
	public void deleteOldConcerts()
	{
		Log.i("Deleter", "Szukam starych koncertow");
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);
		String selection = new String("YEAR < ? OR (YEAR = ? AND MONTH < ?) OR (YEAR = ? AND MONTH = ? AND DAY < ?)");
		String selectionArgs[] = new String[] 
				{
				String.valueOf(year),
				String.valueOf(year),
				String.valueOf(month),
				String.valueOf(year),
				String.valueOf(month),
				String.valueOf(day),
				};
		Cursor c = database.query("Concerts", new String[]{"ORD"}, selection, selectionArgs, null, null, null);
		while(c.moveToNext())
		{
			Log.i("Deleter", c.getString(0));
		}
		int deleted = database.delete("Concerts", selection, selectionArgs);
		Log.i("Deleter", "Wyjebano " + deleted + " przestarzalych koncertow!");
	}
	
	private String[] universalGetter3000(String columnName){
		String [] column = {columnName};
		Cursor c = database.query("Concerts",column,null,null,null,null,null);
		int size = c.getCount();
		String[] array = new String[size];
		for(int i =0; c.moveToNext();i++)
			array[i] = c.getString(0);
		c.close();
		return array;
	}
	
	private String[] deleteDuplicates(String[] arr){
		HashSet<String> hashSet = new HashSet<String>(Arrays.asList(arr));
		String[] res = new String[hashSet.size()];
		hashSet.toArray(res);
		return res;
	}
	
	public String[] getArtists(){
		return deleteDuplicates(universalGetter3000("ARTIST"));
	}
	
	public String[] getCities(){
		return deleteDuplicates(universalGetter3000("CITY"));
	}
	
	public String getArtist(int ID){
		return fieldGetter(ID,"ARTIST");
	}
	
	public String getCity (int ID){
		return fieldGetter(ID,"CITY");
	}
	
	public String getSpot (int ID){
		return fieldGetter(ID,"SPOT");	
	}
	
	public String getUrl(int ID) {
		return fieldGetter(ID,"URL");
	}
	
	public String getDate(int ID){
		String [] columns = {"ORD","DAY","MONTH","YEAR"};
		Cursor c = database.query("Concerts", columns, "ORD = "+ID, null,null,null,null);
		c.moveToFirst();
		int day = c.getInt(1);
		String dayS = day<10? "0"+day : ""+day;
		int month = c.getInt(2);
		String monthS = month<10? "0"+month : ""+month;
		String res = dayS+"."+monthS+"."+c.getInt(3);
		c.close();
		return res;
	}
	
	private String fieldGetter (int ID, String fieldName){
		String [] columns = {"ORD",fieldName};
		Cursor c = database.query("Concerts", columns, "ORD = "+ID,null,null,null,null);
		String res = c.moveToFirst()? c.getString(1) : null;
		c.close();
		return res;
	}
	
	private AgencyName getAgency(String s) {
		AgencyName agency = null;
		if (s.equals("GOAHEAD"))
			agency = AgencyName.GOAHEAD;
		else if (s.equals("ALTERART"))
			agency = AgencyName.ALTERART;
		return agency;
	}
	
	private Concert[] getConcertsBy(String condition){
		try
		{
			if(download!=null)
				download.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		String[] columns = { "ORD", "ARTIST", "CITY", "SPOT", "DAY", "MONTH", "YEAR", "AGENCY", "URL" };
		Cursor c = database.query("Concerts", columns, condition, null, null, null,"YEAR,MONTH,DAY");
		Concert[] concerts =  new Concert[c.getCount()];
		for(int i=0; c.moveToNext(); i++){
			concerts[i] = new Concert(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
					c.getInt(4), c.getInt(5), c.getInt(6), getAgency(c.getString(7)), c.getString(8));
		}
		c.close();
		return concerts;
	}
	
	public Concert [] getAllConcerts(){
		return getConcertsBy(null);
	}
	
	public Concert[] getConcertsByArtist(String artist){
		String condition = "ARTIST = '" + artist +"'";
		return getConcertsBy(condition);
	}
	
	public Concert[] getConcertsByCity(String city){
		String condition = "CITY = '"+city+"'";
		return getConcertsBy(condition);
	}
	
	public Concert[] getConcertsByDate(int day,int month,int year){
		String condition = "DAY = "+day+" AND MONTH = "+month+" AND YEAR = "+year;
		return getConcertsBy(condition);
	}


}