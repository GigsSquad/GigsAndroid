package pl.javaparty.fragments;

import pl.javaparty.adapters.ConcertAdapter;
import pl.javaparty.concertfinder.R;
import pl.javaparty.sql.dbManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FavoriteFragment extends Fragment{
	private dbManager dbm;
	private ArrayAdapter<String> adapterDrawer;
	private ListView list;
	private ConcertAdapter adapter;
	private Context context;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.fragment_favorite, container, false);

		getActivity().getActionBar().setTitle("Twoje koncerty");
		dbm = (dbManager) getArguments().getSerializable("dbManager");//przekazujemy dbm od mainActivity
		context = inflater.getContext();
		list = (ListView) view.findViewById(R.id.FavouriteList);
		
		
		adapter = new ConcertAdapter(getActivity(), R.layout.list_row,dbm.getAllFavouriteConcert());
		list.setAdapter(adapter);
		
		return view;
	}
}