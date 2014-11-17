public class Concert
{
	private String concertName;
	private String place;
	private String date;
	private int day;//Dzien miesiac i rok narazie sa nieuzywane, ale powstaly bo przydadza sie moze
	private int month;//do sortowania po dacie potem.
	private int year;
	
	Concert(String conName, String place, int day, int month, int year)
	{
		concertName = conName;
		this.place = place;
		this.day = day;
		this.month = month;
		this.year = year;
		this.date = "";
	}
	
	Concert(String conName, String place, String date)
	{
		concertName = conName;
		this.place = place;
		this.date = date;
	}
	
	@Override
	public String toString()
	{
		return concertName + "\n" + place + "\n" + date;
	}
}
