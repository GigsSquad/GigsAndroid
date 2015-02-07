package pl.javaparty.fragments;

import java.util.HashMap;
import java.util.Map;

import pl.javaparty.concertfinder.R;
import pl.javaparty.fragments.FilterDialogFragment.FilterDialogListener;
import pl.javaparty.items.Concert.AgencyName;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class SearchFragment extends Fragment{

	private FragmentTabHost mTabHost;
	//private boolean[] checked;
	private Map<CharSequence, Boolean> checkedAgencies;
	private Bundle conditions;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		/*
		 * checked = new boolean[AgencyName.values().length];
		 * for(int i = 0; i<checked.length; i++)
		 *  	checked[i] = true;
		 */		
		checkedAgencies = new HashMap<>();
		AgencyName[] vals = AgencyName.values();
		for(int i = 0; i< vals.length; i++)
		{
			checkedAgencies.put(vals[i].name(), true);
		}
		
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		getActivity().getActionBar().setHomeButtonEnabled(true);
		
		setHasOptionsMenu(true);
		conditions = new Bundle();
		conditions.putString("CONDITIONS", filterAgencies());
		
		
		mTabHost = new FragmentTabHost(getActivity());
		
		mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.tab_host);
		
		mTabHost.addTab(mTabHost.newTabSpec("Artist").setIndicator("Artysta"), ArtistSearch.class, conditions);
		mTabHost.addTab(mTabHost.newTabSpec("Place").setIndicator("Miejsce"), PlaceSearch.class, conditions);
		mTabHost.addTab(mTabHost.newTabSpec("Date").setIndicator("Data"), DateSearch.class, conditions);
		for (int i = 0; i < 3; i++)
			mTabHost.getTabWidget().getChildAt(i).setFocusable(false);
		
		return (View) mTabHost;
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
						conditions.putString("CONDITIONS", filterAgencies());
						//mTabHost.invalidate();
						refreshTab();
						//mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab())
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
	
	private void refreshTab()
	{
		int currentTab = mTabHost.getCurrentTab();
		Log.i("FILTER","Refresh "+currentTab);
		if (currentTab == 0) 
		{
		    ArtistSearch ar = (ArtistSearch) getChildFragmentManager().findFragmentByTag("Artist");
		    // update the list
		    ar.refresh();
		} 
		else if (currentTab == 1)
		{
		    PlaceSearch pl = (PlaceSearch) getChildFragmentManager().findFragmentByTag("Place");
		    // update the list
		    pl.refresh();
		}
		else
		{
			//Daty nie trzeba odswiezac, bo nie ma AutoBoxa
		}
	}
}