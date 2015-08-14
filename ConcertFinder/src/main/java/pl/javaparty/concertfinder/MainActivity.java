package pl.javaparty.concertfinder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.Toast;
import pl.javaparty.adapters.NavDrawerAdapter;
import pl.javaparty.fragments.*;
import pl.javaparty.items.Agencies;
import pl.javaparty.items.Concert;
import pl.javaparty.items.NavDrawerItem;
import pl.javaparty.prefs.Prefs;
import pl.javaparty.sql.DatabaseManager;
import pl.javaparty.utils.UtilsObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends FragmentActivity {

    /* Drawer */
    private static ArrayList<NavDrawerItem> navDrawerItems;
    private static NavDrawerAdapter adapter;
    private static ExpandableListView drawerList;
    private static Context context;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    TypedArray navMenuIcons;
    String[] navMenuTitles;
    FragmentsFabric fabric;
    Fragment fragment;

    /* Fragmenty */
    static FragmentManager fragmentManager;
    private int currentFragment = 1;
    private static Bundle arguments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fabric = new FragmentsFabric();
        navMenuTitles = getResources().getStringArray(R.array.nav_menu);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_menu_icons);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ExpandableListView) findViewById(R.id.left_drawer);
        drawerList.setGroupIndicator(null);

        ArrayList<String> agencies = new ArrayList<>();//Arrays.asList(getResources().getStringArray(R.array.agencje_submenu)));
        ArrayList<String> ticketers = new ArrayList<>();

        for (Agencies a : Agencies.values()) {
            int posInDrawer = a.fragmentNumber / 100;
            if (posInDrawer == 7)
                agencies.add(a.toString);
            if (posInDrawer == 8)
                ticketers.add(a.toString);
        }

        ArrayList<String> events = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.events_submenu)));
        navDrawerItems = new ArrayList<>();
        navDrawerItems.add(new NavDrawerItem("Szukaj", navMenuIcons.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem("Najbliższe koncerty", navMenuIcons.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem("Minione koncerty", navMenuIcons.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem("Twoje koncerty", navMenuIcons.getResourceId(2, -1)));
        navDrawerItems.add(new NavDrawerItem("Spektakle", navMenuIcons.getResourceId(3, -1)));
        navDrawerItems.add(new NavDrawerItem("Preferencje", navMenuIcons.getResourceId(4, -1)));
        navDrawerItems.add(new NavDrawerItem("Agencje", navMenuIcons.getResourceId(4, -1), agencies));//TODO icona
        navDrawerItems.add(new NavDrawerItem("Bileterie", navMenuIcons.getResourceId(4, -1), ticketers));
        navDrawerItems.add(new NavDrawerItem("Festiwale", navMenuIcons.getResourceId(4, -1), events));
        navMenuIcons.recycle();

        context = getApplicationContext();
        fragmentManager = getSupportFragmentManager();


		/* ustawianie actionbara by mozna go bylo wcisnac */
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.ic_drawer, // ikonka
                R.string.app_name
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
        drawerList.setSelector(android.R.color.holo_blue_dark);
        drawerList.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                if (navDrawerItems.get(groupPosition).getSubmenu() == null) {
                    drawerLayout.closeDrawers();
                    changeFragment(groupPosition);
                    return true;
                }
                changeFragment(groupPosition);
                return false;
            }

        });
        drawerList.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                drawerLayout.closeDrawers();
                if (currentFragment != groupPosition * 100 + childPosition && groupPosition < 9) {
                    changeFragment(groupPosition * 100 + childPosition);
                    return true;
                } else if (groupPosition == 9) {
                    changeFragment(groupPosition * 10 + childPosition);
                    return true;
                }
                return false;
            }
        });

        changeFragment(0);
        drawerLayout.openDrawer(drawerList);
        validateDatabase();
//        showDownloadDialog();

        //jeśli miasto w Prefs wciąż jest puste to wyświetlamy okienko z prośbą o wpisanie
        if (Prefs.getInstance(context).getCity().isEmpty() && Prefs.getInstance(context).getStart()) {
            showCityDialog();
        }

        checkFollowingArtists();
    }

    public void changeFragment(int position) {
        fragment = fabric.produceFragment(position);

        fragment.setArguments(arguments);
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null).commitAllowingStateLoss();
        updateCounters();
    }

    private void validateDatabase() {
        if (!DatabaseManager.getInstance(context).isValid()) {
            DatabaseManager.getInstance(context).deleteDatabase(context);
            if (UtilsObject.isOnline(context)) {
                Toast.makeText(context, "Po aktualizacji konieczne jest ponowne pobranie bazy.", Toast.LENGTH_LONG).show();
                new ConcertDownloader(context).execute();
            }
        }
    }

    private void checkFollowingArtists() {
        int followingArtists = 0;
        for (Concert c1 : DatabaseManager.getInstance(context).getFutureConcertsByCity(Prefs.getInstance(context).getCity().split(" ")[0])) {
            for (Concert c2 : DatabaseManager.getInstance(context).getAllFollowingArtists())
                if (c2.getArtist().equals(c1.getArtist()) && c1.getCity().equals(c2.getCity()))
                    followingArtists++;
        }

        Log.i("FOLL", Integer.toString(followingArtists));
        if (followingArtists > 0) {
            Toast.makeText(context, "Jest " + followingArtists + " koncertów ulubionych artystów w Twojej okolicy", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showCityDialog() {
        final AutoCompleteTextView input = new AutoCompleteTextView(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.COUNTIES, android.R.layout.simple_dropdown_item_1line);
        input.setAdapter(adapter);

        Prefs.getInstance(context).setStart(false); // żeby już nie pytało o miasto, infomacja o tym że apk już kiedyś była uruchamiana

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Wprowadź swoje miasto")
                .setMessage("Potrzebujemy nazwy Twojej miejscowości, aby dobrze posortować koncerty :)")
                .setView(input)
                .setCancelable(true)
                .setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!input.getText().toString().isEmpty()) {
                            Prefs.getInstance(context).setCity(input.getText().toString());
                            Toast.makeText(getApplication(), "Dziękujemy, " + input.getText().toString(), Toast.LENGTH_SHORT).show();
                        }

                        //jeśli online to od razu łączymy sie z mapami i pobieramy latlng
                        if (UtilsObject.isOnline(context))
                            new LatLngConnector(getApplicationContext()).execute();
                    }
                }).setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    private void showDownloadDialog() {

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Czy chcesz teraz pobrać koncerty z bazy?")
                .setMessage("Może zająć nam to kilka minut.")
                .setCancelable(true)
                .setPositiveButton("Pobierz", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        //jeśli nie ma pobranyuch jeszcze współrzędnych dla swojego miasta, czyli w Prefs LAT i LON są na -1 to łączy sie z mapami żeby pobrać
                        if (Prefs.getInstance(context).getLon().equals("-1") || Prefs.getInstance(context).getLat().equals("-1"))
                            new LatLngConnector(getApplicationContext()).execute();
                        else // w przeciwnym wypadku od razu przechodzimy do pobierania kocnertów
                            new ConcertDownloader(context).execute();
                    }
                }).setNegativeButton("Później", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }

        }).show();
    }

    @Override
    public void onBackPressed() {
        drawerLayout.closeDrawer(drawerList);
        int count = fragmentManager.getBackStackEntryCount();
        if (count > 0) {
            Fragment curr = fragmentManager.getFragments().get(count - 1);
            //to bedzie zalosne... uwaga:
            int pos = 1;
            if (curr instanceof SearchFragment)
                pos = 0;
            else if (curr instanceof RecentFragment)
                pos = 1;
            else if (curr instanceof FavoriteFragment)
                pos = 2;
            else if (curr instanceof SettingsFragment)
                pos = 4;
            else if (curr instanceof AboutFragment)
                pos = 5;
            currentFragment = pos;
        } else {
            finish();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch (keycode) {
            case KeyEvent.KEYCODE_MENU:

                if (drawerLayout.isDrawerOpen(drawerList))
                    drawerLayout.closeDrawer(drawerList);
                else
                    drawerLayout.openDrawer(drawerList);
                return true;
        }
        return super.onKeyDown(keycode, e);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
        updateCounters();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // No call for super(). Bug on API Level > 11. lol
    }

    public static void updateCounters() {
        navDrawerItems.get(1).setCount("" + DatabaseManager.getInstance(context).getSize(DatabaseManager.CONCERTS_TABLE));
        navDrawerItems.get(1).setCounterVisibility(true);

        //TODO: setCount dla Past (nie miałem czasu już, sry)

        navDrawerItems.get(3).setCount("" + DatabaseManager.getInstance(context).getSize(DatabaseManager.FAVOURITES_TABLE));
        navDrawerItems.get(3).setCounterVisibility(true);

        adapter = new NavDrawerAdapter(context, navDrawerItems);
        drawerList.setAdapter(adapter);
    }

}
