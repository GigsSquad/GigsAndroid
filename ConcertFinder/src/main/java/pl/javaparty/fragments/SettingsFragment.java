package pl.javaparty.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.google.android.gms.maps.model.LatLng;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import pl.javaparty.enums.PHPurls;
import pl.javaparty.imageloader.FileExplorer;
import pl.javaparty.map.MapHelper;
import pl.javaparty.prefs.Prefs;
import pl.javaparty.sql.JSONthing;
import pl.javaparty.sql.dbManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsFragment extends Fragment {

    AutoCompleteTextView citySearchBox;
    Button saveButton, clearButton, downloadButton;
    RadioButton distanceSort, dateSort;
    Context context;
    ArrayAdapter<CharSequence> adapter;
    static dbManager dbm;
    ProgressDialog mapDialog;
    ProgressDialog loadingDialog;
    MapHelper mapHelper;
    String sortOrder = "YEAR,MONTH,DAY";

    private static final String TAG = "Facebook";
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        context = inflater.getContext();
        getActivity().getActionBar().setTitle(getString(R.string.action_settings));
        dbm = MainActivity.getDBManager();// przekazujemy dbm od mainActivity
        citySearchBox = (AutoCompleteTextView) view.findViewById(R.id.cityAutoComplete);
        saveButton = (Button) view.findViewById(R.id.saveSettingsButton);
        clearButton = (Button) view.findViewById(R.id.clearFilesButton);
        downloadButton = (Button) view.findViewById(R.id.downloadButton);
        distanceSort = (RadioButton) view.findViewById(R.id.radioButtonDist);
        dateSort = (RadioButton) view.findViewById(R.id.radioButtonDate);
        mapDialog = new ProgressDialog(getActivity());
        mapDialog.setCancelable(false);

        mapHelper = new MapHelper(getActivity());
        loadingDialog = new ProgressDialog(getActivity());
        loadingDialog.setCancelable(false);
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.COUNTIES, android.R.layout.simple_dropdown_item_1line);
        citySearchBox.setAdapter(adapter);

        citySearchBox.setThreshold(1);

        if (Prefs.getCity(getActivity()) != null)
            citySearchBox.setText(Prefs.getCity(getActivity()));

        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs.setCity(getActivity(), citySearchBox.getText().toString());

                Prefs.setSortOrder(getActivity(), sortOrder);
                Log.i("SETTINGS", "Zapisano");
                Log.i("SETTINGS", "Miasto: " + citySearchBox.getText().toString());

                Toast.makeText(getActivity(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
                new GetLatLng().execute();
            }

        });

        clearButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment dialog = new ClearDialog();
                dialog.show(getActivity().getFragmentManager(), "CLEAR");
            }
        });

        downloadButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline())
                    new GetLatLng2(context).execute(); //nowa lepsza kurwa funkcja stary
                else
                    Toast.makeText(context, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            }
        });


        distanceSort.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sortOrder = "DIST";
            }
        });

        dateSort.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sortOrder = "YEAR,MONTH,DAY";
            }
        });

        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("user_location", "user_birthday", "user_likes"));

        return view;
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }


    public static class ClearDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.question_clear))
                    .setMessage(getString(R.string.msg_clear))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FileExplorer f = new FileExplorer(getActivity());
                            f.clear();
                            Log.i("SETTINGS", "Usunięto obrazki z dysku");
                            dbm.deleteTables();
                            Log.i("SETTINGS", "Wyczyszczono bazę");
                            MainActivity.updateCounters();
                            Prefs.setLastID(getActivity(), -1);
                            Toast.makeText(getActivity(), getString(R.string.cleared), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //puste :(
                        }
                    });
            return builder.create();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sortOrder = Prefs.getSortOrder(getActivity());
        if (sortOrder.equals("DIST")) {
            distanceSort.setChecked(true);
            dateSort.setChecked(false);
        } else if (sortOrder.equals("YEAR,MONTH,DAY")) {
            distanceSort.setChecked(false);
            dateSort.setChecked(true);
        }


        Session session = Session.getActiveSession();

        if (session != null &&
                (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }


    //za mało dostaję zeby to zrobić porządnie, lol
    class GetLatLng2 extends AsyncTask<String, Void, String> {
        LatLng latlng;
        JSONthing jsonthing;
        String id;
        Context cont;

        public GetLatLng2(Context context) {
            super();
            cont = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mapDialog.setMessage("Łączę się z Google Maps");
            mapDialog.show();
            jsonthing = new JSONthing();
            id = String.valueOf(Prefs.getUserID(context)); //stirng żeby się PHPy nie srały
        }

        @Override
        protected String doInBackground(String... args) {
            String city = Prefs.getCity(context);
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
                Prefs.setLat(context, String.valueOf(latlng.latitude));
                Prefs.setLon(context, String.valueOf(latlng.longitude));
                Log.d("LATLNG", "Lat:" + Prefs.getLat(context) + " Long:" + Prefs.getLon(context));
            }
            mapDialog.dismiss();
            new DownloadConcerts().execute();

            super.onPostExecute(city);
        }
    }

    class DownloadConcerts extends AsyncTask<String, Void, String> {

        boolean updateNeeded = false;
        int success, jsonLastId, prefsLastId, count;
        JSONObject mJsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.setMessage(getString(R.string.database_update));
            loadingDialog.setProgress(0);
            loadingDialog.setMax(1);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<>();
            mJsonObject = JSONthing.getThisShit(PHPurls.getConcerts, params);

            try {
                success = mJsonObject.getInt("success");
                jsonLastId = Integer.parseInt(mJsonObject.getString("last_id"));
                prefsLastId = Prefs.getLastID(context);
                count = mJsonObject.getInt("count");
                //chłopcy i dziewczęta pamiętajmy iż last_id != count

                //jak poprawnie pobierzemy z internetów
                if (success == 1) {
                    loadingDialog.setMax(prefsLastId + jsonLastId);

                    Log.i("DBSettings", "ID - Prefs: " + prefsLastId + " JSON: " + jsonLastId);
                    Log.i("DBSettings", "COUNT - Prefs: " + dbm.getSize(dbManager.CONCERTS_TABLE) + " JSON: " + count);

                    //aktualizujemy obecne koncerty bez względu na wszystko
//                    updateConcerts();

                    dbm.deleteTables(); //wypierdol dziada
                    Prefs.setLastID(context, -1); // zmieniamy ostatnie id na początkowe
                    downloadConcerts(); //i zapełnij od nowa

//                    if (count + 1 > dbm.getSize(dbManager.CONCERTS_TABLE)) { //aktualizacja - kiedy liczba koncertów z internetu jest większa od liczby koncertów które mamy w aplikacji
//                        updateConcerts();
//                    } else { // tak nie powinno się nigdy stać
//                        Log.wtf("DBSettings", "Baza w aplikacji ma wiecej koncertów niż na serwerze?");
//                        dbm.deleteTables(); //wypierdol dziada
//                        Prefs.setLastID(context, -1); // zmieniamy ostatnie id na początkowe
//                        downloadConcerts(); //i zapełnij od nowa
//                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        //Aktualizuje koncerty
        protected void updateConcerts() throws JSONException {
            //współrzędne z prefs wpisane do latLng
            LatLng latLng = new LatLng(Double.parseDouble(Prefs.getLon(context)), Double.parseDouble(Prefs.getLat(context)));

            loadingDialog.setMax(count - prefsLastId);
            Prefs.setLastID(context, jsonLastId);
            JSONArray mJsonArray = mJsonObject.getJSONArray("concerts");
            double distance;
            dbm.beginTransaction();
            for (int i = 0; i < mJsonArray.length(); i++) {
                JSONObject JSONconcert = mJsonArray.getJSONObject(i);
                if (latLng.longitude != -1) {
                    distance = mapHelper.inaccurateDistanceTo(Double.parseDouble(JSONconcert.getString("lat")), Double.parseDouble(JSONconcert.getString("lon")), latLng);
                } else {
                    distance = 0;
                }
                dbm.updateConcert(
                        JSONconcert.getInt("id"),
                        JSONconcert.getString("artist"),
                        JSONconcert.getString("city"),
                        JSONconcert.getString("spot"),
                        JSONconcert.getInt("day"),
                        JSONconcert.getInt("month"),
                        JSONconcert.getInt("year"),
                        JSONconcert.getString("agency"),
                        JSONconcert.getString("url"),
                        JSONconcert.getString("updated"),
                        JSONconcert.getString("lat"),
                        JSONconcert.getString("lon"),
                        distance,
                        JSONconcert.getString("entrance_fee"));

                loadingDialog.incrementProgressBy(1);
            }
            dbm.endTransaction();
            updateNeeded = true;
        }

        //Pobiera nowe koncerty
        protected void downloadConcerts() throws JSONException {
            //współrzędne z prefs wpisane do latLng
            LatLng latLng = new LatLng(Double.parseDouble(Prefs.getLon(context)), Double.parseDouble(Prefs.getLat(context)));

            Prefs.setLastID(context, jsonLastId);
            JSONArray mJsonArray = mJsonObject.getJSONArray("concerts");
            double distance;
            dbm.beginTransaction();
            for (int i = 0; i < mJsonArray.length(); i++) {
                JSONObject JSONconcert = mJsonArray.getJSONObject(i);
                if (latLng.longitude != -1)
                    distance = mapHelper.inaccurateDistanceTo(Double.parseDouble(JSONconcert.getString("lat")), Double.parseDouble(JSONconcert.getString("lon")), latLng);
                else
                    distance = 0;

                dbm.addConcert(
                        JSONconcert.getInt("id"),
                        JSONconcert.getString("artist"),
                        JSONconcert.getString("city"),
                        JSONconcert.getString("spot"),
                        JSONconcert.getInt("day"),
                        JSONconcert.getInt("month"),
                        JSONconcert.getInt("year"),
                        JSONconcert.getString("agency"),
                        JSONconcert.getString("url"),
                        JSONconcert.getString("updated"),
                        JSONconcert.getString("lat"),
                        JSONconcert.getString("lon"),
                        distance,
                        JSONconcert.getString("entrance_fee"));

                loadingDialog.incrementProgressBy(1);
            }
            dbm.endTransaction();
            updateNeeded = true;
        }

        @Override
        protected void onPostExecute(String s) {
            loadingDialog.dismiss();
            super.onPostExecute(s);
        }

    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    class GetLatLng extends AsyncTask<String, Void, String> {
        LatLng latlng;
        JSONthing jsonthing;
        String id;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mapDialog.setMessage("Łączę się z Google Maps");
            mapDialog.show();
            jsonthing = new JSONthing();
            id = String.valueOf(Prefs.getUserID(getActivity().getApplicationContext())); //stirng żeby się PHPy nie srały
        }

        @Override
        protected String doInBackground(String... params) {
            String city = Prefs.getCity(getActivity());

            if (!city.isEmpty()) {
                if (!id.equals("-1"))
                    updateServerLatLng(city);
                latlng = MapHelper.getLatLongFromAddress(city);
                MainActivity.getDBManager().update(latlng);
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
            params.add(new BasicNameValuePair("user_id", id));
            params.add(new BasicNameValuePair("location", city));
            jsonthing.makeHttpRequest(PHPurls.updateUser.toString(), "POST", params); //TIGHT and ELEGANT
        }

        @Override
        protected void onPostExecute(String city) {
            if (!city.isEmpty()) {
                Prefs.setLat(getActivity(), String.valueOf(latlng.latitude));
                Prefs.setLon(getActivity(), String.valueOf(latlng.longitude));
            }
            mapDialog.dismiss();
            super.onPostExecute(city);
        }
    }
}