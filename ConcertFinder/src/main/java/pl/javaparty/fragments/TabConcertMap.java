package pl.javaparty.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import pl.javaparty.items.Concert;
import pl.javaparty.sql.DatabaseManager;

public class TabConcertMap extends Fragment {

    private ImageView image;
    private int ID;
    private ListView lv;
    private ArrayAdapter<String> adapter;
    private TextView tv;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        View view = inflater.inflate(R.layout.tab_fragment_concert_map, container, false);
        image = (ImageView) view.findViewById(R.id.concert_map);
        context = inflater.getContext();
        setHasOptionsMenu(true);

        ID = (getArguments().getInt("ID", -1)); // -1 bo bazadanych numeruje od 1 a nie od 0

        Concert con = DatabaseManager.getInstance(context).getConcertByID(ID);
        final String artist = con.getArtist();
        String city = con.getCity();

        return view;
    }

    //TODO tu będziemy pobierac mapkę koncertu
    private ImageView downloadConcertMap() {
        return null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.concert_info_menu, menu);
        if (DatabaseManager.getInstance(context).isConcertFavourite(ID))
            menu.getItem(0).setIcon(R.drawable.ic_action_important_w);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorite_icon:
                if (DatabaseManager.getInstance(context).isConcertFavourite(ID))// wyjebujemy
                {
                    DatabaseManager.getInstance(context).removeFavouriteConcert(ID);
                    item.setIcon(R.drawable.ic_action_not_important_w);
                } else {
                    DatabaseManager.getInstance(context).addFavouriteConcert(ID);
                    item.setIcon(R.drawable.ic_action_important_w);
                }

                return true;
            case R.id.website_icon:
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(DatabaseManager.getInstance(context).getUrl(ID)));
                startActivity(websiteIntent);
                return true;

            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, DatabaseManager.getInstance(context).getArtist(ID) + ", " + DatabaseManager.getInstance(context).getCity(ID) + " (" + DatabaseManager.getInstance(context).getDate(ID) + ")");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
