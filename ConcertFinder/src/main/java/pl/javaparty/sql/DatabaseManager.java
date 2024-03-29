package pl.javaparty.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import pl.javaparty.items.Agencies;
import pl.javaparty.items.Concert;
import pl.javaparty.map.MapHelper;
import pl.javaparty.prefs.Prefs;

import java.util.*;

public class DatabaseManager extends SQLiteOpenHelper {

    private static volatile DatabaseManager INSTANCE;


    private DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
        database = getWritableDatabase();
        setSortOrder(context);
    }

    public static DatabaseManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DatabaseManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DatabaseManager(context);
                }
            }
        }
        return INSTANCE;
    }

    private SQLiteDatabase database;
    private final static String DATABASE_NAME = "baza.db";
    public final static String CONCERTS_TABLE = "Concerts";
    public final static String FAVOURITES_TABLE = "Favourites";
    public final static String FOLLOWING_TABLE = "Following";
    public final static String SEARCH_TABLE = "Search";
    private static String SORT_ORDER = "";
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
                    "UPDATED DATE," +
                    "LAT TEXT," +
                    "LON TEXT," +
                    "DIST REAL," +
                    "ENTRANCE_FEE INTEGER)";

    private String[] allColumns = {"ORD", "ARTIST", "CITY", "SPOT", "DAY", "MONTH", "YEAR", "AGENCY", "URL", "LAT", "LON", "DIST", "ENTRANCE_FEE"};

    // nowa tabela zawieraj�ca ulubione koncerty
    private static String CreateFavouriteTable =
            "CREATE TABLE " + FAVOURITES_TABLE + "(" +
                    "ID INTEGER)";

    private static String CreateFollowingTable =
            "CREATE TABLE " + FOLLOWING_TABLE + "(" +
                    "NAME TEXT)";

    //nowa tabela na wyszukiwania użytkownika
    private static String CreateSearchTable =
            "CREATE TABLE " + SEARCH_TABLE + "(" +
                    "USER_ID INTEGER NOT NULL," +
                    "ARTIST TEXT," +
                    "CITY TEXT," +
                    "DAY INTEGER," +     // data dodania rekord
                    "MONTH INTEGER," +
                    "YEAR INTEGER)";


    private void setSortOrder(Context context) {
        SORT_ORDER = Prefs.getInstance(context).getSortOrder();
    }

    public void close() {
        database.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateConcertTable);
        db.execSQL(CreateFavouriteTable);
        db.execSQL(CreateSearchTable);
        db.execSQL(CreateFollowingTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void deleteTables() {
        database.delete(CONCERTS_TABLE, null, null);
        database.delete(FAVOURITES_TABLE, null, null);
        database.delete(FOLLOWING_TABLE, null, null);
        Log.i("DB", "Tabele usunięte");
    }

    public void beginTransaction() {
        database.beginTransaction();
    }

    public void endTransaction() {
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public void saveOrUpdateConcert(List<Object> list) {
        int i;
        try {
            String insertSQL = "INSERT INTO " + CONCERTS_TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            SQLiteStatement statement = database.compileStatement(insertSQL);
            statement.clearBindings();
            statement.bindLong(1, (int) list.get(0));
            statement.bindString(2, (String) list.get(1));
            statement.bindString(3, (String) list.get(2));
            statement.bindString(4, (String) list.get(3));
            statement.bindLong(5, (int) list.get(4));
            statement.bindLong(6, (int) list.get(5));
            statement.bindLong(7, (int) list.get(6));
            statement.bindString(8, (String) list.get(7));
            statement.bindString(9, (String) list.get(8));
            statement.bindString(10, (String) list.get(9));
            statement.bindString(11, (String) list.get(10));
            statement.bindString(12, (String) list.get(11));
            statement.bindDouble(13, (double) list.get(12));
            statement.bindString(14, (String) list.get(13));
            statement.execute();
        } catch (SQLiteConstraintException sqlce) {
            Log.i("DB", "Takie samo id, więc update");
            String updateSQL = "UPDATE " + CONCERTS_TABLE + " SET artist = ?, city = ?, spot = ?, day = ?, month = ?, year = ?, agency = ?, url = ?, updated = ?, lat = ?, lon = ?, dist = ?, entrance_fee = ? WHERE ord = ?";
            SQLiteStatement statement = database.compileStatement(updateSQL);
            statement.clearBindings();
            i = 1;
            statement.bindString(1, (String) list.get(1));
            statement.bindString(2, (String) list.get(2));
            statement.bindString(3, (String) list.get(3));
            statement.bindLong(4, (int) list.get(4));
            statement.bindLong(5, (int) list.get(5));
            statement.bindLong(6, (int) list.get(6));
            statement.bindString(7, (String) list.get(7));
            statement.bindString(8, (String) list.get(8));
            statement.bindString(9, (String) list.get(9));
            statement.bindString(10, (String) list.get(10));
            statement.bindString(11, (String) list.get(11));
            statement.bindDouble(12, (double) list.get(12));
            statement.bindString(13, (String) list.get(13));
            statement.bindLong(14, (int) list.get(0));
            statement.execute();
        }
    }

    public void deleteDatabase(Context context) {
        database.close();
        context.deleteDatabase(DATABASE_NAME);
        Prefs.getInstance(context).setLastID(-1);
//        new DatabaseManager(context); //jakie to jest glupie :D a najlepsze ze dziala xD
        Log.i("DB", "Baza usunięta i stworzona na nowo");
    }

    /**
     * Sprawdza poprawnosc bazy danych
     *
     * @return true - poprawna
     * false - zła i została usunieta
     */
    public boolean isValid() {
        Cursor dbCursor = database.query(CONCERTS_TABLE, null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();
        String substring = CreateConcertTable.substring(CreateConcertTable.indexOf("(") + 1);
        String[] newColumns = substring.split(",");
        boolean valid = true;
        if ((columnNames == null && newColumns != null) || (columnNames != null && newColumns == null) || columnNames.length != newColumns.length)
            valid = false;
        else {
            Log.i("Checking Database", "Sprawdzam poprawnosc tabeli.");
            for (int i = 0; i < columnNames.length && i < newColumns.length && valid; i++) {
                String newColumn = newColumns[i].substring(0, newColumns[i].indexOf(" "));
                Log.i("Checking Database", newColumns[i] + " " + newColumn + " " + columnNames[i]);
                if (!columnNames[i].equals(newColumn)) {
                    valid = false;
                }
            }
        }
        return valid;
    }

    public boolean contains(String value) {
        boolean contains = false;
        String[] columns = {"NAME"};
        Cursor c = database.query(FOLLOWING_TABLE, columns, null, null, null, null, null);
        while (c.moveToNext() && !contains)
            contains = value.equals(c.getString(0));
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
        Cursor c = database.query(CONCERTS_TABLE, allColumns, null, null, null, null, null);
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
     * metoda dodajaca id obserwowanego artysty do tabeli Artsist
     */
    public void addFollowingArtist(String value) {
        if (!contains(value)) {
            ContentValues cv = new ContentValues();
            cv.put("NAME", value);
            Log.i("FOLL", "Wrzucomo nazwę obserwowanego: " + value);
            database.insertOrThrow(FOLLOWING_TABLE, null, cv);
        }
    }

    public void removeFollowingArtist(String value) {
        String selection = "NAME = '" + value + "'";
        database.delete(FOLLOWING_TABLE, selection, null);
    }

    /**
     * Metoda uzyskuj�ca ulubione koncerty z tabeli Favourite
     *
     * @return tablica concertow awierajaca ulubione koncerty
     */
    public Concert[] getAllFollowingArtists() {
        String[] columns = {"NAME"};
        Cursor c = database.query(FOLLOWING_TABLE, columns, null, null, null, null, null);
        Concert[] concerts = new Concert[getSize(FOLLOWING_TABLE)];
        for (int i = 0; c.moveToNext(); i++) {
            concerts[i] = getFolConcertByName(c.getString(0));
        }

        c.close();

        return concerts;
    }

    public boolean isArtistFollowing(String value) {
        String[] columns = {"NAME"};
        boolean following = false;
        Cursor c = database.query(FOLLOWING_TABLE, columns, null, null, null, null, null);
        for (int i = 0; c.moveToNext(); i++)
            // wut? Czo to za niewykorzystane i?
            //no na chuj drazyc temat
            if (c.getString(0).equals(value))
                following = true;
        c.close();

        return following;
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
        Cursor c = database.query(CONCERTS_TABLE, allColumns, condition, null, null, null, SORT_ORDER);

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

    public Concert[] getFutureConcertsByCity(String city) {
        String condition = "CITY = '" + city + "'";
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
        String[] columns = {"ORD", "ARTIST", "CITY", "SPOT", "DAY", "MONTH", "YEAR", "AGENCY", "URL", "LAT", "LON", "DIST"};
        String condition = "(YEAR < ? OR (YEAR = ? AND MONTH < ?) OR (YEAR = ? AND MONTH = ? AND DAY < ?))"
                + "AND (" + filter + ")";
        String[] selectionArgs = {
                String.valueOf(date[2]),//year
                String.valueOf(date[2]),//year
                String.valueOf(date[1]),//month
                String.valueOf(date[2]),//year
                String.valueOf(date[1]),//month
                String.valueOf(date[0]),//day

        };
        Cursor c = database.query(true, CONCERTS_TABLE, columns, condition, selectionArgs, null, null, "YEAR DESC, MONTH DESC, DAY DESC", null);
        Concert[] concerts = new Concert[c.getCount()];
        for (int i = 0; c.moveToNext(); i++) {
            concerts[i] = new Concert(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
                    c.getInt(4), c.getInt(5), c.getInt(6), getAgency(c.getString(7)), c.getString(8), c.getString(9), c.getString(10), c.getDouble(11));
        }
        c.close();
        return concerts;
        //    return getConcertsByDateRange(0, 0, 0, date[0], date[1], date[2], filter);
    }

    public Concert[] getFutureConcerts(String filter) {
        Calendar today = Calendar.getInstance();
        int[] date = new int[]{today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.MONTH) + 1, today.get(Calendar.YEAR)};
        return getConcertsByDateRange(date[0], date[1], date[2], 32, 13, 3000, filter);
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
        Log.i("DB", "SortOrder: " + SORT_ORDER);
        setSortOrder(context);
        Cursor c = database.query(true, CONCERTS_TABLE, allColumns, condition, selectionArgs, null, null, SORT_ORDER, null);
        Concert[] concerts = new Concert[c.getCount()];
        for (int i = 0; c.moveToNext(); i++) {
            concerts[i] = new Concert(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
                    c.getInt(4), c.getInt(5), c.getInt(6), getAgency(c.getString(7)), c.getString(8), c.getString(9), c.getString(10), c.getDouble(11));
        }
        c.close();
        return concerts;
    }


    public void updateDistance() {
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
            distance = mapHelper.distanceFromHometown(Double.parseDouble(c.getString(1)), Double.parseDouble(c.getString(2)));
            cv.put("DIST", distance);
            database.update(CONCERTS_TABLE, cv, "ORD=" + id, null);
        }
        endTransaction();
    }

    private Concert getFavConcertByID(int id) {
        String condition = "ORD = " + id;
        return getConcertsBy(condition)[0]; // id jest unuikalne wiec bedzie to zawsze tablica jednoelementowa
    }

    private Concert getFolConcertByName(String value) {
        String condition = "ARTIST = '" + value + "'";
        return getConcertsBy(condition)[0]; // id jest unuikalne wiec bedzie to zawsze tablica jednoelementowa
    }


    public void addSearch(int usrId, String artist, String city, int d, int m, int y) {
        if (!searchAlreadyIn(usrId, artist, city)) {
            ContentValues cv = new ContentValues();
            cv.put("USER_ID", usrId);
            cv.put("ARTIST", artist);
            cv.put("CITY", city);
            cv.put("DAY", d);
            cv.put("MONTH", m);
            cv.put("YEAR", y);
            Log.i("SEARCH", "Wrzucono [usrId = " + usrId + " ][artist = " + artist + " ][city = " + city + "]" +
                    "[data: " + d + "." + m + "." + y + "]");
            database.insertOrThrow(SEARCH_TABLE, null, cv);
        } else
            Log.i("SEARCH", "Już jest [usrId = " + usrId + " ][artist = " + artist + " ][city = " + city + "]");
    }

    private boolean searchAlreadyIn(int usrId, String artist, String city) {
        String[] columns;
        Cursor c;
        String condition;
        if (city == null) {
            columns = new String[]{"USER_ID", "ARTIST"};
            condition = "USER_ID = " + usrId + " AND ARTIST = '" + artist + "'";
        } else {
            columns = new String[]{"USER_ID", "CITY"};
            condition = "USER_ID = " + usrId + " AND CITY = '" + city + "'";
        }
        c = database.query(SEARCH_TABLE, columns, condition, null, null, null, null);
        return c.moveToFirst();
    }
}