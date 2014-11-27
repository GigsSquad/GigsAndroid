package pl.javaparty.concertmanager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class Concert
{
	public enum AgencyName {
		GOAHEAD, INNE
	}
	private String artist; 
	private String place; // to leci potem do google maps api
	private String dateString;
	private Calendar date; // sam wyliczy dzieñ tygodnia, mo¿na mu dodaæ godzinê etc.
	private String url; //url do strony ze szczegolowymi informacjami o danym koncercie 
	private AgencyName agency;
	//additional info
	private String adress; //uzupelnienie do place, tez moze isc do google maps api
	private String entryHours; 
	private String ticketsPrice;
	//jeszcze sa adresy do stron gdzie mozna kupic bilet, ale na razie tego nie dodaje
	
	public Concert(String artist, String place, String dateString, AgencyName agency, String url)
	{
		this.agency = agency;
		this.artist = artist;
		this.place = place;
		this.dateString = dateString;
		date = new GregorianCalendar(); 
		setDate();
		this.url = url;
		this.adress = "";
		this.entryHours = "";
		this.ticketsPrice = "";
	}
	
	private void setDate(){
		String[] arr = dateString.split(" ");
		final String[] months = { "st", "lu", "mar", "kw", "maj", "cz", "lip", "si", "wr", "pa", "lis", "gr" };
		int mon = 0;
		while(!arr[1].startsWith(months[mon]))
			mon++;
		date.set(Integer.valueOf(arr[2]),mon,Integer.valueOf(arr[0]));
	}
	
	public String getArtist()
	{
		return artist;
	}

	public String getPlace()
	{
		return place;
	}
	
	public AgencyName getAgency()
	{
		return agency;
	}
	
	public Calendar getDate(){
		return date;
	}
	
	public String dateToString()
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		String day="";
		switch(date.get(Calendar.DAY_OF_WEEK)){
		case 1 : day = "niedziela"; break;
		case 2 : day = "poniedzia³ek"; break;
		case 3 : day = "wtorek"; break;
		case 4 : day = "œroda"; break;
		case 5 : day = "czwartek"; break;
		case 6 : day = "pi¹tek"; break;
		case 7 : day = "sobota"; break;
		default: break;
		}
		return dateFormat.format(date.getTime())+" ("+day+")";
	}
	
	public String getURL()
	{
		return url;
	}
	
	public void setMoreData(String adress, String entryHours, String ticketsPrice)
	{
		this.adress = adress;
		this.entryHours = entryHours;
		this.ticketsPrice = ticketsPrice; 
	}
	
	public String getMoreData()//tymczasowe raczej, bo po co to komu? :D
	{
		return adress + " " + entryHours + " " + ticketsPrice;
	}
	
	@Override
	public String toString()
	{
		return artist + " " + place + " " + dateToString() + "\n";
	}

}
