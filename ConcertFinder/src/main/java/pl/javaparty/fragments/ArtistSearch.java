package pl.javaparty.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import pl.javaparty.adapters.ConcertAdapter;
import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import pl.javaparty.items.Concert;
import pl.javaparty.sql.dbManager;

public class ArtistSearch extends Fragment {

	AutoCompleteTextView searchBox;
    Switch switchCon;
	ListView concertList;
    ArrayAdapter<String> adapterSearchBox;
    ConcertAdapter adapter;
	dbManager dbm;
    private String lastSearching = "";
    private int lastPosition;
    private boolean future = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.tab_search_artist, container, false);
		getActivity().getActionBar().setTitle("Szukaj wg Artysty");

		dbm = MainActivity.getDBManager();// przekazujemy dbm od mainActivity

		searchBox = (AutoCompleteTextView) view.findViewById(R.id.searchBoxArtist);
        switchCon = (Switch) view.findViewById(R.id.switchCon);
		concertList = (ListView) view.findViewById(R.id.concertListArtist);
		String filter = getArguments().getString("CONDITIONS");
		Log.i("FILTRUJE", filter);
		adapterSearchBox = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, dbm.getFutureArtists(filter));

		searchBox.setAdapter(adapterSearchBox);
		searchBox.setThreshold(1);

		searchBox.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				String artist = searchBox.getText().toString();
				String filter = getArguments().getString("CONDITIONS");
                adapter = new ConcertAdapter(getActivity(), future?
                        dbm.getFutureConcertsByArtist(artist, filter):dbm.getPastConcertsByArtist(artist, filter));
                adapterSearchBox = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line,
                        future? dbm.getFutureArtists(filter) : dbm.getPastArtists(filter));
                searchBox.setAdapter(adapterSearchBox);
                searchBox.setThreshold(1);
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
				Intent concertInfo = new Intent(getActivity().getApplicationContext(), ConcertFragment.class);
				Concert item = (Concert) parent.getAdapter().getItem(position);
				concertInfo.putExtra("ID", item.getID());
				startActivity(concertInfo);
			}
		});

        switchCon.setChecked(true);
        switchCon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                future = isChecked;
                String artist = lastSearching;
                String filter = getArguments().getString("CONDITIONS");
                adapter = new ConcertAdapter(getActivity(), future ?
                        dbm.getFutureConcertsByArtist(artist, filter) : dbm.getPastConcertsByArtist(artist, filter));
                adapterSearchBox = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line,
                        future ? dbm.getFutureArtists(filter) : dbm.getPastArtists(filter));
                searchBox.setAdapter(adapterSearchBox);
                searchBox.setThreshold(1);
                concertList.setAdapter(adapter);
                // zapisywanie danych, coby potem przywrocic
                getActivity().getActionBar().setTitle("Szukaj: " + artist);
                searchBox.setText("");

                if (adapter.getCount() == 0 && artist.length() > 0)
                    Toast.makeText(getActivity(), (future ? switchCon.getTextOn() : switchCon.getTextOff()) +
                            " koncerty niedostępne dla " + artist, Toast.LENGTH_LONG).show();
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
	
	public void refresh()
	{
		String filter = getArguments().getString("CONDITIONS");
		adapterSearchBox = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, dbm.getArtists(filter));

		searchBox.setAdapter(adapterSearchBox);
	}
}
