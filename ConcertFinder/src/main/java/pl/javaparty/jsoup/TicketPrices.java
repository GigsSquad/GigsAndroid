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

	@Override protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {
		return getPrices();
	}

	protected void onPostExecute(String result) {

		if ((result == null) || (result.equals(" "))) {
			td2.setVisibility(View.VISIBLE);
			td2.setText("Brak informacji o cenach biletów");
		}

		try {

			if (result.equals("")) {
				td1.setVisibility(View.VISIBLE);
				td1.setText("Bilety wyprzedane");
			}

			prices = result.split(" ");
			if ((prices.length < 1))
				td2.setText("Brak informacji o cenach biletów");

			if ((prices.length >= 1) && (prices[0] != null && !prices[0].equals(""))) {
				td1.setVisibility(View.VISIBLE);
				td1.setText(prices[0] + "zł");
			}

			if ((prices.length >= 2) && (prices[1] != null)) {
				td2.setVisibility(View.VISIBLE);
				td2.setText(prices[1] + "zł");
			}
			if ((prices.length >= 3) && (prices[2] != null)) {
				td3.setVisibility(View.VISIBLE);
				td3.setText(prices[2] + "zł");
			}
		} catch (NullPointerException exc) {
			td2.setVisibility(View.VISIBLE);
			td2.setText("Brak informacji o cenach biletów");
		}

	}

	private String getPrices() {
		if ("TICKETPRO".equals(agencyName)) {
			return getPricesFromTicketPro();
		} else if ("GOAHEAD".equals(agencyName)) {
			return getPricesFromGoAhead();
		} else if ("ALTERART".equals(agencyName) || "EBILET".equals(agencyName) || "LIVENATION".equals(agencyName))
			return " "; // magic, lol
		return null;
	}

	private String getPricesFromTicketPro() {
		Element el = null;
		try {
			Document doc = Jsoup.connect(url).timeout(1000000).get();
			el = doc.select("div[id=poleCena]").first();

		} catch (IOException e) {
			Log.i("PobieranieCenyKoncertu", "Blad podczas pobierania cennika");
		}

		return getRealPrices(el.text());
	}

	private String getPricesFromGoAhead() {

		try {
			Document doc = Jsoup.connect(url).timeout(1000000).get();
			Element el = doc.getElementsByClass("kk2").get(6);
			return getRealPrices(el.text());
		} catch (IOException e) {
            Log.i("PobieranieCenyKoncertu", "Blad podczas pobierania cennika");
            return "";
        } catch (RuntimeException re) {
            Log.i("PobieranieCenyKoncertu", "Blad podczas pobierania cennika");
            return "";
        }
    }

	private String getRealPrices(String rawString) {
		String[] potentialPrices;
		String[] realPrices = new String[3];
		try {
			String onlyNumbers = rawString.replaceAll("[^0-9]+", " ");
			rawString = rawString.trim();// gdyby komus sie 2 spacyjki nacislo
			onlyNumbers = onlyNumbers.trim();
			//Log.i("TicketPrices", "Same numery:" + onlyNumbers);
			potentialPrices = onlyNumbers.split(" "); //wszystkie liczby jakie sa w danym stringu
			//Log.i("TicketPrices", "Potencjalna cena: " + potentialPrices[0]);
			int j = 0;
			for (int i = 0; i < potentialPrices.length; i++) {
				int postion = rawString.indexOf(potentialPrices[i]);  //obliczamy pozycje
				int resolution = potentialPrices[i].length();  // jak dluga jest liczba
				//Log.i("TicketPrices", "dwa");

				if ((rawString.charAt(postion + resolution) == 'z') || (rawString.charAt(postion + resolution + 1) == 'z')) {
					realPrices[j] = potentialPrices[i];
					j++;
					if (j == 3)
						break;
				}
				//Log.i("TicketPrices", "trzy");

			}
		} catch (Exception exc) {
			return null;
		}

		String tmp = "";
		for (String st : realPrices) {
			if (st != null)
				tmp = tmp + (st + " ");
		}

		return tmp;
	}
}


