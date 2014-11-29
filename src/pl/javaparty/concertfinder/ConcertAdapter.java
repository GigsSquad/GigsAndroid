package pl.javaparty.concertfinder;

import java.util.List;

import pl.javaparty.concertmanager.Concert;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ConcertAdapter extends ArrayAdapter<Concert> {

	Context context;

	public ConcertAdapter(Context context, int resourceId, List<Concert> items) {
		super(context, resourceId, items);
		this.context = context;
	}

	public class ViewHolder {
		ImageView image;
		TextView title;
		TextView description;
		LinearLayout card;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Concert rowItem = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_row, null);
			holder = new ViewHolder();
			holder.card = (LinearLayout) convertView.findViewById(R.id.card);
			holder.image = (ImageView) convertView.findViewById(R.id.list_image);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.description = (TextView) convertView.findViewById(R.id.description);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.title.setText(rowItem.getPlace());
		holder.description.setText(rowItem.getPlace());

		Animation animation = AnimationUtils.loadAnimation(context, R.anim.card_animation);
		holder.card.startAnimation(animation);

		return convertView;
	}
}