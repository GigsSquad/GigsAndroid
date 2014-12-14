package pl.javaparty.fragments;


import pl.javaparty.concertfinder.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SearchFragment extends Fragment {

		private FragmentTabHost mTabHost;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			getActivity().getActionBar().setHomeButtonEnabled(true);

			mTabHost = new FragmentTabHost(getActivity());
			mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.tab_host);

			mTabHost.addTab(mTabHost.newTabSpec("fragmenta").setIndicator("Artysta"), ArtistSearch.class, getArguments());
			mTabHost.addTab(mTabHost.newTabSpec("fragmentb").setIndicator("Miejsce"), PlaceSearch.class, getArguments());
			mTabHost.addTab(mTabHost.newTabSpec("fragmentc").setIndicator("Data"), DateSearch.class, getArguments());
			for(int i=0;i<3;i++)
				mTabHost.getTabWidget().getChildAt(i).setFocusable(false);
			
			return (View) mTabHost;
		}
}