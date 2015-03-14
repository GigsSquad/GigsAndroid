package pl.javaparty.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import pl.javaparty.items.Concert;
import pl.javaparty.items.Concert.AgencyName;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;

public class dbManager extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "baza.db";
    private static SQLiteDatabase database;
    public final static String CONCERTS_TABLE = "Concerts";
    public final static String FAVOURITES_TABLE = "Favourites";

    private static String CreateConcertTable =
            "CREATE TABLE " + CONCERTS_TABLE + "(" +
                    "ORD INTEGER PRIMARY KEY," +
                    "ARTIST TEXT," +
                    "CITY TEXT," +
                    "SPOT TEXT," +
                    "DAY INTEGER," +
                    "MONTH INTEGER," +
                    "YEAR INTEGER," +
                    "AGENCY TEXT," +
                    "URL TEXT," +
                    "LAT TEXT," +
                    "LON TEXT)";

    // nowa tabela zawieraj�ca ulubione koncerty
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
        db.execSQL(CreateConcertTable);
        db.execSQL(CreateFavouriteTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }

    public void deleteTables() {
        database.delete(CONCERTS_TABLE, null, null);
        database.delete(FAVOURITES_TABLE, null, null);
        Log.i("DB", "Tabele usunięte");
    }

    public void deleteDatabase(Context context) {
        database.close();
        context.deleteDatabase(DATABASE_NAME);
        new dbManager(context);
        Log.i("DB", "Baza usunięta i stworzona na nowo");
    }


    public void addConcert(long id, String artistName, String city, String spot,
                           int day, int month, int year, String agency, String url, String lat, String lon) {
        if (!contains(artistName, city, spot, day, month, year)) {
            ContentValues cv = new ContentValues();
            Log.d("DB2", id + " " + artistName);
            cv.put("ORD", id);
            cv.put("ARTIST", artistName);
            cv.put("CITY", city);
            cv.put("SPOT", spot);
            cv.put("DAY", day);
            cv.put("MONTH", month);
            cv.put("YEAR", year);
            cv.put("AGENCY", agency);
            cv.put("URL", url);
            cv.put("LAT", lat);
            cv.put("LON", lon);
            database.insertOrThrow("Concerts", null, cv);
        }
    }

    public boolean contains(String artistName, String city, String spot, int day, int month, int year) {
        boolean contains = false;
        int h1 = (artistName + city + spot + String.valueOf(day) + String.valueOf(month) + String.valueOf(year)).hashCode();
        String[] columns = {"ARTIST", "CITY", "SPOT", "DAY", "MONTH", "YEAR"};
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
        String[] columns = {"ID"};
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
        String[] columns = {"ORD", "ARTIST", "CITY", "SPOT", "DAY", "MONTH", "YEAR", "AGENCY", "URL", "LAT", "LON"};
        // dodane pobieranie ID na pocz�tku
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

    public void deleteOldConcerts() // wypierdalator starch koncert�w //<- L
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
        Cursor c = database.query(CONCERTS_TABLE, new String[]{"ORD"}, selection, selectionArgs, null, null, null);
        while (c.moveToNext()) {
            Log.i("Deleter", c.getString(0));
        }
        int deleted = database.delete(CONCERTS_TABLE, selection, selectionArgs);
        Log.i("Deleter", "Wyjebano " + deleted + " przestarzalych koncertow!");
    }

    private String[] universalGetter3000(String columnName, String condition) {
        String[] column = {columnName};
        Cursor c = database.query(CONCERTS_TABLE, column, condition, null, null, null, null);

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

    public String[] getArtists(String condition) {
        return deleteDuplicates(universalGetter3000("ARTIST", condition));// "'1'='0' OR AGENCY = 'GOAHEAD'"));
    }

    public String[] getCities(String condition) {
        return deleteDuplicates(universalGetter3000("CITY", condition));
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

    public String getAgency(int ID) {
        return fieldGetter(ID, "AGENCY");
    }

    public String getLat(int ID) {
        return fieldGetter(ID, "LAT");
    }

    public String getLon(int ID) {
        return fieldGetter(ID, "LON");
    }

    public String getDate(int ID) {
        String[] columns = {"ORD", "DAY", "MONTH", "YEAR"};
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

    public int[] dateArray(int ID) {
        String[] columns = {"ORD", "DAY", "MONTH", "YEAR"};
        Cursor c = database.query(CONCERTS_TABLE, columns, "ORD = " + ID, null, null, null, null);
        c.moveToFirst();
        int day = c.getInt(1);
        int month = c.getInt(2);
        int year = c.getInt(3);
        c.close();
        return new int[]{day, month, year};
    }

    /**
     * metoda dodajaca id ulubionego koncertu do tabeli Favourite
     */
    public void addFavouriteConcert(int id) {
        if (!contains(id)) {
            ContentValues cv = new ContentValues();
            cv.put("ID", id);
            Log.i("FAV", "Wrzucomo id ulubionego: " + id);
            database.insertOrThrow(FAVOURITES_TABLE, null, cv);
        }
    }

    public void removeFavouriteConcert(int id) {
        String selection = new String("ID = " + id);
        //	int deleted = database.delete(CONCERTS_TABLE, selection, selectionArgs);
        database.delete(FAVOURITES_TABLE, selection, null);
    }

    /**
     * Metoda uzyskuj�ca ulubione koncerty z tabeli Favourite
     *
     * @return tablica concertow awierajaca ulubione koncerty
     */
    public Concert[] getAllFavouriteConcert() {
        String[] columns = {"ID"};
        Cursor c = database.query(FAVOURITES_TABLE, columns, null, null, null, null, null);
        Concert[] concerts = new Concert[getSize(FAVOURITES_TABLE)];
        for (int i = 0; c.moveToNext(); i++) {
            concerts[i] = getFavConcertByID(c.getInt(0));
            //c.moveToNext();
        /*	int id = c.getInt(0);
            String condition = "ID = " + id;
			concerts[i] = getConcertsBy(condition)[0];
			*/
        }

        c.close();

        return concerts;
    }

    public boolean isConcertFavourite(int id) {
        String[] columns = {"ID"};
        boolean favourite = false;
        Cursor c = database.query(FAVOURITES_TABLE, columns, null, null, null, null, null);
        for (int i = 0; c.moveToNext(); i++)
            // TODO wut? Czo to za niewykorzystane i?
            if (c.getInt(0) == id)
                favourite = true;
        c.close();

        return favourite;
    }

    private String fieldGetter(int ID, String fieldName) {
        String[] columns = {"ORD", fieldName};
        Cursor c = database.query(CONCERTS_TABLE, columns, "ORD = " + ID, null, null, null, null);
        String res = c.moveToFirst() ? c.getString(1) : null;
        c.close();
        return res;
    }

    private AgencyName getAgency(String s) {
        AgencyName agency = null;
        AgencyName[] agencyNames = AgencyName.values();
        for (int i = 0; i < agencyNames.length && agency == null; i++) {
            if (agencyNames[i].name().equals(s))
                agency = agencyNames[i];
        }
        return agency;
    }

    private Concert[] getConcertsBy(String condition) {
        String[] columns = {"ORD", "ARTIST", "CITY", "SPOT", "DAY", "MONTH", "YEAR", "AGENCY", "URL", "LAT", "LON"};
        Cursor c = database.query(CONCERTS_TABLE, columns, condition, null, null, null, "YEAR,MONTH,DAY");
        Concert[] concerts = new Concert[c.getCount()];
        for (int i = 0; c.moveToNext(); i++) {
            concerts[i] = new Concert(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
                    c.getInt(4), c.getInt(5), c.getInt(6), getAgency(c.getString(7)), c.getString(8), c.getString(9), c.getString(10));
        }
        c.close();
        return concerts;
    }

    public Concert[] getAllConcerts(String filter) {
        return getConcertsBy(filter);
    }

    public Concert[] getConcertsByArtist(String artist, String filter) {
        String condition = "ARTIST = '" + artist + "' AND ( " + filter + " )";
        return getConcertsBy(condition);
    }

    public Concert[] getConcertsByCity(String city, String filter) {
        String condition = "CITY = '" + city + "' AND ( " + filter + " )";
        return getConcertsBy(condition);
    }

    public Concert getConcertByID(int ID) {
        String condition = "ORD = " + ID;
        return getConcertsBy(condition)[0];
    }


    public Concert[] getPastConcerts(String filter) {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        int[] date = new int[]{yesterday.get(Calendar.DAY_OF_MONTH), yesterday.get(Calendar.MONTH) + 1, yesterday.get(Calendar.YEAR)};
        return getConcertsByDateRange(0, 0, 0, date[0], date[1], date[2], filter);
    }

    public Concert[] getFutureConcerts(String filter) {
        Calendar today = Calendar.getInstance();
        int[] date = new int[]{today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.MONTH) + 1, today.get(Calendar.YEAR)};
        return getConcertsByDateRange(date[0], date[1], date[2], 32, 13, 3000, filter);
    }

	/*
	 * public Concert[] getConcertsByDate(int day, int month, int year) { String condition = "DAY = " + day +
	 * " AND MONTH = " + month + " AND YEAR = " + year; return getConcertsBy(condition); }
	 */

    public Concert[] getConcertsByDateRange(int dF, int mF, int yF, int dT, int mT, int yT, String filter) {
        String[] columns = {"ORD", "ARTIST", "CITY", "SPOT", "DAY", "MONTH", "YEAR", "AGENCY", "URL", "LAT", "LON"};
        String condition = "(YEAR > ? OR (YEAR = ? AND MONTH > ?) OR (YEAR = ? AND MONTH = ? AND DAY >= ?))"
                + "AND (YEAR < ? OR (YEAR = ? AND MONTH < ?) OR (YEAR = ? AND MONTH = ? AND DAY <= ?)) "
                + "AND (" + filter + ")";
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
        Cursor c = database.query(CONCERTS_TABLE, columns, condition, selectionArgs, null, null, "YEAR,MONTH,DAY");
        Concert[] concerts = new Concert[c.getCount()];
        for (int i = 0; c.moveToNext(); i++) {
            concerts[i] = new Concert(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
                    c.getInt(4), c.getInt(5), c.getInt(6), getAgency(c.getString(7)), c.getString(8), c.getString(9), c.getString(10));
            Log.d("DBMGR", concerts[i].getID() + " " + concerts[i].getArtist());
        }
        c.close();


        return concerts;
    }

    private Concert getFavConcertByID(int id) {
        String condition = "ORD = " + id;
        return getConcertsBy(condition)[0]; // id jest unuikalne wiec bedzie to zawsze tablica jednoelementowa
    }

}