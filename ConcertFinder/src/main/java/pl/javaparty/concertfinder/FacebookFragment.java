package pl.javaparty.concertfinder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.Toast;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.javaparty.enums.PHPurls;
import pl.javaparty.prefs.Prefs;
import pl.javaparty.sql.JSONthing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FacebookFragment extends Fragment {

    LoginButton authButton;
    Button skipButton;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    ProgressDialog loadingDialog;
    Intent mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
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
        mainActivity = new Intent(getActivity(), MainActivity.class);

        skipButton = (Button) view.findViewById(R.id.skipBtn);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mainActivity);
            }
        });

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

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i("FB", "Logged in...");

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

                        Prefs.setCity(getActivity(), columns[4]);
                        Toast.makeText(getActivity(), getString(R.string.hello) + ", " + columns[0], Toast.LENGTH_SHORT).show();
                        new loginUser(columns).execute();
                    }
                }
            });

        } else if (state.isClosed()) {
            Log.i("FB", "Logged out...");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        int userId = Prefs.getUserID(getActivity());
        if (userId == -1) {
            // brak id użytwnika w Prefs, wiec dajemy mu szansę na zalogowanie się przez facebooka lub pominiecia logowania
            Log.i("LOGIN", "Brak ID w Prefs");
            authButton.setVisibility(View.VISIBLE);
//            skipButton.setVisibility(View.VISIBLE);
        } else { // mamy id w Prefs więc nie pokazujemy przycisków tylko od razu idziemy do aplikacji
            Log.i("LOGIN", "ID znajduje się w Prefs (" + userId + ")");
            authButton.setVisibility(View.INVISIBLE);
//            skipButton.setVisibility(View.INVISIBLE);
            startActivity(mainActivity);
        }

        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed())) {
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

    class loginUser extends AsyncTask<String, Void, String> {

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
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<>();
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
            Prefs.setUserID(getActivity(), userId);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            loadingDialog.dismiss();
            startActivity(mainActivity);
            super.onPostExecute(s);
        }
    }
}
