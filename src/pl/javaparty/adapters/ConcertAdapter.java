package pl.javaparty.adapters;

import pl.javaparty.concertfinder.R;
import pl.javaparty.imageloader.ImageLoader;
import pl.javaparty.items.Concert;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ConcertAdapter extends ArrayAdapter<Concert> {

	Concert rowItem;
	ViewHolder holder;
	ImageLoader imageLoader;
	private Typeface tf;
	int ID; // unikalne id koncertu, nie jest wyœwietlane ale bêdzie przydatne przy

	public ConcertAdapter(Context context, Concert[] items) {
		super(context, R.layout.card_layout, items);
		imageLoader = new ImageLoader(context);
		tf = Typeface.createFromAsset(getContext().getAssets(), "font/robotocondensed-light.ttf");
	}

	public class ViewHolder {
		ImageView image;
		TextView title;
		TextView place;
		TextView date;
		RelativeLayout card;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		rowItem = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.card_layout, null);
			holder = new ViewHolder();
			holder.card = (RelativeLayout) convertView.findViewById(R.id.card);
			holder.image = (ImageView) convertView.findViewById(R.id.list_image);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.place = (TextView) convertView.findViewById(R.id.placeTV);
			holder.date = (TextView) convertView.findViewById(R.id.dateTV);

			holder.title.setTypeface(tf);
			holder.place.setTypeface(tf);
			holder.date.setTypeface(tf);


			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		String titleString = rowItem.getArtist();
		
		titleString = titleString.replace(" - ", "\n");
		titleString = titleString.replace(": ", "\n");
		
		int length = titleString.length();
		holder.title.setText(titleString);
		holder.title.setTextSize(50 - (length / 3));
		holder.place.setText(getPreparedPlace(rowItem.getPlace()));
		holder.date.setText(rowItem.dateToString());
		
		// holder.pb.setVisibility(View.GONE); //progress bar, bo Miachu wyrazil zezaprobate 
		imageLoader.DisplayImage(rowItem.getArtist(), holder.image);
		Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
		holder.card.startAnimation(animation);
		return convertView;
	}

	public int getID()
	{
		return ID;
	}
	
	private String getPreparedPlace(String place)
	{
		final int MAX_LENGTH = 25;
		if(place!=null && place.length()>MAX_LENGTH)
			return place.substring(0, MAX_LENGTH)+"...";
		return place;
	}
}