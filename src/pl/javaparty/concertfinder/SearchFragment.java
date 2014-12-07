package pl.javaparty.concertfinder;

import java.util.ArrayList;

import pl.javaparty.concertmanager.Concert;
import pl.javaparty.concertmanager.ConcertManager;
import pl.javaparty.sql.dbManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

public class SearchFragment extends Fragment {

	AutoCompleteTextView searchBox;
	ListView concertList;
	ArrayAdapter<String> adapterSearchBox, adapterList;
	ConcertAdapter adapter;
	Context context;
	ConcertManager concertMgr;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.search_fragment, container, false);
		getActivity().getActionBar().setTitle("Szukaj");

		context = inflater.getContext();
		concertMgr = new ConcertManager(new dbManager(context));

		searchBox = (AutoCompleteTextView) view.findViewById(R.id.searchBox);
		concertList = (ListView) view.findViewById(R.id.concertList);

		new DownloadTask().execute();

		searchBox.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				adapter = new ConcertAdapter(getActivity(), R.layout.list_row, concertMgr.getConcertList(searchBox.getText().toString()));
				concertList.setAdapter(adapter);

				getActivity().getActionBar().setTitle("Szukaj: " + searchBox.getText().toString());
				searchBox.setText("");
			}
		});

		concertList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

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

	private class DownloadTask extends AsyncTask<Void, Void, String> {
		// TODO: zrobiæ informacje ze stanem pobierania

		@Override
		protected String doInBackground(Void... params) {

			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) { // zostanie wykonane po skoñczeniu doInBackground
			super.onPostExecute(result);
			dbManager dbm = new dbManager(getActivity());
			String[] stockArr = new String[dbm.getSize()];
			stockArr = dbm.getArtist().toArray(stockArr);

			Log.i("ARTIST", "Lista: " + stockArr);

			adapterSearchBox = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, stockArr);

			searchBox.setAdapter(adapterSearchBox);
			searchBox.setThreshold(1);

		}
	}
}