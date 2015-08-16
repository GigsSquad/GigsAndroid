package pl.javaparty.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import pl.javaparty.sql.DatabaseManager;
import pl.javaparty.utils.UtilsObject;

public class TabConcertGoogleMap extends Fragment {

	static int ID;
	private static View view;
	private static GoogleMap mMap;
	private static FragmentManager fragmentManager;
	static Context context;

	private static void setUpMapIfNeeded() {
		if (mMap == null) {
			mMap = ((SupportMapFragment) fragmentManager.findFragmentById(R.id.location_map)).getMap();
			if (mMap != null)
				setUpMap();
		}
	}

	private static void setUpMap() {
		mMap.setMyLocationEnabled(true); // pokazuje naszą pozycje
		LatLng latLng;

		//Log.i("MAP", "RAW: " + Double.parseDouble(DatabaseManager.getInstance(context).getLat(ID) + " " + Double.parseDouble(DatabaseManager.getInstance(context).getLon(ID))));
		try {

			latLng = new LatLng(Double.parseDouble(DatabaseManager.getInstance(context).getLat(ID)), Double.parseDouble(DatabaseManager.getInstance(context).getLon(ID)));

			if (latLng.latitude == 0 || latLng.longitude == 0)
				throw new NumberFormatException();

			mMap.addMarker(new MarkerOptions().position(latLng).title(DatabaseManager.getInstance(context).getCity(ID) + " " + DatabaseManager.getInstance(context).getSpot(ID))
					.snippet(DatabaseManager.getInstance(context).getArtist(ID) + " " + DatabaseManager.getInstance(context).getDate(ID))); // ustawia marker
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f)); // przybliza do markera
		} catch (NumberFormatException nfe) {
            Toast.makeText(context, context.getString(R.string.wrong_adress), Toast.LENGTH_SHORT).show();
            Log.w("MAP", "Brak adresu");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		ID = getArguments().getInt("ID", -1);
		context = getActivity();
		setHasOptionsMenu(true);

		if (container == null)
			return null;

		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}

		try {
			view = inflater.inflate(R.layout.tab_fragment_concert_google_map, container, false);
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		}

		fragmentManager = getChildFragmentManager();

		if (UtilsObject.isOnline(context)) {
			setUpMapIfNeeded();
		} else {
            Toast.makeText(getActivity(), context.getString(R.string.no_connection), Toast.LENGTH_LONG).show();
            Log.w("MAP", "Urządzenie nie ma dostępu do internetu");
		}

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (mMap != null && UtilsObject.isOnline(context))
			setUpMap();

		if (mMap == null) {
			mMap = ((SupportMapFragment) fragmentManager.findFragmentById(R.id.location_map)).getMap();
			if (mMap != null && UtilsObject.isOnline(context))
				setUpMap();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mMap != null)
			mMap = null;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.concert_map_menu, menu);
		if (DatabaseManager.getInstance(context).isConcertFavourite(ID))
			menu.getItem(0).setIcon(R.drawable.ic_action_important_w);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.favorite_icon:
			if (DatabaseManager.getInstance(context).isConcertFavourite(ID))// wyjebujemy
			{
				DatabaseManager.getInstance(context).removeFavouriteConcert(ID);
				item.setIcon(R.drawable.ic_action_not_important_w);
			} else {
				DatabaseManager.getInstance(context).addFavouriteConcert(ID);
				item.setIcon(R.drawable.ic_action_important_w);
			}
			return true;
		case R.id.website_icon:
			Intent websiteIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(DatabaseManager.getInstance(context).getUrl(ID)));
			startActivity(websiteIntent);
			return true;

		case R.id.share:
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, DatabaseManager.getInstance(context).getArtist(ID) + ", " + DatabaseManager.getInstance(context).getCity(ID) + " (" + DatabaseManager.getInstance(context).getDate(ID) + ")");
			sendIntent.setType("text/plain");
			startActivity(sendIntent);
			return true;

		case R.id.naviagte_icon:
			Intent navIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://maps.google.com/maps?saddr=&daddr=" + DatabaseManager.getInstance(context).getCity(ID) + " " + DatabaseManager.getInstance(context).getSpot(ID)));
			startActivity(navIntent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
