package pl.javaparty.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ConcertFragment extends Fragment {

	private FragmentTabHost mTabHost;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		getActivity().getActionBar().setHomeButtonEnabled(true);

		mTabHost = new FragmentTabHost(getActivity());
		mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabhost);

		mTabHost.addTab(mTabHost.newTabSpec("fragmentb").setIndicator("Informacje"), InfoConcertTab.class, getArguments());
		mTabHost.addTab(mTabHost.newTabSpec("fragmenta").setIndicator("Mapa"), MapConcertTab.class, getArguments());

		return (View) mTabHost;
	}

}