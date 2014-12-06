package pl.javaparty.concertfinder;

import java.util.List;

import pl.javaparty.concertmanager.Concert;
import pl.javaparty.jsoup.ImageDownloader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ConcertAdapter extends ArrayAdapter<Concert> {

	Context context;
	Concert rowItem;
	ViewHolder holder;
	int ID; // unikalne id koncertu, nie jest wyœwietlane ale bêdzie przydatne przy

	public ConcertAdapter(Context context, int resourceId, List<Concert> items) {
		super(context, resourceId, items);
		this.context = context;
	}

	public class ViewHolder {
		ImageView image;
		TextView title;
		TextView description;
		RelativeLayout card;
		ProgressBar pb;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		rowItem = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_row, null);
			holder = new ViewHolder();
			holder.card = (RelativeLayout) convertView.findViewById(R.id.card);
			holder.image = (ImageView) convertView.findViewById(R.id.list_image);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.description = (TextView) convertView.findViewById(R.id.description);
			holder.pb = (ProgressBar) convertView.findViewById(R.id.progress_bar);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.title.setText(rowItem.getArtist());
		Log.i("ROW", rowItem.getArtist());
		holder.description.setText(rowItem.getPlace() + " " + rowItem.dateToString());

		// new DownloadTask().execute();
		test(); // w ImageDownlaoder tworzony jest thread, dlatego nie korzystam z asyncTaska

		Animation animation = AnimationUtils.loadAnimation(context, R.anim.card_animation);
		holder.card.startAnimation(animation);
		return convertView;
	}
	
	public int getID()
	{
		return ID;
	}
	
	private void test()
	{

		ImageDownloader.bandImage(Environment.getExternalStorageDirectory(), rowItem.getArtist());
		if (holder != null)
		{
			String bandName = holder.title.getText().toString();
			int index = bandName.indexOf(" ");
			if (index != -1)
				bandName = bandName.substring(0, index);
			String path = ImageDownloader.exists(Environment.getExternalStorageDirectory(), bandName);
			if (path != null)
			{
				Bitmap picture = BitmapFactory.decodeFile(path);
				holder.image.setImageBitmap(picture);
				holder.pb.setVisibility(View.GONE);
			}
		}
	}
}