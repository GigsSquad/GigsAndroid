package pl.javaparty.concertfinder;

import pl.javaparty.concertmanager.ConcertManager;
import pl.javaparty.prefs.Prefs;
import pl.javaparty.sql.dbManager;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends Fragment {

	AutoCompleteTextView citySearchBox;
	SeekBar distanceSeekBar;
	TextView distanceTextView;
	Button saveButton;
	ArrayAdapter<String> adapter;
	ConcertManager cm;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.settings_fragment, container, false);
		getActivity().getActionBar().setTitle("Preferencje");

		citySearchBox = (AutoCompleteTextView) view.findViewById(R.id.cityAutoComplete);
		distanceSeekBar = (SeekBar) view.findViewById(R.id.distanceSeekBar);
		distanceTextView = (TextView) view.findViewById(R.id.distanceTextView);
		saveButton = (Button) view.findViewById(R.id.saveSettingsButton);

		cm = new ConcertManager(new dbManager(getActivity()));

		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, cm.getCities());
		citySearchBox.setAdapter(adapter);
		citySearchBox.setThreshold(1);

		if (Prefs.getCity(getActivity()) != null)
			citySearchBox.setText(Prefs.getCity(getActivity()));

		if (Prefs.getDistance(getActivity()) != 0) {
			distanceTextView.setText(Prefs.getDistance(getActivity()) + "km");
			distanceSeekBar.setProgress(Prefs.getDistance(getActivity()));
		}

		distanceSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				distanceTextView.setText(distanceSeekBar.getProgress() + "km");
			}
		});

		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Prefs.setCity(getActivity(), citySearchBox.getText().toString());
				Prefs.setDistance(getActivity(), distanceSeekBar.getProgress());
				Log.i("SETTINGS", "Zapisano");
				Log.i("SETTINGS", "Miasto: " + citySearchBox.getText().toString());
				Log.i("SETTINGS", "Dystans: " + distanceSeekBar.getProgress());
				Toast.makeText(getActivity(), "Zapisano!", Toast.LENGTH_SHORT).show();
			}
		});

		return view;
	}
}