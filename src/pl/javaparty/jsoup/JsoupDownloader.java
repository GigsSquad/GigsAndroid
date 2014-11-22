package pl.javaparty.jsoup;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.javaparty.concertmanager.Concert;
import pl.javaparty.concertmanager.Concert.AgencyName;
import pl.javaparty.concertmanager.ConcertManager;

//Nie bede zasmiecal gita biblioteka JSoup, trzeba ja sobie dopisac do projektu, zeby dzialalo.
//TODO: dodac link do kazdego koncertu ktory zawiera szczeg�owe informacje, sam link, bedzie do zapisywane w Concert
//TODO: pobieranie reszty szczeg�owych informacji o koncercie po podaniu linka do metody
//TODO mo�liwo�� pobrania TYLKO listy artyst�w lub/i miejsc, do wyszukiwania;
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
			
			String conName = el.getElementsByClass("b_c_b").first().text();
			Element place = el.getElementsByClass("b_c_cp").first();
			String conPlace = place.text();
			Element date = el.getElementsByClass("b_c_d").first();
			String conDate = date.text();
			Element url = el.getElementsByClass("b_c b_c_active").first();
			String conUrl = url.text();
			concertMgr.getList().add(new Concert(conName, conPlace, conDate, AgencyName.GOAHEAD,conUrl));
		}
	}
}
