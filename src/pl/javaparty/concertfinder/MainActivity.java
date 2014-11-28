package pl.javaparty.concertfinder;

import java.io.IOException;

import pl.javaparty.concertmanager.Concert;
import pl.javaparty.concertmanager.ConcertManager;
import pl.javaparty.jsoup.JsoupDownloader;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	JsoupDownloader jsoupDownloader;
	StringBuilder stringBuilder;
	ConcertManager concertMgr;
	AutoCompleteTextView searchBox;
	ArrayAdapter<String> adapter, adapterList;
	ListView concertList;
	TextView artistTextView;
	CheckBox goaheadCheckbox;
	String[] links;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main); // ustawiamy layout z "res > layouot > activity_main.xml"
		searchBox = (AutoCompleteTextView) findViewById(R.id.searchBox);
		concertList = (ListView) findViewById(R.id.concertList);
		artistTextView = (TextView) findViewById(R.id.artistName);
		goaheadCheckbox = (CheckBox) findViewById(R.id.checkBoxGoAhead);

		jsoupDownloader = new JsoupDownloader();
		stringBuilder = new StringBuilder();
		concertMgr = new ConcertManager();

		new DownloadTask().execute();

		concertList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent = new Intent(MainActivity.this, InfoPage.class);
				// intent.putExtra("URL", concert.getURL()); //bêdziemy wysy³aæ konkretny url koncertu
				intent.putExtra("URL", "Clicked: " + position); // bêdziemy wysy³aæ konkretny url koncertu
				startActivity(intent);
			}
		});

		searchBox.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				adapterList = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, concertMgr.getConcerts(searchBox.getText()
						.toString()));
				concertList.setAdapter(adapterList);

				links = new String[adapterList.getCount()];
				// /for (Concert c : concertMgr.getConcerts(searchBox.getText().toString()))
				// {
				// c.getURL();
				// }

				artistTextView.setText(searchBox.getText().toString());
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
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) { // zostanie wykonane po skoñczeniu doInBackground
			super.onPostExecute(result);
			String[] stockArr = new String[concertMgr.getArtists().size()];
			stockArr = concertMgr.getArtists().toArray(stockArr);

			adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, stockArr);

			searchBox.setAdapter(adapter);
			searchBox.setThreshold(1);
		}
	}
}
