package pl.javaparty.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import pl.javaparty.adapters.ConcertAdapter;
import pl.javaparty.concertfinder.Observer;
import pl.javaparty.concertfinder.R;
import pl.javaparty.items.Concert;
import pl.javaparty.sql.DatabaseManager;

public class FavoriteFragment extends Fragment implements Observer {
	private ListView list;
	private ConcertAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        getActivity().getActionBar().setTitle(getString(R.string.favourite));
		list = (ListView) view.findViewById(R.id.FavouriteList);

		adapter = new ConcertAdapter(getActivity(), DatabaseManager.getInstance(getActivity().getApplicationContext()).getAllFavouriteConcert());
		list.setAdapter(adapter);

		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				Intent concertInfo = new Intent(getActivity().getApplicationContext(), ConcertFragment.class);
				Concert item = (Concert) parent.getAdapter().getItem(position);
				concertInfo.putExtra("ID", item.getID());
				startActivity(concertInfo);
			}
		});

		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		refresh();
	}

	public void refresh()
	{
		adapter = new ConcertAdapter(getActivity(), DatabaseManager.getInstance(getActivity().getApplicationContext()).getAllFavouriteConcert());
		list.setAdapter(adapter);
	}

}