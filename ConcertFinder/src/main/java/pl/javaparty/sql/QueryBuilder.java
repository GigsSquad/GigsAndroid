package pl.javaparty.sql;

import android.database.Cursor;
import pl.javaparty.items.Concert;

/**
 * Created by jakub on 8/22/15.
 */
public class QueryBuilder {

    int id;
    String artist;
    String city;
    String spot;
    int day, month, year;
    String agency;
    String lat, lon;
    double distance;
    int entranceFee; // 0 not set
    int futureConcert; // 0 not set, 1 future, 2 past

    //TODO pomyśleć jak z AND'ami
    public String build() {
        String condition;

        if (id != null) {
            condition += "ORD = '" + id + "'";
        }

        if (!artist.isEmpty()) {
            condition += "ARTIST = '" + artist + "'";
        }

        if (!city.isEmpty()) {
            condition += "CITY = '" + city + "'";
        }

        if (!spot.isEmpty()) {
            condition += "SPOT = '" + spot + "'";
        }

        if (day != null) {
            condition += "DAY = '" + day + "'";
        }

        if (month != null) {
            condition += "MONTH = '" + month + "'";
        }

        if (year != null) {
            condition += "YEAR = '" + year + "'";
        }

        if (!agency.isEmpty()) {
            condition += "AGENCY = '" + agency + "'";
        }

        if (!lat.isEmpty()) {
            condition += "LAT = '" lat + "'";
        }

        if (!lon.isEmpty()) {
            condition += "LON = '" + lon + "'";
        }

        if (distance != null) {
            condition += "DIST = '" + distance + "'";
        }

        //TODO: entrance fee
        if (futureConcert != 0)
            condition +=
            return this;
    }

    private Concert[] getConcerts(QueryBuilder queryBuilder) {

        String condition;


        Cursor c = database.query(CONCERTS_TABLE, columns, condition, null, null, null, SORT_ORDER);

        Concert[] concerts = new Concert[c.getCount()];
        for (int i = 0; c.moveToNext(); i++) {
            concerts[i] = new Concert(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
                    c.getInt(4), c.getInt(5), c.getInt(6), getAgency(c.getString(7)), c.getString(8), c.getString(9), c.getString(10), c.getDouble(11));
        }
        c.close();
        return concerts;
    }


    public int getId() {
        return id;
    }

    public QueryBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public String getArtist() {
        return artist;
    }

    public QueryBuilder setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public String getCity() {
        return city;
    }

    public QueryBuilder setCity(String city) {
        this.city = city;
        return this;
    }

    public String getSpot() {
        return spot;
    }

    public QueryBuilder setSpot(String spot) {
        this.spot = spot;
        return this;
    }

    public int getDay() {
        return day;
    }

    public QueryBuilder setDay(int day) {
        this.day = day;
        return this;
    }

    public int getMonth() {
        return month;
    }

    public QueryBuilder setMonth(int month) {
        this.month = month;
        return this;
    }

    public int getYear() {
        return year;
    }

    public QueryBuilder setYear(int year) {
        this.year = year;
        return this;
    }

    public String getAgency() {
        return agency;
    }

    public QueryBuilder setAgency(String agency) {
        this.agency = agency;
        return this;
    }

    public String getLat() {
        return lat;
    }

    public QueryBuilder setLat(String lat) {
        this.lat = lat;
        return this;
    }

    public String getLon() {
        return lon;
    }

    public QueryBuilder setLon(String lon) {
        this.lon = lon;
        return this;
    }

    public double getDistance() {
        return distance;
    }

    public QueryBuilder setDistance(double distance) {
        this.distance = distance;
        return this;
    }

    public boolean isEntranceFee() {
        return entranceFee;
    }

    public QueryBuilder setEntranceFee(boolean entranceFee) {
        this.entranceFee = entranceFee;
        return this;
    }

    public boolean isFutureConcert() {
        return entranceFee;
    }

    public QueryBuilder setFutureConcert(boolean futureConcert) {
        this.futureConcert = futureConcert;
        return this;
    }
}
