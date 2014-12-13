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
	private List<Address> addressList;
	static Address address;
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

		geoCoder = new Geocoder(getActivity());

		try {
			addressList = geoCoder.getFromLocationName(dbm.getCity(ID) + " " + dbm.getSpot(ID), 1);
			Log.i("DBM", "City: " + dbm.getCity(ID) + " Spot: " + dbm.getSpot(ID));
			address = addressList.get(0); // pierwsze co znajdzie i bedzie najlepiej dopasowane
		} catch (IndexOutOfBoundsException e) {

			try {
				addressList = geoCoder.getFromLocationName(dbm.getCity(ID), 1);
				address = addressList.get(0); // pierwsze co znajdzie i bedzie najlepiej dopasowane
			} catch (IOException e1) {
			} catch (IndexOutOfBoundsException e2)
			{
			}

		} catch (IOException e) {
		} finally {
		}

		destLatLng = new LatLng(address.getLatitude(), address.getLongitude());
		Log.i("LATLNG", "Lat: " + address.getLatitude() + "Long: " + address.getLongitude());

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
