package pl.javaparty.jsoup;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import pl.javaparty.map.MapHelper;
import pl.javaparty.prefs.Prefs;
import pl.javaparty.sql.DatabaseUpdater;
import pl.javaparty.sql.WebConnector;
import pl.javaparty.sql.dbManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.InputMismatchException;
import java.util.Scanner;

public class PhpParser {
    private final static String SEPARATOR = ";";
    private final static String END_SEPARATOR = "#";
    private final Activity activity;
    private final dbManager dbm;
    // private final String homeCity;
    MapHelper mapHelper;
    private double homeLat;
    private double homeLon;

    public PhpParser(Activity activity, dbManager dbm) {
        this.activity = activity;
        this.dbm = dbm;
        mapHelper = new MapHelper(activity);
        try {
            LatLng homeLatLng = mapHelper.getLatLng("Wrocław"); //TODO miasta z prefs
            homeLat = homeLatLng.latitude;
            homeLon = homeLatLng.longitude;

        } catch (NullPointerException npexc) {
            homeLat = 52.2289922;
            homeLon = 21.0034725;
        }
        Log.i("HOMETOWNLAT", String.valueOf(homeLat));
        Log.i("HOMETOWNLON", String.valueOf(homeLon));

    }

    public void parse(InputStream is) {
        if (is != null) {
            try {
                Document doc = Jsoup.parse(is, WebConnector.CHARSET, WebConnector.URL);
                int lastID = -1;
                String line = doc.text();
                Scanner sc = new Scanner(line).useDelimiter(END_SEPARATOR);
                while (sc.hasNext()) {
                    String next = sc.next();
                    lastID = addToDatabase(next);
                    DatabaseUpdater.progressDialog.incrementProgressBy(1);
                }
                Prefs.setLastID(activity.getApplicationContext(), lastID);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * @param concert {@code String} which contains all concert data separated by const separator.
     * @return ID of red concert, or -1 if error.
     */
    private int addToDatabase(String concert) {
        Scanner sc = new Scanner(concert).useDelimiter(SEPARATOR);
        int id = -1;
        try {
            id = sc.nextInt();
            String artistName = sc.next();
            String conCity = sc.next();
            String conSpot = sc.next();
            int conDay = sc.nextInt();
            int conMonth = sc.nextInt();
            int conYear = sc.nextInt();
            String conAgency = sc.next();
            String conURL = sc.next();
            String lat = sc.next();
            String lon = sc.next();
            //    Log.i("lat",latLonHometown.toString());
            double distance = countDistance(lat, lon);
            // Log.i("ajdik",String.valueOf(id));
            //  Log.i("distance", String.valueOf(distance));
            //  Log.i("distanceCity",conCity);
            //	dbm.addConcert(id, sc.next(), sc.next(), sc.next(), sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.next(), sc.next(), sc.next(), sc.next());
            dbm.addConcert(id, artistName, conCity, conSpot, conDay, conMonth, conYear, conAgency, conURL, lat, lon, distance);

        } catch (InputMismatchException e) {
            Log.i("UPDATER", "Wrong input: " + concert);
        }
        return id;
    }

    private double countDistance(String lat, String lon) {

        double a = Math.abs(Double.parseDouble(lat) - homeLat);
        double b = Math.abs(Double.parseDouble(lon) - homeLon);
        a=a*2;//To wynika z geografi
        return (Math.sqrt((a * a) + (b * b)));

    }
}
