import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
//Nie bede zasmiecal gita biblioteka JSoup, trzeba ja sobie dopisac do projektu, zeby dzialalo.
public class DataGetter
{
	ArrayList<Concert> concerts;
	
	public DataGetter()
	{
		concerts = new ArrayList<>();
	}
	
	public void getter() throws IOException
	{
		Document doc = Jsoup.connect("http://www.go-ahead.pl/pl/koncerty.html").get();
		Elements concertData = doc.getElementsByClass("b_c_left");
		for(Element el: concertData)
		{
			Element name = el.getElementsByClass("b_c_b").first();
			String conName = name.text();
			Element place = el.getElementsByClass("b_c_cp").first();
			String conPlace = place.text();
			Element date = el.getElementsByClass("b_c_d").first();
			String conDate = date.text();
			concerts.add(new Concert(conName, conPlace, conDate));
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		DataGetter dg = new DataGetter();
		dg.getter();
		for(Concert c: dg.concerts)
		{
			System.out.println(c.toString());
		}
	}
}
