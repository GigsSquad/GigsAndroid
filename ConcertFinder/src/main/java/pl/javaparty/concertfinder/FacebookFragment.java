package pl.javaparty.concertfinder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.javaparty.enums.PHPurls;
import pl.javaparty.prefs.PrefsSingleton;
import pl.javaparty.sql.JSONthing;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FacebookFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    /* GOOGLE PLUS */
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private boolean mSignInClicked;
    private boolean mIntentInProgress;

    /* FACEBOOK */
    LoginButton authButton;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    final String GOOGLE_FORMAT = "yyyy-mm-dd";
    final String DB_FORMAT = "dd/mm/yyyy";

    ProgressDialog loadingDialog;
    Intent mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = new Intent(getActivity(), MainActivity.class);

        //FACEBOOK
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);

        //GOOGLE PLUS
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_splash_screen, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        loadingDialog = new ProgressDialog(getActivity());
        loadingDialog.setCancelable(false);

        //GOOGLE PLUS
        view.findViewById(R.id.sign_in_button).setOnClickListener(this);

        //FACEBOOK
        authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("user_location", "user_birthday", "user_likes", "email"));

        return view;
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /* FACEBOOK */
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i("LOGIN", "Zalogowano przez Facebooka");

            // Request user data and show the results
            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null && isOnline()) {
                        String[] columns = new String[10];
                        columns[0] = user.getFirstName().trim();
                        columns[1] = user.getLastName().trim();
                        columns[2] = user.getProperty("email").toString().trim();
                        columns[5] = user.getId();

                        try {
                            columns[3] = user.getBirthday().trim();
                            columns[4] = user.getLocation().getProperty("name").toString().trim();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            columns[3] = "";
                            columns[4] = "";
                        }

                        PrefsSingleton.getInstance().setCity(getActivity(), columns[4]);
                        Toast.makeText(getActivity(), getString(R.string.hello) + ", " + columns[0], Toast.LENGTH_SHORT).show();
                        if (isAdded() && isOnline())
                            new loginUser(columns).execute();
                        else
                            Toast.makeText(getActivity().getApplicationContext(), "Brak połączenia", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else if (state.isClosed()) {
            Log.i("LOGIN", "Wylogowano z Facebooka");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("LOGIN", "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Log.i("LOGIN", "onActivityResult - Google");
            if (resultCode != Activity.RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.reconnect();
            }
        } else {
            Log.i("LOGIN", "onActivityResult - Facebook");
            uiHelper.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i("LOGIN", "onResume");

        //FACEBOOK
        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
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

    /* GOOGLE PLUS */
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    public void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            if (mSignInClicked && result.hasResolution()) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                try {
                    result.startResolutionForResult(getActivity(), RC_SIGN_IN);
                    mIntentInProgress = true;
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button && !mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mSignInClicked = false;
        Log.i("LOGIN", "Zalogowano przez Google+");

        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

//            if (
//                currentPerson.getRelationshipStatus() == 1 &&
//                currentPerson.getAgeRange().getMax() == 26 &&
//                currentPerson.getAgeRange().getMin() == 18 &&
//                currentPerson.getGender() == 0 &&
//                !currentPerson.getCurrentLocation().equals("Kraków") &&
//                currentPerson.hasImage())
//            {
//                //TODO send email
//
//            }


            String[] columns = new String[10];
            columns[0] = currentPerson.getDisplayName().split(" ")[0].trim();
            columns[1] = currentPerson.getDisplayName().split(" ")[1].trim();
            columns[2] = email.trim();
            columns[5] = "-1";

            try {
                columns[3] = currentPerson.getBirthday().trim();
                columns[4] = currentPerson.getPlacesLived().get(0).getValue();
            } catch (NullPointerException e) {
                e.printStackTrace();
                columns[3] = "";
                columns[4] = "";
            }


            Date date = null;
            SimpleDateFormat sdf = new SimpleDateFormat(GOOGLE_FORMAT);
            try {
                date = sdf.parse(columns[3]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            sdf.applyPattern(DB_FORMAT);
            columns[3] = sdf.format(date);

            PrefsSingleton.getInstance().setCity(getActivity(), columns[4]);
            Toast.makeText(getActivity(), getString(R.string.hello) + ", " + columns[0], Toast.LENGTH_SHORT).show();
            if (isAdded() && isOnline())
                new loginUser(columns).execute();
            else
                Toast.makeText(getActivity().getApplicationContext(), "Brak połączenia", Toast.LENGTH_SHORT).show();
        }
    }

    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    /* BAZA DANYCH / PHPY */
    class loginUser extends AsyncTask<String, Void, Integer> {

        String[] array = new String[10];
        JSONthing jsonthing;

        public loginUser(String[] array) {
            this.array = Arrays.copyOf(array, array.length, String[].class);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.setMessage("Logowanie");
            loadingDialog.show();
            jsonthing = new JSONthing();
        }

        @Override
        protected Integer doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<>();

            Log.i("LOGIN", array[5].equals("-1") ? "Zalogowano przez Google+" : "Zalogowano przez Facebooka");
            Log.i("LOGIN", "Imie: \t" + array[0]);
            Log.i("LOGIN", "Nazwisko: \t" + array[1]);
            Log.i("LOGIN", "Email: \t" + array[2]);
            Log.i("LOGIN", "Urodziny: \t" + array[3]);
            Log.i("LOGIN", "Lokalizacja: \t" + array[4]);
            Log.i("LOGIN", "Id: \t" + array[5]);

            //wysyłamy dane użytkownika, jeśli nie ma go w bazie to doda
            params.add(new BasicNameValuePair("firstName", array[0]));
            params.add(new BasicNameValuePair("lastName", array[1]));
            params.add(new BasicNameValuePair("email", array[2]));
            params.add(new BasicNameValuePair("birthday", array[3]));
            params.add(new BasicNameValuePair("location", array[4]));
            params.add(new BasicNameValuePair("fb_id", array[5]));

            JSONObject mJsonObject = jsonthing.makeHttpRequest(PHPurls.login.toString(), "GET", params); //TIGHT and ELEGANT

            int userId = -1;
            try {
                JSONArray mJsonArray = mJsonObject.getJSONArray("login");
                for (int i = 0; i < mJsonArray.length(); i++) {
                    JSONObject JSONlogin = mJsonArray.getJSONObject(i);
                    userId = JSONlogin.getInt("user_id");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("LOGIN", "Pobrany ID z bazy: " + userId);

            return userId;
        }

        @Override
        protected void onPostExecute(Integer userId) {
            Log.i("LOGIN", "ID zapisany do Prefs: " + userId);
            PrefsSingleton.getInstance().setUserID(getActivity(), userId);
            loadingDialog.dismiss();
            startActivity(mainActivity);
            super.onPostExecute(userId);
        }
    }
}
