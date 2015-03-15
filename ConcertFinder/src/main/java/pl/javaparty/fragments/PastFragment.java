package pl.javaparty.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import pl.javaparty.adapters.ConcertAdapter;
import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import pl.javaparty.fragments.FilterDialogFragment.FilterDialogListener;
import pl.javaparty.items.Agencies;
import pl.javaparty.items.Concert;
import pl.javaparty.sql.dbManager;

import java.util.Arrays;
import java.util.Map;

public class PastFragment extends Fragment {

    ConcertAdapter adapter;
    ListView lv;
    Context context;
    dbManager dbm;
    Button nextButton;
    private int lastPosition = 0;
    private int showedConcerts = 20;
    public Map<Agencies, Boolean> checkedAgencies;

    public PastFragment()
    {
        super();

        checkedAgencies = Agencies.AgenciesMethods.initialize();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        getActivity().getActionBar().setTitle("Minione koncerty");
        context = inflater.getContext();
        lv = (ListView) view.findViewById(R.id.recentList);
        dbm = MainActivity.getDBManager();

        setHasOptionsMenu(true);

        // button na koncu listy ktory rozwija liste o wincyj jesli sie da
        nextButton = new Button(context);
        nextButton.setText("Pokaż więcej");
        nextButton.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                lastPosition = showedConcerts;
                showedConcerts += 20;
                refresh();
                lv.setSelection(lastPosition - 1);
            }
        });

        lv.addFooterView(nextButton);

        adapter = new ConcertAdapter(getActivity(), cutArray(dbm.getPastConcerts(Agencies.AgenciesMethods.filterAgencies(checkedAgencies))));
        lv.setAdapter(adapter);
        lv.setEmptyView(view.findViewById(R.id.emptyList));

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastPosition = position;

                Intent concertInfo = new Intent(context, ConcertFragment.class);
                Concert item = (Concert) parent.getAdapter().getItem(position);
                concertInfo.putExtra("ID", item.getID());
                startActivity(concertInfo);
            }
        });
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        refresh();
        lv.setSelection(lastPosition);
    }



    private Concert[] cutArray(Concert[] array)
    {
        if (array != null &&  array.length != 0)
        {
            Log.i("EMPTYLIST", String.valueOf(array.length));
            if (showedConcerts >= dbm.getSize(dbManager.CONCERTS_TABLE) - 1)
            {
                showedConcerts = dbm.getSize(dbManager.CONCERTS_TABLE) - 1;
                nextButton.setVisibility(View.GONE);
                return array;
            }
            else
                return Arrays.copyOfRange(array, 0, showedConcerts);
        }
        return new Concert[0];
    }

    public void refresh()
    {
        //adapter = new ConcertAdapter(getActivity(), cutArray(dbm.getAllConcerts(filterAgencies())));
        adapter.changeData(cutArray(dbm.getPastConcerts(Agencies.AgenciesMethods.filterAgencies(checkedAgencies))));
        //lv.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.search_fragment_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.filter_icon:
                FilterDialogFragment dialog = new FilterDialogFragment();
                dialog.setFilterDialogListener(new FilterDialogListener()
                {
                    @Override
                    public void onDialogPositiveClick(boolean[] checked)
                    {
                        // this.checked = checked;
                        int i = 0;
                        for (Agencies c : checkedAgencies.keySet())
                        {
                            checkedAgencies.put(c, checked[i++]);
                        }

                        refresh();

                    }

                    @Override
                    public void onDialogNegativeClick(boolean[] checked)
                    {
                        // nevermind
                    }
                });
                Bundle args = new Bundle();

                boolean[] checked = new boolean[checkedAgencies.size()];
                int i = 0;
                for (Boolean b : checkedAgencies.values())
                {
                    checked[i++] = b;
                }
                args.putBooleanArray("CHECKED", checked);
                dialog.setArguments(args);

                dialog.show(getActivity().getFragmentManager(), "FILTER");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
