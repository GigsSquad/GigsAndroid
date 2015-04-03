package pl.javaparty.map;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pl.javaparty.prefs.Prefs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapHelper {
    private Geocoder geoCoder;

    private Address hometownAddress; // to z ustawie�
    private String hometownString;

    public MapHelper(Context context) {
        geoCoder = new Geocoder(context);
        hometownString = Prefs.getCity(context);
    }

    //wynik zwracany w jakimś gównie a nie w kilometrach
    //obliczam odległość między dwoma punktami z pitagorasa
    public double inaccurateDistanceTo(double lat, double lon, LatLng hometown) { //slicznie to wyglada
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

    private Address getAddress(String place) {
        int tryDownload = 0;
        List<Address> addressList;
        while (tryDownload++ < 5) {
            try {
                addressList = geoCoder.getFromLocationName(place, 1);
                if (addressList.size() > 0)
                    return addressList.get(0);
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException | IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public LatLng getLatLng(String place) {
        LatLng latLng;

        if (!hometownAddress.hasLatitude() && !hometownAddress.hasLongitude()) {
            hometownAddress = getAddress(place);
        }
        latLng = new LatLng(hometownAddress.getLatitude(), hometownAddress.getLongitude());
        return latLng;
    }


    public LatLng getAlternateLatLng(String city) throws JSONException, IOException {
        JSONObject jso = getJSON(city.replace(" ", "+") + "&format=json").getJSONObject(0);
        Log.d("MAPS", jso.toString());
        return new LatLng(Double.parseDouble(jso.getString("lat")), Double.parseDouble(jso.getString("lon")));
    }

    private JSONArray getJSON(String params) throws IOException, JSONException {
        String url_front = "http://nominatim.openstreetmap.org/search?q=";
        Document doc = Jsoup.connect(url_front + params).ignoreContentType(true).get();
        String docContent = doc.toString().split("<body>")[1].split("</body>")[0];
        return docContent.equals("[]") ? null : new JSONArray(docContent);
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
            Log.d("LATLNGcity", address);
            Log.d("LATLNGlatitude", "" + lat);
            Log.d("LATLNGlongitude", "" + lng);
            return new LatLng(lat, lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new LatLng(52.232938, 21.0611941);
    }


    public LatLng getLocationFromAddress(String strAddress, Context context) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng((int) (location.getLatitude() * 1E6),
                    (int) (location.getLongitude() * 1E6));

            return p1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
