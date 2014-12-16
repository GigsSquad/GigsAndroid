package pl.javaparty.sql;

import java.io.IOException;

import pl.javaparty.jsoup.JSoupDownloader;
import android.support.v4.app.FragmentActivity;

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
			JSoupDownloader js = new JSoupDownloader(dbm);
			try
			{
				{
					js.getData();
					dbm.deleteOldConcerts();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			activity.runOnUiThread(r);
		}
		
	}
}
