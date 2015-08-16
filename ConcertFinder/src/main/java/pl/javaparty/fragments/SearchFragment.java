package pl.javaparty.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.*;
import pl.javaparty.concertfinder.Observer;
import pl.javaparty.concertfinder.R;
import pl.javaparty.fragments.FilterDialogFragment.FilterDialogListener;
import pl.javaparty.items.Agencies;

import java.util.Map;

public class SearchFragment extends Fragment implements Observer {

    private FragmentTabHost mTabHost;
    //private boolean[] checked;
    private Map<Agencies, Boolean> checkedAgencies;
    private Bundle conditions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        checkedAgencies = Agencies.AgenciesMethods.initialize();


        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getActionBar().setHomeButtonEnabled(true);

        setHasOptionsMenu(true);
        conditions = new Bundle();
        conditions.putString("CONDITIONS", Agencies.AgenciesMethods.filterAgencies(checkedAgencies));


        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("Artist").setIndicator(getString(R.string.artist)), ArtistSearch.class, conditions);
        mTabHost.addTab(mTabHost.newTabSpec("Place").setIndicator(getString(R.string.place)), PlaceSearch.class, conditions);
        mTabHost.addTab(mTabHost.newTabSpec("Date").setIndicator(getString(R.string.date)), DateSearch.class, conditions);
        for (int i = 0; i < 3; i++)
            mTabHost.getTabWidget().getChildAt(i).setFocusable(false);

        return mTabHost;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_fragment_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_icon:
                FilterDialogFragment dialog = new FilterDialogFragment();

                Bundle args = new Bundle();

                boolean[] checked = new boolean[checkedAgencies.size()];
                int i = 0;
                for (Boolean b : checkedAgencies.values()) {
                    checked[i++] = b;
                }
                args.putBooleanArray("CHECKED", checked);
                dialog.setArguments(args);

                dialog.setFilterDialogListener(new FilterDialogListener() {
                    @Override
                    public void onDialogPositiveClick(boolean[] checked) {
                        //this.checked = checked;
                        int i = 0;
                        for (Agencies c : checkedAgencies.keySet()) {
                            checkedAgencies.put(c, checked[i++]);
                        }
                        conditions.putString("CONDITIONS", Agencies.AgenciesMethods.filterAgencies(checkedAgencies));
                        //mTabHost.invalidate();
                        refresh();
                        //mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab())
                    }

                    @Override
                    public void onDialogNegativeClick(boolean[] checked) {
                        //nevermind
                    }
                });

                dialog.show(getActivity().getFragmentManager(), "FILTER");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private String filterAgencies() {
        String returned = "'1'='0'";
        for (Agencies c : checkedAgencies.keySet()) {
            if (checkedAgencies.get(c)) {
                returned += " OR AGENCY = '" + c + "'";
            }
        }
        return returned;

    }

    @Override
    public void refresh() {
        int currentTab = mTabHost.getCurrentTab();
        Log.i("FILTER", "Refresh " + currentTab);
        if (currentTab == 0) {
            ArtistSearch ar = (ArtistSearch) getChildFragmentManager().findFragmentByTag("Artist");
            // update the list
            ar.refresh();
        } else if (currentTab == 1) {
            PlaceSearch pl = (PlaceSearch) getChildFragmentManager().findFragmentByTag("Place");
            // update the list
            pl.refresh();
        } else {
            //Daty nie trzeba odswiezac, bo nie ma AutoBoxa
        }
    }
}