package pl.javaparty.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class PlaceSearch extends Fragment {

	AutoCompleteTextView searchBox;
	ListView concertList;
	ArrayAdapter<String> adapterSearchBox;
	ConcertAdapter adapter;
    Switch switchCon;
    private boolean future = true;
	Context context;
	dbManager dbm;
	private String lastSearching;
	private int lastPosition;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.tab_search_place, container, false);
		getActivity().getActionBar().setTitle("Szukaj wg miejsca");

		dbm = MainActivity.getDBManager();// przekazujemy dbm od mainActivity

		context = inflater.getContext();

		searchBox = (AutoCompleteTextView) view.findViewById(R.id.searchBoxPlace);
        switchCon = (Switch) view.findViewById(R.id.switchCon2);
        concertList = (ListView) view.findViewById(R.id.concertListPlace);
		String filter = getArguments().getString("CONDITIONS");
		adapterSearchBox = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, dbm.getCities(filter));

		searchBox.setAdapter(adapterSearchBox);
		searchBox.setThreshold(1);
		// new DownloadTask().execute();

		searchBox.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				String city = searchBox.getText().toString();
				String filter = getArguments().getString("CONDITIONS");
				adapter = new ConcertAdapter(getActivity(), future?
                        dbm.getFutureConcertsByCity(city,filter):dbm.getPastConcertsByCity(city,filter));
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
                String city = lastSearching;
                String filter = getArguments().getString("CONDITIONS");
                adapter = new ConcertAdapter(getActivity(), future?
                        dbm.getFutureConcertsByCity(city, filter):dbm.getPastConcertsByCity(city, filter));
                concertList.setAdapter(adapter);
                // zapisywanie danych, coby potem przywrocic
                getActivity().getActionBar().setTitle("Szukaj: " + city);
                searchBox.setText("");
                if(adapter.getCount() == 0 && city.length()>0)
                    Toast.makeText(getActivity(),(future? switchCon.getTextOn():switchCon.getTextOff())+
                            " koncerty niedostępne dla "+city,Toast.LENGTH_LONG).show();

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
		adapterSearchBox = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, dbm.getCities(filter));

		searchBox.setAdapter(adapterSearchBox);
	}
}
