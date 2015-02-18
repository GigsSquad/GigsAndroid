package pl.javaparty.jsoup;


import android.content.Context;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pl.javaparty.prefs.Prefs;
import pl.javaparty.sql.WebConnector;
import pl.javaparty.sql.dbManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.InputMismatchException;
import java.util.Scanner;

public class PhpParser
{
    private final static String SEPARATOR = ";";
    private final static String END_SEPARATOR = ";;";
    private final Context context;
    private final dbManager dbm;

    public PhpParser(Context context, dbManager dbm)
    {
        this.context = context;
        this.dbm = dbm;
    }

    public void parse(InputStream is)
    {
        if(is!=null)
        {
//            BufferedReader br = null;
//
//
//            String line;
            try
            {
                Document doc = Jsoup.parse(is, WebConnector.CHARSET, WebConnector.URL);
//                br = new BufferedReader(new InputStreamReader(is));
                int lastID = -1;
//                while ((line = br.readLine()) != null)
//                {
//                    lastID = addToDatabase(line);
//                }
                String line = doc.text();
                Scanner sc = new Scanner(line).useDelimiter(END_SEPARATOR);
                int thelastID = sc.nextInt(); //TODO <--------- RAFAŁKU TUTAJ!
                Log.i("int", "int: " + thelastID);
                while(sc.hasNext())
                {
                    String next = sc.next();
                    lastID = addToDatabase(next);
                }
                Prefs.setLastID(context, lastID);

            } catch (IOException e)
            {
                e.printStackTrace();
            }
//            finally
//            {
//                if (br != null)
//                {
//                    try
//                    {
//                        br.close();
//                    } catch (IOException e)
//                    {
//                        e.printStackTrace();
//                    }
//                }
//            }
        }

    }

    /**
     *
     * @param concert {@code String} which contains all concert data separated by const separator.
     * @return ID of red concert, or -1 if error.
     */
    private int addToDatabase(String concert)
    {
        Scanner sc = new Scanner(concert).useDelimiter(SEPARATOR);
        int id = -1;
        try
        {
            id = sc.nextInt();
			dbm.addConcert(id, sc.next(), sc.next(), sc.next(), sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.next(), sc.next(), sc.next(), sc.next());
		}
        catch (InputMismatchException e)
        {
            Log.i("UPDATER", "Wrong input: "+concert);
        }
        return id;
    }
}
