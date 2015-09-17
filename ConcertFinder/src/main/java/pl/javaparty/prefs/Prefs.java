package pl.javaparty.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    private static final Prefs INSTANCE = new Prefs();
    private static Context context;

    private Prefs() {
    }

    public static Prefs getInstance(Context context) {
        Prefs.context = context;
        return INSTANCE;
    }

    public final String SETTINGS = "Settings";

    public SharedPreferences getPrefs() {
        return context.getSharedPreferences(SETTINGS, 0);
    }

    // Miasto
    public String getCity() {
        return getPrefs().getString("CITY", "");
    }

    public void setCity(String value) {
        getPrefs().edit().putString("CITY", value).commit();
    }

    //zewnetrzna baza
    public int getLastID() {
        return getPrefs().getInt("LASTID", -1);
    }

    public void setLastID(int value) {
        getPrefs().edit().putInt("LASTID", value).commit();
    }

    public void setUserID(int value) {
        getPrefs().edit().putInt("USERID", value).commit();
    }

    public void setLat(String value) {
        getPrefs().edit().putString("LAT", value).commit();
    }

    public void setLon(String value) {
        getPrefs().edit().putString("LON", value).commit();
    }

    public String getLat() {
        return getPrefs().getString("LAT", "-1");
    }

    public String getLon() {
        return getPrefs().getString("LON", "-1");
    }

    public int getUserID() {
        return getPrefs().getInt("USERID", -1);
    }

    public String getSortOrder() {
        return getPrefs().getString("SORTORDER", "YEAR,MONTH,DAY");
    }

    public boolean getStart() {
        return getPrefs().getBoolean("FIRSTBOOT", true);
    }

    public void setStart(boolean value) {
        getPrefs().edit().putBoolean("FIRSTBOOT", value).commit();
    }

    public void setSortOrder(String value) { //data -> YEAR,MONTH,DAY, miejsce -> DIST
        getPrefs().edit().putString("SORTORDER", value).commit();
    }

}