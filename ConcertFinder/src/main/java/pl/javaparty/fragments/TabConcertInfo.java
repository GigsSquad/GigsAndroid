package pl.javaparty.fragments;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import pl.javaparty.concertfinder.R;
import pl.javaparty.imageloader.ImageLoader;
import pl.javaparty.items.Concert;
import pl.javaparty.jsoup.TicketPrices;
import pl.javaparty.map.MapHelper;
import pl.javaparty.prefs.Prefs;
import pl.javaparty.sql.DatabaseManager;
import pl.javaparty.utils.UtilsObject;

public class TabConcertInfo extends Fragment {

    TextView artist, place, date, addCalendar, howlong, distance,
            ticketsDetails1, ticketsDetails2, ticketsDetails3;
    ImageView image;
    MapHelper mapHelper;
    Context context;
    int ID;
    Concert currentConcert;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {
        View view = inflater.inflate(R.layout.tab_fragment_concert_info, container, false);
        artist = (TextView) view.findViewById(R.id.artist_name);
        place = (TextView) view.findViewById(R.id.place_name);
        distance = (TextView) view.findViewById(R.id.distanceTV);
        date = (TextView) view.findViewById(R.id.con_date);
        howlong = (TextView) view.findViewById(R.id.howlongTV);
        addCalendar = (TextView) view.findViewById(R.id.add_to_calendar_btn);
        image = (ImageView) view.findViewById(R.id.artist_image);
        ticketsDetails1 = (TextView) view.findViewById(R.id.ticketsDetails1);
        ticketsDetails2 = (TextView) view.findViewById(R.id.ticketsDetails2);
        ticketsDetails3 = (TextView) view.findViewById(R.id.ticketsDetails3);
        context = inflater.getContext();
        mapHelper = new MapHelper(context);
        setHasOptionsMenu(true);

        ID = (getArguments().getInt("ID", -1));
        currentConcert = DatabaseManager.getInstance(getActivity().getApplicationContext()).getConcertByID(ID);

        String artistName = currentConcert.getArtist();
        getActivity().getActionBar().setTitle(artistName);

        ImageLoader.init(inflater.getContext()).DisplayImage(artistName, image);
        artistName = artistName.replace(" - ", "\n");
        artistName = artistName.replace(": ", ":\n");

        artist.setText(artistName);
        place.setText(currentConcert.getCity() + " " + currentConcert.getSpot());
        date.setText(currentConcert.dateToString());

        String hometownFromPrefs = Prefs.getInstance(context).getCity();
        String distanceInKm = String.valueOf((int) mapHelper.distanceFromHometown(currentConcert.getLatLng()));

        distance.setText(distanceInKm + "km " + getString(R.string.distance_to) + " " + hometownFromPrefs);

        int days = currentConcert.daysTo();
        if (days < 0) {
            howlong.setVisibility(View.GONE);
        } else if (days == 0) {
            howlong.setText(getString(R.string.today));
        } else if (days == 1) {
            howlong.setText(getString(R.string.tomorow));
        } else {
            howlong.setText(getString(R.string.remaining) + " " + days + " " + getString(R.string.days));
        }

        addCalendar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(Events.TITLE, artist.getText());
                intent.putExtra(Events.EVENT_LOCATION, place.getText());
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, currentConcert.getCalendar().getTimeInMillis());
                intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
                // intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                // DatabaseManager.getInstance(context).getConcertByID(ID).getCalendar().getTimeInMillis() + ilość_godzin*(60 * 60 * 1000));
                // raczej niepotrzebne, ale można ustawić jak coś ;)
                startActivity(intent);
            }
        });

        //Pobieranie cen biletów
        if (UtilsObject.isOnline(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new TicketPrices(currentConcert.getUrl(), currentConcert.getAgency(), ticketsDetails1, ticketsDetails2, ticketsDetails3)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new TicketPrices(currentConcert.getUrl(), currentConcert.getAgency(), ticketsDetails1, ticketsDetails2, ticketsDetails3).execute();

            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        }
        ticketsDetails1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentConcert.getUrl()));
                startActivity(browserIntent);

            }
        });

        ticketsDetails2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentConcert.getUrl()));
                startActivity(browserIntent);

            }
        });

        ticketsDetails3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentConcert.getUrl()));
                startActivity(browserIntent);
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.concert_info_menu, menu);
        if (DatabaseManager.getInstance(context).isConcertFavourite(ID))
            menu.getItem(0).setIcon(R.drawable.ic_action_important_w);
        if (DatabaseManager.getInstance(context).isArtistFollowing(artist.getText().toString()))
            menu.getItem(4).setTitle("Przestań obserować");
        else
            menu.getItem(4).setTitle("Obserwuj");
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
                        Uri.parse(currentConcert.getUrl()));
                startActivity(websiteIntent);
                return true;

            case R.id.spotify:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
                intent.setComponent(new ComponentName(
                        "com.spotify.music",
                        "com.spotify.music.MainActivity"));
                intent.putExtra(SearchManager.QUERY, currentConcert.getArtist());
                this.startActivity(intent);
                return true;

            case R.id.follow:

                if (DatabaseManager.getInstance(context).isArtistFollowing(currentConcert.getArtist()))// wyjebujemy
                {
                    item.setTitle("Przestań obserwować");
                    DatabaseManager.getInstance(context).removeFollowingArtist(currentConcert.getArtist());
                    item.setIcon(R.drawable.ic_action_not_important_w);
                } else {
                    item.setTitle("Obserwuj");
                    DatabaseManager.getInstance(context).addFollowingArtist(currentConcert.getArtist());
                    item.setIcon(R.drawable.ic_action_important_w);
                }

                return true;

            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, currentConcert.getArtist() + ", " + currentConcert.getCity() + " (" + DatabaseManager.getInstance(context).getDate(ID) + ")");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;

            case R.id.naviagte_icon:
                Intent navIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=&daddr=" + currentConcert.getCity() + " " + currentConcert.getSpot()));
                startActivity(navIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}