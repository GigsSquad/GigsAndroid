package pl.javaparty.concertmanager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.util.Log;

public class Concert
{
	public enum AgencyName {
		GOAHEAD, ALTERART, LIVENATION ,INNE
	}

	private final int ID; //unikalne id ka¿dego koncertu
	private String artist;
	private String city; // to leci potem do google maps api
	private String spot; // lokalizacja w mieœcie, jakiœ klub czy coœ
	private Calendar date; // sam wyliczy dzieñ tygodnia, mo¿na mu dodaæ godzinê etc.
	private String url; // url do strony ze szczegolowymi informacjami o danym koncercie
	private AgencyName agency;
	// additional info
	private String adress; // uzupelnienie do place, tez moze isc do google maps api
	private String entryHours;
	private String ticketsPrice;

	// jeszcze sa adresy do stron gdzie mozna kupic bilet, ale na razie tego nie dodaje

	public Concert(int ID, String artist, String city, String spot, int day, int month, int year, AgencyName agency, String url)
	{
		Log.i("ID", "ID: " + ID);
		this.ID = ID;
		this.agency = agency;
		this.artist = artist;
		this.city = city;
		this.spot = spot;
		date = new GregorianCalendar(year, month - 1, day);
		this.url = url;
		this.adress = "";
		this.entryHours = "";
		this.ticketsPrice = "";
	}

	public String getArtist()
	{
		return artist;
	}

	public String getCity()
	{
		return city;
	}

	public String getSpot() {
		return spot;
	}

	public String getPlace() {
		return city + " " + spot;
	}

	public AgencyName getAgency()
	{
		return agency;
	}

	public Calendar getDate() {
		return date;
	}
	
	public int getID()
	{
		return ID;
	}

	public String dateToString()
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		String day = "";
		switch (date.get(Calendar.DAY_OF_WEEK)) {
		case 1:
			day = "niedziela";
			break;
		case 2:
			day = "poniedzia³ek";
			break;
		case 3:
			day = "wtorek";
			break;
		case 4:
			day = "œroda";
			break;
		case 5:
			day = "czwartek";
			break;
		case 6:
			day = "pi¹tek";
			break;
		case 7:
			day = "sobota";
			break;
		default:
			break;
		}
		return dateFormat.format(date.getTime()) + " (" + day + ")";
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

	public String getMoreData()// tymczasowe raczej, bo po co to komu? :D
	{
		return adress + " " + entryHours + " " + ticketsPrice;
	}

	@Override
	public String toString()
	{
		return artist + " " + getPlace() + " " + dateToString() + "\n";
	}

}
