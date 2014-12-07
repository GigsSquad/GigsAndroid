package pl.javaparty.imageloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ImageDownloader
{
	private final static String LASTFM_URL = new String("http://www.lastfm.pl/music/"/* i tu nazwa zespolu */);
	private final static int BUFFER_SIZE = 4096;
	
	public static void bandImage(File fileDir, String bandName)
	{
		try
		{
			String bandImgUrl = getBandPictureAdress(bandName);
			saveImage(fileDir, bandImgUrl);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private static String getBandPictureAdress(String bandName) throws IOException
	{
		Document doc = Jsoup.connect(LASTFM_URL + bandName).timeout(10000).get();
		Element imgClass = doc.getElementsByClass("resource-images").first();
		Element imgTag = imgClass.select("img").first();
		return imgTag.attr("src");
	}
	
	private static void saveImage(File fileDir, String url) throws IOException
	{
		try
		{
			URL link = new URL(url);
			HttpURLConnection connection = (HttpURLConnection)link.openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setInstanceFollowRedirects(true);
			BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
			BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(fileDir));
			
			byte[] buffer = new byte[BUFFER_SIZE];
			int readedBytes;

			while ((readedBytes = is.read(buffer, 0, BUFFER_SIZE)) != -1)
				os.write(buffer, 0, readedBytes);

			is.close();
			os.close();
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
	}
}
