package pl.javaparty.concertfinder;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ConcertFragment extends Fragment {

	TextView artist, place;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.concert_information_fragment, container, false);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		
		artist = (TextView) view.findViewById(R.id.artistTextView);
		place = (TextView) view.findViewById(R.id.placeTextView);
		
		
		

		return view;
	}
	
	
}