package pl.javaparty.items;

import com.google.android.gms.maps.model.LatLng;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Concert {
    private final int ID;
    private String artist;
    private String city;
    private String spot;
    private Calendar date;
    String url;
    String lat;
    String lon;
    Agencies agency;
    double distance;

    public Concert(int ID, String artist, String city, String spot, int day, int month, int year, Agencies agency, String url, String lat, String lon, double dist) {
        this.ID = ID;
        this.agency = agency;
        this.artist = artist.trim();
        this.city = city.trim();
        this.spot = spot.trim();
        date = new GregorianCalendar(year, month - 1, day);
        this.url = url.trim();
        this.lat = lat.trim();
        this.lon = lon.trim();
        this.distance = dist;
    }

    public Concert(int ID) {
        this.ID = ID;
    }


    public String getArtist() {
        return artist;
    }

    public String getCity() {
        return city;
    }

    public String getSpot() {
        return city + " " + spot;
    }

    public int getID() {
        return ID;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public LatLng getLatLng() {
        return new LatLng(Double.parseDouble(getLat()), Double.parseDouble(getLon()));
    }

    public Calendar getCalendar() {
        return date;
    }

    public int getYear() {
        return date.get(Calendar.YEAR);
    }

    public int getMonth() {
        return date.get(Calendar.MONTH);
    }

    public String dateToString() {
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

    public boolean happened() {
        Calendar today = Calendar.getInstance();
        //boolean sameDate = date.YEAR == today.YEAR && date.MONTH == today.MONTH && date.DAY_OF_MONTH == today.DAY_OF_MONTH;
        return date.before(Calendar.getInstance()) && !isSameDay(today, date);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public int getDay() {
        return date.get(Calendar.DAY_OF_MONTH);
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public int daysTo() {
        Calendar today = Calendar.getInstance();
        if (date.get(Calendar.DAY_OF_MONTH) - (today.get(Calendar.DAY_OF_MONTH)) == 0)
            return 0;
        return Days.daysBetween(new DateTime(today), new DateTime(date)).getDays() + 1;

    }

    public String getUrl() {
        return url;
    }

    public String getAgency() {
        return agency.toString();
    }

}
