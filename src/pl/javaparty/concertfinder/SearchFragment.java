package pl.javaparty.concertfinder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import pl.javaparty.concertmanager.Concert;
import pl.javaparty.concertmanager.ConcertManager;
import pl.javaparty.jsoup.ImageDownloader;
import pl.javaparty.jsoup.JsoupDownloader;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

public class SearchFragment extends Fragment {

	JsoupDownloader jsoupDownloader;
	AutoCompleteTextView searchBox;
	ConcertManager concertMgr;
	ListView concertList;
	ArrayAdapter<String> adapterSearchBox, adapterList;
	ConcertAdapter adapter;
	Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.search_fragment, container, false);
		// String menu = getArguments().getString("Menu");
		context = inflater.getContext();
		jsoupDownloader = new JsoupDownloader();
		concertMgr = new ConcertManager();

		searchBox = (AutoCompleteTextView) view.findViewById(R.id.searchBox);
		concertList = (ListView) view.findViewById(R.id.concertList);
		
		getActivity().getActionBar().setTitle("Szukaj");

		new DownloadTask().execute();

		searchBox.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				//adapter = new ConcertAdapter(getActivity(), R.layout.list_row, concertMgr.getList());
				//lv.setAdapter(adapter);
				
				
				adapter =  new ConcertAdapter(getActivity(), R.layout.list_row, concertMgr.getConcertList(searchBox.getText().toString()));
				concertList.setAdapter(adapter);

				// links = new String[adapterList.getCount()];
				// /for (Concert c : concertMgr.getConcerts(searchBox.getText().toString()))
				// {
				// c.getURL();
				// }

				getActivity().getActionBar().setTitle("Szukaj: " + searchBox.getText().toString());
				searchBox.setText("");
			}
		});
		
		concertList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				Fragment fragment = new ConcertFragment();
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
				
				//Intent intent = new Intent(MainActivity.this, InfoPage.class);
				// intent.putExtra("URL", concert.getURL()); 
				//intent.putExtra("URL", "Clicked: " + position); 
				//startActivity(intent);
			}
		});

		return view;
	}

	private class DownloadTask extends AsyncTask<Void, Void, String> {
		// TODO: zrobiæ informacje ze stanem pobierania

		@Override
		protected String doInBackground(Void... params) {

			try {
				jsoupDownloader.getData(); // pobieramy dane
				
				/*//pobieramy obrazki do artystow
				HashSet<String> artists = jsoupDownloader.getArtists();
				Iterator<String> it = artists.iterator();
				while(it.hasNext())
				{
					String artist = it.next();
					ImageDownloader.bandImage(Environment.getExternalStorageDirectory(), artist);//downloadedImages/<firstBandWord>.png/jpg
				}*/
				
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) { // zostanie wykonane po skoñczeniu doInBackground
			super.onPostExecute(result);
			String[] stockArr = new String[concertMgr.getArtists().size()];
			stockArr = concertMgr.getArtists().toArray(stockArr);

			adapterSearchBox = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, stockArr);

			searchBox.setAdapter(adapterSearchBox);
			searchBox.setThreshold(1);

		}
	}
}