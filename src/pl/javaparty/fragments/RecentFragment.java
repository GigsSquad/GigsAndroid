package pl.javaparty.fragments;

import pl.javaparty.adapters.ConcertAdapter;
import pl.javaparty.concertfinder.R;
import pl.javaparty.concertmanager.Concert;
import pl.javaparty.concertmanager.ConcertManager;
import pl.javaparty.sql.dbManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RecentFragment extends Fragment {

	ConcertManager concertMgr;
	ArrayAdapter<String> adapterSearchBox, adapterList, adapterDrawer;
	ConcertAdapter adapter;
	ListView lv;
	Context context;
	dbManager dbm;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.fragment_recent, container, false);
		getActivity().getActionBar().setTitle("Ostatnie koncerty");
		context = inflater.getContext();
		lv = (ListView) view.findViewById(R.id.recentList);

		new DownloadTask().execute();

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				Fragment fragment = new ConcertFragment();
				Bundle args = new Bundle();

				Concert item = (Concert) parent.getAdapter().getItem(position);
				args.putInt("ID", item.getID()); // przesylam unikalne id koncertu

				fragment.setArguments(args);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
			}
		});
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
			lv.setAdapter(adapter);
			super.onPostExecute(result);
		}
	}
}
