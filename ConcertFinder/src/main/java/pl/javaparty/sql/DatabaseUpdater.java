package pl.javaparty.sql;

import java.io.IOException;
import java.io.InputStream;

import pl.javaparty.jsoup.JDAlterArt;
import pl.javaparty.jsoup.JDGoAhead;
import pl.javaparty.jsoup.JDLiveNation;
import pl.javaparty.jsoup.JDTicketPro;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import pl.javaparty.prefs.Prefs;

public class DatabaseUpdater
{
	dbManager dbm;
	FragmentActivity activity;

	public DatabaseUpdater(dbManager dbm, FragmentActivity activity)
	{
		this.dbm = dbm;
		this.activity = activity;
	}

	public void update(Runnable r)
	{
		new Thread(new Download(r)).start();
	}

	private class Download implements Runnable
	{
		Runnable r;

		public Download(Runnable r)
		{
			this.r = r;
		}

		@Override
		public void run()
		{
			//try
			{

                /*
				Log.i("JSD", "Pobieram GoAhead 1/5");
				long startTime = System.currentTimeMillis();
				new JDGoAhead(dbm).getData();
				int time = (int) ((System.currentTimeMillis() - startTime) / 1000);
				Log.i("JSD", "Pobrano GoAhead w " + time + "sekund");

				Log.i("JSD", "Pobieram AlterArt 2/5");
				startTime = System.currentTimeMillis();
				new JDAlterArt(dbm).getData();
				time = (int) ((System.currentTimeMillis() - startTime) / 1000);
				Log.i("JSD", "Pobrano AlterArt w " + time + "sekund");

				Log.i("JSD", "Pobieram LiveNation 3/5");
				startTime = System.currentTimeMillis();
				new JDLiveNation(dbm).getData();
				time = (int) ((System.currentTimeMillis() - startTime) / 1000);
				Log.i("JSD", "Pobrano LiveNation w " + time + "sekund");

				Log.i("JSD", "Usuwam stare koncerty i odświeżam listę");
				dbm.deleteOldConcerts();
				activity.runOnUiThread(r);

				Log.i("JSD", "Pobieram TicketPro 4/5");
				startTime = System.currentTimeMillis();
				new JDTicketPro(dbm).getData();
				time = (int) ((System.currentTimeMillis() - startTime) / 1000);
				Log.i("JSD", "Pobrano TicketPro w " + time + "sekund");

				Log.i("JSD", "Pobieram Ebilet 5/5");
				startTime = System.currentTimeMillis();
				// new JDEBilet(dbm).getData();
				time = (int) ((System.currentTimeMillis() - startTime) / 1000);
				Log.i("JSD", "Pobrano Ebilet w " + time + "sekund");
*/
                long startTime = System.currentTimeMillis();
                PhpParser parser = new PhpParser(activity, dbm);

                //parametry
                String lastID = String.valueOf(Prefs.getLastID(activity));
                String device = android.os.Build.MODEL.replaceAll(" ", "+");

                InputStream input = WebConnector.post(new String[]{"get="+lastID, "device="+device});
                parser.parse(input);
                int time = (int) ((System.currentTimeMillis() - startTime) / 1000);
                Log.i("JSD", "Uzupelniono baze w " + time + "sekund");

			}
            //catch (IOException e)
			//{
			//	e.printStackTrace();
			//}
			//dbm.deleteOldConcerts();
			activity.runOnUiThread(r);
		}

	}
}
