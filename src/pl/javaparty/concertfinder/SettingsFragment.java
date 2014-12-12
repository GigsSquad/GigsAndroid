package pl.javaparty.concertfinder;

import pl.javaparty.imageloader.FileExplorer;
import pl.javaparty.prefs.Prefs;
import pl.javaparty.sql.dbManager;
import android.app.Fragment;
import android.content.Context;
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

public class SettingsFragment extends Fragment{

	AutoCompleteTextView citySearchBox;
	SeekBar distanceSeekBar;
	TextView distanceTextView;
	Button saveButton, clearButton;
	Context context;
	ArrayAdapter<String> adapter;
	dbManager dbm;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.settings_fragment, container, false);
		context = inflater.getContext();
		getActivity().getActionBar().setTitle("Preferencje");
		dbm = (dbManager) getArguments().getSerializable("dbManager");//przekazujemy dbm od mainActivity
		citySearchBox = (AutoCompleteTextView) view.findViewById(R.id.cityAutoComplete);
		distanceSeekBar = (SeekBar) view.findViewById(R.id.distanceSeekBar);
		distanceTextView = (TextView) view.findViewById(R.id.distanceTextView);
		saveButton = (Button) view.findViewById(R.id.saveSettingsButton);
		clearButton = (Button) view.findViewById(R.id.clearFilesButton);

		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, dbm.getCities());
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
		
		clearButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				FileExplorer f = new FileExplorer(context);	
				f.clear();
				Log.i("SETTINGS", "Usunieto obrazki z dysku");
				Toast.makeText(getActivity(), "Wyczyszczono pamiec!", Toast.LENGTH_SHORT).show();
			}
		});

		return view;
	}
}