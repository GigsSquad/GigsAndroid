package pl.javaparty.imageloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.javaparty.concertfinder.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader
{
	private final static String TAG = "ImageLoader";
	 FileExplorer fileExplorer;//bedzie pobieral pliki obrazkow zapisanych w pamieci
	 MemoryCache memoryCache; //przechowuje bitmapy w pamieci podrecznej
	 ExecutorService executorService;
	 Handler handler; //bedzie wrzucal Runnable do kolejki wykonania sie
	 private Map<ImageView, String> imageViews; //do tej pory uzyte ImageView i nazwa zespolu do niego przypisana
	 Context context;
	 private final int DEF_IMG_ID = R.drawable.stock_pic;
	 
	 public ImageLoader(Context context)
	 {
		 this.context = context;
		 imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>()); 
		// handler = new Handler();
		 fileExplorer = new FileExplorer(context);
		 memoryCache = new MemoryCache();
		 executorService = Executors.newFixedThreadPool(5);

	 }
	 
	 public void DisplayImage(String bandName, ImageView imageView)
	    {
		 	Log.i(TAG, "Proces ladowania obrazka: " + bandName);
	        imageViews.put(imageView, bandName);
	        Bitmap bitmap=memoryCache.get(bandName);
	        if(bitmap!=null)
	        {
	        	//najszybsze
	        	imageView.setImageBitmap(bitmap);
	        	Log.i(TAG, "Obrazek "+ bandName + " zaladowany z pamieci podrecznej.");
	        }
	        else
	        {
	            queuePhoto(bandName, imageView);
	            imageView.setImageResource(DEF_IMG_ID);
	        }
	    }
	 
	 public void queuePhoto(String bandName, ImageView imageView)
	 {
		 PhotoToLoad p = new PhotoToLoad(bandName, imageView);
		 executorService.submit(new LoadPhoto(p));
	 }
	 
	 private class PhotoToLoad //przechowuje dane zdjecia do zaladowania
	 {
		 public String bandName;
		 public ImageView imageView;
		 public PhotoToLoad(String bandName, ImageView imageView)
		 {
			 this.bandName = bandName;
			 this.imageView = imageView;
		 }
	 }
	 
	 public class LoadPhoto implements Runnable
	 {
		 PhotoToLoad photoToLoad;
		 public LoadPhoto(PhotoToLoad p)
		 {
			 photoToLoad = p;
		 }
		 
		@Override
		public void run()
		{
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.bandName);
			memoryCache.put(photoToLoad.bandName, bmp);// TODO czasem wrzuca niepotrzebnie
			Log.i(TAG, "Wrzucanie obrazka do pamiďż˝ci podrďż˝cznej.");

			// if (imageViewReused(photoToLoad))
			// return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			photoToLoad.imageView.post(bd);
		}
		 
	 }

	private boolean imageViewReused(PhotoToLoad photoToLoad)//rowniez wylacza progressBara
	{
		String tag = imageViews.get(photoToLoad.imageView);
		/*if(tag==null || !tag.equals(photoToLoad.bandName))
		{
			return true;
		}
		return false;*/
		return tag==null || !tag.equals(photoToLoad.bandName);
	}

	private Bitmap getBitmap(String bandName)
	{
		
		bandName = parseName(bandName);
		synchronized (bandName.intern())//jesli jakis watek w danej chwili pracuje nad tym to ten czeka
		{
			File bandPictureFile = fileExplorer.getFile(bandName);

			Bitmap bitmap = null;
			try
			{
				if (!bandPictureFile.exists())
				{
					ImageDownloader.bandImage(bandPictureFile, bandName);// sciaga
																			// obrazek
				}

				// dekoduje obrazek
				bitmap = decodeFile(bandPictureFile);

				return bitmap;
			} catch (Throwable ex) //gdy zabraknie nam pamieci
			{
				if (ex instanceof OutOfMemoryError)
					memoryCache.clear();
				else
					ex.printStackTrace();
				return null;
			}
		}
	}
	
	//dekoduje obrazek i skaluje go (oszczedzanie ramu)
    private Bitmap decodeFile(File f){
        try 
        {            
            //decode with inSampleSize
            FileInputStream stream =new FileInputStream(f);
            Bitmap bitmap=BitmapFactory.decodeStream(stream);
            stream.close();
            return bitmap;
        } catch (FileNotFoundException e) 
        {
        	Log.e(TAG, "Nie znaleziono pliku!");
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return null;
    }
	
	private static String parseName(String bandName)
	{
		String edited = bandName;
		int indexOfColon = edited.indexOf(": "); // w goAhead dodatkowe info po myslniku tu nie potrzebne
		if (indexOfColon != -1)
			edited = edited.substring(indexOfColon+1, edited.length());
		
		int indexOfHyphen = edited.indexOf(" - "); // w goAhead dodatkowe info po myslniku tu nie potrzebne
		if (indexOfHyphen != -1)
			edited = edited.substring(0, indexOfHyphen);

		int indexOfAmpersand = edited.indexOf(" & ");// w goAhead jak jest kilka zespolow
		if (indexOfAmpersand != -1)
			edited = edited.substring(0, indexOfAmpersand);

		int indexOfSlash = edited.indexOf(" / "); // w goAhead jak jest kilka zespolow
		if (indexOfSlash != -1)
			edited = edited.substring(0, indexOfSlash);
		
		int indexOfPlus = edited.indexOf(" + "); // w ticketPro jak jest kilka zespolow
		if (indexOfPlus != -1)
			edited = edited.substring(0, indexOfPlus);
		
		int indexOfComa = edited.indexOf(", "); //w ticketPro jak jest kilka zespolow
		if (indexOfComa != -1)
			edited = edited.substring(0, indexOfComa);
		
		edited = edited.trim();
		edited = edited.replace(' ', '+');// w last fm spacja zastepowana plusem
		//zmiana polskich znakow
		edited = edited.toUpperCase(Locale.ENGLISH);
		edited = edited.replace('Ą', 'A');
		edited = edited.replace('Ć', 'C');
		edited = edited.replace('Ę', 'E');
		edited = edited.replace('Ł', 'L');
		edited = edited.replace('Ń', 'N');
		edited = edited.replace('Ó', 'O');
		edited = edited.replace('Ś', 'S');
		edited = edited.replace('Ż', 'Z');
		edited = edited.replace('Ź', 'Z');
		Log.i(TAG, "po parsie: " + edited);
		return edited;
	}
	
	public class BitmapDisplayer implements Runnable
	{
		Bitmap bitmap;
        PhotoToLoad photoToLoad;
        
        public BitmapDisplayer(Bitmap b, PhotoToLoad p)
        {
        	bitmap=b;
        	photoToLoad=p;
        }
		@Override
		public void run()
		{
			if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null)
            {
            	Log.i(TAG, "LADOWANIE...");
                photoToLoad.imageView.setImageBitmap(bitmap);
            }
		}
	}
	
	public void clearCache()
	{
		memoryCache.clear();
		fileExplorer.clear();
	}
}
