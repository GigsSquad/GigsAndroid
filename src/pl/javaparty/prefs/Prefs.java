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
		return getPrefs(context).getString("CITY", null);
	}

	public static void setCity(Context context, String value) {
		getPrefs(context).edit().putString("CITY", value).commit();
	}

}