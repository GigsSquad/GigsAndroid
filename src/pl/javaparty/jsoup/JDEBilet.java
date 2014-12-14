package pl.javaparty.jsoup;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.javaparty.sql.dbManager;







public class JDEBilet {
	private dbManager dbm;
	
	JDEBilet(dbManager dbm){
		this.dbm= dbm;
	}
	
	private static final String URL_EBILET= new String("http://www.ebilet.pl/kat.php?ndzial=koncerty");
	 ArrayList<String>adresy = new ArrayList<String>();
	 Document doc;
	 
	 
	
	 public  void getData() throws IOException{
		String conArtist;
		String city;
		String club;
		int day = 0;
		int month = 0;
		int year = 0;
		String conUrl = null;
		
		
		HashSet<String> sources = new HashSet<String>();
		doc= Jsoup.connect(URL_EBILET).get();
		
		Elements concertData = doc.body().getElementsByClass("act_act");
		
		for (Element el : concertData)
		{
			conUrl=("http://ebilet.pl/"+ el.select("a[href]").attr("href"));
			sources.add(conUrl);
		}
		for(String url: sources){
				Document poddoc= Jsoup.connect(url).get();
			Element midcolumn = poddoc.body().getElementById("midcolumn");
			if(midcolumn!=null){
				conArtist= midcolumn.getElementsByClass("wTitle").text();
				if(!conArtist.equals("")){
				Elements cdate= midcolumn.select("table[width=100%][cellpadding=0][cellspacing=0][border=0]");//.text().substring(0,10);
				Elements cplace = midcolumn.select("td[width][colspan][valign][align]");
				for(int i=0;i<cdate.size()&&i<cplace.size();i++){
					
					String conDate = cdate.get(i).text().substring(0,10);
					
					if(conDate.charAt(0)=='2'){
						String[] data =conDate.split("-");
						year = Integer.parseInt(data[0]);
						month=Integer.parseInt(data[1]);
						day = Integer.parseInt(data[2]);
					}
					 
					String kod = cplace.get(i).html().split("<br>")[0].toString();
					String [] cl = kod.split(",");
					String [] ci = kod.split(" ");
					club = cl[0];
					city = ci[ci.length-1];
					dbm.addConcert(conArtist, city, club, day, month, year, "EBILET", conUrl);
					
				}
				}
		}
			
		
			poddoc=null;
	}
}
	
}



