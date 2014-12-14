package pl.javaparty.adapters;

import pl.javaparty.concertfinder.R;
import pl.javaparty.concertmanager.Concert;
import pl.javaparty.imageloader.ImageLoader;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ConcertAdapter extends ArrayAdapter<Concert> {

	Context context;
	Concert rowItem;
	ViewHolder holder;
	ImageLoader imageLoader;
	private Typeface tf;
	int ID; // unikalne id koncertu, nie jest wyœwietlane ale bêdzie przydatne przy

	public ConcertAdapter(Context context, int resourceId, Concert[] items) {
		super(context, resourceId, items);
		this.context = context;
		imageLoader = new ImageLoader(context);
		tf = Typeface.createFromAsset(context.getAssets(), "font/robotocondensed-light.ttf");
	}

	public class ViewHolder {
		ImageView image;
		TextView title;
		TextView description;
		RelativeLayout card;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		rowItem = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.card_layout, null);
			holder = new ViewHolder();
			holder.card = (RelativeLayout) convertView.findViewById(R.id.card);
			holder.image = (ImageView) convertView.findViewById(R.id.list_image);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.description = (TextView) convertView.findViewById(R.id.description);

			holder.title.setTypeface(tf);
			holder.description.setTypeface(tf);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.title.setText(rowItem.getArtist());
		Log.i("ROW", rowItem.getArtist());
		holder.description.setText(rowItem.getPlace() + " " + rowItem.dateToString());

		// holder.pb.setVisibility(View.GONE);
		imageLoader.DisplayImage(rowItem.getArtist(), holder.image);

		// Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
		// holder.card.startAnimation(animation);
		return convertView;
	}

	public int getID()
	{
		return ID;
	}
}