package pl.javaparty.jsoup;

import java.io.IOException;
import java.util.HashSet;

import pl.javaparty.concertmanager.Concert;

public interface JSoupDownloader {
	void getData() throws IOException;
	HashSet<String> getArtists() throws IOException;
	HashSet<String> getPlaces() throws IOException;
	void getMoreData(Concert c) throws IOException;
}
