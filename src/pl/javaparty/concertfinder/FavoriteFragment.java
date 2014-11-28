package pl.javaparty.concertfinder;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FavoriteFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.favorite_fragment, container, false);

		getActivity().getActionBar().setTitle("Twoje koncerty");

		return view;
	}
}