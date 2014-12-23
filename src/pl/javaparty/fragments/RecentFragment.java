package pl.javaparty.fragments;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import pl.javaparty.adapters.ConcertAdapter;
import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import pl.javaparty.fragments.FilterDialogFragment.FilterDialogListener;
import pl.javaparty.items.Concert;
import pl.javaparty.items.Concert.AgencyName;
import pl.javaparty.sql.dbManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	private Map<CharSequence, Boolean> checkedAgencies;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.fragment_recent, container, false);
		getActivity().getActionBar().setTitle("Najbli¿sze koncerty");
		context = inflater.getContext();
		lv = (ListView) view.findViewById(R.id.recentList);
		
		checkedAgencies = new HashMap<>();
		AgencyName[] vals = AgencyName.values();
		for(int i = 0; i< vals.length; i++)
		{
			checkedAgencies.put(vals[i].name(), true);
		}
		
		setHasOptionsMenu(true);
		
		dbm = MainActivity.getDBManager();

		// button na koncu listy ktory rozwija liste o wincyj jesli sie da
		nextButton = new Button(context);
		nextButton.setText("Poka¿ wiêcej");
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

		adapter = new ConcertAdapter(getActivity(), cutArray(dbm.getAllConcerts(filterAgencies())));
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
		adapter = new ConcertAdapter(getActivity(), cutArray(dbm.getAllConcerts(filterAgencies())));
		lv.setAdapter(adapter);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.search_fragment_actions, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
		case R.id.filter_icon:
			FilterDialogFragment dialog = new FilterDialogFragment();
			dialog.setFilterDialogListener(new FilterDialogListener()
			{
					@Override
					public void onDialogPositiveClick(boolean[] checked)
					{
						//this.checked = checked;
						int i = 0;
						for(CharSequence c: checkedAgencies.keySet())
						{
							checkedAgencies.put(c, checked[i++]);
						}

						refresh();

					}

					@Override
					public void onDialogNegativeClick(boolean[] checked)
					{
						//nevermind
					}
			});
			Bundle args = new Bundle();
			
			boolean[] checked = new boolean[checkedAgencies.size()];
			int i = 0;
			for(Boolean b: checkedAgencies.values())
			{
				checked[i++]=b;
			}
			args.putBooleanArray("CHECKED", checked);
			
			CharSequence[] agencies = new CharSequence[checkedAgencies.size()];
			checkedAgencies.keySet().toArray(agencies);
			args.putCharSequenceArray("AGENCIES", agencies);
			dialog.setArguments(args);
			
			dialog.show(getActivity().getFragmentManager(), "FILTER");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private String filterAgencies()
	{
		String returned = "'1'='0'";
		for(CharSequence c: checkedAgencies.keySet())
		{
			if(checkedAgencies.get(c))
			{
				returned+=" OR AGENCY = '" + c + "'";
			}
		}
		return returned;
		
	}
}
