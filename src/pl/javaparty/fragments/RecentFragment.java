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
import android.widget.ListView;

public class RecentFragment extends Fragment {

	ArrayAdapter<String> adapterSearchBox, adapterList, adapterDrawer;
	ConcertAdapter adapter;
	ListView lv;
	Context context;
	dbManager dbm;
	private int lastPosition = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.fragment_recent, container, false);
		getActivity().getActionBar().setTitle("Najbli¿sze koncerty");
		context = inflater.getContext();
		lv = (ListView) view.findViewById(R.id.recentList);
		
		dbm = (dbManager)getArguments().getSerializable("dbManager");
		
		adapter = new ConcertAdapter(getActivity(), R.layout.list_row, dbm.getAllConcerts());
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
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
				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
			}
		});
		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		lv.setSelection(lastPosition);
	}
	
}
