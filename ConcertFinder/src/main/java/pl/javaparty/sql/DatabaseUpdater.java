package pl.javaparty.sql;

import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import pl.javaparty.prefs.Prefs;

import java.io.InputStream;

public class DatabaseUpdater {
	dbManager dbm;
	FragmentActivity activity;

	public DatabaseUpdater(dbManager dbm, FragmentActivity activity) {
		this.dbm = dbm;
		this.activity = activity;
	}

	public void update(Runnable r) {
		new Thread(new Download(r)).start();
	}

	private class Download implements Runnable {
		Runnable r;

		public Download(Runnable r) {
			this.r = r;
		}

		@Override
		public void run() {

			long startTime = System.currentTimeMillis();
			PhpParser parser = new PhpParser(activity, dbm);

			//parametry
			String lastID = String.valueOf(Prefs.getLastID(activity));
			String device = Build.MODEL.replaceAll(" ", "+");

			InputStream input = WebConnector.post(new String[] { "get=" + lastID, "device=" + device });
			parser.parse(input);

			int time = (int) ((System.currentTimeMillis() - startTime) / 1000);
			Log.i("JSD", "Uzupelniono baze w " + time + "sekund");

			//dbm.deleteOldConcerts(); TODO to ma chyba zostac ale troche byc zmienione
			activity.runOnUiThread(r);
		}

	}
}
