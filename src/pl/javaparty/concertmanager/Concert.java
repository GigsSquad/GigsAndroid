package pl.javaparty.concertmanager;


public class Concert
{
	public enum AgencyName {
		GOAHEAD, INNE
	}

	private String artist; 
	private String place; // to leci potem do google maps api
	private String dateString;
	private int day; // u¿y³bym tutaj czegoœ w stylu Date albo Calendar
	private int month = 0;
	private int year;
	private String[] stringArray;
	private String dayOfWeek; // pi¹tek, sobota, etc.
	private String[] months = { "st", "lu", "mar", "kw", "maj", "cz", "lip", "si", "wr", "pa", "lis", "gr" };
	private String url; //url do strony ze szczegolowymi informacjami o danym koncercie 
	private AgencyName agency;

	public Concert(String artist, String place, String dateString, AgencyName agency)
	{
		this.agency = agency;
		this.artist = artist;
		this.place = place;
		this.dateString = dateString;
		parse();
	}

	private void parse() // niektóre koncery trwaj¹ kilka dni i s¹ oddzielone sa myœlnikami
	{
		stringArray = dateString.split(" ");
		day = Integer.parseInt(stringArray[0]);

		while (!stringArray[1].startsWith(months[month]))
			month++; // bêdê p³aka³ jak siê zapêtli #YOLO

		if (stringArray[2].startsWith("20"))
			year = Integer.parseInt(stringArray[2]);
		else if (stringArray[2].startsWith("("))
			dayOfWeek = stringArray[2];

		if (stringArray[3].startsWith("("))
			dayOfWeek = stringArray[3];

		// TODO: przydalo by sie usunac nawiasy z nazw dni tygodnia i dodaæ zera
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
	
	@Override
	public String toString()
	{
		return artist + " " + place + " " + day + "." + month + "." + year + " " + dayOfWeek + "\n";
	}
}
