package pl.javaparty.fragments;

import pl.javaparty.concertfinder.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.fragment_about, container, false);

		getActivity().getActionBar().setTitle("Informacje");

		return view;
	}
}