package pl.javaparty.imageloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageLoader
{
	private final String TAG = "ImageLoader";
	 FileExplorer fileExplorer;//bedzie pobieral pliki obrazkow zapisanych w pamieci
	 MemoryCache memoryCache; //przechowuje bitmapy w pamieci podrecznej
	 ExecutorService executorService;
	 Handler handler; //bedzie wrzucal Runnable do kolejki wykonania sie
	 private Map<ImageView, String> imageViews; //do tej pory uzyte ImageView i nazwa zespolu do niego przypisana
	 Context context;
	 private final int DEF_IMG_ID = R.drawable.dummy_img;
	 
	 public ImageLoader(Context context)
	 {
		 this.context = context;
		 imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>()); 
		// handler = new Handler();
		 fileExplorer = new FileExplorer(context);
		 memoryCache = new MemoryCache();
		 executorService = Executors.newFixedThreadPool(4);
	 }
	 
	 public void DisplayImage(String bandName, ImageView imageView, ProgressBar progressBar)
	    {
		 	Log.i(TAG, "Proces ladowania obrazka: " + bandName);
	        imageViews.put(imageView, bandName);
	        Bitmap bitmap=memoryCache.get(bandName);
	        if(bitmap!=null)
	        {
	        	imageView.setImageBitmap(bitmap);
	        	progressBar.setVisibility(View.GONE);
	        	Log.i(TAG, "Obrazek "+ bandName + " zaladowany z pamieci podrecznej.");
	        }
	        else
	        {
	            queuePhoto(bandName, imageView, progressBar);
	            imageView.setImageResource(DEF_IMG_ID);
	        }
	    }
	 
	 public void queuePhoto(String bandName, ImageView imageView, ProgressBar progressBar)
	 {
		 PhotoToLoad p = new PhotoToLoad(bandName, imageView, progressBar);
		 executorService.submit(new LoadPhoto(p));
	 }
	 
	 private class PhotoToLoad //przechowuje dane zdjecia do zaladowania
	 {
		 public String bandName;
		 public ImageView imageView;
		 public ProgressBar progressBar;
		 public PhotoToLoad(String bandName, ImageView imageView, ProgressBar progressBar)
		 {
			 this.bandName = bandName;
			 this.imageView = imageView;
			 this.progressBar = progressBar;
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
			if(imageViewReused(photoToLoad))
                return;
            Bitmap bmp=getBitmap(photoToLoad.bandName);
            memoryCache.put(photoToLoad.bandName, bmp);
            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
            photoToLoad.imageView.post(bd);
            //handler.post(bd);
		}
		 
	 }

	private boolean imageViewReused(PhotoToLoad photoToLoad)//rowniez wylacza progressBara
	{
		String tag = imageViews.get(photoToLoad.imageView);
		if(tag==null || !tag.equals(photoToLoad.bandName))
		{
			//photoToLoad.progressBar.setVisibility(View.GONE);//TODO sprawdzam czy zadziala xD
			return true;
		}
		return false;
		//return tag==null || !tag.equals(photoToLoad.bandName);
	}

	private Bitmap getBitmap(String bandName)
	{
		bandName = parseName(bandName);
		File bandPictureFile=fileExplorer.getFile(bandName);
		Bitmap bitmap = null;
		
		//jesli plik nie istnieje najpierw sciaga obrazek
		if(!bandPictureFile.exists())
			ImageDownloader.bandImage(bandPictureFile, bandName);//sciaga obrazek
		
		//dekoduje obrazek
		bitmap = decodeFile(bandPictureFile);
		
        return bitmap;
	}
	
	//dekoduje obrazek i skaluje go (oszczedzanie ramu)
    private Bitmap decodeFile(File f){
        try 
        {
        	/*
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true; //nie zwraca bitmapy
            FileInputStream stream1=new FileInputStream(f);
            BitmapFactory.decodeStream(stream1,null,o);
            stream1.close();
            
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=300;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true)
            {
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }*/ 
        	//Skalowanie niepotrzebne, gdyz rozmiar niewiele mniejszy od obrazka
            
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
		int indexOfHyphen = edited.indexOf(" - "); // w goAhead dodatkowe info po myslniku tu nie potrzebne
		if (indexOfHyphen != -1)
			edited = edited.substring(0, indexOfHyphen);

		int indexOfAmpersand = edited.indexOf(" & ");// w goAhead jak jest kilka zespolow
		if (indexOfAmpersand != -1)
			edited = edited.substring(0, indexOfAmpersand);

		int indexOfSlash = edited.indexOf(" / "); // w goAhead jak jest kilka zespolow
		if (indexOfSlash != -1)
			edited = edited.substring(0, indexOfSlash);
		
		edited = edited.trim();
		edited = edited.replace(' ', '+');// w last fm spacja zastepowana plusem
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
            	Log.i(TAG, "Obrazek nie byl w pamieci podrecznej, LADOWANIE...");
                photoToLoad.imageView.setImageBitmap(bitmap);
                photoToLoad.progressBar.setVisibility(View.GONE);
            }
		}
	}
	
	public void clearCache()
	{
		memoryCache.clear();
		fileExplorer.clear();
	}
}
