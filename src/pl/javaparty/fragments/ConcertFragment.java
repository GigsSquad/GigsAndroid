package pl.javaparty.fragments;

import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import pl.javaparty.sql.dbManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class ConcertFragment extends Fragment {

	private FragmentTabHost mTabHost;
	private dbManager dbm;
	int ID;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		getActivity().getActionBar().setHomeButtonEnabled(true);
		dbm = ((MainActivity) getActivity()).getDBManager();
		ID = (getArguments().getInt("ID", -1)); // -1 bo bazadanych numeruje od 1 a nie od 0
		setHasOptionsMenu(true);

		mTabHost = new FragmentTabHost(getActivity());
		mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabhost);

		mTabHost.addTab(mTabHost.newTabSpec("fragmentb").setIndicator("Informacje"), InfoConcertTab.class, getArguments());
		mTabHost.addTab(mTabHost.newTabSpec("fragmenta").setIndicator("Mapa"), MapConcertTab.class, getArguments());
		return (View) mTabHost;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.activity_main_actions, menu);
		
		//zmienia ikonkê na 
		//if(ID == /*ulubione z bazy*/)
		//menu.getItem(0).setIcon(R.drawable.ic_action_favorite_on);
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.favorite_icon:
			dbm.addFavouriteConcert(ID);
			//TODO: tutaj taki sam IF jak w onCreateOptionsMenu()
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}