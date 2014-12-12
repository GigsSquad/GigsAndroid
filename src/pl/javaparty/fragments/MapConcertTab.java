package pl.javaparty.fragments;

import java.io.IOException;
import java.util.List;

import pl.javaparty.concertfinder.R;
import pl.javaparty.sql.dbManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapConcertTab extends Fragment {

	private static View view;
	private static GoogleMap mMap;
	private static FragmentManager fragmentManager;
	private Geocoder geoCoder;
	private List<Address> address;
	private static LatLng destLatLng;
	static int ID;
	static dbManager dbm;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		ID = getArguments().getInt("ID", -1);
		dbm = (dbManager) getArguments().getSerializable("dbManager");

		if (container == null)
			return null;

		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}

		try {
			view = inflater.inflate(R.layout.tab_fragment_concert_map, container, false);
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		}

		try {
			geoCoder = new Geocoder(getActivity());
			address = geoCoder.getFromLocationName(dbm.getCity(ID) + " " + dbm.getSpot(ID), 1);
			Address loc = address.get(0); // pierwsze co znajdzie i bedzie najlepiej dopasowane
			destLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
			Log.i("LATLNG", "Lat: " + loc.getLatitude() + "Long: " + loc.getLongitude());
		} catch (IOException e) {
			e.printStackTrace();
		}

		fragmentManager = getChildFragmentManager();

		setUpMapIfNeeded();

		return view;
	}

	public static void setUpMapIfNeeded() {
		if (mMap == null) {
			mMap = ((SupportMapFragment) fragmentManager.findFragmentById(R.id.location_map)).getMap();
			if (mMap != null)
				setUpMap();
		}
	}

	private static void setUpMap() {
		mMap.setMyLocationEnabled(true); // pokazuje nasz¹ pozycje
		mMap.addMarker(new MarkerOptions().position(destLatLng).title(dbm.getCity(ID) + " " + dbm.getSpot(ID))
				.snippet(dbm.getArtist(ID) + " " + dbm.getDate(ID))); // ustawia marker
		mMap.animateCamera(CameraUpdateFactory
				.newLatLngZoom(new LatLng(destLatLng.latitude, destLatLng.longitude), 19.0f)); // przybliza do markera
	}

	//
	// public double CalculationByDistance(GeoPoint StartP, GeoPoint EndP) {
	// int Radius = 6371;// radius of earth in Km
	// double lat1 = StartP.getLatitudeE6() / 1E6;
	// double lat2 = EndP.getLatitudeE6() / 1E6;
	// double lon1 = StartP.getLongitudeE6() / 1E6;
	// double lon2 = EndP.getLongitudeE6() / 1E6;
	// double dLat = Math.toRadians(lat2 - lat1);
	// double dLon = Math.toRadians(lon2 - lon1);
	// double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
	// Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	// Math.sin(dLon / 2) * Math.sin(dLon / 2);
	// double c = 2 * Math.asin(Math.sqrt(a));
	// double valueResult = Radius * c;
	// double km = valueResult / 1;
	// DecimalFormat newFormat = new DecimalFormat("####");
	// kmInDec = Integer.valueOf(newFormat.format(km));
	// meter = valueResult % 1000;
	// meterInDec = Integer.valueOf(newFormat.format(meter));
	// Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec);
	//
	// return Radius * c;
	// }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (mMap != null)
			setUpMap();

		if (mMap == null) {
			mMap = ((SupportMapFragment) fragmentManager.findFragmentById(R.id.location_map)).getMap();
			if (mMap != null)
				setUpMap();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mMap != null)
			mMap = null;
	}
}
