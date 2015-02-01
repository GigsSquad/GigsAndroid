package pl.javaparty.jsoup;

import java.io.IOException;




import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.javaparty.sql.dbManager;


public class JDLiveNation {
	
	
	
	private dbManager dbm;
	private static final String URL_LIVE_NATION= new String("http://www.livenation.pl/event/allevents?page=1");
	
	public JDLiveNation(dbManager dbm){
		
		this.dbm=dbm;
	}
	
	
	public void getData() throws IOException{
		boolean end= false;
		String adres= URL_LIVE_NATION;
		Document doc;
		while(!end){
			doc = Jsoup.connect(adres).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
				      .referrer("http://www.google.com")
				      .get();
		Elements concertData = doc.select("div.tbl_r");
		for (Element concert:concertData){
			String url = "http://www.livenation.pl"+concert.select("div.eventName").select("a").attr("href");
			String artist= concert.select("div.eventName").text();
			
			String data = concert.select("div.dateFrom").text();
			String date[] = data.split(" ");
			int day = Integer.parseInt(date[0]);
			int month=0;

			if(date[1].equals("sty"))
				month=1;
			else if(date[1].equals("lut"))
				month=2;
			else if(date[1].equals("mar"))
				month=3;
			else if(date[1].equals("kwi"))
				month=4;
			else if(date[1].equals("maj"))
				month=5;
			else if(date[1].equals("cze"))
				month=6;
			else if(date[1].equals("lip"))
				month=7;
			else if(date[1].equals("sie"))
				month=8;
			else if(date[1].equals("wrz"))
				month=9;
			else if(date[1].equals("paź"))
				month=10;
			else if(date[1].equals("lis"))
				month=11;
			else if(date[1].equals("gru"))
				month=12;
			data=null;

			int year = Integer.parseInt(date[2]);
			String place = concert.getElementsByClass("venueName").text();
			String city= concert.getElementsByClass("venueCity").text();
			
			dbm.addConcert(artist,city,place, day,month,year,"LIVENATION", url);
		}
		Elements next = doc.getElementsByClass("next");
		String conUrl=next.select("a[href]").attr("href");
		if(conUrl.equals("")){
			end=true;
		}
		else{
			adres="http://www.livenation.pl"+conUrl;
		}
	}	      
	}
	
	

	
}
