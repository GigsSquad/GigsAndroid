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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
	private static Address loc;
	static int ID;
	static dbManager dbm;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		if (container == null) {
			return null;
		}

		ID = getArguments().getInt("ID", -1);
		dbm = (dbManager) getArguments().getSerializable("dbManager");

		try {
			geoCoder = new Geocoder(getActivity());
			address = geoCoder.getFromLocationName(dbm.getCity(ID) + " " + dbm.getSpot(ID), 1);
			loc = address.get(0); // pierwsze co znajdzie i bedzie najlepiej dopasowane
			loc.getLatitude();
			loc.getLongitude();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		view = inflater.inflate(R.layout.tab_fragment_concert_map, container, false);
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
		mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title(dbm.getSpot(ID)).snippet(dbm.getArtist(ID))); // ustawia
																																				// marker
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 12.0f)); // przybliza
																															// do
																															// tego
																															// markera
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
	public void onDestroyView() { // TODO: problem
		super.onDestroyView();
		if (mMap != null) {
			// fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
			fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.location_map)).commit();
			mMap = null;
		}
	}
}
