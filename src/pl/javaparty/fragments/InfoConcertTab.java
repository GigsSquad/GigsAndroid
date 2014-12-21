package pl.javaparty.fragments;

import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import pl.javaparty.imageloader.ImageLoader;
import pl.javaparty.sql.dbManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoConcertTab extends Fragment {

	TextView artist, place, date, price, url;
	ImageView image;
	Button connect;
	dbManager dbm;
	int ID;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.tab_fragment_concert_info, container, false);
		artist = (TextView) view.findViewById(R.id.artist_name);
		place = (TextView) view.findViewById(R.id.place_name);
		date = (TextView) view.findViewById(R.id.con_date);
		// price = (TextView) view.findViewById(R.id.priceTV);
		image = (ImageView) view.findViewById(R.id.artist_image);
		connect = (Button) view.findViewById(R.id.connect);
		dbm = MainActivity.getDBManager();

		Log.i("KURWA", "INFO");

		ID = (getArguments().getInt("ID", -1)); // -1 bo bazadanych numeruje od 1 a nie od 0
		dbm = MainActivity.getDBManager();
		Log.i("KURWA", "Przes³ane id: " + ID);
		String artistName = dbm.getArtist(ID);
		getActivity().getActionBar().setTitle(artistName);
		artist.setText(artistName);
		place.setText(dbm.getCity(ID) + " " + dbm.getSpot(ID));
		Log.i("DMB", "City: " + dbm.getCity(ID));
		Log.i("DMB", "Spot: " + dbm.getSpot(ID));
		date.setText(dbm.getDate(ID));
		// price.setText(concert.get);
		final String URL = dbm.getUrl(ID);
		new ImageLoader(inflater.getContext()).DisplayImage(artistName, image);

		connect.setOnClickListener(new OnClickListener() { // otwiera przegladarkï¿½ z linkiem do koncertu
			@Override
			public void onClick(View arg0) {
				Log.i("KLIK", "KLIK");
				Intent intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(URL));
				startActivity(intent);
			}

		});
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.activity_main_actions, menu);

		// zmienia ikonkê na
		if (dbm.isConcertFavourite(ID))
			menu.getItem(0).setIcon(R.drawable.ic_action_favorite_on);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.favorite_icon:
			dbm.addFavouriteConcert(ID);
			// TODO: tutaj taki sam IF jak w onCreateOptionsMenu()
			if (dbm.isConcertFavourite(ID))
				item.setIcon(R.drawable.ic_action_favorite_on);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}