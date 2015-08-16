package pl.javaparty.fragments;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import pl.javaparty.concertfinder.R;

/**
 * Created by Szymon on 2015-03-16.
 */
public class FestivalFragment extends Fragment {
    ImageView festivalImage;
    ActionBar actionbar;
    String title = "Festival";
    int imgID = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.festival_fragment, container, false);
        festivalImage = (ImageView) view.findViewById(R.id.festivalImage);

        getActivity().getActionBar().setTitle(title);
        if (imgID != 0)
            festivalImage.setImageResource(imgID);

        return view;
    }

    public void setFestival(String nameOfFestival, int imageID) {
        title = nameOfFestival;
        imgID = imageID;
    }
}
