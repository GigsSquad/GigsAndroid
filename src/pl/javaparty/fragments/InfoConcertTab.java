package pl.javaparty.fragments;

import java.io.IOException;
import java.util.Calendar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import pl.javaparty.imageloader.ImageLoader;
import pl.javaparty.map.MapHelper;
import pl.javaparty.prefs.Prefs;
import pl.javaparty.sql.dbManager;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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

	TextView artist, place, date, addCalendar, howlong, distance,ticketsDetails1, ticketsDetails2,ticketsDetails3 ;
	ImageView image;
	dbManager dbm;
	MapHelper mapHelper;
	String[] prices;
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
		prices = new String[2]; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		prices[0] = "LEFT";
		prices[1] = "CENTER";
		setHasOptionsMenu(true);

		ID = (getArguments().getInt("ID", -1)); // -1 bo bazadanych numeruje od 1 a nie od 0
		dbm = MainActivity.getDBManager();

		String artistName = dbm.getArtist(ID);
		getActivity().getActionBar().setTitle(artistName);
		new ImageLoader(inflater.getContext()).DisplayImage(artistName, image);
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
		
	/*	mHandler = new Handler(Looper.getMainLooper())
		{
			  
	          handleMessage() defines the operations to perform when
	          the Handler receives a new Message to process.
			   
			 @Override
	        public void handleMessage(Message inputMessage) {
	            ticketsDetails1.setVisibility(View.VISIBLE);
				ticketsDetails1.setText("DUPA");// to zmienie zaraz
	        }*/
		//}; 
	       

			
		new Thread( new Runnable() {
			
			@Override
			public void run()  {
				
				String agencyName = dbm.getAgency(ID);
			//	Handler mHandler = new Handler();
				
				if(agencyName.equals("TICKETPRO"))
				{	
					
					try{
						Document doc = Jsoup.connect(dbm.getUrl(ID)).timeout(1000000).get();
						Element el = doc.select("div[id=poleCena]").first();
						String rawString = el.text();
						
						rawString = rawString.replaceAll("[^0-9]+", " ");
						rawString = rawString.trim();
						prices = rawString.split(" ");
						
						Log.i("CENYPrzed", rawString);
					}catch(IOException e)
					{
						Log.i("PobieranieCenyKoncertu", "Blad podczas pobierania cennika");
					}
						//zmiana gui
						getActivity().runOnUiThread(new Runnable(){
							
							public void run(){
								Log.i("PrzedUIUPdate", "TUTAJ");
								try {
									Thread.sleep(3000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								updatePricesUI();
								Log.i("PoUIUPdate", "TUTAJ");
							}			
						});
				}	
			}
		}).start();
		
	
		
		ticketsDetails1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				Intent browserIntent = 
                        new Intent(Intent.ACTION_VIEW, Uri.parse(dbm.getUrl(ID)));
		    startActivity(browserIntent);
				
			}
		});
			
	ticketsDetails2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent browserIntent = 
                        new Intent(Intent.ACTION_VIEW, Uri.parse(dbm.getUrl(ID)));
		    startActivity(browserIntent);
					
				}
		});
	
	ticketsDetails3.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			Intent browserIntent = 
                        new Intent(Intent.ACTION_VIEW, Uri.parse(dbm.getUrl(ID)));
		    startActivity(browserIntent);
		}
	});
		
		return view;
	}
	
private void updatePricesUI()
	{	
		if(prices!=null){
			
		
			if(prices.length>=1) 
			{	
				ticketsDetails1.setVisibility(View.VISIBLE);
				ticketsDetails1.setText(prices[0]);
				Log.i("RAz", "LEWO");
			}		
				
			if(prices.length>=2) 
			{	ticketsDetails2.setVisibility(View.VISIBLE);
				ticketsDetails2.setText(prices[1]);
				Log.i("RAz", "Center");
			}
			if(prices.length>=3) 
			{
				ticketsDetails3.setVisibility(View.VISIBLE);
				ticketsDetails3.setText(prices[2]);
			}	
		}
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
			Intent navIntent = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse("http://maps.google.com/maps?saddr=&daddr=" + dbm.getCity(ID) + " " + dbm.getSpot(ID)));
			startActivity(navIntent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}


