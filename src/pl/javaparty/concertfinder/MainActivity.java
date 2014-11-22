package pl.javaparty.concertfinder;

import java.io.IOException;

import pl.javaparty.concertmanager.Concert;
import pl.javaparty.concertmanager.ConcertManager;
import pl.javaparty.jsoup.JsoupDownloader;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	Button downloadData;
	JsoupDownloader jsoupDownloader;
	StringBuilder stringBuilder;
	ConcertManager concertMgr;
	AutoCompleteTextView searchBox;
	ArrayAdapter<String> adapter;
	TextView downloadText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); // ustawiamy layout z "res > layouot > activity_main.xml"
		downloadData = (Button) findViewById(R.id.downloadButton);
		searchBox = (AutoCompleteTextView) findViewById(R.id.searchBox);
		downloadText = (TextView) findViewById(R.id.downloadText);
		
		jsoupDownloader = new JsoupDownloader();
		stringBuilder = new StringBuilder();
		concertMgr = new ConcertManager();
		new DownloadTask().execute();
		
		downloadData.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				downloadText.setText(searchBox.getText() + "\n\n" + concertMgr.searchArtis(searchBox.getText().toString()));
				searchBox.setText("");
			}
		});
	}

	private class DownloadTask extends AsyncTask<Void, Void, String> {
		// TODO: zrobiæ informacje ze stanem pobierania

		@Override
		protected String doInBackground(Void... params) {

			try {
				jsoupDownloader.getData(); // pobieramy dane
			} catch (IOException e) {
				e.printStackTrace();
			}

			for (Concert c : concertMgr.getList())
				stringBuilder.append(c.toString()); // dodajemy informacjê o ka¿dym koncercie do stringa

			return null;
		}

		@Override
		protected void onPreExecute() {
			Toast.makeText(getApplicationContext(), "Pobieram...", Toast.LENGTH_SHORT).show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) { // zostanie wykonane po skoñczeniu doInBackground
			super.onPostExecute(result);
			downloadText.setText(stringBuilder.toString());			
			
			String[] stockArr = new String[concertMgr.getArtists().size()];
			stockArr = concertMgr.getArtists().toArray(stockArr);
			
			adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_dropdown_item_1line,stockArr);

			searchBox.setAdapter(adapter);
			searchBox.setThreshold(1);

			Toast.makeText(getApplicationContext(), "Pobrano!", Toast.LENGTH_LONG).show(); // wyœwietlanie powiadomienia
		}
	}
}