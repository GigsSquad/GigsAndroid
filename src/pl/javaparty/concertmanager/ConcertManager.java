package pl.javaparty.concertmanager;

import java.util.ArrayList;
import java.util.Iterator;

public class ConcertManager {
	public static ArrayList<Concert> concerts;

	public ConcertManager()
	{
		concerts = new ArrayList<Concert>();
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
	
	

	public String searchArtist(String artist)
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
