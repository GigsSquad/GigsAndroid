package pl.javaparty.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import pl.javaparty.imageloader.ImageLoader;
import pl.javaparty.jsoup.TicketPrices;
import pl.javaparty.map.MapHelper;
import pl.javaparty.prefs.Prefs;
import pl.javaparty.sql.dbManager;

import java.util.Calendar;

public class TabConcertInfo extends Fragment {

	TextView artist, place, date, addCalendar, howlong, distance,
			ticketsDetails1, ticketsDetails2, ticketsDetails3;
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
		ticketsDetails1 = (TextView) view.findViewById(R.id.ticketsDetails1);
		ticketsDetails2 = (TextView) view.findViewById(R.id.ticketsDetails2);
		ticketsDetails3 = (TextView) view.findViewById(R.id.ticketsDetails3);

		setHasOptionsMenu(true);

		ID = (getArguments().getInt("ID", -1)); // -1 bo bazadanych numeruje od 1 a nie od 0
		dbm = MainActivity.getDBManager();

		String artistName = dbm.getArtist(ID);
		getActivity().getActionBar().setTitle(artistName);

		new ImageLoader(inflater.getContext()).DisplayImage(artistName, image);
		new calculateDistance().execute();

		artistName = artistName.replace(" - ", "\n");
		artistName = artistName.replace(": ", ":\n");

		artist.setText(artistName);
		place.setText((dbm.getCity(ID) + " " + dbm.getSpot(ID)).trim());
		date.setText(dbm.getDate(ID));

		Calendar today = Calendar.getInstance();
		long diff = dbm.getConcertByID(ID).getCalendar().getTimeInMillis() - today.getTimeInMillis();
		int days = (int) Math.ceil((diff / (24 * 60 * 60 * 1000))) + 1;

		if (days < 0) {
			howlong.setVisibility(View.GONE);
		} else if (days == 1) {
			howlong.setText("To już dziś!");
		} else if (days == 2) {
			howlong.setText("To już jutro!");
		} else {
			howlong.setText("pozostało jeszcze " + days + " dni");
		}


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

		//Pobieranie cen biletów
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new TicketPrices(dbm.getUrl(ID), dbm.getAgency(ID), ticketsDetails1, ticketsDetails2, ticketsDetails3)
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			new TicketPrices(dbm.getUrl(ID), dbm.getAgency(ID), ticketsDetails1, ticketsDetails2, ticketsDetails3).execute();

		}
		ticketsDetails1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dbm.getUrl(ID)));
				startActivity(browserIntent);

			}
		});

		ticketsDetails2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dbm.getUrl(ID)));
				startActivity(browserIntent);

			}
		});

		ticketsDetails3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dbm.getUrl(ID)));
				startActivity(browserIntent);
			}
		});
		return view;
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
			} else {
				dbm.addFavouriteConcert(ID);
				item.setIcon(R.drawable.ic_action_important_w);
			}

			MainActivity.updateCounters();
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
			Intent navIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://maps.google.com/maps?saddr=&daddr=" + dbm.getCity(ID) + " " + dbm.getSpot(ID)));
			startActivity(navIntent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class calculateDistance extends AsyncTask<Void, Void, Void> {
		int distanceInt = 0;

		@Override
		protected void onPreExecute() {
			distance.setVisibility(View.GONE);
			mapHelper = new MapHelper(getActivity());
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			distanceInt = mapHelper.distanceTo(dbm.getCity(ID).trim());
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
}