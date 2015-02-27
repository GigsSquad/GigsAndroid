package pl.javaparty.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import pl.javaparty.map.MapHelper;

public class Prefs {
    public final static String SETTINGS = "Settings";

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(SETTINGS, 0);
    }

    // Dystans
    public static int getDistance(Context context) {
        return getPrefs(context).getInt("DIST", 0);
    }

    public static void setDistance(Context context, int value) {
        getPrefs(context).edit().putInt("DIST", value).commit();
    }

    // Miasto
    public static String getCity(Context context) {
        return getPrefs(context).getString("CITY", "");
    }

    public static void setCity(Context context, String value) {
        getPrefs(context).edit().putString("CITY", value).commit();
        /*MapHelper mapHelper = new MapHelper(context);
        //Log.i("SortDouble", mapHelper.getLat(value);
        value = value.substring(0,value.indexOf("(")).trim();
        Log.i("HomeTownPlaceAfter",value);
        setLat(context, Double.doubleToLongBits(mapHelper.getLat(value)));
        setLon(context, Double.doubleToLongBits(mapHelper.getLon(value)));*/
    }


    public static void setLat(Context context, long lat) {
        getPrefs(context).edit().putLong("LAT",lat);

    }

    public static void setLon(Context context, long lon){
        getPrefs(context).edit().putLong("LON", lon);

    }

    public static double getLat(Context context) {
        return Double.longBitsToDouble(getPrefs(context).getLong("LAT",0));
    }

    public static double getLon(Context context) {
        return Double.longBitsToDouble(getPrefs(context).getLong("LON",0));
    }

    //zewnetrzna baza
    public static int getLastID(Context context) {
        return getPrefs(context).getInt("LASTID", -1);
    }

    public static void setLastID(Context context, int value) {
        getPrefs(context).edit().putInt("LASTID", value).commit();
    }

}