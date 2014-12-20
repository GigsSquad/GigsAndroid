package pl.javaparty.fragments;

import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import pl.javaparty.imageloader.FileExplorer;
import pl.javaparty.prefs.Prefs;
import pl.javaparty.sql.dbManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends Fragment {

	AutoCompleteTextView citySearchBox;
	SeekBar distanceSeekBar;
	TextView distanceTextView;
	Button saveButton, clearButton;
	Context context;
	ArrayAdapter<String> adapter;
	dbManager dbm;
	Spinner countySpinner;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.fragment_settings, container, false);
		context = inflater.getContext();
		getActivity().getActionBar().setTitle("Preferencje");
		dbm = ((MainActivity)getActivity()).getDBManager();//przekazujemy dbm od mainActivity
		citySearchBox = (AutoCompleteTextView) view.findViewById(R.id.cityAutoComplete);
		distanceSeekBar = (SeekBar) view.findViewById(R.id.distanceSeekBar);
		distanceTextView = (TextView) view.findViewById(R.id.distanceTextView);
		saveButton = (Button) view.findViewById(R.id.saveSettingsButton);
		clearButton = (Button) view.findViewById(R.id.clearFilesButton);
		countySpinner = (Spinner) view.findViewById(R.id.countySpinner);
		
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, getCountiesNames());
		countySpinner.setAdapter(adapter);
		
		countySpinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id)
			{
				
				String county = (String)parent.getItemAtPosition(position);
				
				ArrayAdapter<CharSequence> adapt = ArrayAdapter.createFromResource(getActivity(), getCounty(county).ID, android.R.layout.simple_dropdown_item_1line);
				citySearchBox.setAdapter(adapt);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
				// TODO Auto-generated method stub
				
			}
		});
		
		//adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, dbm.getCities());
		
		//citySearchBox.setAdapter(adapter);
		citySearchBox.setThreshold(1);

		if (Prefs.getCity(getActivity()) != null)
			citySearchBox.setText(Prefs.getCity(getActivity()));
		
		if(Prefs.getCounty(getActivity()) != null)
		{
			int positionOfCounty = -1;
			for(int i = 0; i<countySpinner.getCount() && positionOfCounty == -1; i++)
			{
				if(Prefs.getCounty(getActivity()).equals((String)countySpinner.getItemAtPosition(i)))
					positionOfCounty = i;
			}
			countySpinner.setSelection(positionOfCounty);
		}

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
				Prefs.setCity(getActivity(), citySearchBox.getText().toString(), countySpinner.getSelectedItem().toString());
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
	
	private String[] getCountiesNames()
	{
		Counties[] counties = Counties.values();
		String[] names = new String[counties.length];
		int i = 0;
		for(Counties c: counties)
		{
			names[i++] = c.name;
		}
		return names;
	}
	
	private Counties getCounty(String name)
	{
		Counties[] counties = Counties.values();
		for(Counties c: counties)
		{
			if(c.name.equals(name))
				return c; //niestrukturalnie!!!11ONE :(
		}
		return null;
	}
	
	private enum Counties
	{
		DS("dolnoœl¹skie",R.array.DS),
		KP("kujawsko-pomorskie",R.array.KP),
		LB("lubelskie",R.array.LB),
		LS("lubuskie",R.array.LS),
		LD("³ódzkie",R.array.LD),
		MP("ma³opolskie",R.array.MP),
		MZ("mazowieckie",R.array.MZ),
		OP("opolskie",R.array.OP),
		PK("podkarpackie",R.array.PK),
		PL("podlaskie",R.array.PL),
		PM("pomorskie",R.array.PM),
		SL("œl¹skie",R.array.SL),
		SK("œwiêtokrzyskie",R.array.SK),
		WM("warmiñsko-mazurskie",R.array.WM),
		WP("wielkopolskie",R.array.WP),
		ZP("zachodniopomorskie",R.array.ZP);
		
		String name;
		int ID;
		
		Counties(String name, int ID)
		{
			this.name = name;
			this.ID = ID;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}