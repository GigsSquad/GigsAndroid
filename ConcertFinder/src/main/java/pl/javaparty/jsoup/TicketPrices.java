package pl.javaparty.jsoup;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * Created by Rafal on 2015-02-02.
 */
public class TicketPrices extends AsyncTask<String, Void, String> {
	String url, agencyName;
	TextView td1, td2, td3;

	String[] prices;

	public TicketPrices(String url, String agencyName, TextView td1, TextView td2, TextView td3) {
		super();
		this.url = url;
		this.agencyName = agencyName;
		this.td1 = td1;
		this.td2 = td2;
		this.td3 = td3;

	}

	@Override
	protected String doInBackground(String... params) {
		return getPrices();
	}

	protected void onPostExecute(String result) {

		try {
			prices = result.split(" ");
		} catch (Exception exc) {
			td2.setVisibility(View.VISIBLE);
			td2.setText("Brak informacji o cenach biletów");
		}

		if (prices != null) {

			if (prices.length < 1)
				td2.setText("Brak informacji o cenach biletów");

			if (prices.length >= 1) {
				td1.setVisibility(View.VISIBLE);
				td1.setText(prices[0] + "zł");

			}

			if (prices.length >= 2) {
				td2.setVisibility(View.VISIBLE);
				td2.setText(prices[1] + "zł");

			}
			if (prices.length >= 3) {
				td3.setVisibility(View.VISIBLE);
				td3.setText(prices[2] + "zł");
			}
		}
	}

	private String getPrices() {
		if ("TICKETPRO".equals(agencyName)) {
			return getPricesFromTicketPro();
		} else if ("GOAHEAD".equals(agencyName)) {
			return getPricesFromGoAhead();
		}
		return null;
	}

	private String getPricesFromTicketPro() {
		String rawString = null;
		Log.i("rafal", "w do in background przed ifem");
		try {
			Document doc = Jsoup.connect(url).timeout(1000000).get();
			Element el = doc.select("div[id=poleCena]").first();
			rawString = el.text();

			rawString = rawString.replaceAll("[^0-9]+", " ");
			rawString = rawString.trim();

			// Log.i("rafal", rawString);

		} catch (IOException e) {
			Log.i("PobieranieCenyKoncertu", "Blad podczas pobierania cennika");
		}

		return rawString;
	}

	private String getPricesFromGoAhead() {
		String rawString = null;
		try {
			Document doc = Jsoup.connect(url).timeout(1000000).get();
			Element el = doc.getElementsByClass("kk2").get(6);
			rawString = el.text();

			rawString = rawString.replaceAll("[^0-9]+", " ");
			rawString = rawString.trim();

		} catch (IOException e) {
			//  Log.i("PobieranieCenyKoncertu", "Blad podczas pobierania cennika");
		}
		return rawString;
	}
}


