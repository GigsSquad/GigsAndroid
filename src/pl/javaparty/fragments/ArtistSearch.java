package pl.javaparty.fragments;

import pl.javaparty.adapters.ConcertAdapter;
import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import pl.javaparty.items.Concert;
import pl.javaparty.sql.dbManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

public class ArtistSearch extends Fragment {

	AutoCompleteTextView searchBox;
	ListView concertList;
	ArrayAdapter<String> adapterSearchBox, adapterList;
	ConcertAdapter adapter;
	dbManager dbm;
	private String lastSearching;
	private int lastPosition;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.tab_search_artist, container, false);
		getActivity().getActionBar().setTitle("Szukaj wg Artysty");

		dbm = ((MainActivity) getActivity()).getDBManager();// przekazujemy dbm od mainActivity

		searchBox = (AutoCompleteTextView) view.findViewById(R.id.searchBoxArtist);
		concertList = (ListView) view.findViewById(R.id.concertListArtist);

		adapterSearchBox = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, dbm.getArtists());

		searchBox.setAdapter(adapterSearchBox);
		searchBox.setThreshold(1);
		// new DownloadTask().execute();

		searchBox.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				String artist = searchBox.getText().toString();
				adapter = new ConcertAdapter(getActivity(), dbm.getConcertsByArtist(artist));
				concertList.setAdapter(adapter);
				// zapisywanie danych, coby potem przywrocic
				lastSearching = searchBox.getText().toString();
				getActivity().getActionBar().setTitle("Szukaj: " + searchBox.getText().toString());
				searchBox.setText("");
			}
		});

		concertList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				lastPosition = position;
				Fragment fragment = new ConcertFragment();
				Bundle args = new Bundle();
				Concert item = (Concert) parent.getAdapter().getItem(position);
				args.putInt("ID", item.getID()); // przesylam unikalne id koncertu
				fragment.setArguments(args);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(getTag()).commit();
			}
		});

		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (adapter != null)
		{
			concertList.setAdapter(adapter);
			concertList.setSelection(lastPosition);
		}
		if (lastSearching != null)
			getActivity().getActionBar().setTitle("Szukaj: " + lastSearching);

	}

}
