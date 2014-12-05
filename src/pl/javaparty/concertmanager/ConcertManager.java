package pl.javaparty.concertmanager;

import java.util.ArrayList;
import java.util.Iterator;

import pl.javaparty.concertmanager.Concert.AgencyName;
import sql.dbManager;
import android.database.Cursor;

public class ConcertManager {
	public static ArrayList<Concert> concerts;
	dbManager dbm;

	public ConcertManager(dbManager dbm)
	{
		if(concerts==null)
			concerts = new ArrayList<Concert>();
		this.dbm = dbm;
	}

	public void collect(){
		Cursor c = dbm.getData();
		while(c.moveToNext()){
			String name = c.getString(0);
			String city = c.getString(1);
			String spot = c.getString(2);
			int day = c.getInt(3);
			int month = c.getInt(4);
			int year = c.getInt(5);
			AgencyName agency = getAgency(c.getString(6));
			String url = c.getString(7);
			concerts.add(new Concert(name,city,spot,day,month,year,agency,url));
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
	
	public ArrayList<Concert> getConcertList(String artist)
	{
		ArrayList<Concert> list = new ArrayList<Concert>();
		Iterator<Concert> iter = concerts.iterator();
		while (iter.hasNext())
		{
			Concert c = iter.next();
			if (c.getArtist().equals(artist))
				list.add(c);
		}
		
		return list;
	}
	
	private AgencyName getAgency(String s){
		AgencyName agency = null;
		if(s.equals("GOAHEAD"))
			agency = AgencyName.GOAHEAD;
		else if (s.equals("ALTERART"))
			agency = AgencyName.ALTERART;
		return agency;
	}
}
