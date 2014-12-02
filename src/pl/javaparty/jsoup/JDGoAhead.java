package pl.javaparty.jsoup;

import java.io.IOException;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.android.gms.internal.db;

import pl.javaparty.concertmanager.Concert;
import pl.javaparty.concertmanager.Concert.AgencyName;
import sql.dbManager;
import android.content.Context;

public class JDGoAhead implements JSoupDownloader
{
	private dbManager dbm;
	private final static String URL_GOAHEAD = new String("http://www.go-ahead.pl/pl/koncerty.html");
	
	public JDGoAhead(Context context)
	{
		dbm = new dbManager(context);
	}
	
	public void getData() throws IOException
	{
		
		Document doc = Jsoup.connect(URL_GOAHEAD).get();
		Elements concertData = doc.getElementsByClass("b_c");
		for (Element el : concertData)
		{
			String conUrl = el.attr("href");
			String conName = el.getElementsByClass("b_c_b").first().text();
			String conPlace = el.getElementsByClass("b_c_cp").first().text();
			String conDate = el.getElementsByClass("b_c_d").first().text();
			String conCity = conDate.split(" ")[0];
			String conSpot = conDate.split(conCity+" ")[0];
			int conDay = Integer.valueOf(conDate.split(" ")[0]);
			String[] months = { "st", "lu", "mar", "kw", "maj", "cz", "lip", "si", "wr", "pa", "lis", "gr" };
			int conMonth = 0;
			while (!conDate.split(" ")[1].startsWith(months[conMonth]))
				conMonth++;
			conMonth++;
			int conYear = Integer.valueOf(conDate.split(" ")[2]);
			dbm.addConcert(conName, conDay, conMonth, conYear, conCity, conSpot, "GOAHEAD", conUrl);
			System.out.println("Dodano");
		}
	}

	
	/*public void getMoreData(Concert concert) throws IOException//GoAhead
	{
		Document doc = Jsoup.connect(concert.getURL()).get();
		Elements concertData = doc.getElementsByClass("kk2");
		int index = 0;
		String name = concertData.get(index++).text();//nie wiem co z tym zrobic, zostawiac, czy nie?
		String date = concertData.get(index++).text();
		String city = concertData.get(index++).text();
		String place = concertData.get(index++).text();
		String adress = concertData.get(index++).text();
		String entry = concertData.get(index++).text();
		String ticketsPrice = concertData.get(index++).text();
		String whereToBuy = concertData.get(index++).text();
		concert.setMoreData(adress, entry, ticketsPrice);
		//return String.format("%s%n%s%n%s %s %s%n%s%n%s%n%s", name, date, city, place, adress,entry,ticketsPrice,whereToBuy);
	}*/
}
