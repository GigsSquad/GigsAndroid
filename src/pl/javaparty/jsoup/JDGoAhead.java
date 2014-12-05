package pl.javaparty.jsoup;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.javaparty.sql.dbManager;

public class JDGoAhead{
	private dbManager dbm;
	private final static String URL_GOAHEAD = new String("http://www.go-ahead.pl/pl/koncerty.html");
	
	public JDGoAhead(dbManager dbm){
		this.dbm = dbm;
	}
	
	public void getData() throws IOException{
		Document doc = Jsoup.connect(URL_GOAHEAD).get();
		Elements concertData = doc.getElementsByClass("b_c");
		int currentHash = concertData.first().hashCode();
		if(needsToBeUpdated(currentHash)){
			System.out.println("UPDATING GA");
			dbm.updateHash("GOAHEAD", currentHash);
			for (Element el : concertData){
				String conUrl = el.attr("href");
				String conName = el.getElementsByClass("b_c_b").first().text();
				String conPlace = el.getElementsByClass("b_c_cp").first().text();
				String conDate = el.getElementsByClass("b_c_d").first().text();
				String conCity = conPlace.split(" ")[0];
				String conSpot = conPlace.split(conCity+" ")[0];
				int conDay = Integer.valueOf(conDate.split(" ")[0]);
				String[] months = { "st", "lu", "mar", "kw", "maj", "cz", "lip", "si", "wr", "pa", "lis", "gr" };
				int conMonth = 0;
				while (!conDate.split(" ")[1].startsWith(months[conMonth]))
				conMonth++;
				conMonth++;
				int conYear = Integer.valueOf(conDate.split(" ")[2]);
				dbm.addConcert(conName,conCity, conSpot,  conDay, conMonth, conYear, "GOAHEAD", conUrl);
			}
		}
	}
	
	/*
	 * Bazê nale¿y aktualizowaæ je¿eli:
	 *  - jeszcze nie mamy danych z tej strony
	 *  - dane tej strony s¹ przestarza³e (dodano nowe koncerty)
	 *       - sprawdzamy to porównuj¹c hashcody najnowszych koncertów
	 */
	private boolean needsToBeUpdated(int currentHash){
		boolean check = false;
		if(!dbm.agencyHashCodeExists("GOAHEAD"))
			check = true;
		else 
			if(dbm.getHash("GOAHEAD")!=currentHash)
				check = true;
		if(!check)
			System.out.println("No need to update");
		else System.out.println("UPDAAATE!");
		return check;
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
