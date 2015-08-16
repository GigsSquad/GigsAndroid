package pl.javaparty.concertfinder;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import pl.javaparty.enums.DialogType;
import pl.javaparty.enums.PHPurls;
import pl.javaparty.map.MapHelper;
import pl.javaparty.prefs.Prefs;
import pl.javaparty.sql.JSONthing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jakub on 8/14/15.
 * pobiera z serwera lokalizację uzytkownika
 */
public class LatLngConnector extends AsyncTask<String, Void, String> {
    LatLng latlng;
    JSONthing jsonthing;
    String id;
    Context context;
    ProgressDialog dialog;

    public LatLngConnector(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        DialogFactory dialogFabric = new DialogFactory(context);
        dialog = dialogFabric.produceDialog(DialogType.simple);
        dialog.setMessage("Łączę się z Google Maps");
        dialog.show();

        jsonthing = new JSONthing();
        id = String.valueOf(Prefs.getInstance(context).getUserID()); //stirng żeby się PHPy nie srały
    }

    @Override
    protected String doInBackground(String... args) {
        String city = Prefs.getInstance(context).getCity();
        if (!city.isEmpty()) {
            if (!id.equals("-1"))
                updateServerLatLng(city);
            latlng = MapHelper.getLatLongFromAddress(city);
        }
        return city;
    }

    /**
     * Aktualizuje miasto użytkownia na to któe pisał w okienku
     *
     * @param city
     */
    protected void updateServerLatLng(String city) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("location", city.split(" ")[0]));
        params.add(new BasicNameValuePair("user_id", id));
        jsonthing.makeHttpRequest(PHPurls.updateUser.toString(), "GET", params); //TIGHT and ELEGANT
    }


    @Override
    protected void onPostExecute(String city) {
        if (!city.isEmpty()) {
            Prefs.getInstance(context).setLat(String.valueOf(latlng.latitude));
            Prefs.getInstance(context).setLon(String.valueOf(latlng.longitude));
            Log.d("LATLNG", "Lat:" + Prefs.getInstance(context).getLat() + " Long:" + Prefs.getInstance(context).getLon());
        }
        dialog.dismiss();
        ConcertDownloader concertDownloader = new ConcertDownloader(context);
        concertDownloader.execute();
        super.onPostExecute(city);
    }
}
