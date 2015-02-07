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
import pl.javaparty.map.MapHelper;
import pl.javaparty.sql.dbManager;

public class TabConcertGoogleMap extends Fragment {

	static int ID;
	static dbManager dbm;
	private static View view;
	private static GoogleMap mMap;
	private static FragmentManager fragmentManager;
	private static MapHelper mapHelper;

	private static void setUpMapIfNeeded() {
		if (mMap == null) {
			mMap = ((SupportMapFragment) fragmentManager.findFragmentById(R.id.location_map)).getMap();
			if (mMap != null)
				setUpMap();
		}
	}

	private static void setUpMap() {
		mMap.setMyLocationEnabled(true); // pokazuje naszą pozycje
		String spot = dbm.getCity(ID);
		LatLng latLng = mapHelper.getLatLng(spot);
		mMap.addMarker(new MarkerOptions().position(latLng).title(dbm.getCity(ID) + " " + dbm.getSpot(ID)).snippet(
				dbm.getArtist(ID) + " " + dbm.getDate(ID))); // ustawia marker
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f)); // przybliza do markera
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		ID = getArguments().getInt("ID", -1);

		dbm = MainActivity.getDBManager();

		mapHelper = new MapHelper(getActivity());

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

		if (isOnline()) {
			setUpMapIfNeeded();
		} else {
			Toast.makeText(getActivity(), "Brak połączenia", Toast.LENGTH_LONG).show();
			Log.w("MAP", "Urządzenie nie ma dostępu do internetu");
		}

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (mMap != null && isOnline())
			setUpMap();

		if (mMap == null) {
			mMap = ((SupportMapFragment) fragmentManager.findFragmentById(R.id.location_map)).getMap();
			if (mMap != null && isOnline())
				setUpMap();
		}
	}

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
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
}
