package pl.javaparty.map;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.javaparty.prefs.Prefs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

public class MapHelper {
    private String hometownString;
    private Context context;

    public MapHelper(Context context) {
        this.context = context;
        hometownString = Prefs.getInstance(context).getCity();
    }

    private void getHometownCoords() {
        LatLng hometownLatLng = MapHelper.getLatLongFromAddress(hometownString);
        Prefs.getInstance(context).setLat(String.valueOf(hometownLatLng.latitude));
        Prefs.getInstance(context).setLon(String.valueOf(hometownLatLng.longitude));
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6372.8; // In kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }

    public double distanceFromHometown(LatLng destination) {
        boolean noHometownCoords = (Prefs.getInstance(context).getLat().equals("-1") || Prefs.getInstance(context).getLon().equals("-1"));
        if (noHometownCoords) {
            getHometownCoords();
        }

        double originLat = Double.parseDouble(Prefs.getInstance(context).getLat());
        double originLon = Double.parseDouble(Prefs.getInstance(context).getLon());

        Log.i("MAP", "Koncert, lat: " + destination.latitude + " lon: " + destination.longitude);
        Log.i("MAP", "Hometown, lat: " + originLat + " lon: " + originLon);
        return haversine(destination.latitude, destination.longitude, originLat, originLon);
    }

    public double distanceFromHometown(double latitude, double longitude) {
        boolean noHometownCoords = (Prefs.getInstance(context).getLat().equals("-1") || Prefs.getInstance(context).getLon().equals("-1"));
        if (noHometownCoords) {
            getHometownCoords();
        }

        double originLat = Double.parseDouble(Prefs.getInstance(context).getLat());
        double originLon = Double.parseDouble(Prefs.getInstance(context).getLon());

        Log.i("MAP", "Koncert, lat: " + latitude + " lon: " + longitude);
        Log.i("MAP", "Hometown, lat: " + originLat + " lon: " + originLon);
        return haversine(latitude, longitude, originLat, originLon);
    }

    public double distanceBetween(LatLng origin, LatLng destination) {
        Log.i("MAP", "Skąd: , lat: " + origin.latitude + " lon: " + origin.longitude);
        Log.i("MAP", "Do: , lat: " + destination.latitude + " lon: " + destination.longitude);
        return haversine(destination.latitude, destination.longitude, origin.latitude, origin.longitude);
    }

    public static LatLng getLatLongFromAddress(String address) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
                    URLEncoder.encode(address, "UTF-8") + "&sensor=false";
            HttpGet httpGet = new HttpGet(uri);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(stringBuilder.toString());

            double lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            double lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");
            Log.i("MAP", "City: " + address);
            Log.i("MAP", "Lat: " + lat);
            Log.i("MAP", "Long: " + lng);
            return new LatLng(lat, lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new LatLng(52.232938, 21.0611941);
    }
}
