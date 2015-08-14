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
import pl.javaparty.concertfinder.LatLngConnector;
import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import pl.javaparty.imageloader.FileExplorer;
import pl.javaparty.map.MapHelper;
import pl.javaparty.prefs.Prefs;
import pl.javaparty.sql.DatabaseManager;
import pl.javaparty.utils.UtilsObject;

import java.util.Arrays;

public class SettingsFragment extends Fragment {

    AutoCompleteTextView citySearchBox;
    Button saveButton, clearButton, downloadButton;
    RadioButton distanceSort, dateSort;
    Context context;
    ArrayAdapter<CharSequence> adapter;
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

        if (Prefs.getInstance(context).getCity() != null)
            citySearchBox.setText(Prefs.getInstance(context).getCity());

        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs.getInstance(context).setCity(citySearchBox.getText().toString());

                Prefs.getInstance(context).setSortOrder(sortOrder);
                Log.i("SETTINGS", "Zapisano");
                Log.i("SETTINGS", "Miasto: " + citySearchBox.getText().toString());

                Toast.makeText(getActivity(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
                new LatLngConnector(context).execute();
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
                if (UtilsObject.isOnline(context))
                    new LatLngConnector(context).execute();
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
                            DatabaseManager.getInstance(getActivity().getApplicationContext()).deleteTables();
                            Log.i("SETTINGS", "Wyczyszczono bazę");
                            MainActivity.updateCounters();
                            Prefs.getInstance(getActivity()).setLastID(-1);
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
        sortOrder = Prefs.getInstance(context).getSortOrder();
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

}