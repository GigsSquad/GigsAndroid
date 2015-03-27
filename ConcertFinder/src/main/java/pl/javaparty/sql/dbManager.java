package pl.javaparty.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import pl.javaparty.items.Agencies;
import pl.javaparty.items.Concert;
import pl.javaparty.map.MapHelper;
import pl.javaparty.prefs.Prefs;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;

public class dbManager extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "baza.db";
    private static SQLiteDatabase database;
    public final static String CONCERTS_TABLE = "Concerts";
    public final static String FAVOURITES_TABLE = "Favourites";
    public static String SORT_ORDER = "";
    private Context context;
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
                    "LON TEXT," +
                    "DIST REAL)";

    // nowa tabela zawieraj�ca ulubione koncerty
    private static String CreateFavouriteTable =
            "CREATE TABLE " + FAVOURITES_TABLE + "(" +
                    "ID INTEGER)";

    public dbManager(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;

        database = getWritableDatabase();
        setSortOrder(context);
    }

    private void setSortOrder(Context context) {
        SORT_ORDER = Prefs.getSortOrder(context);
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
    }

    public void deleteTables() {
        database.delete(CONCERTS_TABLE, null, null);
        database.delete(FAVOURITES_TABLE, null, null);
        Log.i("DB", "Tabele usunięte");
    }

    public void beginTransaction() {
        database.beginTransaction();
    }

    public void endTransaction() {
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public void addConcert(long id, String artist, String city, String spot,
                           int day, int month, int year, String agency, String url, String lat, String lon, double distance) {

        String sql = "INSERT INTO " + CONCERTS_TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindLong(1, id);
        statement.bindString(2, artist);
        statement.bindString(3, city);
        statement.bindString(4, spot);
        statement.bindLong(5, day);
        statement.bindLong(6, month);
        statement.bindLong(7, year);
        statement.bindString(8, agency);
        statement.bindString(9, url);
        statement.bindString(10, lat);
        statement.bindString(11, lon);
        statement.bindDouble(12, distance);
        statement.execute();
    }

    public void deleteDatabase(Context context) {
        database.close();
        context.deleteDatabase(DATABASE_NAME);
        new dbManager(context);
        Log.i("DB", "Baza usunięta i stworzona na nowo");
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
        String[] columns = {"ORD", "ARTIST", "CITY", "SPOT", "DAY", "MONTH", "YEAR", "AGENCY", "URL", "LAT", "LON", "DIST"};
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

    public String[] getArtistsByDateRange(int dF, int mF, int yF, int dT, int mT, int yT, String filter) {
        String[] columns = {"ARTIST"};
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
        //Cursor c = database.query(CONCERTS_TABLE, columns, condition, selectionArgs, null, null, "YEAR,MONTH,DAY");
        Cursor c = database.query(true, CONCERTS_TABLE, columns, condition, selectionArgs, null, null, SORT_ORDER, null);
        String[] artists = new String[c.getCount()];
        for (int i = 0; c.moveToNext(); i++) {
            artists[i] = c.getString(0);
        }
        c.close();
        return artists;
    }

    public String[] getPastArtists(String filter) {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        int[] date = new int[]{yesterday.get(Calendar.DAY_OF_MONTH), yesterday.get(Calendar.MONTH) + 1, yesterday.get(Calendar.YEAR)};
        return getArtistsByDateRange(0, 0, 0, date[0], date[1], date[2], filter);
    }

    public String[] getFutureArtists(String filter) {
        Calendar today = Calendar.getInstance();
        int[] date = new int[]{today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.MONTH) + 1, today.get(Calendar.YEAR)};
        return getArtistsByDateRange(date[0], date[1], date[2], 32, 13, 3000, filter);
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
        }

        c.close();

        return concerts;
    }

    public boolean isConcertFavourite(int id) {
        String[] columns = {"ID"};
        boolean favourite = false;
        Cursor c = database.query(FAVOURITES_TABLE, columns, null, null, null, null, null);
        for (int i = 0; c.moveToNext(); i++)
            // wut? Czo to za niewykorzystane i?
            //no na chuj drazyc temat
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

    private Agencies getAgency(String s) {
        Agencies agency = null;
        Agencies[] agencyNames = Agencies.values();
        for (int i = 0; i < agencyNames.length && agency == null; i++) {
            if (agencyNames[i].name().equals(s))
                agency = agencyNames[i];
        }
        return agency;
    }

    private Concert[] getConcertsBy(String condition) {
        String[] columns = {"ORD", "ARTIST", "CITY", "SPOT", "DAY", "MONTH", "YEAR", "AGENCY", "URL", "LAT", "LON", "DIST"};

        Cursor c = database.query(CONCERTS_TABLE, columns, condition, null, null, null, SORT_ORDER);

        Concert[] concerts = new Concert[c.getCount()];
        for (int i = 0; c.moveToNext(); i++) {
            concerts[i] = new Concert(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
                    c.getInt(4), c.getInt(5), c.getInt(6), getAgency(c.getString(7)), c.getString(8), c.getString(9), c.getString(10), c.getDouble(11));
        }
        c.close();
        return concerts;
    }

    public Concert[] getConcertsByArtist(String artist, String filter) {
        String condition = "ARTIST = '" + artist + "' AND ( " + filter + " )";
        return getConcertsBy(condition);
    }

    public Concert[] getConcertsByCity(String city, String filter) {
        String condition = "CITY = '" + city + "' AND ( " + filter + " )";
        return getConcertsBy(condition);
    }

    public Concert[] getPastConcertsByCity(String city, String filter) {
        String condition = "CITY = '" + city + "' AND ( " + filter + " )";
        return getPastConcerts(condition);
    }

    public Concert[] getFutureConcertsByCity(String city, String filter) {
        String condition = "CITY = '" + city + "' AND ( " + filter + " )";
        return getFutureConcerts(condition);
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

        //        Calendar today = Calendar.getInstance();
//        int[] date = new int[]{today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.MONTH) + 1, today.get(Calendar.YEAR)};
//        Concert[] firstPart = getConcertsByDateRange(date[0], date[1], date[2], date[0], (date[1] + 1) % 12, date[2], filter, "DIST");
//        Concert[] secondPart = getConcertsByDateRange(date[0], (date[1] + 1) % 12, date[2], 32, 12, 3000, filter, "YEAR, MONTH, DAY, DIST");
////        for(int i = 0; i<firstPart.length;i++)
////        {
////            Log.i("firstPartConcertARTYSTA",firstPart[i].getArtist());
////            Log.i("firstPartConcertARTYSTA",String.valueOf(firstPart[i].getDistance()));
////        }
//        return joinConcertArray(firstPart, secondPart);

    }

    public Concert[] getPastConcertsByArtist(String artist, String filter) {
        String condition = "ARTIST = '" + artist + "' AND (" + filter + ")";
        return getPastConcerts(condition);
    }


    public Concert[] getFutureConcertsByArtist(String artist, String filter) {


        String condition = "ARTIST = '" + artist + "' AND (" + filter + ")";
        return getFutureConcerts(condition);
    }

    public Concert[] joinConcertArray(Concert[] a, Concert[] b) {
        int length = a.length + b.length;
        Concert[] result = new Concert[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public Concert[] getConcertsByDateRange(int dF, int mF, int yF, int dT, int mT, int yT, String filter) {
        String[] columns = {"ORD", "ARTIST", "CITY", "SPOT", "DAY", "MONTH", "YEAR", "AGENCY", "URL", "LAT", "LON", "DIST"};
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
        //Cursor c = database.query(CONCERTS_TABLE, columns, condition, selectionArgs, null, null, SORT_ORDER);
        Cursor c = database.query(true, CONCERTS_TABLE, columns, condition, selectionArgs, null, null, SORT_ORDER, null);
        Concert[] concerts = new Concert[c.getCount()];
        for (int i = 0; c.moveToNext(); i++) {
            concerts[i] = new Concert(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
                    c.getInt(4), c.getInt(5), c.getInt(6), getAgency(c.getString(7)), c.getString(8), c.getString(9), c.getString(10), c.getDouble(11));
        }
        c.close();
        return concerts;
    }

    public void update(LatLng latLng) {
        setSortOrder(context);

        String[] columns = {"ORD", "LAT", "LON"};
        MapHelper mapHelper = new MapHelper(context);
        Cursor c = database.query(CONCERTS_TABLE, columns, null, null, null, null, null);
        ContentValues cv = new ContentValues();
        double distance;

        beginTransaction();
        for (int i = 0; c.moveToNext(); i++) { //trolololo nie uzywam i co Pan na to
            //String selectSql = "SELECT lat, lon" + CONCERTS_TABLE + " VALUES WHERE ORD = ?;";
            long id = c.getInt(0);
            distance = mapHelper.inaccurateDistanceTo(Double.parseDouble(c.getString(1)), Double.parseDouble(c.getString(2)), latLng);
            cv.put("DIST", distance);
            database.update(CONCERTS_TABLE, cv, "ORD=" + id, null);
//            String updateSql = "UPDATE " + CONCERTS_TABLE + " SET DIST = ?;";
//            SQLiteStatement statement = database.compileStatement(updateSql);
//            statement.clearBindings();
//            statement.bindDouble(1, distance);
//            statement.execute();
        }
        endTransaction();
    }

    private Concert getFavConcertByID(int id) {
        String condition = "ORD = " + id;
        return getConcertsBy(condition)[0]; // id jest unuikalne wiec bedzie to zawsze tablica jednoelementowa
    }

}