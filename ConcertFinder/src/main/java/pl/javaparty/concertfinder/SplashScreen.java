package pl.javaparty.concertfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SplashScreen extends FragmentActivity {

	Button skipBtn;
	private FacebookFragment facebookFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

		if (savedInstanceState == null) {
			// Add the fragment on initial activity setup
			facebookFragment = new FacebookFragment();
			getSupportFragmentManager()
					.beginTransaction()
					.add(android.R.id.content, facebookFragment)
					.commit();
		} else {
			// Or set the fragment from restored state info
			facebookFragment = (FacebookFragment) getSupportFragmentManager()
					.findFragmentById(android.R.id.content);
		}

		//findViewById(R.id.sign_in_button).setOnClickListener(SplashScreen.this);

		skipBtn = (Button) findViewById(R.id.skipBtn);
		skipBtn.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {


				Intent intent = new Intent(getApplicationContext(), MainActivity.class);


                startActivity(intent);
			}
		});
	}


}
