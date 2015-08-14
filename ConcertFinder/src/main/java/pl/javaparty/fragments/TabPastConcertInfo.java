package pl.javaparty.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.*;
import pl.javaparty.concertfinder.MainActivity;
import pl.javaparty.concertfinder.R;
import pl.javaparty.imageloader.ImageLoader;
import pl.javaparty.items.Concert;
import pl.javaparty.jsoup.SetList;
import pl.javaparty.sql.DatabaseManager;

import java.util.ArrayList;

public class TabPastConcertInfo extends Fragment {

    private ImageView image;
    private int ID;
    Context context;
    private ListView lv;
    private TextView tv, artist, placeTV, dateTV, loadTV;
    private ProgressBar pbar;
    private String artistName, city, place;
    int d, m, y;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        View view = inflater.inflate(R.layout.tab_fragment_past_concert_info, container, false);
        image = (ImageView) view.findViewById(R.id.artist_image_past);
        artist = (TextView) view.findViewById(R.id.artist_past);
        placeTV = (TextView) view.findViewById(R.id.place_past);
        dateTV = (TextView) view.findViewById(R.id.date_past);
        tv = (TextView) view.findViewById(R.id.textView3);
        lv = (ListView) view.findViewById(R.id.songs);
        pbar = (ProgressBar) view.findViewById(R.id.setlist_progress);
        loadTV = (TextView) view.findViewById(R.id.loading_text);
        context = inflater.getContext();
        setHasOptionsMenu(true);

        ID = (getArguments().getInt("ID", -1)); // -1 bo bazadanych numeruje od 1 a nie od 0

        Concert con = DatabaseManager.getInstance(context).getConcertByID(ID);
        artistName = con.getArtist();
        city = con.getCity();
        place = con.getPlace();
        int[] date = DatabaseManager.getInstance(context).dateArray(ID);
        d = date[0];
        m = date[1];
        y = date[2];

        getActivity().getActionBar().setTitle(artistName);
        lv.setVisibility(View.INVISIBLE);
        tv.setVisibility(View.INVISIBLE);

        ImageLoader.init(inflater.getContext()).DisplayImage(artistName, image);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetSetlist().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        artist.setText(artistName);
        placeTV.setText(place);
        dateTV.setText(con.dateToString());

        return view;
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

                MainActivity.updateCounters();
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

    public void onViewCreated(View view, Bundle savedInstanceState) {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String url = SetList.getYT(artistName, (String) lv.getAdapter().getItem(position));
                    Log.i("ONVIEW", "Przeszło");
                    if (url != null) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    } else
                        Toast.makeText(getActivity(), getString(R.string.not_found), Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    private class GetSetlist extends AsyncTask<Void, Void, Void> {

        ArrayAdapter<String> adapter;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.i("ASYNC", "back");
                ArrayList<String> setlist = SetList.getSetlist(artistName, city, d, m, y);
                Log.i("ASYNC", "songs: " + setlist.size());
                adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, setlist) {

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView textView = (TextView) view.findViewById(android.R.id.text1);
                        textView.setTextColor(Color.BLUE);
                        return view;
                    }
                };
                Log.i("ASYNC", "back_done");
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (adapter != null) {
                lv.setAdapter(adapter);

                lv.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), getString(R.string.setlist_hint), Toast.LENGTH_LONG);
            } else
                tv.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.INVISIBLE);
            loadTV.setVisibility(View.INVISIBLE);
        }
    }
}

