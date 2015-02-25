package pl.javaparty.concertfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.playback.*;

/**
 * Created by Evelan on 22-02-2015 - 23:43
 */
public class SpotifyActivity extends Activity implements PlayerNotificationCallback, ConnectionStateCallback {

	private static final String CLIENT_ID = "27dae8518a194912a7c54170d8508025";
	private static final String REDIRECT_URI = "concertfinder://callback";
	private static final int REQUEST_CODE = 1337;
	private Player mPlayer;
	TextView trackTV;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spotify);

		trackTV = (TextView) findViewById(R.id.track);

		Button doThings = (Button) findViewById(R.id.thingBtn);
		doThings.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mPlayer.addConnectionStateCallback(SpotifyActivity.this);
				mPlayer.addPlayerNotificationCallback(SpotifyActivity.this);
				mPlayer.play("spotify:track:3LmpQiFNgFCnvAnhhvKUyI");
			}
		});

		Button backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
				startActivity(intent);
			}
		});

		Button shitBtn = (Button) findViewById(R.id.shitBtn);
		shitBtn.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				mPlayer.pause();
			}
		});



		/* SPOTIFY */
		AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
				AuthenticationResponse.Type.TOKEN,
				REDIRECT_URI);
		builder.setScopes(new String[] { "user-read-private", "streaming" });
		AuthenticationRequest request = builder.build();
		AuthenticationClient.openLoginActivity(SpotifyActivity.this, REQUEST_CODE, request);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		// Check if result comes from the correct activity
		if (requestCode == REQUEST_CODE) {
			AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
			if (response.getType() == AuthenticationResponse.Type.TOKEN) {
				Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
				mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
					@Override
					public void onInitialized(Player player) {
						mPlayer.addConnectionStateCallback(SpotifyActivity.this);
						mPlayer.addPlayerNotificationCallback(SpotifyActivity.this);
						mPlayer.play("spotify:track:3LmpQiFNgFCnvAnhhvKUyI");
					}

					@Override
					public void onError(Throwable throwable) {
						Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
					}
				});
			}
		}
	}

	@Override
	public void onLoggedIn() {
		Log.d("MainActivity", "User logged in");
	}

	@Override
	public void onLoggedOut() {
		Log.d("MainActivity", "User logged out");
	}

	@Override
	public void onLoginFailed(Throwable error) {
		Log.d("MainActivity", "Login failed");
	}

	@Override
	public void onTemporaryError() {
		Log.d("MainActivity", "Temporary error occurred");
	}

	@Override
	public void onConnectionMessage(String message) {
		Log.d("MainActivity", "Received connection message: " + message);
	}

	@Override
	public void onPlaybackEvent(PlayerNotificationCallback.EventType eventType, PlayerState playerState) {
		Log.d("MainActivity", "Playback event received: " + eventType.name());
	}

	@Override
	public void onPlaybackError(PlayerNotificationCallback.ErrorType errorType, String errorDetails) {
		Log.d("MainActivity", "Playback error received: " + errorType.name());
	}

	@Override
	public void onDestroy() {
		Spotify.destroyPlayer(this);
		super.onDestroy();
	}
}
