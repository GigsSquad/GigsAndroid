package pl.javaparty.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
    Button saveButton, clearButton, betaButton;
    RadioButton distanceSort, dateSort;
    Context context;
    ArrayAdapter<CharSequence> adapter;
    static dbManager dbm;
    ProgressDialog mapDialog;
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
        betaButton = (Button) view.findViewById(R.id.betaButton);
        distanceSort = (RadioButton) view.findViewById(R.id.radioButtonDist);
        dateSort = (RadioButton) view.findViewById(R.id.radioButtonDate);
        mapDialog = new ProgressDialog(getActivity());
        mapDialog.setCancelable(false);

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

        betaButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/u/0/communities/118084215751714430151")));
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