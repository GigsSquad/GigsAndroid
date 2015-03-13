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

    public static int getUserID(Context context) {
        return getPrefs(context).getInt("USERID", -1);
    }
}