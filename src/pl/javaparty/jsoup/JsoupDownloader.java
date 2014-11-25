package pl.javaparty.jsoup;

import java.io.IOException;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.javaparty.concertmanager.Concert;
import pl.javaparty.concertmanager.Concert.AgencyName;
import pl.javaparty.concertmanager.ConcertManager;

public class JsoupDownloader
{
	private ConcertManager concertMgr;
	private final static String URL_GOAHEAD = new String("http://www.go-ahead.pl/pl/koncerty.html");
	
	public JsoupDownloader()
	{
		concertMgr = new ConcertManager();
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
			concertMgr.getList().add(new Concert(conName, conPlace, conDate, AgencyName.GOAHEAD,conUrl));
		}
	}
	
	//pobiera wykonawcow, nie dubluje wpisow
	public HashSet<String> getArtists() throws IOException
	{
		HashSet<String> artists = new HashSet<String>();
		Document doc = Jsoup.connect(URL_GOAHEAD).get();
		Elements concertData = doc.getElementsByClass("b_c_left");
		for (Element el : concertData)
		{
			Element name = el.getElementsByClass("b_c_b").first();
			String conName = name.text();
			artists.add(conName);
		}
		return artists;
	}
	
	//pobiera same miejsca koncertow, nie dubluje wpisow
	public HashSet<String> getPlaces() throws IOException
	{
		HashSet<String> places = new HashSet<String>();
		Document doc = Jsoup.connect(URL_GOAHEAD).get();
		Elements concertData = doc.getElementsByClass("b_c_left");
		for (Element el : concertData)
		{
			Element place = el.getElementsByClass("b_c_cp").first();
			String conPlace = place.text();
			places.add(conPlace);
		}
		return places;
	}
	
	public void getMoreDataAboutConcert(Concert concert) throws IOException//GoAhead
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
	}
}
