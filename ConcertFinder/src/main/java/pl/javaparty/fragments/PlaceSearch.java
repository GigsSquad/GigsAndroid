package pl.javaparty.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import pl.javaparty.adapters.ConcertAdapter;
import pl.javaparty.concertfinder.R;
import pl.javaparty.items.Concert;
import pl.javaparty.prefs.Prefs;
import pl.javaparty.sql.DatabaseManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class PlaceSearch extends Fragment {

    AutoCompleteTextView searchBox;
    ListView concertList;
    ArrayAdapter<String> adapterSearchBox;
    ConcertAdapter adapter;
    Switch switchCon;
    private boolean future = true;
    Context context;
    private String lastSearching = "";
    private int lastPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        View view = inflater.inflate(R.layout.tab_search_place, container, false);
        getActivity().getActionBar().setTitle(getString(R.string.search_by_place));

        context = inflater.getContext();

        searchBox = (AutoCompleteTextView) view.findViewById(R.id.searchBoxPlace);
        concertList = (ListView) view.findViewById(R.id.concertListPlace);
        switchCon = (Switch) view.findViewById(R.id.switchCon2);
        String filter = getArguments().getString("CONDITIONS");
        ArrayList<String> cities = new ArrayList<>(Arrays.asList(DatabaseManager.getInstance(context).getCities(filter)));
        adapterSearchBox = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, cities);

        searchBox.setAdapter(adapterSearchBox);
        searchBox.setThreshold(1);
        // new DownloadTask().execute();

        searchBox.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                hideSoftKeyboard(getActivity(),searchBox);

                String city = searchBox.getText().toString();
                String filter = getArguments().getString("CONDITIONS");
                adapter = new ConcertAdapter(getActivity(), future ?
                        DatabaseManager.getInstance(context).getFutureConcertsByCity(city, filter) : DatabaseManager.getInstance(context).getPastConcertsByCity(city, filter));
                concertList.setAdapter(adapter);

                //wrzucenie szukania do lokalnej
                int usrId = Prefs.getInstance(context).getUserID();
                Calendar c = GregorianCalendar.getInstance();
                int day = c.get(Calendar.DATE);
                int month = c.get(Calendar.MONTH)+1;
                int year = c.get(Calendar.YEAR);
                DatabaseManager.getInstance(context).addSearch(usrId, null, city, day, month, year);

                // zapisywanie danych, coby potem przywrocic
                lastSearching = searchBox.getText().toString();
                getActivity().getActionBar().setTitle(getString(R.string.search) + ": " + searchBox.getText().toString());
                searchBox.setText("");
            }
        });

        concertList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                lastPosition = position;
                Intent concertInfo = new Intent(getActivity().getApplicationContext(), ConcertFragment.class);
                Concert item = (Concert) parent.getAdapter().getItem(position);
                concertInfo.putExtra("ID", item.getID());
                startActivity(concertInfo);
            }
        });

        switchCon.setChecked(true);
        switchCon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                future = isChecked;
                String city = lastSearching;
                String filter = getArguments().getString("CONDITIONS");
                adapter = new ConcertAdapter(getActivity(), future ?
                        DatabaseManager.getInstance(context).getFutureConcertsByCity(city, filter) : DatabaseManager.getInstance(context).getPastConcertsByCity(city, filter));
                concertList.setAdapter(adapter);
                // zapisywanie danych, coby potem przywrocic
                getActivity().getActionBar().setTitle(getString(R.string.search) + ": " + city);
                searchBox.setText("");
                searchBox.setHint(getString(R.string.place) + " " + (future ? "(przyszłe koncerty)" : "(minione koncerty)"));
                try {
                    if (adapter.getCount() == 0 && city.length() > 0) {
                        Toast.makeText(getActivity(), (future ? switchCon.getTextOn() : switchCon.getTextOff()) + " " + getString(R.string.concerts_unavailable_for) + " " + city, Toast.LENGTH_LONG).show();
                    }
                } catch (NullPointerException npe) {
                    Toast.makeText(getActivity(), (future ? switchCon.getTextOn() : switchCon.getTextOff()) + " " + getString(R.string.concerts_unavailable_for), Toast.LENGTH_LONG).show();
                }

            }
        });

        return view;
    }
    private static void hideSoftKeyboard(Context mContext,EditText username){  // dziala, kod ze stacka
        if(((Activity) mContext).getCurrentFocus()!=null && ((Activity) mContext).getCurrentFocus() instanceof EditText){
            InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(username.getWindowToken(), 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            concertList.setAdapter(adapter);
            concertList.setSelection(lastPosition);
        }
        if (lastSearching != null)
            getActivity().getActionBar().setTitle(getString(R.string.search) + ": " + lastSearching);
    }

    public void refresh() {
//		String filter = getArguments().getString("CONDITIONS");
//		adapterSearchBox = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, DatabaseManager.getInstance(context).getCities(filter));
//
//		searchBox.setAdapter(adapterSearchBox);

        String filter = getArguments().getString("CONDITIONS");
        //adapterSearchBox = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, DatabaseManager.getInstance(context).getCities(filter));
        if (adapter != null) {
            adapter.changeData(DatabaseManager.getInstance(context).getConcertsByCity(lastSearching, filter));
        }

        adapterSearchBox.clear();
        adapterSearchBox.addAll(DatabaseManager.getInstance(context).getCities(filter));
        adapterSearchBox.notifyDataSetChanged();
    }
}
