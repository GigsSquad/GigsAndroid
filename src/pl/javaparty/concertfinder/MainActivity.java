package pl.javaparty.concertfinder;

import java.io.IOException;

import pl.javaparty.concertmanager.Concert;
import pl.javaparty.concertmanager.ConcertManager;
import pl.javaparty.jsoup.JDAlterArt;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	Button downloadData;
	//JDGoAhead jdGoAhead;
	JDAlterArt jdAlterArt;
	StringBuilder stringBuilder;
	ConcertManager concertMgr;
	AutoCompleteTextView searchBox;
	ArrayAdapter<String> adapter, adapterList;
	ListView concertList;
	TextView artistTextView;
	CheckBox goaheadCheckbox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main); // ustawiamy layout z "res > layouot > activity_main.xml"
		downloadData = (Button) findViewById(R.id.downloadButton);
		searchBox = (AutoCompleteTextView) findViewById(R.id.searchBox);
		concertList = (ListView) findViewById(R.id.concertList);
		artistTextView = (TextView) findViewById(R.id.artistName);
		goaheadCheckbox = (CheckBox) findViewById(R.id.checkBoxGoAhead);
		
		//jdGoAhead = new JDGoAhead();
		jdAlterArt = new JDAlterArt();
		stringBuilder = new StringBuilder();
		concertMgr = new ConcertManager();

		new DownloadTask().execute();

		downloadData.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				adapterList = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, concertMgr.getConcerts(searchBox.getText()
						.toString()));
				concertList.setAdapter(adapterList);
				artistTextView.setText(searchBox.getText().toString());
				searchBox.setText("");
			}
		});
		
		concertList.setOnItemClickListener(new OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> parent, View view, int position,
	                    long id) {
	                Intent intent = new Intent(MainActivity.this, InfoPage.class);
	                //intent.putExtra("PLACE", );
	                startActivity(intent);
	            }
	        });
		
	}

	private class DownloadTask extends AsyncTask<Void, Void, String> {
		// TODO: zrobiæ informacje ze stanem pobierania

		@Override
		protected String doInBackground(Void... params) {

			try {
				//jdGoAhead.getData(); // pobieramy dane
				jdAlterArt.getData();
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
			String[] stockArr = new String[concertMgr.getArtists().size()];
			stockArr = concertMgr.getArtists().toArray(stockArr);

			adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, stockArr);

			searchBox.setAdapter(adapter);
			searchBox.setThreshold(1);

			Toast.makeText(getApplicationContext(), "Pobrano!", Toast.LENGTH_LONG).show(); // wyœwietlanie powiadomienia
		}
	}
}
