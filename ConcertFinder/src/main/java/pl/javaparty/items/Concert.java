package pl.javaparty.items;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Concert
{
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artist == null) ? 0 : artist.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((spot == null) ? 0 : spot.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Concert other = (Concert) obj;
		if (artist == null) {
			if (other.artist != null)
				return false;
		} else if (!artist.equals(other.artist))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (spot == null) {
			if (other.spot != null)
				return false;
		} else if (!spot.equals(other.spot))
			return false;
		return true;
	}

	public enum AgencyName {
		GOAHEAD, ALTERART, EBILET, LIVENATION, TICKETPRO, INNE
	}

	private final int ID; // unikalne id ka�dego koncertu
	private String artist;
	private String city; // to leci potem do google maps api
	private String spot; // lokalizacja w mie�cie, jaki� klub czy co� (ulica?)
	private Calendar date; // sam wyliczy dzie� tygodnia, mo�na mu doda� godzin� etc.
	private String url; // url do strony ze szczegolowymi informacjami o danym koncercie
	private AgencyName agency;
	// additional info
	private String entryHours;
	private String ticketsPrice;

	// jeszcze sa adresy do stron gdzie mozna kupic bilet, ale na razie tego nie dodaje

	public Concert(int ID, String artist, String city, String spot, int day, int month, int year, AgencyName agency, String url)
	{
		this.ID = ID;
		this.agency = agency;
		this.artist = artist.trim();
		this.city = city.trim();
		this.spot = spot.trim();
		date = new GregorianCalendar(year, month - 1, day);
		this.url = url.trim();
		this.entryHours = "";
		this.ticketsPrice = "";
	}

	public Concert(int ID) {
		this.ID = ID;
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

	public Calendar getCalendar()
	{
		return date;
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
			day = "poniedziałek";
			break;
		case 3:
			day = "wtorek";
			break;
		case 4:
			day = "środa";
			break;
		case 5:
			day = "czwartek";
			break;
		case 6:
			day = "piątek";
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
		this.entryHours = entryHours;
		this.ticketsPrice = ticketsPrice;
	}

	public String getMoreData()// tymczasowe raczej, bo po co to komu? :D
	{
		return entryHours + " " + ticketsPrice;
	}

	@Override
	public String toString()
	{
		return artist + " " + getPlace() + " " + dateToString() + "\n";
	}

}
