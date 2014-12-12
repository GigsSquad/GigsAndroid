package pl.javaparty.concertfinder;

import java.util.Arrays;

import pl.javaparty.concertmanager.Concert;
import pl.javaparty.sql.dbManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class RecentFragment extends Fragment{

	ArrayAdapter<String> adapterSearchBox, adapterList, adapterDrawer;
	ConcertAdapter adapter;
	ListView lv;
	Context context;
	dbManager dbm;
	Button nextButton;
	private int lastPosition = 0;
	private int showedConcerts = 20;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.recent_fragment, container, false);
		getActivity().getActionBar().setTitle("Ostatnie koncerty");
		context = inflater.getContext();
		lv = (ListView) view.findViewById(R.id.recentList);
		
		dbm = (dbManager)getArguments().getSerializable("dbManager");
		
		//button na koncu listy ktory rozwija liste o wincyj jesli sie da
		nextButton = new Button(context);
		nextButton.setText("Poka¿ wiêcej");
		nextButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				lastPosition = showedConcerts;
				showedConcerts += 20;
				refresh();
				lv.setSelection(lastPosition-1);
			}
		});
		
		lv.addFooterView(nextButton);
		
		adapter = new ConcertAdapter(getActivity(), R.layout.list_row, cutArray(dbm.getAllConcerts()));
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				lastPosition = position;
				Fragment fragment = new ConcertFragment();
				Bundle args = new Bundle();
				
				Concert item = (Concert) parent.getAdapter().getItem(position);
				args.putInt("ID", item.getID()); // przesylam unikalne id koncertu
				args.putSerializable("dbManager", dbm);

				fragment.setArguments(args);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(getTag()).commit();
			}
		});
		return view;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		lv.setSelection(lastPosition);
	}
	
	private Concert[] cutArray(Concert[] array)
	{
		if(showedConcerts>=dbm.getSize()-1)
		{
			showedConcerts = dbm.getSize()-1;
			nextButton.setVisibility(View.GONE);
		}
		return Arrays.copyOfRange(array, 0, showedConcerts);
	}
	
	public void refresh()
	{
		adapter = new ConcertAdapter(getActivity(), R.layout.list_row, cutArray(dbm.getAllConcerts()));
		lv.setAdapter(adapter);
	}
	
}
