package pl.javaparty.concertfinder;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.javaparty.enums.DialogType;
import pl.javaparty.enums.PHPurls;
import pl.javaparty.map.MapHelper;
import pl.javaparty.prefs.Prefs;
import pl.javaparty.sql.DatabaseManager;
import pl.javaparty.sql.JSONthing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jakub on 8/14/15.
 */
public class ConcertDownloader extends AsyncTask<String, Void, String> implements Observable {

    private static List<Observer> observerList = new ArrayList<>();
    private int success, jsonLastId, prefsLastId, count;
    private LatLng latLng;
    private JSONObject mJsonObject;
    private Context context;
    private ProgressDialog dialog;
    private MapHelper mapHelper;
    private DialogFactory fabric;

    public ConcertDownloader(Context context) {
        this.context = context;
        mapHelper = new MapHelper(context);
        latLng = new LatLng(Double.parseDouble(Prefs.getInstance(context).getLon()), Double.parseDouble(Prefs.getInstance(context).getLat()));
        fabric = new DialogFactory(context);
        dialog = fabric.produceDialog(DialogType.progress);
    }

    public void process() throws JSONException {
        List<NameValuePair> params = new ArrayList<>();
        mJsonObject = JSONthing.getThisShit(PHPurls.getConcerts, params);
        success = mJsonObject.getInt("success");
        jsonLastId = Integer.parseInt(mJsonObject.getString("last_id"));
        prefsLastId = Prefs.getInstance(context).getLastID();
        count = mJsonObject.getInt("count");
        //chłopcy i dziewczęta pamiętajmy iż last_id != count

        //jak poprawnie pobierzemy z internetów
        if (success == 1) {
            dialog.setMax(prefsLastId + jsonLastId);

            Log.i("DB", "ID - Prefs: " + prefsLastId + " JSON: " + jsonLastId);
            Log.i("DB", "COUNT - Prefs: " + DatabaseManager.getInstance(context).getSize(DatabaseManager.CONCERTS_TABLE) + " JSON: " + count);

            if (DatabaseManager.getInstance(context).getSize(DatabaseManager.CONCERTS_TABLE) > count) { // tak nie powinno się nigdy stać
                Log.wtf("DB", "Baza w aplikacji ma wiecej koncertów niż na serwerze?");
                DatabaseManager.getInstance(context).deleteTables(); //wypierdol dziada
                Prefs.getInstance(context).setLastID(-1); // zmieniamy ostatnie id na początkowe
            }
            saveOrUpdate();
        }
    }

    //Pobiera nowe koncerty
    private void saveOrUpdate() throws JSONException {
        double distance;
        Prefs.getInstance(context).setLastID(jsonLastId);
        JSONArray mJsonArray = mJsonObject.getJSONArray("concerts");
        DatabaseManager.getInstance(context).beginTransaction();
        for (int i = 0; i < mJsonArray.length(); i++) {
            JSONObject JSONconcert = mJsonArray.getJSONObject(i);
            if (latLng.longitude != -1)
                distance = mapHelper.inaccurateDistanceTo(Double.parseDouble(JSONconcert.getString("lat")), Double.parseDouble(JSONconcert.getString("lon")), latLng);
            else
                distance = 0;

            List<Object> list = new ArrayList<>();
            list.add(JSONconcert.getInt("id"));
            list.add(JSONconcert.getString("artist"));
            list.add(JSONconcert.getString("city"));
            list.add(JSONconcert.getString("spot"));
            list.add(JSONconcert.getInt("day"));
            list.add(JSONconcert.getInt("month"));
            list.add(JSONconcert.getInt("year"));
            list.add(JSONconcert.getString("agency"));
            list.add(JSONconcert.getString("url"));
            list.add(JSONconcert.getString("updated"));
            list.add(JSONconcert.getString("lat"));
            list.add(JSONconcert.getString("lon"));
            list.add(distance);
            list.add(JSONconcert.getString("entrance_fee"));

            DatabaseManager.getInstance(context).saveOrUpdateConcert(list);

            dialog.incrementProgressBy(1);
        }
        DatabaseManager.getInstance(context).endTransaction();
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            process();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        notifyObservers();
        Toast.makeText(context, context.getString(R.string.database_is_up_to_date), Toast.LENGTH_SHORT).show();

        dialog.dismiss();
        super.onPostExecute(s);
    }

    @Override
    public void register(Observer o) {
        observerList.add(o);
    }

    @Override
    public void remove(Observer o) {
        observerList.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : observerList) o.refresh();
    }
}
