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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.javaparty.adapters.NavDrawerAdapter;
import pl.javaparty.enums.PHPurls;
import pl.javaparty.fragments.*;
import pl.javaparty.items.Agencies;
import pl.javaparty.items.Concert;
import pl.javaparty.items.NavDrawerItem;
import pl.javaparty.map.MapHelper;
import pl.javaparty.prefs.PrefsSingleton;
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
    static FragmentManager fragmentManager;
    private int currentFragment = 1;
    private static Bundle arguments;

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
        mapHelper = new MapHelper(this);
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


        // pierwsza inicjalizacja
        fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right).replace(R.id.content_frame, new RecentFragment()).commit();
        drawerLayout.openDrawer(drawerList);


        //Sprawdzamy czy nie nastapily zmiany w tabeli koncertow (w razie gdyby zmieniono kolejnosc albo dodano kolumny)
        if (dbManager.getInstance(context).checkIfConcertTableChanged()) {
            dbManager.getInstance(context).deleteDatabase(context);
            if (isOnline()) {
                Toast.makeText(context, "Po aktualizacji konieczne jest ponowne pobranie bazy.", Toast.LENGTH_LONG).show();
                new DownloadConcerts().execute();
            }
        } else
            //żeby pobrać cokolwiek to użytkownik musi być online oraz mieć mniej niż np 100 koncertów (np jak przerwie pobieranie w którymś momencie, to wtedy będzie mieć mniej)
            if (isOnline() && dbManager.getInstance(context).getSize(dbManager.CONCERTS_TABLE) < 100)
                showDownloadDialog();

        //jeśli miasto w Prefs wciąż jest puste to wyświetlamy okienko z prośbą o wpisanie
        if (PrefsSingleton.getInstance().getCity(getApplicationContext()).isEmpty() && PrefsSingleton.getInstance().getStart(getApplicationContext()))
            showCityDialog();

        checkFollowingArtists();
    }

    private void checkFollowingArtists() {
        int followingArtists = 0;
        for (Concert c1 : dbManager.getInstance(context).getFutureConcertsByCity(PrefsSingleton.getInstance().getCity(context).split(" ")[0])) {
            for (Concert c2 : dbManager.getInstance(context).getAllFollowingArtists())
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

        PrefsSingleton.getInstance().setStart(getApplicationContext(), false); // żeby już nie pytało o miasto, infomacja o tym że apk już kiedyś była uruchamiana

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Wprowadź swoje miasto")
                .setMessage("Potrzebujemy nazwy Twojej miejscowości, aby dobrze posortować koncerty :)")
                .setView(input)
                .setCancelable(true)
                .setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!input.getText().toString().isEmpty()) {
                            PrefsSingleton.getInstance().setCity(getApplicationContext(), input.getText().toString());
                            Toast.makeText(getApplication(), "Dziękujemy, " + input.getText().toString(), Toast.LENGTH_SHORT).show();
                        }

                        //jeśli online to od razu łączymy sie z mapami i pobieramy latlng
                        if (isOnline())
                            new GetLatLng(getApplicationContext()).execute();
                    }
                }).setNegativeButton("Anluje", new DialogInterface.OnClickListener() {
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
                        if (PrefsSingleton.getInstance().getLon(getApplication()).equals("-1") || PrefsSingleton.getInstance().getLat(getApplication()).equals("-1"))
                            new GetLatLng(getApplicationContext()).execute();
                        else // w przeciwnym wypadku od razu przechodzimy do pobierania kocnertów
                            new DownloadConcerts().execute();
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

    public static void changeFragment(int position) {
        FragmentsFabric fabric = new FragmentsFabric();
        Fragment fragment = fabric.produceFragment(position);

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
        navDrawerItems.get(1).setCount("" + dbManager.getInstance(context).getSize(dbManager.CONCERTS_TABLE));
        navDrawerItems.get(1).setCounterVisibility(true);

        //TODO: setCount dla Past (nie miałem czasu już, sry)

        navDrawerItems.get(3).setCount("" + dbManager.getInstance(context).getSize(dbManager.FAVOURITES_TABLE));
        navDrawerItems.get(3).setCounterVisibility(true);

        adapter = new NavDrawerAdapter(context, navDrawerItems);
        drawerList.setAdapter(adapter);
    }

    class GetLatLng extends AsyncTask<String, Void, String> {
        LatLng latlng;
        JSONthing jsonthing;
        String id;
        Context cont;

        public GetLatLng(Context context) {
            super();
            cont = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mapDialog.setMessage("Łączę się z Google Maps");
            mapDialog.show();
            jsonthing = new JSONthing();
            id = String.valueOf(PrefsSingleton.getInstance().getUserID(getApplication())); //stirng żeby się PHPy nie srały
        }

        @Override
        protected String doInBackground(String... args) {
            String city = PrefsSingleton.getInstance().getCity(getApplicationContext());
            if (!city.isEmpty()) {
                if (!id.equals("-1"))
                    updateServerLatLng(city);
                latlng = MapHelper.getLatLongFromAddress(city);
            }
            return city;
        }

        /**
         * Aktualizuje miasto użytkownia na to któe pisał w okienku
         *
         * @param city
         */
        protected void updateServerLatLng(String city) {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("location", city.split(" ")[0]));
            params.add(new BasicNameValuePair("user_id", id));
            jsonthing.makeHttpRequest(PHPurls.updateUser.toString(), "GET", params); //TIGHT and ELEGANT
        }


        @Override
        protected void onPostExecute(String city) {
            if (!city.isEmpty()) {
                PrefsSingleton.getInstance().setLat(getApplicationContext(), String.valueOf(latlng.latitude));
                PrefsSingleton.getInstance().setLon(getApplicationContext(), String.valueOf(latlng.longitude));
                Log.d("LATLNG", "Lat:" + PrefsSingleton.getInstance().getLat(getApplicationContext()) + " Long:" + PrefsSingleton.getInstance().getLon(getApplicationContext()));
            }
            mapDialog.dismiss();
            new DownloadConcerts().execute();

            super.onPostExecute(city);
        }
    }

    class DownloadConcerts extends AsyncTask<String, Void, String> {

        boolean updateNeeded = false;
        int success, jsonLastId, prefsLastId, count;
        JSONObject mJsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.setMessage(getString(R.string.database_update));
            loadingDialog.setProgress(0);
            loadingDialog.setMax(1);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<>();
            mJsonObject = JSONthing.getThisShit(PHPurls.getConcerts, params);

            try {
                success = mJsonObject.getInt("success");
                jsonLastId = Integer.parseInt(mJsonObject.getString("last_id"));
                prefsLastId = PrefsSingleton.getInstance().getLastID(getApplicationContext());
                count = mJsonObject.getInt("count");
                //chłopcy i dziewczęta pamiętajmy iż last_id != count

                //jak poprawnie pobierzemy z internetów
                if (success == 1) {
                    loadingDialog.setMax(prefsLastId + jsonLastId);

                    Log.i("DB", "ID - Prefs: " + prefsLastId + " JSON: " + jsonLastId);
                    Log.i("DB", "COUNT - Prefs: " + dbManager.getInstance(context).getSize(dbManager.CONCERTS_TABLE) + " JSON: " + count);

                    //aktualizujemy obecne koncerty bez względu na wszystko
                    updateConcerts();

                    if (count + 1 > dbManager.getInstance(context).getSize(dbManager.CONCERTS_TABLE)) { //aktualizacja - kiedy liczba koncertów z internetu jest większa od liczby koncertów które mamy w aplikacji
                        updateConcerts();
                    } else { // tak nie powinno się nigdy stać
                        Log.wtf("DB", "Baza w aplikacji ma wiecej koncertów niż na serwerze?");
                        dbManager.getInstance(context).deleteTables(); //wypierdol dziada
                        PrefsSingleton.getInstance().setLastID(getApplicationContext(), -1); // zmieniamy ostatnie id na początkowe
                        downloadConcerts(); //i zapełnij od nowa
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        //Aktualizuje koncerty
        protected void updateConcerts() throws JSONException {
            //współrzędne z prefs wpisane do latLng
            LatLng latLng = new LatLng(Double.parseDouble(PrefsSingleton.getInstance().getLon(getApplicationContext())), Double.parseDouble(PrefsSingleton.getInstance().getLat(getApplicationContext())));

            loadingDialog.setMax(count - prefsLastId);
            PrefsSingleton.getInstance().setLastID(getApplicationContext(), jsonLastId);
            JSONArray mJsonArray = mJsonObject.getJSONArray("concerts");
            double distance;
            dbManager.getInstance(context).beginTransaction();
            for (int i = 0; i < mJsonArray.length(); i++) {
                JSONObject JSONconcert = mJsonArray.getJSONObject(i);
                if (latLng.longitude != -1)
                    distance = mapHelper.inaccurateDistanceTo(Double.parseDouble(JSONconcert.getString("lat")), Double.parseDouble(JSONconcert.getString("lon")), latLng);
                else
                    distance = 0;

                dbManager.getInstance(context).updateConcert(
                        JSONconcert.getInt("id"),
                        JSONconcert.getString("artist"),
                        JSONconcert.getString("city"),
                        JSONconcert.getString("spot"),
                        JSONconcert.getInt("day"),
                        JSONconcert.getInt("month"),
                        JSONconcert.getInt("year"),
                        JSONconcert.getString("agency"),
                        JSONconcert.getString("url"),
                        JSONconcert.getString("updated"),
                        JSONconcert.getString("lat"),
                        JSONconcert.getString("lon"),
                        distance,
                        JSONconcert.getString("entrance_fee"));

                loadingDialog.incrementProgressBy(1);
            }
            dbManager.getInstance(context).endTransaction();
            updateNeeded = true;
        }

        //Pobiera nowe koncerty
        protected void downloadConcerts() throws JSONException {
            //współrzędne z prefs wpisane do latLng
            LatLng latLng = new LatLng(Double.parseDouble(PrefsSingleton.getInstance().getLon(getApplicationContext())), Double.parseDouble(PrefsSingleton.getInstance().getLat(getApplicationContext())));

            PrefsSingleton.getInstance().setLastID(getApplicationContext(), jsonLastId);
            JSONArray mJsonArray = mJsonObject.getJSONArray("concerts");
            double distance;
            dbManager.getInstance(context).beginTransaction();
            for (int i = 0; i < mJsonArray.length(); i++) {
                JSONObject JSONconcert = mJsonArray.getJSONObject(i);
                if (latLng.longitude != -1)
                    distance = mapHelper.inaccurateDistanceTo(Double.parseDouble(JSONconcert.getString("lat")), Double.parseDouble(JSONconcert.getString("lon")), latLng);
                else
                    distance = 0;

                dbManager.getInstance(context).addConcert(
                        JSONconcert.getInt("id"),
                        JSONconcert.getString("artist"),
                        JSONconcert.getString("city"),
                        JSONconcert.getString("spot"),
                        JSONconcert.getInt("day"),
                        JSONconcert.getInt("month"),
                        JSONconcert.getInt("year"),
                        JSONconcert.getString("agency"),
                        JSONconcert.getString("url"),
                        JSONconcert.getString("updated"),
                        JSONconcert.getString("lat"),
                        JSONconcert.getString("lon"),
                        distance,
                        JSONconcert.getString("entrance_fee"));

                loadingDialog.incrementProgressBy(1);
            }
            dbManager.getInstance(context).endTransaction();
            updateNeeded = true;
        }

        @Override
        protected void onPostExecute(String s) {
            updateCounters();
            if (updateNeeded)
                changeFragment(currentFragment);// odswieza dany fragment po synchronizacji bazy
            else
                Toast.makeText(getApplicationContext(), getString(R.string.database_is_up_to_date), Toast.LENGTH_SHORT).show();

            loadingDialog.dismiss();
            checkFollowingArtists();
            super.onPostExecute(s);
        }

    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
