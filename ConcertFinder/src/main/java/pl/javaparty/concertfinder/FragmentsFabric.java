package pl.javaparty.concertfinder;

import android.support.v4.app.Fragment;
import pl.javaparty.fragments.*;
import pl.javaparty.items.Agencies;

/**
 * Created by jakub on 8/14/15.
 */
public class FragmentsFabric {

    public Fragment produceFragment(int position) {
        Fragment fragment = null;
        if (position == 0)
            fragment = new SearchFragment();
        else if (position == 1)
            fragment = new RecentFragment();
        else if (position == 2)
            fragment = new PastFragment();
        else if (position == 3)
            fragment = new FavoriteFragment();
        else if (position == 4)
            fragment = new SpectacleFragment();
        else if (position == 5)
            fragment = new SettingsFragment();
        else if (position == 6)
            fragment = new AboutFragment();
        else if (position >= 90 && position < 100) {
            FestivalFragment ffragment = new FestivalFragment();
            if (position % 90 == 0)
                ffragment.setFestival("Jarocin Festival", R.drawable.jarocin);
            else if (position % 90 == 1)
                ffragment.setFestival("Life Festival Oświęcim", R.drawable.lifefestival);
            fragment = ffragment;
        } else if (position >= 100) {
            RecentFragment rfragment = new RecentFragment();
            for (Agencies ch : RecentFragment.checkedAgencies.keySet())
                if (ch.fragmentNumber != position)
                    RecentFragment.checkedAgencies.put(ch, false);

            fragment = rfragment;
        }
        return fragment;
    }
}
