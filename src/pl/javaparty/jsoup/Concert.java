package pl.javaparty.jsoup;

public class Concert
{
	private String artist; 
	private String place; // to leci potem do google maps api
	private String dateString;
	private int day; // u�y�bym tutaj czego� w stylu Date albo Calendar
	private int month = 0;
	private int year;
	String[] stringArray;
	private String dayOfWeek; // pi�tek, sobota, etc.
	private String[] months = { "st", "lu", "mar", "kw", "maj", "cz", "lip", "si", "wr", "pa", "lis", "gr" };

	Concert(String artist, String place, int day, int month, int year)
	{
		this.artist = artist;
		this.place = place;
		this.dateString = "";
	}

	Concert(String artist, String place, String dateString)
	{
		this.artist = artist;
		this.place = place;
		this.dateString = dateString;
		parse();
	}

	private void parse() // niekt�re koncery trwaj� kilka dni i s� oddzielone sa my�lnikami
	{
		stringArray = dateString.split(" ");
		day = Integer.parseInt(stringArray[0]);

		while (!stringArray[1].startsWith(months[month]))
			month++; // b�d� p�aka� jak si� zap�tli #YOLO

		if (stringArray[2].startsWith("20"))
			year = Integer.parseInt(stringArray[2]);
		else if (stringArray[2].startsWith("("))
			dayOfWeek = stringArray[2];

		if (stringArray[3].startsWith("("))
			dayOfWeek = stringArray[3];

		// TODO: przydalo by sie usunac nawiasy z nazw dni tygodnia i doda� zera
		// jestli dzien albo miesiac jedno cyfrowy String.format("%02d", day)
	}
	
	public String getArtis()
	{
		return artist;
	}

	public String getPlace()
	{
		return place;
	}
	
	@Override
	public String toString()
	{
		return artist + " " + place + " " + day + "." + month + "." + year + " " + dayOfWeek + "\n";
	}
}
