package pl.javaparty.concertfinder;

import pl.javaparty.concertmanager.Concert;
import pl.javaparty.concertmanager.ConcertManager;
import pl.javaparty.sql.dbManager;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RecentFragment extends Fragment {

	ConcertManager concertMgr;
	ArrayAdapter<String> adapterSearchBox, adapterList, adapterDrawer;
	ConcertAdapter adapter;
	ListView lv;
	Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.recent_fragment, container, false);
		getActivity().getActionBar().setTitle("Ostatnie koncerty");
		context = inflater.getContext();
		lv = (ListView) view.findViewById(R.id.recentList);

		new DownloadTask().execute();

		return view;
	}

	private class DownloadTask extends AsyncTask<Void, Void, String> {
		// TODO: zrobiæ informacje ze stanem pobierania

		@Override
		protected String doInBackground(Void... params) {
			concertMgr = new ConcertManager(new dbManager(context));
			adapter = new ConcertAdapter(getActivity(), R.layout.list_row, concertMgr.getList());
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) { // zostanie wykonane po skoñczeniu doInBackground

			for (Concert c : concertMgr.getList())
				Log.i("ARRAY", c.getArtist());

			for (int i = 0; i < concertMgr.getList().size(); i++)
				Log.i("ADAPTER", adapter.getItem(i).getArtist());

			lv.setAdapter(adapter);

			for (int i = 0; i < concertMgr.getList().size(); i++)
				Log.i("LIST", lv.getItemAtPosition(i).toString());

			super.onPostExecute(result);
		}
	}
}
