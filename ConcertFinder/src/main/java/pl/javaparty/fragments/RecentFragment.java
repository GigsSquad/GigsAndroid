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
import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import pl.javaparty.fragments.FilterDialogFragment.FilterDialogListener;
import pl.javaparty.items.Concert;
import pl.javaparty.items.Concert.AgencyName;
import pl.javaparty.sql.dbManager;

import java.util.*;

public class RecentFragment extends Fragment {

    ConcertAdapter concertsAdapter;
    ListView concertsListView;
    Context context;
    dbManager dbm;
    Button nextButton;
    private int lastPosition = 0;
    private int showedConcerts = 20;
    public Map<CharSequence, Boolean> checkedAgencies;

    public RecentFragment() {
        super();

        checkedAgencies = new TreeMap<>();
        AgencyName[] vals = AgencyName.values();
        for (AgencyName val : vals) {
            checkedAgencies.put(val.name(), true);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        getActivity().getActionBar().setTitle("Najbliższe koncerty");
        context = inflater.getContext();
        concertsListView = (ListView) view.findViewById(R.id.recentList);
        dbm = MainActivity.getDBManager();


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        setHasOptionsMenu(true);

        // button na koncu listy ktory rozwija liste o wincyj jesli sie da
        nextButton = new Button(context);
        nextButton.setText("Pokaż więcej");
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
        Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
        int currentDay = localCalendar.get(Calendar.DATE);
        int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
        int currentYear = localCalendar.get(Calendar.YEAR);


        concertsAdapter = new ConcertAdapter(getActivity(), cutArray(dbm.getConcertsByDateRange(currentDay, currentMonth, currentYear, 33, 13, 2050, filterAgencies())));
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
        //fc4355eb184e82380296c170cc0bd2dc664fc195 cut array
        if (array != null && array.length != 0) {
            Log.i("EMPTYLIST", String.valueOf(array.length));
            if (showedConcerts >= dbm.getSize(dbManager.CONCERTS_TABLE) - 1) {
                showedConcerts = dbm.getSize(dbManager.CONCERTS_TABLE) - 1;
                nextButton.setVisibility(View.GONE);
                return array;
            } else
                return Arrays.copyOfRange(array, 0, showedConcerts);
        }
        return new Concert[0];
    }

    private double CalculateDistance(double homeLat, double homeLon, String lat, String lon) {
        double a = Math.abs(Double.parseDouble(lat) - homeLat);
        double b = Math.abs(Double.parseDouble(lon) - homeLon);

        return (Math.sqrt((a * a) + (b * b)));
    }

    public void refresh() {
        Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
        int currentDay = localCalendar.get(Calendar.DATE);
        int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
        int currentYear = localCalendar.get(Calendar.YEAR);
        concertsAdapter = new ConcertAdapter(getActivity(), cutArray(dbm.getAllConcerts(filterAgencies())));
        concertsAdapter.changeData(cutArray(dbm.getFutureConcerts(filterAgencies())));
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
                dialog.setFilterDialogListener(new FilterDialogListener() {
                    @Override
                    public void onDialogPositiveClick(boolean[] checked) {
                        // this.checked = checked;
                        int i = 0;
                        for (CharSequence c : checkedAgencies.keySet()) {
                            checkedAgencies.put(c, checked[i++]);
                        }

                        refresh();

                    }

                    @Override
                    public void onDialogNegativeClick(boolean[] checked) {
                        // nevermind
                    }
                });
                Bundle args = new Bundle();

                boolean[] checked = new boolean[checkedAgencies.size()];
                int i = 0;
                for (Boolean b : checkedAgencies.values()) {
                    checked[i++] = b;
                }
                args.putBooleanArray("CHECKED", checked);

                CharSequence[] agencies = new CharSequence[checkedAgencies.size()];
                checkedAgencies.keySet().toArray(agencies);

                args.putCharSequenceArray("AGENCIES", agencies);
                dialog.setArguments(args);

                dialog.show(getActivity().getFragmentManager(), "FILTER");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String filterAgencies() {
        String returned = "'1'='0'";
        for (CharSequence c : checkedAgencies.keySet()) {
            if (checkedAgencies.get(c)) {
                returned += " OR AGENCY = '" + c + "'";
            }
        }
        return returned;

    }
}
