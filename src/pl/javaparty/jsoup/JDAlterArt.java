package pl.javaparty.jsoup;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import sql.dbManager;

public class JDAlterArt{

	private dbManager dbm;
	private final static String URL_ALTERART = new String("http://alterart.pl/pl/Archiwum");
	
	public JDAlterArt(dbManager dbm) {
		this.dbm = dbm;
	}
	
	
	public void getData() throws IOException{
		Document doc = Jsoup.connect(URL_ALTERART).get();
		Element allEvents = doc.getElementById("all_events");
		int currentHash = allEvents.hashCode();
		if(needsToBeUpdated(currentHash)){
			System.out.println("UPDATING AA");
			dbm.updateHash("ALTERART", currentHash);
			Elements names = allEvents.getElementsByClass("concert-box-data-name");
			ArrayList<String> urls = new ArrayList<String>();
			for(Element el : names)
				urls.add(el.select("a").attr("href"));
			Elements datesPlaces = allEvents.getElementsByClass("concert-box-data-date");
			Elements dates = new Elements(); 
			Elements spots = new Elements();
			for(int i = 0;i<datesPlaces.size();i++){
				if(i%2==0)
					dates.add(datesPlaces.get(i));
				else spots.add(datesPlaces.get(i));
			}
			Elements cities = allEvents.getElementsByClass("concert-box-data-city");
			for(int i = 0; i<names.size(); i++){
				String dateStrArr[] = dates.get(i).text().split("\\.");
				int day = Integer.valueOf(dateStrArr[0]);
				int month = Integer.valueOf(dateStrArr[1]);
				int year = Integer.valueOf(dateStrArr[2]);
				dbm.addConcert(names.get(i).text(),cities.get(i).text(), spots.get(i).text(),
						day,month,year,"ALTERART", urls.get(i));
			}
		}
	}
	/*
	 * Bazê nale¿y aktualizowaæ je¿eli:
	 *  - jeszcze nie mamy danych z tej strony
	 *  - dane tej strony s¹ przestarza³e (dodano nowe koncerty)
	 *       - sprawdzamy to porównuj¹c hashcody TABELI koncertów (inaczej ni¿ w GoAhead)
	 */
	private boolean needsToBeUpdated(int currentHash){
		boolean check = false;
		if(!dbm.agencyHashCodeExists("ALTERART"))
			check = true;
		else 
			if(dbm.getHash("ALTERART")!=currentHash)
				check = true;
		if(!check)
			System.out.println("No need to update");
		else System.out.println("UPDAAATE!");
		return check;
	}

	public static void main(String[] args) throws IOException{
		Document doc = Jsoup.connect(URL_ALTERART).get();
		Element allEvents = doc.getElementById("all_events");
		Elements names = allEvents.getElementsByClass("concert-box-data-name");
		ArrayList<String> urls = new ArrayList<String>();
		for(Element el : names)
			urls.add(el.select("a").attr("href"));
		for(String s : urls)
			System.out.println(s);
	}


}
