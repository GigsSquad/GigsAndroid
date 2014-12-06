package pl.javaparty.jsoup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ImageDownloader
{
	private final static String LASTFM_URL = new String("http://www.lastfm.pl/music/"/* i tu nazwa zespolu */);
	public static final String IMAGES_DIR = new String("/downloadedImages/");
	private static String[] extentions = { ".jpg", ".png", ".gif", ".bmp" };

	static String bandNameEdited;
	static String fileName;
	static File fileDir1;

	public static void bandImage(File fileDir2, String bandName)
	{
		bandNameEdited = bandName;
		fileDir1 = fileDir2; // bez komentarza, wiem, ale potrzebowalem tego
		
		int indexOfHyphen = bandNameEdited.indexOf(" - "); // w goAhead dodatkowe info po myslniku tu nie potrzebne
		if (indexOfHyphen != -1)
			bandNameEdited = bandNameEdited.substring(0, indexOfHyphen);

		int indexOfAmpersand = bandNameEdited.indexOf(" & ");// w goAhead jak jest kilka zespolow
		if (indexOfAmpersand != -1)
			bandNameEdited = bandNameEdited.substring(0, indexOfAmpersand);

		int indexOfSlash = bandNameEdited.indexOf(" / "); // w goAhead jak jest kilka zespolow
		if (indexOfSlash != -1)
			bandNameEdited = bandNameEdited.substring(0, indexOfSlash);

		bandNameEdited = bandNameEdited.replace(' ', '+');// w last fm spacja zastepowana plusem

		fileName = bandName;
		int indexOfWS = bandName.indexOf(" ");
		if (indexOfWS != -1)
			fileName = bandName.substring(0, indexOfWS);

		if (exists(fileDir1, fileName) == null)
		{
			new Thread(new Runnable() { // uruchamiam w¹tek do pobrania zdjecia  i ustawienia go
				//Threadów u¿ywa siê do ciê¿szych zadan
				//AsyncTasków do czegoœ co trwa krótko
				@Override
				public void run() {
					String bandImgUrl;
					try {
						bandImgUrl = getBandPictureAdress(bandNameEdited);
						saveImage(fileDir1, bandImgUrl, fileName);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			}).start();
		}
		else
			System.out.println(fileName + " exists.");
	}

	private static String getBandPictureAdress(String bandName) throws IOException
	{
		Document doc = Jsoup.connect(LASTFM_URL + bandName).timeout(5000).get();
		Element imgClass = doc.getElementsByClass("resource-images").first();
		Element imgTag = imgClass.select("img").first();
		return imgTag.attr("src");
	}

	public static void saveImage(File fileDir, String url, String fileName)// w przypadku gdy bedziemy zapisywac img url
																			// do concert mozna uzyc tego
	{
		String imgContainer = url.substring(url.length() - 3); // rozszerzenie

		try
		{
			URL link = new URL(url);
			(new File(fileDir, IMAGES_DIR)).mkdir();// nowy folder, nie trzeba sprawdzac czy istnieje
			File toSave = new File(fileDir, IMAGES_DIR + fileName + "." + imgContainer);
			if (!toSave.exists())
			{
				BufferedInputStream is = new BufferedInputStream(link.openStream());
				BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(toSave));

				// /byte[] buffer = new byte[8192];
				int buffer;

				while ((buffer = is.read()) != -1)
					os.write(buffer);

				is.close();
				os.close();
				System.out.println(fileName + " saved.");
			}
			else
			{
				System.out.println(fileName + "already exists.");
			}
		} catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * if file doesn't exist returns null
	 */
	public static String exists(File fileDir, String fileName)
	{
		String path = null;
		for (int i = 0; i < extentions.length && path == null; i++)
		{
			File image = new File(fileDir, IMAGES_DIR + fileName + extentions[i]);
			if (image.exists())
			{
				path = image.getAbsolutePath();
			}
		}
		return path;
	}
}
