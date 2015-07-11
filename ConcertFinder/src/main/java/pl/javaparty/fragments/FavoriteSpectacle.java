package pl.javaparty.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import pl.javaparty.adapters.ConcertAdapter;
import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import pl.javaparty.sql.dbManager;

public class FavoriteSpectacle extends Fragment {
    private dbManager dbm;
    private ListView list;
    private ConcertAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        getActivity().getActionBar().setTitle("Spektakle");
        dbm = MainActivity.getDBManager();// przekazujemy dbm od mainActivity

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void refresh() {
        adapter = new ConcertAdapter(getActivity(), dbm.getAllFavouriteConcert());
        list.setAdapter(adapter);
    }

}