package pl.javaparty.jsoup;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.javaparty.concertfinder.ConcertManager;

//Nie bede zasmiecal gita biblioteka JSoup, trzeba ja sobie dopisac do projektu, zeby dzialalo.
//TODO: dodac link do kazdego koncertu ktory zawiera szczegó³owe informacje, sam link, bedzie do zapisywane w Concert
//TODO: pobieranie reszty szczegó³owych informacji o koncercie po podaniu linka do metody
//TODO mo¿liwoœæ pobrania TYLKO listy artystów lub/i miejsc, do wyszukiwania;
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
		Elements concertData = doc.getElementsByClass("b_c_left");
		for (Element el : concertData)
		{
			Element name = el.getElementsByClass("b_c_b").first();
			String conName = name.text();
			Element place = el.getElementsByClass("b_c_cp").first();
			String conPlace = place.text();
			Element date = el.getElementsByClass("b_c_d").first();
			String conDate = date.text();
			concertMgr.getList().add(new Concert(conName, conPlace, conDate));
		}
	}
}
