package pl.javaparty.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import pl.javaparty.adapters.ConcertAdapter;
import pl.javaparty.concertfinder.R;
import pl.javaparty.items.Concert;
import pl.javaparty.prefs.PrefsSingleton;
import pl.javaparty.sql.dbManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ArtistSearch extends Fragment {

    AutoCompleteTextView searchBox;
    Switch switchCon;
    ListView concertList;
    ArrayAdapter<String> adapterSearchBox;
    ConcertAdapter adapter;
    Context context;
    private String lastSearching = "";
    private int lastPosition;
    private boolean future = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        View view = inflater.inflate(R.layout.tab_search_artist, container, false);
        getActivity().getActionBar().setTitle(getString(R.string.search_by_arist));
        context = inflater.getContext();

        searchBox = (AutoCompleteTextView) view.findViewById(R.id.searchBoxArtist);
        switchCon = (Switch) view.findViewById(R.id.switchCon);
        concertList = (ListView) view.findViewById(R.id.concertListArtist);

        String filter = getArguments().getString("CONDITIONS");
        Log.i("FILTRUJE", filter);
        ArrayList<String> artists = new ArrayList<>(Arrays.asList(dbManager.getInstance(context).getArtists(filter)));
        adapterSearchBox = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, dbManager.getInstance(context).getFutureArtists(filter));

        searchBox.setAdapter(adapterSearchBox);
        searchBox.setThreshold(1);

        searchBox.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                //chowanie sie klawiatury po kliknieciu
                hideSoftKeyboard(getActivity(), searchBox);

                String artist = searchBox.getText().toString();
                String filter = getArguments().getString("CONDITIONS");
                adapter = new ConcertAdapter(getActivity(), future ?
                        dbManager.getInstance(context).getFutureConcertsByArtist(artist, filter) : dbManager.getInstance(context).getPastConcertsByArtist(artist, filter));
                adapterSearchBox = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line,
                        future ? dbManager.getInstance(context).getFutureArtists(filter) : dbManager.getInstance(context).getPastArtists(filter));
                searchBox.setAdapter(adapterSearchBox);
                searchBox.setThreshold(1);
                concertList.setAdapter(adapter);

                //wrzucenie szukania do lokalnej
                int usrId = PrefsSingleton.getInstance().getUserID(context);
                Calendar c = GregorianCalendar.getInstance();
                int day = c.get(Calendar.DATE);
                int month = c.get(Calendar.MONTH) + 1;
                int year = c.get(Calendar.YEAR);
                dbManager.getInstance(context).addSearch(usrId, artist, null, day, month, year);

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
                String artist = lastSearching;
                String filter = getArguments().getString("CONDITIONS");
                adapter = new ConcertAdapter(getActivity(), future ?
                        dbManager.getInstance(context).getFutureConcertsByArtist(artist, filter) : dbManager.getInstance(context).getPastConcertsByArtist(artist, filter));
                adapterSearchBox = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line,
                        future ? dbManager.getInstance(context).getFutureArtists(filter) : dbManager.getInstance(context).getPastArtists(filter));
                searchBox.setAdapter(adapterSearchBox);
                searchBox.setThreshold(1);
                concertList.setAdapter(adapter);
                // zapisywanie danych, coby potem przywrocic
                getActivity().getActionBar().setTitle(getString(R.string.search) + ": " + artist);
                searchBox.setText("");

                searchBox.setHint(getString(R.string.artist) + " " + (future ? "(przyszłe koncerty)" : "(minione koncerty)"));

                try {
                    if (adapter.getCount() == 0 && artist.length() > 0) {
                        Toast.makeText(getActivity(), (future ? switchCon.getTextOn() : switchCon.getTextOff()) + " " + getString(R.string.concerts_unavailable_for) + " " + artist, Toast.LENGTH_LONG).show();
                    }
                } catch (NullPointerException npe) {
                    Toast.makeText(getActivity(), (future ? switchCon.getTextOn() : switchCon.getTextOff()) + " " + getString(R.string.concerts_unavailable_for), Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    private static void hideSoftKeyboard(Context mContext, EditText username) {  // dziala, kod ze stacka
        if (((Activity) mContext).getCurrentFocus() != null && ((Activity) mContext).getCurrentFocus() instanceof EditText) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
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
            getActivity().getActionBar().setTitle("Szukaj: " + lastSearching);
    }

    public void refresh() {
        String filter = getArguments().getString("CONDITIONS");
        //adapterSearchBox = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, dbManager.getInstance(context).getArtists(filter));
        if (adapter != null) {
            adapter.changeData(dbManager.getInstance(context).getConcertsByArtist(lastSearching, filter));
        }

        adapterSearchBox.clear();
        adapterSearchBox.addAll(dbManager.getInstance(context).getArtists(filter));
        adapterSearchBox.notifyDataSetChanged();
        //searchBox.setAdapter(adapterSearchBox);
    }
}