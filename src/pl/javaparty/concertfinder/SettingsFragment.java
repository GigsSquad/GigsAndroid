package pl.javaparty.concertfinder;

import pl.javaparty.prefs.Prefs;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SettingsFragment extends Fragment {

	AutoCompleteTextView citySearchBox;
	SeekBar distanceSeekBar;
	TextView distanceTextView;
	Button saveButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.settings_fragment, container, false);
		getActivity().getActionBar().setTitle("Preferencje");

		citySearchBox = (AutoCompleteTextView) view.findViewById(R.id.cityAutoComplete);
		distanceSeekBar = (SeekBar) view.findViewById(R.id.distanceSeekBar);
		distanceTextView = (TextView) view.findViewById(R.id.distanceTextView);
		saveButton = (Button) view.findViewById(R.id.saveSettingsButton);

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