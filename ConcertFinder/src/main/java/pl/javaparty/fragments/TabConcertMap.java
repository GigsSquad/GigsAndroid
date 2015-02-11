package pl.javaparty.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import pl.javaparty.items.Concert;
import pl.javaparty.jsoup.SetList;
import pl.javaparty.sql.dbManager;


import java.io.IOException;
import java.util.ArrayList;

public class TabConcertMap extends Fragment {

	private ImageView image;
	private static dbManager dbm;
	private int ID;
    private ListView lv;
    private ArrayAdapter<String> adapter;
    private TextView tv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
		View view = inflater.inflate(R.layout.tab_fragment_concert_map, container, false);
		image = (ImageView) view.findViewById(R.id.concert_map);
		dbm = MainActivity.getDBManager();

		setHasOptionsMenu(true);

		ID = (getArguments().getInt("ID", -1)); // -1 bo bazadanych numeruje od 1 a nie od 0
		dbm = MainActivity.getDBManager();

        Concert con = dbm.getConcertByID(ID);
        final String artist = con.getArtist();
        String city = con.getCity();
        int[] date = con.getDayMonthYear();
        int d = date[0], m = date[1], y = date[2];

        tv = (TextView) view.findViewById(R.id.textView3);
        lv = (ListView) view.findViewById(R.id.songs);

        ArrayList<String> setlista;
        try{
            setlista = SetList.getSetlist(artist,city,d,m,y);
        } catch (IOException e){
            setlista = null;
        }
        if(setlista!=null) {
            tv.setText("Setlist (click on song to get to YT)");
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, setlista);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                          @Override
                                          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                              String song = (String) parent.getAdapter().getItem(position);
                                              String videoUrl = null;
                                              try {
                                                  videoUrl = SetList.getYT(artist, song);
                                              } catch (IOException e) {
                                              }

                                              if (videoUrl != null) {
                                                  Intent openYT = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                                                  startActivity(openYT);
                                              } else
                                                  Toast.makeText(getActivity(), "Video unavailable", Toast.LENGTH_SHORT);
                                          }
                                      }
            );
        }
        return view;
    }


    //TODO tu będziemy pobierac mapkę koncertu

    private ImageView downloadConcertMap() {
		return null;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.concert_info_menu, menu);
		if (dbm.isConcertFavourite(ID))
			menu.getItem(0).setIcon(R.drawable.ic_action_important_w);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.favorite_icon:
			if (dbm.isConcertFavourite(ID))// wyjebujemy
			{
				dbm.removeFavouriteConcert(ID);
				item.setIcon(R.drawable.ic_action_not_important_w);
			} else {
				dbm.addFavouriteConcert(ID);
				item.setIcon(R.drawable.ic_action_important_w);
			}

			MainActivity.updateCounters();
			return true;
		case R.id.website_icon:
			Intent websiteIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(dbm.getUrl(ID)));
			startActivity(websiteIntent);
			return true;

		case R.id.share:
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, dbm.getArtist(ID) + ", " + dbm.getCity(ID) + " (" + dbm.getDate(ID) + ")");
			sendIntent.setType("text/plain");
			startActivity(sendIntent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
