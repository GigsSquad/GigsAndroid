package pl.javaparty.concertfinder;

import pl.javaparty.concertmanager.ConcertManager;
import pl.javaparty.sql.dbManager;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.recent_fragment, container, false);
		getActivity().getActionBar().setTitle("Ostatnie koncerty");
		

		concertMgr = new ConcertManager(new dbManager(view.getContext()));
		adapter = new ConcertAdapter(getActivity(), R.layout.list_row, concertMgr.getList());
		
		new DownloadTask().execute();

		lv = (ListView) view.findViewById(R.id.recentList);
		lv.setAdapter(adapter);
		

//		lv = (ListView) view.findViewById(R.id.recentList);
		return view;
	}

	private class DownloadTask extends AsyncTask<Void, Void, String> {
		// TODO: zrobi� informacje ze stanem pobierania

		@Override
		protected String doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) { // zostanie wykonane po sko�czeniu doInBackground
			adapter = new ConcertAdapter(getActivity(), R.layout.list_row, concertMgr.getList());
			lv.setAdapter(adapter);
			super.onPostExecute(result);
		}
	}
}