package pl.javaparty.concertfinder;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class InfoPage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_page);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		savedInstanceState = getIntent().getExtras();
		Toast.makeText(InfoPage.this, savedInstanceState.getString("URL"), Toast.LENGTH_SHORT).show();
		//TODO pobieramy informacje z url, ustawiamy na ekranie pobrane informacje
	}
}
