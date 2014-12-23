package pl.javaparty.concertfinder;

import java.util.ArrayList;

import pl.javaparty.adapters.NavDrawerAdapter;
import pl.javaparty.fragments.AboutFragment;
import pl.javaparty.fragments.FavoriteFragment;
import pl.javaparty.fragments.RecentFragment;
import pl.javaparty.fragments.SearchFragment;
import pl.javaparty.fragments.SettingsFragment;
import pl.javaparty.items.NavDrawerItem;
import pl.javaparty.sql.DatabaseUpdater;
import pl.javaparty.sql.dbManager;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends FragmentActivity {

	/* Drawer */
	private static ArrayList<NavDrawerItem> navDrawerItems;
	private TypedArray navMenuIcons;
	private NavDrawerAdapter adapter;
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private String[] navMenuTitles;
	private ActionBarDrawerToggle drawerToggle;

	/* Fragmenty */
	private FragmentManager fragmentManager;
	private int currentFragment = 1;
	private Bundle arguments;

	/* Baza */
	static dbManager dbMgr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dbMgr = new dbManager(getApplicationContext());

		fragmentManager = getSupportFragmentManager();

		navMenuTitles = getResources().getStringArray(R.array.nav_menu);
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_menu_icons);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);

		navDrawerItems = new ArrayList<NavDrawerItem>();
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
		navMenuIcons.recycle();

		adapter = new NavDrawerAdapter(getApplicationContext(), navDrawerItems);
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
		drawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				drawerLayout.closeDrawers();
				if (position == 3)
					update();
				else if (currentFragment != position)
					changeFragment(position);
			}
		});

		// pierwsza inicjalizacja
		fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right).replace(R.id.content_frame, new RecentFragment()).addToBackStack(null).commit();
		drawerLayout.openDrawer(drawerList);
		updateCounters(); // aktualizuje liczniki w NavDrawerze

		// TODO jak bazy nie ma to update, a tak chuj, niech sami aktualizuja < lol
		update();

	}

	@Override
	public boolean onKeyDown(int keycode, KeyEvent e) {
		switch (keycode) {
		case KeyEvent.KEYCODE_MENU:
			updateCounters();
			if (drawerLayout.isDrawerOpen(drawerList))
				drawerLayout.closeDrawer(drawerList);
			else
				drawerLayout.openDrawer(drawerList);
			return true;
		}

		return super.onKeyDown(keycode, e);
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
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

	public void update()
	{
		DatabaseUpdater db = new DatabaseUpdater(dbMgr, this);
		db.update(new Refresh());
	}

	private void changeFragment(int position)
	{
		Fragment fragment = null;
		if (position == 0)
			fragment = new SearchFragment();
		else if (position == 1)
			fragment = new RecentFragment();
		else if (position == 2)
			fragment = new FavoriteFragment();
		else if (position == 3)
			Log.e("MainActivity", "IMPOSSIBRUUU! Zaminia fragment z pozycji Aktualizuj :O");
		else if (position == 4)
			fragment = new SettingsFragment();
		else if (position == 5)
			fragment = new AboutFragment();

		if (position != 3)// takie zabezpieczenie choc to sie nie powinno wydarzyc
			currentFragment = position;

		if (fragment != null)
		{
			fragment.setArguments(arguments);
			fragmentManager
					.beginTransaction()
					.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
					.replace(R.id.content_frame, fragment)
					.addToBackStack(null).commit();
		}
	}

	public static void updateCounters()
	{
		navDrawerItems.get(1).setCount("" + dbMgr.getSize(dbManager.CONCERTS_TABLE));
		navDrawerItems.get(1).setCounterVisibility(true);

		navDrawerItems.get(2).setCount("" + dbMgr.getSize(dbManager.FAVOURITES_TABLE));
		navDrawerItems.get(2).setCounterVisibility(true);
	}

	// przekazuje DBmanagera
	public static dbManager getDBManager()
	{
		return dbMgr;
	}

	// odswieza aktualny fragment (laduje go od nowa)
	private class Refresh implements Runnable
	{
		@Override
		public void run()
		{
			Log.i("RF", "Olaboga, refreshyk.");
			changeFragment(currentFragment);// odswieza dany fragment

			Log.i("RF", "To tez wyszlo.");
		}
	}
}
