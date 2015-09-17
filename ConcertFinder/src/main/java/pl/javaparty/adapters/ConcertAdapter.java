package pl.javaparty.adapters;

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
import pl.javaparty.concertfinder.R;
import pl.javaparty.imageloader.ImageLoader;
import pl.javaparty.items.Concert;
import pl.javaparty.sql.DatabaseManager;

import java.util.ArrayList;
import java.util.Collections;

public class ConcertAdapter extends ArrayAdapter<Concert> {

    Concert rowItem;
    ViewHolder holder;
    ImageLoader imageLoader;
    private Typeface tf;
    private Concert[] items;

    public ConcertAdapter(Context context, Concert[] items) {
        super(context, R.layout.card_layout, toArrL(items));
        this.items = items;
        imageLoader = ImageLoader.init(context);
        tf = Typeface.createFromAsset(getContext().getAssets(), "font/robotocondensed-light.ttf");
    }

    private static ArrayList<Concert> toArrL(Concert[] items) {
        ArrayList<Concert> ar = new ArrayList<Concert>();
        Collections.addAll(ar, items);
        return ar;
    }

    public class ViewHolder {
        ImageView image;
        ImageView fav;
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
            holder.fav = (ImageView) convertView.findViewById(R.id.fav_image);

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
        //Log.i("MAPS", "Lat:" + Prefs.getInstance(context)..getLat(getContext()) + "\nLon:" + Prefs.getInstance(context)..getLon(getContext()));
        holder.title.setText(titleString); // + "\n" + rowItem.getDistance() + "\nLat: " + rowItem.getLat() + "\nLon" + rowItem.getLon());
        holder.title.setTextSize(50 - (length / 3));
        holder.place.setText(getPreparedPlace(rowItem.getSpot()));
        holder.date.setText(rowItem.dateToString());

        if (DatabaseManager.getInstance(getContext()).isConcertFavourite(rowItem.getID()))
            holder.fav.setImageResource(R.drawable.ic_action_important);
        else
            holder.fav.setImageResource(R.drawable.ic_action_not_important);

        imageLoader.DisplayImage(rowItem.getArtist(), holder.image);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        holder.card.startAnimation(animation);

        return convertView;
    }

    @Override
    public int getCount() {
        return items == null ? 0 : items.length;
    }

    public void changeData(Concert[] newData) {
        items = newData;
        clear();
        if (items != null) {
            addAll(items);
            notifyDataSetChanged();
        } else
            notifyDataSetInvalidated();
    }

    private String getPreparedPlace(String place) {
        final int MAX_LENGTH = 25;
        if (place != null && place.length() > MAX_LENGTH)
            return place.substring(0, MAX_LENGTH) + "(...)";
        return place;
    }
}