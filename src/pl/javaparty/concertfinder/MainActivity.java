package pl.javaparty.concertfinder;

import java.util.ArrayList;

import pl.javaparty.adapters.NavDrawerAdapter;
import pl.javaparty.fragments.AboutFragment;
import pl.javaparty.fragments.FavoriteFragment;
import pl.javaparty.fragments.RecentFragment;
import pl.javaparty.fragments.SearchFragment;
import pl.javaparty.fragments.SettingsFragment;
import pl.javaparty.items.NavDrawerItem;
import pl.javaparty.sql.dbManager;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	DrawerLayout drawerLayout;
	ListView drawerList;
	int currentFragment = 1;
	dbManager dbMgr;
	Bundle arguments;
	private ActionBarDrawerToggle drawerToggle;
	FragmentManager fragmentManager;

	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dbMgr = new dbManager(getApplicationContext());

		fragmentManager = getSupportFragmentManager();

		arguments = new Bundle();
		arguments.putSerializable("dbManager", dbMgr);
		new DownloadTask().execute();

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

		// ustawianie actionbara by mozna go bylo wcisnac
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, 
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
				if (currentFragment != position)
				{
					Fragment fragment = null;
					if (position == 0)
						fragment = new SearchFragment();
					else if (position == 1)
						fragment = new RecentFragment();
					else if (position == 2)
						fragment = new FavoriteFragment();
					else if (position == 3)
					{
						new DownloadTask().execute();
					}
					else if (position == 4)
						fragment = new SettingsFragment();
					else if (position == 5)
						fragment = new AboutFragment();
					currentFragment = position;// TODO luka, przy wyborze 3 nie zmienia sie fragment

					if (fragment != null)
					{
						fragment.setArguments(arguments);
						fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,
								android.R.anim.slide_out_right).replace(R.id.content_frame, fragment).addToBackStack(null).commit();
					}
				}
			}
		});

		Fragment fragment = new RecentFragment();
		// przekazuje managera
		fragment.setArguments(arguments);
		fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right).replace(R.id.content_frame, fragment).addToBackStack(null).commit();
		drawerLayout.openDrawer(drawerList);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	private class DownloadTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params)
		{
			dbMgr.updateDatabase();
			return null;
		}

		@Override
		protected void onPreExecute() {
			Toast.makeText(getApplicationContext(), "Aktualizowanie...", Toast.LENGTH_SHORT).show();
			super.onPreExecute();
		}

		protected void onPostExecute(String result) {
			Log.i("DB", "Koniec pobierania");
			Toast.makeText(getApplicationContext(), "Zaktualizowano!", Toast.LENGTH_SHORT).show();

			navDrawerItems.get(1).setCount("" + dbMgr.getSize());
			navDrawerItems.get(1).setCounterVisibility(true);

			navDrawerItems.get(2).setCount("0"); // TODO licznik ulubionych koncertów
			navDrawerItems.get(2).setCounterVisibility(true);
			// Fragment fragment = fragmentManager.findFragmentById(R.id.content_frame);
			// if (fragment instanceof RecentFragment)
			// {
			// ((RecentFragment) fragment).refresh();
			// }
			super.onPostExecute(result);
		}
	}
}