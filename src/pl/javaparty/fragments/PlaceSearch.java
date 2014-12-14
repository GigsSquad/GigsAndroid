package pl.javaparty.fragments;

import pl.javaparty.adapters.ConcertAdapter;
import pl.javaparty.concertfinder.R;
import pl.javaparty.concertmanager.Concert;
import pl.javaparty.sql.dbManager;
import android.content.Context;
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

public class PlaceSearch extends Fragment{

	AutoCompleteTextView searchBox;
	ListView concertList;
	ArrayAdapter<String> adapterSearchBox, adapterList;
	ConcertAdapter adapter;
	Context context;
	dbManager dbm;
	private String lastSearching;
	private int lastPosition;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.tab_search_place, container, false);
		getActivity().getActionBar().setTitle("Szukaj wg miejsca");
		
		dbm = (dbManager) getArguments().getSerializable("dbManager");//przekazujemy dbm od mainActivity
		
		context = inflater.getContext();

		searchBox = (AutoCompleteTextView) view.findViewById(R.id.searchBoxPlace);
		concertList = (ListView) view.findViewById(R.id.concertListPlace);
		
		adapterSearchBox = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, dbm.getCities());

		searchBox.setAdapter(adapterSearchBox);
		searchBox.setThreshold(1);
		//new DownloadTask().execute();

		searchBox.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				String city = searchBox.getText().toString();
				adapter = new ConcertAdapter(getActivity(), R.layout.list_row, dbm.getConcertsByCity(city)); //concertMgr.getConcertList(searchBox.getText().toString()));
				concertList.setAdapter(adapter);
				//zapisywanie danych, coby potem przywrocic
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
				args.putSerializable("dbManager", dbm);
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
		if(adapter!=null)
		{
			concertList.setAdapter(adapter);
			concertList.setSelection(lastPosition);
		}
		if(lastSearching!=null)
			getActivity().getActionBar().setTitle("Szukaj: " + lastSearching);
		
	}

	
}
