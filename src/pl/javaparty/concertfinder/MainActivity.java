package pl.javaparty.concertfinder;

import java.io.IOException;
import java.util.ArrayList;

import pl.javaparty.jsoup.Concert;
import pl.javaparty.jsoup.JsoupDownloader;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static ArrayList<Concert> concerts;
	Button downloadData;
	JsoupDownloader jsoupDownloader;
	StringBuilder stringBuilder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); // ustawiamy layout z "res > layouot > activity_main.xml"
		downloadData = (Button) findViewById(R.id.downloadButton); // przypisujemy do downloadData przycisk który jest w layoucie "res > layouot > activity_main.xml"

		jsoupDownloader = new JsoupDownloader();
		stringBuilder = new StringBuilder();
		concerts = new ArrayList<Concert>();

		downloadData.setOnClickListener(new OnClickListener() { 

			@Override
			public void onClick(View v) { // to zostanie wykonane po klikniêciu w przycisk "Pobierz" na ekranie 
				new DownloadTask().execute(); // towrzymy i uruchamiamy obiekt który w tle pobierze dane 

			}
		});
	}

	private class DownloadTask extends AsyncTask<Void, Void, String> {
		//TODO: zrobiæ informacje ze stanem pobierania

		@Override
		protected String doInBackground(Void... params) { 

			try {
				jsoupDownloader.getData(); // pobieramy dane 
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for (Concert c : concerts)
				stringBuilder.append(c.toString()); //dodajemy informacjê o ka¿dym koncercie do stringa 

			return null;
		}

		@Override 
		protected void onPostExecute(String result) { // zostanie wykonane po skoñczeniu doInBackground
			super.onPostExecute(result);
			((TextView) findViewById(R.id.downloadText)).setText(stringBuilder.toString()); //zamiast Hello World ustawiamy stringa z koncertami
			Toast.makeText(getApplicationContext(), "Pobrano!", Toast.LENGTH_LONG).show(); // wyœwietlanie powiadomienia 
		}
	}

}