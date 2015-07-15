package pl.javaparty.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pl.javaparty.concertfinder.R;

public class SpectacleFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        View view = inflater.inflate(R.layout.fragment_spectacle, container, false);
        getActivity().getActionBar().setTitle("Spektakle");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}