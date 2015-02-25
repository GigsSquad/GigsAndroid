package pl.javaparty.concertfinder;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import pl.javaparty.prefs.Prefs;

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
		authButton.setReadPermissions(Arrays.asList("user_location", "user_birthday", "user_likes"));

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
}
