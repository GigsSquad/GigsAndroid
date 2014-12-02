package pl.javaparty.concertmanager;

import java.util.ArrayList;
import java.util.Iterator;

import pl.javaparty.concertmanager.Concert.AgencyName;
import pl.javaparty.jsoup.JDGoAhead;
import sql.dbManager;
import android.content.Context;
import android.database.Cursor;

public class ConcertManager {
	public static ArrayList<Concert> concerts;
	dbManager dbm;

	public ConcertManager(dbManager dbm)
	{
		concerts = new ArrayList<Concert>();
		this.dbm = dbm;
		collect();
	}

	private void collect(){
		Cursor c = dbm.getData();
		while(c.moveToNext()){
			String name = c.getString(0);
			int day = c.getInt(1);
			int month = c.getInt(2);
			int year = c.getInt(3);
			String place = c.getString(4)+c.getString(5);
			AgencyName agency = c.getString(6).equals("GOAHEAD")?AgencyName.GOAHEAD:null;
			String url = c.getString(7);
			concerts.add(new Concert(name,place,day,month,year,agency,url));
		}
	}
	
	public ArrayList<Concert> getList()
	{
		return concerts;
	}

	public ArrayList<String> getArtists()
	{
		ArrayList<String> artists = new ArrayList<String>();
		Iterator<Concert> iter = concerts.iterator();

		while (iter.hasNext())
		{
			Concert c = iter.next();
			if (!artists.contains(c.getArtist()))
				artists.add(c.getArtist());
		}
		return artists;
	}

	public ArrayList<String> getConcerts(String artist)
	{
		ArrayList<String> list = new ArrayList<String>();
		Iterator<Concert> iter = concerts.iterator();
		while (iter.hasNext())
		{
			Concert c = iter.next();
			if (c.getArtist().equals(artist))
				list.add(c.getPlace() + " " + c.dateToString());

		}
		return list;
	}

	public String searchArtis(String artist)
	{
		StringBuilder stringBuilder = new StringBuilder();
		Iterator<Concert> iter = concerts.iterator();
		while (iter.hasNext())
		{
			Concert c = iter.next();
			if (c.getArtist().equals(artist))
				stringBuilder.append(c.getPlace() + " " + c.dateToString() + "\n");
		}

		return stringBuilder.toString();
	}
}
