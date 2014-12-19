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
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoConcertTab extends Fragment {

	TextView artist, place, date, price, url;
	ImageView image;
	Button connect;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.tab_fragment_concert_info, container, false);
		artist = (TextView) view.findViewById(R.id.artist_name);
		place = (TextView) view.findViewById(R.id.place_name);
		date = (TextView) view.findViewById(R.id.con_date);
		// price = (TextView) view.findViewById(R.id.priceTV);
		image = (ImageView) view.findViewById(R.id.artist_image);
		connect = (Button) view.findViewById(R.id.connect);

		int ID = (getArguments().getInt("ID", -1)); // -1 bo bazadanych numeruje od 1 a nie od 0
		dbManager dbm = ((MainActivity)getActivity()).getDBManager();
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
		connect.setOnClickListener(new OnClickListener() { // otwiera przegladarkê z linkiem do koncertu
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
}