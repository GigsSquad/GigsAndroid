package pl.javaparty.concertfinder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.javaparty.adapters.NavDrawerAdapter;
import pl.javaparty.enums.PHPurls;
import pl.javaparty.fragments.*;
import pl.javaparty.items.Agencies;
import pl.javaparty.items.NavDrawerItem;
import pl.javaparty.map.MapHelper;
import pl.javaparty.prefs.Prefs;
import pl.javaparty.sql.JSONthing;
import pl.javaparty.sql.dbManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    ProgressDialog loadingDialog;
    ProgressDialog mapDialog;
    MapHelper mapHelper;

    /* Fragmenty */
    FragmentManager fragmentManager;
    private int currentFragment = 1;
    private Bundle arguments;

    /* Baza */
    static dbManager dbMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navMenuTitles = getResources().getStringArray(R.array.nav_menu);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_menu_icons);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ExpandableListView) findViewById(R.id.left_drawer);
        drawerList.setGroupIndicator(null);
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        loadingDialog.setProgress(0);

        mapDialog = new ProgressDialog(this);
        mapDialog.setCancelable(false);

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
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(2, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(3, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(4, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(5, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(4, -1), agencies));//TODO icona
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons.getResourceId(3, -1), ticketers));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[9]/*TODO!*/, navMenuIcons.getResourceId(3, -1), events));
        navMenuIcons.recycle();
        mapHelper = new MapHelper(this);

        dbMgr = new dbManager(getApplicationContext());
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
                    if (groupPosition == 4) {
                        if (isOnline())
                            new GetLatLng().execute(); //nowa lepsza kurwa funkcja stary
                        else
                            Toast.makeText(getApplication(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                    } else if (currentFragment != groupPosition)
                        changeFragment(groupPosition);
                    return true;
                }
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

        final EditText input = new EditText(this);

        if (isOnline() && dbMgr.getSize("Concerts") < 10 && Prefs.getCity(getApplicationContext()).isEmpty()) {
            //Toast.makeText(getApplicationContext(), "Pobierz koncerty", Toast.LENGTH_LONG).show();

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Wprowadź swoje miasto")
                    .setMessage("Potrzebujemy nazwy Twojej miejscowości, aby dobrze posortować koncerty :)")
                    .setView(input)
                    .setCancelable(false)
                    .setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Prefs.setCity(getApplicationContext(), input.getText().toString());
                            new GetLatLng().execute();
                        }
                    }).setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    new DownloadConcerts().execute();
                    // Do nothing.
                }
            }).show();

        }

        // pierwsza inicjalizacja
        fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right).replace(R.id.content_frame, new RecentFragment()).commit();
        drawerLayout.openDrawer(drawerList);
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

    private void changeFragment(int position) {
        Fragment fragment = null;
        if (position == 0)
            fragment = new SearchFragment();
        else if (position == 1)
            fragment = new RecentFragment();
        else if (position == 2)
            fragment = new PastFragment();
        else if (position == 3)
            fragment = new FavoriteFragment();
        else if (position == 4)
            Log.e("MainActivity", "IMPOSSIBRUUU! Zaminia fragment z pozycji Aktualizuj :O");
        else if (position == 5)
            fragment = new SettingsFragment();
        else if (position == 6)
            fragment = new AboutFragment();
        else if (position >= 90 && position < 100) {
            FestivalFragment ffragment = new FestivalFragment();
            if (position % 90 == 0)
                ffragment.setFestival("Jarocin Festival", R.drawable.jarocin);
            else if (position % 90 == 1)
                ffragment.setFestival("Life Festival Oświęcim", R.drawable.lifefestival);
            fragment = ffragment;
        } else if (position >= 100) {
            RecentFragment rfragment = new RecentFragment();
            for (Agencies ch : rfragment.checkedAgencies.keySet())
                if (ch.fragmentNumber != position)
                    rfragment.checkedAgencies.put(ch, false);

            fragment = rfragment;
        }

        if (position != 4)// takie zabezpieczenie choc to sie nie powinno wydarzyc
            currentFragment = position;

        if (fragment != null) {
            updateCounters();
            fragment.setArguments(arguments);
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.content_frame, fragment)
                    .addToBackStack(null).commitAllowingStateLoss();
        }
    }

    public static void updateCounters() {
        navDrawerItems.get(1).setCount("" + dbMgr.getSize(dbManager.CONCERTS_TABLE));
        navDrawerItems.get(1).setCounterVisibility(true);

        //TODO: setCount dla Past (nie miałem czasu już, sry)

        navDrawerItems.get(3).setCount("" + dbMgr.getSize(dbManager.FAVOURITES_TABLE));
        navDrawerItems.get(3).setCounterVisibility(true);

        adapter = new NavDrawerAdapter(context, navDrawerItems);
        drawerList.setAdapter(adapter);
    }

    // przekazuje DBmanagera
    public static dbManager getDBManager() {
        return dbMgr;
    }


    class GetLatLng extends AsyncTask<String, Void, String> {
        LatLng latlng;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mapDialog.setMessage("Łączę się z mapami");
            mapDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String city = Prefs.getCity(getApplicationContext());
            if (!city.isEmpty()) {
                try {
                    latlng = mapHelper.getLatLng(Prefs.getCity(getApplicationContext()));
                } catch (NullPointerException npexc) {
                    latlng = new LatLng(50.0528282, 19.972944); //Kraków, bo tam bedzie pokazywana, cwele
                }
            }
            return city;
        }

        @Override
        protected void onPostExecute(String city) {
            if (!city.isEmpty()) {
                Prefs.setLat(getApplicationContext(), String.valueOf(latlng.latitude));
                Prefs.setLon(getApplicationContext(), String.valueOf(latlng.longitude));
            }
            new DownloadConcerts().execute();
            mapDialog.dismiss();
            super.onPostExecute(city);
        }
    }

    class DownloadConcerts extends AsyncTask<String, Void, String> {

        boolean updateNeeded = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.setMessage(getString(R.string.database_update));
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<>();
            JSONObject mJsonObject = JSONthing.getThisShit(PHPurls.getConcerts, params);
            //Log.d("All: ", mJsonObject.toString());

            LatLng latLng = new LatLng(Double.parseDouble(Prefs.getLon(getApplicationContext())), Double.parseDouble(Prefs.getLat(getApplicationContext())));

            try {
                int success = mJsonObject.getInt("success");
                int lastid = Integer.parseInt(mJsonObject.getString("last_id"));
                int count = mJsonObject.getInt("count");
                //chłopcy i dziewczęta pamiętajmy iż last_id != count
                loadingDialog.setMax(count);
                if (success == 1 && lastid != Prefs.getLastID(getApplicationContext())) {
                    Prefs.setLastID(getApplicationContext(), lastid);
                    JSONArray mJsonArray = mJsonObject.getJSONArray("concerts");
                    for (int i = 0; i < mJsonArray.length(); i++) {
                        JSONObject JSONconcert = mJsonArray.getJSONObject(i);
                        String id = JSONconcert.getString("ord");
                        String artist = JSONconcert.getString("artist");
                        String city = JSONconcert.getString("city");
                        String spot = JSONconcert.getString("spot");
                        String day = JSONconcert.getString("day");
                        String month = JSONconcert.getString("month");
                        String year = JSONconcert.getString("year");
                        String agency = JSONconcert.getString("agency");
                        String url = JSONconcert.getString("url");
                        String lat = JSONconcert.getString("lat");
                        String lon = JSONconcert.getString("lon");

                        double distance = 0;
                        if (latLng.longitude != -1 || latLng.latitude != -1)
                            distance = mapHelper.inaccurateDistanceTo(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)), latLng);

                        dbMgr.addConcert(Long.parseLong(id), artist, city, spot, Integer.parseInt(day), Integer.parseInt(month), Integer.parseInt(year), agency, url, lat, lon, distance);
                        loadingDialog.incrementProgressBy(1);
                    }
                    updateNeeded = true;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            updateCounters();
            if (updateNeeded)
                changeFragment(currentFragment);// odswieza dany fragment po synchronizacji bazy
            else
                Toast.makeText(getApplicationContext(), getString(R.string.database_is_up_to_date), Toast.LENGTH_SHORT).show();

            loadingDialog.dismiss();
            super.onPostExecute(s);
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
