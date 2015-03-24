package pl.javaparty.map;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pl.javaparty.prefs.Prefs;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapHelper {
    private Geocoder geoCoder;
    private List<Address> addressList;

    private Address destinationAddress; // to zadane
    private Address hometownAddress; // to z ustawie�
    private String hometownString;

    public MapHelper(Context context) {
        geoCoder = new Geocoder(context);
        hometownString = Prefs.getCity(context);
    }

    /**
     * Oblicza odleglosc miedzy zadanym miastem w stringu a miastem ktore zostalo zapisane w ustawieniach.
     *
     * @param city nazwa miasta
     * @return zwraca odleglosc w kilometrach
     */

    public int distanceTo(final String city) {
        destinationAddress = getAddress(city);
        hometownAddress = getAddress(hometownString);

        Log.i("MAP", "Miasto:" + city);
        float[] distanceFloat = new float[3];

        try {
            Location.distanceBetween(
                    hometownAddress.getLatitude(), hometownAddress.getLongitude(),
                    destinationAddress.getLatitude(), destinationAddress.getLongitude(),
                    distanceFloat);
        } catch (NullPointerException ne) {
            return 0;
        }
        Log.i("MAP", "Dystans w km: " + (int) (distanceFloat[0] / 1000));
        return (int) (distanceFloat[0] / 1000);
    }

    //wynik zwracany w jakimś gównie a nie w kilometrach
    //obliczam odległość między dwoma punktami z pitagorasa
    public double inaccurateDistanceTo(double lat, double lon, LatLng hometown) {
        double a = Math.abs(lat - hometown.latitude);
        double b = Math.abs(lon - hometown.longitude);
        return (Math.sqrt((a * a) + (b * b)));
    }


    public int distanceTo(final LatLng spot) {
        hometownAddress = getAddress(hometownString);

        Log.i("MAP", "Miasto:" + spot);
        float[] distanceFloat = new float[3];

        try {
            Location.distanceBetween(
                    hometownAddress.getLongitude(), hometownAddress.getLatitude(),
                    spot.latitude, spot.longitude,
                    distanceFloat);
        } catch (NullPointerException ne) {
            return 0;
        }
        Log.i("MAP", "Dystans w km: " + (int) (distanceFloat[0] / 1000));
        return (int) (distanceFloat[0] / 1000);
    }

    public Address getAddress(String place) {
        int tryDownload = 0;
        try {
            while (tryDownload++ < 3) {
                addressList = geoCoder.getFromLocationName(place, 1);
                if (addressList.size() > 0)
                    return addressList.get(0);
                TimeUnit.SECONDS.sleep(3);
            }
        } catch (InterruptedException | IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LatLng getLatLng(String place) {
        return (new LatLng(getAddress(place).getLatitude(), getAddress(place).getLongitude()));

    }

    private String url_front = "http://nominatim.openstreetmap.org/search?q=";
    private String email = null; // do kontaktu z nominatim jakby coś się zjebało, coooo?

    public LatLng getAlternateLatLng(String city) throws JSONException {
        String params = city + "&format=json";
        return getCoordinates(params);
    }

    private LatLng getCoordinates(String params) throws JSONException {
        params = params.replace(" ", "+") + (email != null ? email : "");
        JSONObject jso;
        try {
            jso = getJSON(params).getJSONObject(0);
        } catch (Exception e) {
            return new LatLng(0, 0);
        }
        return new LatLng(Double.parseDouble(jso.getString("lat")), Double.parseDouble(jso.getString("lon")));
    }

    private JSONArray getJSON(String params) throws IOException, JSONException {
        Document doc = Jsoup.connect(url_front + params).ignoreContentType(true).get();
        String docContent = doc.toString().split("<body>")[1].split("</body>")[0];
        return docContent.equals("[]") ? null : new JSONArray(docContent);
    }
}
