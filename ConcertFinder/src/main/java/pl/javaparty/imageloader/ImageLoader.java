package pl.javaparty.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import pl.javaparty.concertfinder.R;

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

public class ImageLoader {
	private final static String TAG = "ImageLoader";
	private final int DEF_IMG_ID = R.drawable.stock_pic;
	FileExplorer fileExplorer;//bedzie pobieral pliki obrazkow zapisanych w pamieci
	MemoryCache memoryCache; //przechowuje bitmapy w pamieci podrecznej
	ExecutorService executorService;
	Context context;
	private Map<ImageView, String> imageViews; //do tej pory uzyte ImageView i nazwa zespolu do niego przypisana
    private static ImageLoader iloader;

	private ImageLoader(Context context) {
		this.context = context;
		imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
		fileExplorer = new FileExplorer(context);
		memoryCache = new MemoryCache();
		executorService = Executors.newFixedThreadPool(5);

	}

    public static ImageLoader init(Context context)
    {
        if(iloader==null)
        {
            iloader = new ImageLoader(context);
        }
        return iloader;
    }

	private static String parseName(String bandName) {
		String edited = bandName;
		int indexOfColon = edited.indexOf(": "); // w goAhead dodatkowe info po myslniku tu nie potrzebne
		if (indexOfColon != -1)
			edited = edited.substring(indexOfColon + 1, edited.length());

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
		return edited;
	}

	public void DisplayImage(String bandName, ImageView imageView) {
		Log.i(TAG, "Proces ladowania obrazka: " + bandName);
		imageViews.put(imageView, bandName);
		Bitmap bitmap = memoryCache.get(bandName);
		if (bitmap != null) {
			//najszybsze
			imageView.setImageBitmap(bitmap);
			//Log.i(TAG, "Obrazek "+ bandName + " zaladowany z pamieci podrecznej.");
		} else {
			queuePhoto(bandName, imageView);
			imageView.setImageResource(DEF_IMG_ID);
		}
	}

	public void queuePhoto(String bandName, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(bandName, imageView);
		executorService.submit(new LoadPhoto(p));
	}

	private boolean imageViewReused(PhotoToLoad photoToLoad)//rowniez wylacza progressBara
	{
		String tag = imageViews.get(photoToLoad.imageView);
		return tag == null || !tag.equals(photoToLoad.bandName);
	}

	private Bitmap getBitmap(String bandName) {

		bandName = parseName(bandName);
		synchronized (bandName.intern())//jesli jakis watek w danej chwili pracuje nad tym to ten czeka
		{
			File bandPictureFile = fileExplorer.getFile(bandName);

			Bitmap bitmap = null;
			try {
				if (!bandPictureFile.exists()) {
					ImageDownloader.bandImage(bandPictureFile, bandName);// sciaga obrazek
				}

				// dekoduje obrazek
				bitmap = decodeFile(bandPictureFile);

				return bitmap;
			} catch (Throwable ex) //gdy zabraknie nam pamieci
			{
				if (ex instanceof OutOfMemoryError)
					memoryCache.clear();
				//else ex.printStackTrace();
				return null;
			}
		}
	}

	//dekoduje obrazek i skaluje go (oszczedzanie ramu)
	private Bitmap decodeFile(File f) {
		try {
			//decode with inSampleSize
			FileInputStream stream = new FileInputStream(f);
			Bitmap bitmap = BitmapFactory.decodeStream(stream);
			stream.close();
			return bitmap;
		} catch (OutOfMemoryError ome) {
			clearCache();
		} catch (FileNotFoundException e) {
			//Log.e(TAG, "Nie znaleziono pliku!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void clearCache() {
		memoryCache.clear();
		fileExplorer.clear();
	}

	private class PhotoToLoad //przechowuje dane zdjecia do zaladowania
	{
		public String bandName;
		public ImageView imageView;

		public PhotoToLoad(String bandName, ImageView imageView) {
			this.bandName = bandName;
			this.imageView = imageView;
		}
	}

	public class LoadPhoto implements Runnable {
		PhotoToLoad photoToLoad;

		public LoadPhoto(PhotoToLoad p) {
			photoToLoad = p;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.bandName);
			memoryCache.put(photoToLoad.bandName, bmp);// TODO czasem wrzuca niepotrzebnie
			//Log.i(TAG, "Wrzucanie obrazka do pamięci podręcznej");

			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			photoToLoad.imageView.post(bd);
		}

	}

	public class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null) {
				//Log.i(TAG, "Ładowanie obrazka");
				photoToLoad.imageView.setImageBitmap(bitmap);
			}
		}
	}
}
