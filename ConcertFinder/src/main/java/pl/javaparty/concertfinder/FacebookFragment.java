package pl.javaparty.concertfinder;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.javaparty.prefs.Prefs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class FacebookFragment extends Fragment {

	private static final String TAG = "MainFragment";
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
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_splash_screen, container, false);

		//ja pierdole... potrzebne  bo inaczej rzuca wyjątkiem, bez pozdrowień
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		Button skip = (Button) view.findViewById(R.id.skipBtn);
		skip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getActivity(), MainActivity.class);
				startActivity(intent);
			}
		});

		Button spotifyBtn = (Button) view.findViewById(R.id.spotifyBtn);
		spotifyBtn.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SpotifyActivity.class);
				startActivity(intent);
			}
		});

		LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
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
			Log.i(TAG, "Logged in...");

			// Request user data and show the results
			Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user != null && isOnline()) {
						Toast.makeText(getActivity(), "Uszanowanko, " + user.getFirstName(), Toast.LENGTH_SHORT).show();
						Prefs.setCity(getActivity(), "" + user.getLocation().getProperty("name").toString().trim());
						//TODO jak chcesz zrobić rejstrację to jestes w dobrym miejscu
						//TODO tutaj jakieś rejestracje się porobi i dałnlołder który bedzie pobierać nagie foteczki if(sex() == woman && scale() >= 8)
						try {
							insertUser(user.getFirstName(), user.getLastName(), user.getProperty("email").toString(),
									user.getBirthday(), user.getLocation().getProperty("name").toString(), user.getId());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
			Intent intent = new Intent(getActivity(), MainActivity.class);
			startActivity(intent);
		} else if (state.isClosed()) {
			Log.i(TAG, "Logged out...");
		}
	}

	@Override
	public void onResume() {
		super.onResume();

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

	//dodaje użytkownika do bazy
	private void insertUser(String firstName, String lastName, String email, String birthday, String location, String fbId) throws JSONException {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://www.javaparty.com.pl/register.php");
		JSONObject json = new JSONObject();

		try {
			httppost.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
			json.put("firstName", firstName);
			json.put("lastName", lastName);
			json.put("email", email);
			json.put("birthday", birthday);
			json.put("location", location);
			json.put("fbId", fbId);

			JSONArray postjson = new JSONArray();
			postjson.put(json);

			// Post the data:
			httppost.setHeader("json", json.toString());
			httppost.getParams().setParameter("jsonpost", postjson);

			// Execute HTTP Post Request
			System.out.print(json);
			HttpResponse response = httpclient.execute(httppost);

			// for JSON:
			if (response != null) {
				InputStream is = response.getEntity().getContent();

				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();

				String line = null;
				try {
					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				Log.i("JSON", "Co wypluł serwer: " + sb.toString());
				Prefs.setUserID(getActivity(), Integer.parseInt(sb.toString()));
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		Log.i("JSON", "REJESTRACJA POSZŁA");
	}
}
