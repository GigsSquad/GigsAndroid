package pl.javaparty.jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.javaparty.concertmanager.Concert;
import pl.javaparty.concertmanager.Concert.AgencyName;
import pl.javaparty.concertmanager.ConcertManager;

public class JDAlterArt implements JSoupDownloader{

	private ConcertManager concertMgr;
	private final static String URL_ALTERART = new String("http://alterart.pl/pl/Archiwum");
	
	public JDAlterArt() {
		concertMgr = new ConcertManager();
	}
	
	@Override
	public void getData() throws IOException {
		Document doc = Jsoup.connect(URL_ALTERART).get();
		Elements names = doc.getElementsByClass("concert-box-data-name");
		ArrayList<String> urls = new ArrayList<String>();
		for(Element el : names)
			urls.add(el.select("a").attr("href"));
		Elements datesPlaces = doc.getElementsByClass("concert-box-data-date");
		Elements dates = new Elements(); 
		Elements places = new Elements();
		for(int i = 0;i<datesPlaces.size();i++){
			if(i%2==0)
				dates.add(datesPlaces.get(i));
			else places.add(datesPlaces.get(i));
		}
		Elements cities = doc.getElementsByClass("concert-box-data-city");
		for(int i = 0; i<names.size(); i++){
			concertMgr.getList().add(new Concert(names.get(i).text(),cities.get(i).text()+" "+
		places.get(i).text(),dates.get(i).text(),AgencyName.ALTERART,urls.get(i)));
		}
	}

	@Override
	public HashSet<String> getArtists() throws IOException {
		Document doc = Jsoup.connect(URL_ALTERART).get();
		HashSet<String> res =  new HashSet<String>();
		Elements names = doc.getElementsByClass("concert-box-data-name");
		for(Element e : names)
			res.add(e.text());
		return res;
	}

	@Override
	public HashSet<String> getPlaces() throws IOException {
		Document doc = Jsoup.connect(URL_ALTERART).get();
		HashSet<String> res =  new HashSet<String>();
		Elements datesPlaces = doc.getElementsByClass("concert-box-data-date");
		Elements cities = doc.getElementsByClass("concert-box-data-city");
		for(int i = 0;i<cities.size();i++)
			res.add(cities.get(i).text()+" "+datesPlaces.get(2*i).text());
		return res;
	}

	@Override
	public void getMoreData(Concert c) throws IOException {
		/*
		 * potem
		 */
	}


}
