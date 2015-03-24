package pl.javaparty.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    public final static String SETTINGS = "Settings";

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(SETTINGS, 0);
    }

    // Miasto
    public static String getCity(Context context) {
        return getPrefs(context).getString("CITY", "");
    }

    public static void setCity(Context context, String value) {
        getPrefs(context).edit().putString("CITY", value).commit();
    }

    //zewnetrzna baza
    public static int getLastID(Context context) {
        return getPrefs(context).getInt("LASTID", -1);
    }

    public static void setLastID(Context context, int value) {
        getPrefs(context).edit().putInt("LASTID", value).commit();
    }

    public static void setUserID(Context context, int value) {
        getPrefs(context).edit().putInt("USERID", value).commit();
    }

    public static void setLat(Context context, String value) {
        getPrefs(context).edit().putString("LAT", value).commit();
    }

    public static void setLon(Context context, String value) {
        getPrefs(context).edit().putString("LON", value).commit();
    }

    public static String getLat(Context context) {
        return getPrefs(context).getString("LAT", "-1");
    }

    public static String getLon(Context context) {
        return getPrefs(context).getString("LON", "-1");
    }

    public static int getUserID(Context context) {
        return getPrefs(context).getInt("USERID", -1);
    }

    public static String getSortOrder(Context context) {
            return getPrefs(context).getString("SORTORDER","YEAR,MONTH,DAY");

    }

}