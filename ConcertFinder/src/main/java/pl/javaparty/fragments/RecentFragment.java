package pl.javaparty.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import pl.javaparty.adapters.ConcertAdapter;
import pl.javaparty.concertfinder.ConcertDownloader;
import pl.javaparty.concertfinder.Observer;
import pl.javaparty.concertfinder.R;
import pl.javaparty.fragments.FilterDialogFragment.FilterDialogListener;
import pl.javaparty.items.Agencies;
import pl.javaparty.items.Concert;
import pl.javaparty.sql.DatabaseManager;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

public class RecentFragment extends Fragment implements Observer {

    ConcertAdapter concertsAdapter;
    ListView concertsListView;
    Context context;
    Button nextButton;
    private int lastPosition = 0;
    private int showedConcerts = 20;
    public static Map<Agencies, Boolean> checkedAgencies;
    ConcertDownloader concertDownloader;

    public RecentFragment() {
        super();

        checkedAgencies = Agencies.AgenciesMethods.initialize();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        getActivity().getActionBar().setTitle(getString(R.string.upcoming_concerts));
        context = inflater.getContext();
        concertsListView = (ListView) view.findViewById(R.id.recentList);
        concertDownloader = new ConcertDownloader(context);
        concertDownloader.register(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setHasOptionsMenu(true);

        // button na koncu listy ktory rozwija liste o wincyj jesli sie da
        nextButton = new Button(context);
        nextButton.setText(getString(R.string.show_moar));
        nextButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                lastPosition = showedConcerts;
                showedConcerts += 20;
                refresh();
                concertsListView.setSelection(lastPosition - 1);
            }
        });

        concertsListView.addFooterView(nextButton);
        concertsAdapter = new ConcertAdapter(getActivity(), cutArray(DatabaseManager.getInstance(context).getFutureConcerts(filterAgencies())));

        concertsListView.setAdapter(concertsAdapter);
        concertsListView.setEmptyView(view.findViewById(R.id.emptyList));

        concertsListView.setOnItemClickListener(new OnItemClickListener() {
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
    public void onResume() {
        super.onResume();
        refresh();
        concertsListView.setSelection(lastPosition);
    }

    private Concert[] cutArray(Concert[] array) {
        if (array != null && array.length != 0) {
            Log.i("EMPTYLIST", String.valueOf(array.length));
            if (showedConcerts >= array.length - 1) //DatabaseManager.getInstance(context).getSize(DatabaseManager.CONCERTS_TABLE) - 1) {
            {
                showedConcerts = array.length - 1;
                //showedConcerts = DatabaseManager.getInstance(context).getSize(DatabaseManager.CONCERTS_TABLE) - 1;
                nextButton.setVisibility(View.GONE);
                return array;
            } else
                return Arrays.copyOfRange(array, 0, showedConcerts);
        }
        return new Concert[0];
    }

    @Override
    public void refresh() {
        Log.i("RF", "Update danych");
        concertsAdapter.changeData(cutArray(DatabaseManager.getInstance(context).getFutureConcerts(filterAgencies())));
//        concertsAdapter = new ConcertAdapter(getActivity(), cutArray(DatabaseManager.getInstance(context).getFutureConcerts(filterAgencies())));
        concertsListView.setAdapter(concertsAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_fragment_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_icon:
                FilterDialogFragment dialog = new FilterDialogFragment();

                Bundle args = new Bundle();

                boolean[] checked = new boolean[checkedAgencies.size()];
                int i = 0;
                for (Boolean b : checkedAgencies.values()) {
                    checked[i++] = b;
                }
                args.putBooleanArray("CHECKED", checked);
                dialog.setArguments(args);

                dialog.setFilterDialogListener(new FilterDialogListener() {
                    @Override
                    public void onDialogPositiveClick(boolean[] checked) {
                        // this.checked = checked;
                        int i = 0;
                        for (Agencies c : checkedAgencies.keySet()) {

                            checkedAgencies.put(c, checked[i++]);
                        }
                        lastPosition = 0;
                        refresh();

                    }

                    @Override
                    public void onDialogNegativeClick(boolean[] checked) {
                        // nevermind
                    }
                });

                dialog.show(getActivity().getFragmentManager(), "FILTER");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static String filterAgencies() {
        String returned = "'1'='0'";
        for (Agencies c : checkedAgencies.keySet()) {
            if (checkedAgencies.get(c)) {
                returned += " OR AGENCY = '" + c.name() + "'";
            }
        }
        return returned;

    }
}
