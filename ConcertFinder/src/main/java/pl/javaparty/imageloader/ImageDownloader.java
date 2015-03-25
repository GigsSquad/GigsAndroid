package pl.javaparty.imageloader;

import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class ImageDownloader {
	private final static String LASTFM_URL = new String("http://www.lastfm.pl/music/"/* i tu nazwa zespolu */);
	private final static int BUFFER_SIZE = 4096;
	private final static String TAG = "ImageDownloader";

	public static void bandImage(File fileDir, String bandName) {
		try {
			String bandImgUrl = getBandPictureAdress(bandName);
			saveImage(fileDir, bandImgUrl);
		} catch (OutOfMemoryError oome) {

		} catch (IOException e) {
			//Log.e(TAG, "Przekroczono czas polaczenia. Nic nie pobrano.");
		}
	}

	private static String getBandPictureAdress(String bandName) throws IOException {
		Document doc = Jsoup.connect(LASTFM_URL + URLEncoder.encode(bandName, "UTF-8")).timeout(30000).get();
		Element imgClass = doc.getElementsByClass("resource-images").first();
		Element imgTag = imgClass.select("img").first();
		return imgTag.attr("src");
	}

	private static void saveImage(File fileDir, String url) throws IOException {
		//synchronized (url)
		{
			//if (!fileDir.exists())
			{
				//Log.i(TAG, "Pobieranie obrazka z: " + url);
				try {
					URL link = new URL(url);
					HttpURLConnection connection = (HttpURLConnection) link
							.openConnection();
					connection.setConnectTimeout(40000);
					connection.setReadTimeout(40000);
					connection.setInstanceFollowRedirects(true);
					BufferedInputStream is = new BufferedInputStream(
							connection.getInputStream());
					BufferedOutputStream os = new BufferedOutputStream(
							new FileOutputStream(fileDir));

					byte[] buffer = new byte[BUFFER_SIZE];
					int readedBytes;

					while ((readedBytes = is.read(buffer, 0, BUFFER_SIZE)) != -1)
						os.write(buffer, 0, readedBytes);

					is.close();
					os.close();
				} catch (MalformedURLException e) {
					Log.e(TAG, "Bledny adres obrazka do pobrania");
				}
			}
		}
	}
}
