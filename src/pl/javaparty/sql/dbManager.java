package pl.javaparty.sql;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;

import pl.javaparty.items.Concert;
import pl.javaparty.items.Concert.AgencyName;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class dbManager extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "baza.db";
	private static SQLiteDatabase database;
	public final static String CONCERTS_TABLE = "Concerts";
	public final static String FAVOURITES_TABLE = "Favourites";
	public final static String HASHCODES_TABLE = "Hashcodes";
	public Thread download;

	private static String CreateConcertTable =
			"CREATE TABLE " + CONCERTS_TABLE + "(" +
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
			"CREATE TABLE " + HASHCODES_TABLE + "(" +
					"AGENCY TEXT PRIMARY KEY," +
					"HASH INTEGER)";

	// nowa tabela zawierajï¿½ca ulubione koncerty
	private static String CreateFavouriteTable =
			"CREATE TABLE " + FAVOURITES_TABLE + "(" +
					"ID INTEGER)";

	public dbManager(Context context) {
		super(context, DATABASE_NAME, null, 1);
		database = getWritableDatabase();
	}

	public void close() {
		database.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i("Rafal", "W EXECSQL");
		db.execSQL(CreateConcertTable);
		db.execSQL(CreateHashcodeTable);
		db.execSQL(CreateFavouriteTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
	
	public void deleteBase()
	{
		database.delete("Concerts", "'1'='1'", null);
	}

	public void addConcert(String artistName, String city, String spot,
			int day, int month, int year, String agency, String url) {
		if (!contains(artistName, city, spot, day, month, year)) {
			ContentValues cv = new ContentValues();
			cv.put("ARTIST", artistName);
			cv.put("CITY", city);
			cv.put("SPOT", spot);
			cv.put("DAY", day);
			cv.put("MONTH", month);
			cv.put("YEAR", year);
			cv.put("AGENCY", agency);
			cv.put("URL", url);
			database.insertOrThrow("Concerts", null, cv);
		}
		else
			System.out.println("Nie dodano " + artistName + city);
	}

	public boolean contains(String artistName, String city, String spot, int day, int month, int year) {
		boolean contains = false;
		int h1 = (artistName + city + spot + String.valueOf(day) + String.valueOf(month) + String.valueOf(year)).hashCode();
		String[] columns = { "ARTIST", "CITY", "SPOT", "DAY", "MONTH", "YEAR" };
		Cursor c = database.query(CONCERTS_TABLE, columns, null, null, null, null, null);
		int h2;
		while (c.moveToNext() && !contains) {
			h2 = (c.getString(0) + c.getString(1) + c.getString(2) + String.valueOf(c.getInt(3)) +
					String.valueOf(c.getInt(4)) + String.valueOf(c.getInt(5))).hashCode();
			contains = h1 == h2;
		}
		c.close();
		return contains;
	}

	public boolean contains(int id) {
		boolean contains = false;
		String[] columns = { "ID" };
		Cursor c = database.query(FAVOURITES_TABLE, columns, null, null, null, null, null);
		int h2;
		while (c.moveToNext() && !contains) {
			h2 = c.getInt(0);
			contains = id == h2;
		}
		c.close();
		return contains;
	}

	public Cursor getData() {
		String[] columns = { "ORD", "ARTIST", "CITY", "SPOT", "DAY", "MONTH", "YEAR", "AGENCY", "URL" };
		// dodane pobieranie ID na poczï¿½tku
		Cursor c = database.query(CONCERTS_TABLE, columns, null, null, null, null, null);
		return c;
	}

	public int getSize(String table) // pobiera ilosc rekordow w bazie
	{
		int count = -1;
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT count(*) FROM " + table, null);
		cursor.moveToFirst();
		count = cursor.getInt(0);
		cursor.close();
		return count;
	}

	public boolean agencyHashCodeExists(String agencyName) {
		String column[] = { "AGENCY" };
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(HASHCODES_TABLE, column, null, null, null, null, null);
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
			Cursor c = db.query(HASHCODES_TABLE, column, null, null, null, null, null);
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
			db.insertOrThrow(HASHCODES_TABLE, null, cv);
		}
		else {
			String update = "UPDATE " + HASHCODES_TABLE + " SET HASH = '" + hash + "' WHERE AGENCY = '" + agencyName + "'";
			db.execSQL(update);
		}
	}

	public void deleteOldConcerts() // wypierdalator starch koncertów
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
		Cursor c = database.query(CONCERTS_TABLE, new String[] { "ORD" }, selection, selectionArgs, null, null, null);
		while (c.moveToNext())
		{
			Log.i("Deleter", c.getString(0));
		}
		int deleted = database.delete(CONCERTS_TABLE, selection, selectionArgs);
		Log.i("Deleter", "Wyjebano " + deleted + " przestarzalych koncertow!");
	}

	private String[] universalGetter3000(String columnName) {
		String[] column = { columnName };
		Cursor c = database.query(CONCERTS_TABLE, column, null, null, null, null, null);

		int size = c.getCount();
		String[] array = new String[size];
		for (int i = 0; c.moveToNext(); i++)
			array[i] = c.getString(0);
		c.close();
		return array;
	}

	private String[] deleteDuplicates(String[] arr) {
		HashSet<String> hashSet = new HashSet<String>(Arrays.asList(arr));
		String[] res = new String[hashSet.size()];
		hashSet.toArray(res);
		return res;
	}

	public void deleteDB(Context context)
	{
		database.close();
		context.deleteDatabase(DATABASE_NAME);
		Log.i("DB", "Baza usuniêta");
		new dbManager(context);
	}

	public String[] getArtists() {
		return deleteDuplicates(universalGetter3000("ARTIST"));
	}

	public String[] getCities() {
		return deleteDuplicates(universalGetter3000("CITY"));
	}

	public String getArtist(int ID) {
		return fieldGetter(ID, "ARTIST");
	}

	public String getCity(int ID) {
		return fieldGetter(ID, "CITY");
	}

	public String getSpot(int ID) {
		return fieldGetter(ID, "SPOT");
	}

	public String getUrl(int ID) {
		return fieldGetter(ID, "URL");
	}

	public String getDate(int ID) {
		String[] columns = { "ORD", "DAY", "MONTH", "YEAR" };
		Cursor c = database.query(CONCERTS_TABLE, columns, "ORD = " + ID, null, null, null, null);
		c.moveToFirst();
		int day = c.getInt(1);
		String dayS = day < 10 ? "0" + day : "" + day;
		int month = c.getInt(2);
		String monthS = month < 10 ? "0" + month : "" + month;
		String res = dayS + "." + monthS + "." + c.getInt(3);
		c.close();
		return res;
	}

	/**
	 * metoda dodajaca id ulubionego koncertu do tabeli Favourite
	 */
	public void addFavouriteConcert(int id)
	{
		if (!contains(id)) {
			ContentValues cv = new ContentValues();
			cv.put("ID", id);
			Log.i("FAV", "Wrzucomo id ulubionego: " + id);
			database.insertOrThrow(FAVOURITES_TABLE, null, cv);
		}
	}

	/**
	 * Metoda uzyskujï¿½ca ulubione koncerty z tabeli Favourite
	 * 
	 * @return tablica concertow awierajaca ulubione koncerty
	 */
	public Concert[] getAllFavouriteConcert()
	{
		String[] columns = { "ID" };
		Cursor c = database.query(FAVOURITES_TABLE, columns, null, null, null, null, null);
		Concert[] concerts = new Concert[getSize(FAVOURITES_TABLE)];
		for (int i = 0; c.moveToNext(); i++)
			concerts[i] = getConcertsByID(c.getInt(0));
		c.close();

		return concerts;
	}

	public boolean isConcertFavourite(int id)
	{
		String[] columns = { "ID" };
		boolean favourite = false;
		Cursor c = database.query(FAVOURITES_TABLE, columns, null, null, null, null, null);
		for (int i = 0; c.moveToNext(); i++)
			if (c.getInt(0) == id)
				favourite = true;
		c.close();

		return favourite;
	}

	private String fieldGetter(int ID, String fieldName) {
		String[] columns = { "ORD", fieldName };
		Cursor c = database.query(CONCERTS_TABLE, columns, "ORD = " + ID, null, null, null, null);
		String res = c.moveToFirst() ? c.getString(1) : null;
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

	private Concert[] getConcertsBy(String condition) {
		String[] columns = { "ORD", "ARTIST", "CITY", "SPOT", "DAY", "MONTH", "YEAR", "AGENCY", "URL" };
		Cursor c = database.query(CONCERTS_TABLE, columns, condition, null, null, null, "YEAR,MONTH,DAY");
		Concert[] concerts = new Concert[c.getCount()];
		for (int i = 0; c.moveToNext(); i++)
			concerts[i] = new Concert(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
					c.getInt(4), c.getInt(5), c.getInt(6), getAgency(c.getString(7)), c.getString(8));
		c.close();
		return concerts;
	}

	public Concert[] getAllConcerts() {
		return getConcertsBy(null);
	}

	public Concert[] getConcertsByArtist(String artist) {
		String condition = "ARTIST = '" + artist + "'";
		return getConcertsBy(condition);
	}

	public Concert[] getConcertsByCity(String city) {
		String condition = "CITY = '" + city + "'";
		return getConcertsBy(condition);
	}

	public Concert[] getConcertsByDate(int day, int month, int year) {
		String condition = "DAY = " + day + " AND MONTH = " + month + " AND YEAR = " + year;
		return getConcertsBy(condition);
	}

	public Concert[] getConcertsByDateRange(int dF, int mF, int yF, int dT, int mT, int yT) {
		String[] columns = { "ORD", "ARTIST", "CITY", "SPOT", "DAY", "MONTH", "YEAR", "AGENCY", "URL" };
		String condition = "(YEAR > ? OR (YEAR = ? AND MONTH > ?) OR (YEAR = ? AND MONTH = ? AND DAY >= ?))"
				+ "AND (YEAR < ? OR (YEAR = ? AND MONTH < ?) OR (YEAR = ? AND MONTH = ? AND DAY <= ?))";
		String[] selectionArgs = {
				String.valueOf(yF),
				String.valueOf(yF),
				String.valueOf(mF),
				String.valueOf(yF),
				String.valueOf(mF),
				String.valueOf(dF),
				String.valueOf(yT),
				String.valueOf(yT),
				String.valueOf(mT),
				String.valueOf(yT),
				String.valueOf(mT),
				String.valueOf(dT)
		};
		Cursor c = database.query(CONCERTS_TABLE, columns, condition, selectionArgs, null, null, null);
		Concert[] concerts = new Concert[c.getCount()];
		for (int i = 0; c.moveToNext(); i++) {
			concerts[i] = new Concert(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
					c.getInt(4), c.getInt(5), c.getInt(6), getAgency(c.getString(7)), c.getString(8));
		}
		c.close();
		return concerts;
	}

	private Concert getConcertsByID(int id) {
		String condition = "ORD = " + id;
		return getConcertsBy(condition)[0]; // id jest unuikalne wiec bedzie to zawsze tablica jednoelementowa
	}

}