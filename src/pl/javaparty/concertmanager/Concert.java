package pl.javaparty.concertmanager;


public class Concert
{
	public enum AgencyName {
		GOAHEAD, INNE
	}
	private String artist; 
	private String place; // to leci potem do google maps api
	private String dateString;
	private int day; // uzylbym tutaj czego� w stylu Date albo Calendar
	private int month = 0;
	private int year;
	private String[] stringArray;
	private String dayOfWeek; // pi�tek, sobota, etc.
	private String[] months = { "st", "lu", "mar", "kw", "maj", "cz", "lip", "si", "wr", "pa", "lis", "gr" };
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
		this.url = url;
		parse();
		this.adress = "";
		this.entryHours = "";
		this.ticketsPrice = "";
	}

	private void parse() // niekt�re koncery trwaj� kilka dni i s� oddzielone sa my�lnikami
	{
		stringArray = dateString.split(" ");
		day = Integer.parseInt(stringArray[0]);

		while (!stringArray[1].startsWith(months[month]))
			month++; // bede plakal jak sie zapetli #YOLO

		if (stringArray[2].startsWith("20"))
			year = Integer.parseInt(stringArray[2]);
		else if (stringArray[2].startsWith("("))
			dayOfWeek = stringArray[2];

		if (stringArray[3].startsWith("("))
			dayOfWeek = stringArray[3];

		// TODO: przydalo by sie usunac nawiasy z nazw dni tygodnia i doda� zera
		// jestli dzien albo miesiac jedno cyfrowy String.format("%02d", day)
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
	
	public String getDate()
	{
		return (String.format("%02d", day) + "." + String.format("%02d", month) + "." + year + " " + dayOfWeek);
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
		return artist + " " + place + " " + day + "." + month + "." + year + " " + dayOfWeek + "\n";
	}
}
