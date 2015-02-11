package pl.javaparty.jsoup;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Arrays;

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
        String rawString;
        String[] potentialPrices;
		try {
            rawString = result.replaceAll("[^0-9]+", " ");
            result = result.trim(); // gdyby komus sie 2 spacyjki nacislo
            rawString = rawString.trim();
			potentialPrices = rawString.split(" "); //wszystkie liczby jakie sa w danym stringu

            prices = new String[3];
            int j=0;
            for(int i=0;i<potentialPrices.length;i++)
            {
               int postion =  result.indexOf(potentialPrices[i]);  //obliczamy pozycje
               int resolution = potentialPrices[i].length();
               if((result.charAt(postion+resolution)=='z')||(result.charAt(postion+resolution+1)=='z'))
               {
                   prices[j] = potentialPrices[i];
                   j++;
                   if(j==3)
                       break;
               }


            }
		} catch (Exception exc) {
			td2.setVisibility(View.VISIBLE);
			td2.setText("Brak informacji o cenach biletów");
		}



			if ((prices.length < 1))
				td2.setText("Brak informacji o cenach biletów");

			if ((prices.length >= 1)&&(prices[0]!=null)) {
				td1.setVisibility(View.VISIBLE);
				td1.setText(prices[0] + "zł");

			}

			if ((prices.length >= 2)&&(prices[1]!=null)) {
				td2.setVisibility(View.VISIBLE);
				td2.setText(prices[1] + "zł");

			}
			if ((prices.length >= 3)&&(prices[2]!=null)) {
				td3.setVisibility(View.VISIBLE);
				td3.setText(prices[2] + "zł");
			}

            if(result.equals(""))
                td1.setText("Bilety wyprzedane");
	}

	private String getPrices() {
		if ("TICKETPRO".equals(agencyName)) {
			return getPricesFromTicketPro();
		} else if ("GOAHEAD".equals(agencyName)) {
			return getPricesFromGoAhead();
		}else if("ALTERART".equals(agencyName)||"EBILET".equals(agencyName)||"LIVENATION".equals(agencyName))
            return " "; // magic
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

		return el.text();
	}

	private String getPricesFromGoAhead() {
		Element el=null;
		try {
			Document doc = Jsoup.connect(url).timeout(1000000).get();
			el = doc.getElementsByClass("kk2").get(6);

           	} catch (IOException e) {
			// Log.i("PobieranieCenyKoncertu", "Blad podczas pobierania cennika");
		}
		return el.text();
	}
}


