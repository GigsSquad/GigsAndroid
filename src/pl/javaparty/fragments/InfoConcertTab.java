package pl.javaparty.fragments;

import java.util.Calendar;

import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import pl.javaparty.imageloader.ImageLoader;
import pl.javaparty.map.MapHelper;
import pl.javaparty.prefs.Prefs;
import pl.javaparty.sql.dbManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoConcertTab extends Fragment {

	TextView artist, place, date, addCalendar, howlong, distance;
	ImageView image;
	dbManager dbm;
	MapHelper mapHelper;
	int ID;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.tab_fragment_concert_info, container, false);
		artist = (TextView) view.findViewById(R.id.artist_name);
		place = (TextView) view.findViewById(R.id.place_name);
		distance = (TextView) view.findViewById(R.id.distanceTV);
		date = (TextView) view.findViewById(R.id.con_date);
		howlong = (TextView) view.findViewById(R.id.howlongTV);
		addCalendar = (TextView) view.findViewById(R.id.add_to_calendar_btn);
		image = (ImageView) view.findViewById(R.id.artist_image);
		dbm = MainActivity.getDBManager();

		setHasOptionsMenu(true);

		ID = (getArguments().getInt("ID", -1)); // -1 bo bazadanych numeruje od 1 a nie od 0
		dbm = MainActivity.getDBManager();

		String artistName = dbm.getArtist(ID);
		getActivity().getActionBar().setTitle(artistName);
		artistName = artistName.replace(" - ", "\n");
		artistName = artistName.replace(": ", "\n");

		artist.setText(artistName);
		place.setText(dbm.getCity(ID) + " " + dbm.getSpot(ID));
		date.setText(dbm.getDate(ID));

		Calendar today = Calendar.getInstance();
		long diff = dbm.getConcertByID(ID).getCalendar().getTimeInMillis() - today.getTimeInMillis();
		int days = (int) Math.ceil((diff / (24 * 60 * 60 * 1000))) + 1;
		
		if (days <= 0)
			howlong.setVisibility(View.GONE);
		else
			howlong.setText("pozostało jeszcze " + days + " dni");

		new calculateDistance().execute();
		new ImageLoader(inflater.getContext()).DisplayImage(artistName, image);

		addCalendar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_INSERT);
				intent.setType("vnd.android.cursor.item/event");
				intent.putExtra(Events.TITLE, artist.getText());
				intent.putExtra(Events.EVENT_LOCATION, place.getText());
				intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, dbm.getConcertByID(ID).getCalendar().getTimeInMillis());
				intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
				// intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
				// dbm.getConcertByID(ID).getCalendar().getTimeInMillis() + ilość_godzin*(60 * 60 * 1000));
				// raczej niepotrzebne, ale można ustawić jak coś ;)
				startActivity(intent);
			}
		});

		return view;
	}

	private class calculateDistance extends AsyncTask<Void, Void, Void> {
		int distanceInt;

		@Override
		protected void onPreExecute() {
			distance.setVisibility(View.GONE);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			mapHelper = new MapHelper(getActivity());
			distanceInt = mapHelper.distanceTo(dbm.getCity(ID));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			distance.setText(distanceInt + "km od " + Prefs.getCity(getActivity()));
			if (distanceInt != 0)
				distance.setVisibility(View.VISIBLE);
			super.onPostExecute(result);
		}

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.concert_info_menu, menu);
		if (dbm.isConcertFavourite(ID))
			menu.getItem(0).setIcon(R.drawable.ic_action_important_w);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.favorite_icon:
			if (dbm.isConcertFavourite(ID))// wyjebujemy
			{
				dbm.removeFavouriteConcert(ID);
				item.setIcon(R.drawable.ic_action_not_important_w);
			}
			else
			{
				dbm.addFavouriteConcert(ID);
				item.setIcon(R.drawable.ic_action_important_w);
			}

			MainActivity.updateCounters(); // aktualizuje liczbę ulubionych koncertów w NavDrawerze
			return true;
		case R.id.website_icon:
			Intent websiteIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(dbm.getUrl(ID)));
			startActivity(websiteIntent);
			return true;

		case R.id.share:
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, dbm.getArtist(ID) + ", " + dbm.getCity(ID) + " (" + dbm.getDate(ID) + ")");
			sendIntent.setType("text/plain");
			startActivity(sendIntent);
			return true;

		case R.id.naviagte_icon:
			Intent navIntent = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse("http://maps.google.com/maps?saddr=" + Prefs.getCity(getActivity()) + "&daddr=" + dbm.getCity(ID) + " " + dbm.getSpot(ID)));
			startActivity(navIntent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}