package pl.javaparty.fragments;

import pl.javaparty.concertfinder.R;
import pl.javaparty.map.MapHelper;
import pl.javaparty.prefs.Prefs;
import pl.javaparty.sql.dbManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapConcertTab extends Fragment {

	private static View view;
	private static GoogleMap mMap;
	private static FragmentManager fragmentManager;
	static int ID;
	static dbManager dbm;
	private static MapHelper mapHelper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		ID = getArguments().getInt("ID", -1);
		dbm = (dbManager) getArguments().getSerializable("dbManager");
		mapHelper = new MapHelper(getActivity());

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

		((Button) view.findViewById(R.id.btn_navigate)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
						Uri.parse("http://maps.google.com/maps?saddr=" + Prefs.getCity(getActivity()) + "&daddr=" + dbm.getCity(ID) + " " + dbm.getSpot(ID)));
				startActivity(intent);
			}
		});

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
		mMap.addMarker(new MarkerOptions().position(mapHelper.getLatLng(dbm.getCity(ID))).title(dbm.getCity(ID) + " " + dbm.getSpot(ID))
				.snippet(dbm.getArtist(ID) + " " + dbm.getDate(ID))); // ustawia marker
		mMap.animateCamera(CameraUpdateFactory
				.newLatLngZoom(mapHelper.getLatLng(dbm.getCity(ID)), 17.0f)); // przybliza do markera
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
