package pl.javaparty.concertfinder;

import java.io.IOException;

import pl.javaparty.concertmanager.ConcertManager;
import pl.javaparty.jsoup.JsoupDownloader;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class RecentFragment extends Fragment {

	JsoupDownloader jsoupDownloader;
	ConcertManager concertMgr;
	ArrayAdapter<String> adapterSearchBox, adapterList, adapterDrawer;
	CardView cards;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.search_fragment, container, false);
		jsoupDownloader = new JsoupDownloader();
		concertMgr = new ConcertManager();

		getActivity().getActionBar().setTitle("Ostatnie koncerty");
		

		new DownloadTask().execute();

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
			super.onPostExecute(result);
		}
	}
}