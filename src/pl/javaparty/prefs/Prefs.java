package pl.javaparty.prefs;

import android.content.Context;
import android.content.SharedPreferences;

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
		return getPrefs(context).getString("CITY", "Warszawa");
	}
	
	public static String getCounty(Context context)
	{
		return getPrefs(context).getString("COUNTY", "mazowieckie");
	}

	public static void setCity(Context context, String value, String county) {
		getPrefs(context).edit().putString("CITY", value).commit();
		getPrefs(context).edit().putString("COUNTY", county).commit();
	}

	// Wspolrzedne
	public static double getLat(Context context) {
		return getPrefs(context).getFloat("LAT", -1);
	}

	public static double getLng(Context context) {
		return getPrefs(context).getFloat("LNG", -1);
	}

	public static void setLatLng(Context context, double lat, double lng) {
		getPrefs(context).edit().putFloat("LAT", (float) lat).commit();
		getPrefs(context).edit().putFloat("LNG", (float) lng).commit();
	}

}