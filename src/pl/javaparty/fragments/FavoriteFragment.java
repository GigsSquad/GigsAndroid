package pl.javaparty.fragments;

import pl.javaparty.adapters.ConcertAdapter;
import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import pl.javaparty.sql.dbManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FavoriteFragment extends Fragment {
	private dbManager dbm;
	private ListView list;
	private ConcertAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.fragment_favorite, container, false);

		getActivity().getActionBar().setTitle("Twoje koncerty");
		dbm = ((MainActivity) getActivity()).getDBManager();// przekazujemy dbm od mainActivity
		list = (ListView) view.findViewById(R.id.FavouriteList);

		//Log.i("FAV", "WIELKOSC: " + .length);
		//Log.i("FAV", "Pierwszy koncert: " + dbm.getAllFavouriteConcert()[0].toString());

		adapter = new ConcertAdapter(getActivity(), dbm.getAllFavouriteConcert());
		list.setAdapter(adapter);

		return view;
	}
}