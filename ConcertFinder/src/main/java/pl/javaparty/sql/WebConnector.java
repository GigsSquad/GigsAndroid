package pl.javaparty.sql;


import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class WebConnector
{
    private final static String URL = "http://javaparty.com.pl/concertfinder.php";
    private final static String CHARSET = "UTF-8";

    /**
     *   Use html POST method with specified {@code params}.
     *
     *   @param params array of parameters to put in POST method (f.e. "name1=name1").
     *   @return InputStream with data received from web, or null if error.
     */
    public static InputStream post(String params[])
    {
        InputStream is = null;
        String query = "";

        for (int i = 0; i < params.length; i++)
        {
            query += String.format("%s", params[i]);
            if (i + 1 < params.length)
                query += "&";
        }

        try
        {
            URLConnection connection = new URL(URL).openConnection();

            connection.setDoOutput(true); // Triggers POST.
            connection.setRequestProperty("Accept-Charset", CHARSET);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + CHARSET);

            OutputStream output = connection.getOutputStream();
            output.write(query.getBytes(CHARSET));
            output.close();

            is = connection.getInputStream();
        } catch (IOException e)
        {
            e.printStackTrace();
            return is;
        }

        return is;
    }
}
