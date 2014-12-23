package pl.javaparty.fragments;

import java.util.Arrays;

import pl.javaparty.adapters.ConcertAdapter;
import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import pl.javaparty.items.Concert;
import pl.javaparty.sql.dbManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class RecentFragment extends Fragment {

	ArrayAdapter<String> adapterSearchBox, adapterList, adapterDrawer;
	ConcertAdapter adapter;
	ListView lv;
	Context context;
	dbManager dbm;
	Button nextButton;
	private int lastPosition = 0;
	private int showedConcerts = 20;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.fragment_recent, container, false);
		getActivity().getActionBar().setTitle("Najbliższe koncerty");
		context = inflater.getContext();
		lv = (ListView) view.findViewById(R.id.recentList);

		dbm = MainActivity.getDBManager();

		// button na koncu listy ktory rozwija liste o wincyj jesli sie da
		nextButton = new Button(context);
		nextButton.setText("Pokaż więcej");
		nextButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				lastPosition = showedConcerts;
				showedConcerts += 20;
				refresh();
				lv.setSelection(lastPosition - 1);
			}
		});

		lv.addFooterView(nextButton);

		adapter = new ConcertAdapter(getActivity(), cutArray(dbm.getAllConcerts()));
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i("LV", "KLIK");
				lastPosition = position;

				Intent concertInfo = new Intent(context, ConcertFragment.class);
				Concert item = (Concert) parent.getAdapter().getItem(position);
				concertInfo.putExtra("ID", item.getID());
				startActivity(concertInfo);
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

	private Concert[] cutArray(Concert[] array)
	{
		if (showedConcerts >= dbm.getSize(dbManager.CONCERTS_TABLE) - 1)
		{
			showedConcerts = dbm.getSize(dbManager.CONCERTS_TABLE) - 1;
			nextButton.setVisibility(View.GONE);
			return array;
		}
		return Arrays.copyOfRange(array, 0, showedConcerts);
	}

	public void refresh()
	{
		adapter = new ConcertAdapter(getActivity(), cutArray(dbm.getAllConcerts()));
		lv.setAdapter(adapter);
	}
}
