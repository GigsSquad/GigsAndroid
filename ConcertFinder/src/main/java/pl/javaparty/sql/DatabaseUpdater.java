package pl.javaparty.sql;


import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.InputStream;

import pl.javaparty.jsoup.PhpParser;
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
            PhpParser parser = new PhpParser(activity, dbm);

            //parametry
            String lastID = String.valueOf(Prefs.getLastID(activity));

            String device = android.os.Build.MODEL.replaceAll(" ", "+");

            InputStream input = WebConnector.post(new String[]{"get=" + lastID, "device=" + device});
            if(input!=null)
            {
                parser.parse(input);
                Log.i("BAZA", "Baza uzupełniona");
                activity.runOnUiThread(r);
            }
            else
            {
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(activity, "Brak nowych koncertów.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            //dbm.deleteOldConcerts();

        }

    }
}
