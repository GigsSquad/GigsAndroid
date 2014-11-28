package pl.javaparty.concertfinder;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.settings_fragment, container, false);

		getActivity().getActionBar().setTitle("Preferencje");
		
		return view;
	}
}