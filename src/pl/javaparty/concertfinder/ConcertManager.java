package pl.javaparty.concertfinder;

import java.util.ArrayList;
import java.util.Iterator;

import pl.javaparty.jsoup.Concert;

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
			artists.add(c.getArtis());
		}
		return artists;
	}
	
	public String searchArtis(String artist)
	{
		StringBuilder stringBuilder = new StringBuilder();
		Iterator<Concert> iter = concerts.iterator();
		while(iter.hasNext())
		{
			Concert c = iter.next();
			if(c.getArtis().equals(artist))
				stringBuilder.append(c.getPlace() + "\n");
		}
		return stringBuilder.toString();
	}
}
