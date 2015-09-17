package pl.javaparty.concertfinder;

import android.support.v4.app.Fragment;
import pl.javaparty.fragments.*;
import pl.javaparty.items.Agencies;

/**
 * Created by jakub on 8/14/15.
 */
public class SimpleFragmentsFactory {

    public Fragment produceFragment(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new SearchFragment();
        } else if (position == 1) {
            fragment = new RecentFragment();
        } else if (position == 2) {
            fragment = new PastFragment();
        } else if (position == 3) {
            fragment = new FavoriteFragment();
        } else if (position == 4) {
            fragment = new SpectacleFragment();
        } else if (position == 5) {
            fragment = new SettingsFragment();
        } else if (position == 6) {
            fragment = new AboutFragment();
        } else if (position >= 100) {
            fragment = new RecentFragment();
            for (Agencies ch : RecentFragment.checkedAgencies.keySet()) {
                if (ch.fragmentNumber != position) {
                    RecentFragment.checkedAgencies.put(ch, false);
                }
            }
        }
        return fragment;
    }
}
