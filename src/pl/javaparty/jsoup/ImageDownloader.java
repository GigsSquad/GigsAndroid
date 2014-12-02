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
	private final static String LASTFM_URL = new String("http://www.lastfm.pl/music/"/*i tu nazwa zespolu*/);
	private static final String IMAGES_DIR = new String("/downloadedImages/");	
	public static void bandImage(File fileDir, String bandName)
	{
		String bandNameEdited = bandName;
		
		int indexOfHyphen = bandNameEdited.indexOf(" - "); //w goAhead dodatkowe info po myslniku tu nie potrzebne
		if(indexOfHyphen != -1)
			bandNameEdited = bandNameEdited.substring(0, indexOfHyphen);
		
		int indexOfAmpersand = bandNameEdited.indexOf(" & ");//w goAhead jak jest kilka zespolow
		if(indexOfAmpersand != -1)
			bandNameEdited = bandNameEdited.substring(0, indexOfAmpersand);
		
		int indexOfSlash = bandNameEdited.indexOf(" / "); //w goAhead jak jest kilka zespolow
		if(indexOfSlash != -1)
			bandNameEdited = bandNameEdited.substring(0, indexOfSlash);
		
		bandNameEdited = bandNameEdited.replace(' ', '+');//w last fm spacja zastepowana plusem
		
		String fileName = bandName;
		int indexOfWS = bandName.indexOf(" ");
		if(indexOfWS != -1)
			fileName = bandName.substring(0, indexOfWS);
		
		try
		{
			String bandImgUrl = getBandPictureAdress(bandNameEdited);
			saveImage(fileDir, bandImgUrl, fileName);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private static String getBandPictureAdress(String bandName) throws IOException
	{
		Document doc = Jsoup.connect(LASTFM_URL+bandName).get();
		Element imgClass = doc.getElementsByClass("resource-images").first();
		Element imgTag = imgClass.select("img").first();
		return imgTag.attr("src");
	}
	
	public static void saveImage (File fileDir, String url, String fileName)//w przypadku gdy bedziemy zapisywac img url do concert mozna uzyc tego
	{
		String imgContainer = url.substring(url.length()-3); //rozszerzenie
		
		try
		{
			URL link = new URL(url);
			BufferedInputStream is = new BufferedInputStream(link.openStream());
			(new File(fileDir, IMAGES_DIR)).mkdir();//nowy folder, nie trzeba sprawdzac czy istnieje
			File toSave = new File(fileDir, IMAGES_DIR+fileName+"."+imgContainer);
			if(!toSave.exists())
			{
				BufferedOutputStream os = new BufferedOutputStream (new FileOutputStream(toSave));
			
				///byte[] buffer = new byte[8192];
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
	
	/*public static void main(String[] args)
	{
		ImageDownloader.bandImage(null,"CHELSEA GRIN / VEIL OF MAYA");
	}*/
}
