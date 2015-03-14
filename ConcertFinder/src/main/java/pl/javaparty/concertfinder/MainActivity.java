package pl.javaparty.concertfinder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
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
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.javaparty.adapters.NavDrawerAdapter;
import pl.javaparty.enums.PHPurls;
import pl.javaparty.fragments.*;
import pl.javaparty.items.NavDrawerItem;
import pl.javaparty.sql.DatabaseUpdater;
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
    FacebookFragment facebookFragment;

    /* Fragmenty */
    FragmentManager fragmentManager;
    private int currentFragment = 1;
    private Bundle arguments;

    /* Baza */
    static dbManager dbMgr;
    DatabaseUpdater dbu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        loadingDialog = new ProgressDialog(this);
        loadingDialog.setCancelable(false);

        dbMgr = new dbManager(getApplicationContext());
        context = getApplicationContext();
        dbu = new DatabaseUpdater(dbMgr, this);
        fragmentManager = getSupportFragmentManager();

        navMenuTitles = getResources().getStringArray(R.array.nav_menu);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_menu_icons);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ExpandableListView) findViewById(R.id.left_drawer);
        drawerList.setGroupIndicator(null);
        ArrayList<String> agencies = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.agencje_submenu)));

        navDrawerItems = new ArrayList<>();
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(2, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(3, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(4, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(5, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(3, -1), agencies));//TODO icona
        navMenuIcons.recycle();

        adapter = new NavDrawerAdapter(context, navDrawerItems);
        drawerList.setAdapter(adapter);

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
                Log.i("DRAWER", "Group: " + groupPosition);
                if (navDrawerItems.get(groupPosition).getSubmenu() == null) {
                    drawerLayout.closeDrawers();
                    if (groupPosition == 4)
                        dbu.update(new Refresh());
                    else if (currentFragment != groupPosition)
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
                Log.i("DRAWER", "Child: " + childPosition);
                drawerLayout.closeDrawers();
                if (currentFragment != groupPosition || currentFragment != 30 + childPosition) {
                    changeFragment(30 + childPosition);
                }
                return false;
            }

        });

        //dbu.update(new Refresh());
        new DownloadConcerts().execute(); //nowa lepsza kurwa funkcja stary

        updateCounters();

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

    // odswieza aktualny fragment (laduje go od nowa)
    private class Refresh implements Runnable {
        @Override
        public void run() {
            Log.i("RF", "Olaboga, refreshyk.");
            changeFragment(currentFragment);// odswieza dany fragment
            updateCounters();
            Log.i("RF", "To tez wyszlo.");
        }
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
        else if (position >= 30) {
            int pos = position - 30;
            RecentFragment rfragment = new RecentFragment();
            for (CharSequence ch : rfragment.checkedAgencies.keySet())
                if (ch != rfragment.checkedAgencies.keySet().toArray()[pos])
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

    enum AgencyFragments {
        //zakladka 3 (bo RecentFragment) a druga liczba to wybrana zakladka podmenu
        GOAHEAD(30), SONGKICK(40), LIVENATION(50), TICKETPRO(60);
        private int fragmentNumber;

        AgencyFragments(int fragment) {
            fragmentNumber = fragment;
        }

        public int nr() {
            return fragmentNumber;
        }
    }

    class DownloadConcerts extends AsyncTask<String, Void, String> {

//        ArrayAdapter arrayAdapter = null;
//        ArrayList commentArrayList = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.setMessage("Synchronizacja bazy");
            loadingDialog.show();
            dbMgr.deleteDB(getApplicationContext());
//            commentArrayList = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject mJsonObject = JSONthing.getThisShit(PHPurls.getConcerts, params);
            Log.d("All: ", mJsonObject.toString());

            try {
                int success = mJsonObject.getInt("success");
                if (success == 1) {
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
                        String lon = JSONconcert.getString("ord");
                        dbMgr.addConcert(Long.parseLong(id), artist, city, spot, Integer.parseInt(day), Integer.parseInt(month), Integer.parseInt(year), agency, url, lat, lon);
                        Log.i("JSON", id + "");
//                        commentArrayList.add(comment + " ~" + author);
                    }
//                    arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, commentArrayList);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
//            commentListView.setAdapter(arrayAdapter);

//            if (!commentArrayList.isEmpty())
//                concertInfo.setVisibility(View.GONE);

            loadingDialog.dismiss();
            super.onPostExecute(s);
        }
    }


}
