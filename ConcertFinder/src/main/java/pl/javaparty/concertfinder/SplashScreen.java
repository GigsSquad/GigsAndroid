package pl.javaparty.concertfinder;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

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
