package pl.javaparty.jsoup;

import java.io.IOException;

import pl.javaparty.sql.dbManager;

public class JSoupDownloader {

	dbManager dbm;

	public JSoupDownloader(dbManager dbm) {
		this.dbm = dbm;
	}

	public void getData() throws IOException {
		new JDGoAhead(dbm).getData();
		new JDAlterArt(dbm).getData();
		new JDTicketPro(dbm).getData();
		new JDLiveNation(dbm).getData();
		new JDEBilet(dbm).getData();
		//dbm.deleteOldConcerts();
	}
}
