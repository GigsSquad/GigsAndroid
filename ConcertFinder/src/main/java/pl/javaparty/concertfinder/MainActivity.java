package pl.javaparty.concertfinder;

import android.content.Context;
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
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import pl.javaparty.adapters.NavDrawerAdapter;
import pl.javaparty.fragments.*;
import pl.javaparty.items.Agencies;
import pl.javaparty.items.NavDrawerItem;
import pl.javaparty.sql.DatabaseUpdater;
import pl.javaparty.sql.dbManager;

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

		dbMgr = new dbManager(getApplicationContext());
		context = getApplicationContext();
		dbu = new DatabaseUpdater(dbMgr, this);
		fragmentManager = getSupportFragmentManager();

		navMenuTitles = getResources().getStringArray(R.array.nav_menu);
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_menu_icons);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ExpandableListView) findViewById(R.id.left_drawer);
		drawerList.setGroupIndicator(null);

		ArrayList<String> agencies = new ArrayList<>();//Arrays.asList(getResources().getStringArray(R.array.agencje_submenu)));
        ArrayList<String> ticketers = new ArrayList<>();

        for(Agencies a: Agencies.values())
        {
            int posInDrawer = a.fragmentNumber/100;
            if(posInDrawer == 7)
                agencies.add(a.toString);
            if(posInDrawer == 8)
                ticketers.add(a.toString);
        }

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
					changeFragment(groupPosition*100 + childPosition);
				}
				return false;
			}

		});

		dbu.update(new Refresh());

		updateCounters();
		// pierwsza inicjalizacja
		fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right).replace(R.id.content_frame, new RecentFragment()).commit();
		drawerLayout.openDrawer(drawerList);
	}

	@Override
	public void onBackPressed() {
		drawerLayout.closeDrawer(drawerList);
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
		else if (position >= 100) {
            Log.i("MainActivity", "POS: " + position);
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
}
