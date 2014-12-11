package pl.javaparty.concertfinder;

import pl.javaparty.concertmanager.ConcertManager;
import pl.javaparty.sql.dbManager;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ConcertFragment extends Fragment {

	TextView artist, place;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.concert_information_fragment, container, false);
		artist = (TextView) view.findViewById(R.id.artistTextView);
		place = (TextView) view.findViewById(R.id.placeTextView);

		ConcertManager cm = new ConcertManager(new dbManager(getActivity()));
		int ID = (this.getArguments().getInt("ID", -1)); // -1 bo bazadanych numeruje od 1 a nie od 0
		Log.i("KURWA", "Przes³ane id: " + ID);
		getActivity().getActionBar().setTitle(cm.getById(ID).getArtist());
		artist.setText(cm.getById(ID).getArtist());

		place.setText(cm.getList().get(ID).getPlace());

		return view;
	}

}