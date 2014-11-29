package pl.javaparty.concertfinder;

import java.io.IOException;
import java.util.List;

import pl.javaparty.concertmanager.Concert;
import pl.javaparty.concertmanager.ConcertManager;
import pl.javaparty.jsoup.JsoupDownloader;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RecentFragment extends Fragment {

	JsoupDownloader jsoupDownloader;
	ConcertManager concertMgr;
	ArrayAdapter<String> adapterSearchBox, adapterList, adapterDrawer;
	ConcertAdapter adapter;
	ListView lv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.recent_fragment, container, false);
		jsoupDownloader = new JsoupDownloader();
		concertMgr = new ConcertManager();

		getActivity().getActionBar().setTitle("Ostatnie koncerty");
		new DownloadTask().execute();

		lv = (ListView) view.findViewById(R.id.recentList);

		return view;
	}

	private class DownloadTask extends AsyncTask<Void, Void, String> {
		// TODO: zrobiæ informacje ze stanem pobierania

		@Override
		protected String doInBackground(Void... params) {

			try {
				jsoupDownloader.getData(); // pobieramy dane
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
			adapter = new ConcertAdapter(getActivity(), R.layout.list_row, concertMgr.getList());
			lv.setAdapter(adapter);
			super.onPostExecute(result);
		}
	}
}