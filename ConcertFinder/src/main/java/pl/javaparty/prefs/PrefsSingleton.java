package pl.javaparty.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsSingleton {

    private static final PrefsSingleton INSTANCE = new PrefsSingleton();

    private PrefsSingleton() {
    }

    public static PrefsSingleton getInstance() {
        return INSTANCE;
    }


    public final String SETTINGS = "Settings";

    public SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(SETTINGS, 0);
    }

    // Miasto
    public String getCity(Context context) {
        return getPrefs(context).getString("CITY", "");
    }

    public void setCity(Context context, String value) {
        getPrefs(context).edit().putString("CITY", value).commit();
    }

    //zewnetrzna baza
    public int getLastID(Context context) {
        return getPrefs(context).getInt("LASTID", -1);
    }

    public void setLastID(Context context, int value) {
        getPrefs(context).edit().putInt("LASTID", value).commit();
    }

    public void setUserID(Context context, int value) {
        getPrefs(context).edit().putInt("USERID", value).commit();
    }

    public void setLat(Context context, String value) {
        getPrefs(context).edit().putString("LAT", value).commit();
        getPrefs(context).edit().putString("LAT", value).commit();
    }

    public void setLon(Context context, String value) {
        getPrefs(context).edit().putString("LON", value).commit();
    }

    public String getLat(Context context) {
        return getPrefs(context).getString("LAT", "-1");
    }

    public String getLon(Context context) {
        return getPrefs(context).getString("LON", "-1");
    }

    public int getUserID(Context context) {
        return getPrefs(context).getInt("USERID", -1);
    }

    public String getSortOrder(Context context) {
        return getPrefs(context).getString("SORTORDER", "YEAR,MONTH,DAY");
    }

    public boolean getStart(Context context) {
        return getPrefs(context).getBoolean("FIRSTBOOT", true);
    }

    public void setStart(Context context, boolean value) {
        getPrefs(context).edit().putBoolean("FIRSTBOOT", value).commit();
    }

    public void setSortOrder(Context context, String value) { //data -> YEAR,MONTH,DAY, miejsce -> DIST
        getPrefs(context).edit().putString("SORTORDER", value).commit();
    }

}