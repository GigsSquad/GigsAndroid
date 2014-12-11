package pl.javaparty.concertmanager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import pl.javaparty.concertmanager.Concert.AgencyName;
import pl.javaparty.sql.dbManager;
import android.database.Cursor;

public class ConcertManager {
	public static HashSet<Concert> concerts;
	dbManager dbm;

	public ConcertManager(dbManager dbm)
	{
		if (concerts == null)
			concerts = new HashSet<Concert>();
		this.dbm = dbm;
	}

	public void collect() {
		Cursor c = dbm.getData();
		while (c.moveToNext()) {
			int id = c.getInt(0); // daje mi unikanlne id kazdego kocnertu, zaczyna liczyc od 1, a nie od 0!!
			String name = c.getString(1);
			String city = c.getString(2);
			String spot = c.getString(3);
			int day = c.getInt(4);
			int month = c.getInt(5);
			int year = c.getInt(6);
			AgencyName agency = getAgency(c.getString(7));
			String url = c.getString(8);
			concerts.add(new Concert(id, name, city, spot, day, month, year, agency, url));
		}
	}

	public ArrayList<Concert> getList()
	{
		return new ArrayList<Concert>(concerts); //
	}

	public ArrayList<String> getCities()
	{
		ArrayList<String> cities = new ArrayList<String>();

		Iterator<Concert> iter = concerts.iterator();
		while (iter.hasNext())
		{
			Concert c = iter.next();
			if (!cities.contains(c.getCity()))
				cities.add(c.getCity());
		}

		return cities;
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

	public Concert getById (int ID){
		ArrayList<Concert> concerts = getList();
		Concert res = null;
		for(Concert c :concerts){
			if (c.getID()==ID){
				res = c;
				break;
			}
		}
		return res;
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

	private AgencyName getAgency(String s) {
		AgencyName agency = null;
		if (s.equals("GOAHEAD"))
			agency = AgencyName.GOAHEAD;
		else if (s.equals("ALTERART"))
			agency = AgencyName.ALTERART;
		return agency;
	}
}
