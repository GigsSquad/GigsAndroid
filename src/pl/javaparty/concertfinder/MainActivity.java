package pl.javaparty.concertfinder;

import java.io.IOException;

import pl.javaparty.fragments.AboutFragment;
import pl.javaparty.fragments.FavoriteFragment;
import pl.javaparty.fragments.RecentFragment;
import pl.javaparty.fragments.SearchFragment;
import pl.javaparty.fragments.SettingsFragment;
import pl.javaparty.jsoup.JSoupDownloader;
import pl.javaparty.map.MapHelper;
import pl.javaparty.sql.dbManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	ArrayAdapter<String> adapterDrawer;
	String[] menu;
	DrawerLayout drawerLayout;
	ListView drawerList;
	Context context;
	int currentFragment = 1;
	dbManager dbMgr;
	private ActionBarDrawerToggle mDrawerToggle;
	public static FragmentManager fragmentManager;
	Bundle arguments;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = getApplicationContext();
		dbMgr = new dbManager(context);
		fragmentManager = getSupportFragmentManager();
		arguments = new Bundle();
		arguments.putSerializable("dbManager", dbMgr);
		new DownloadTask().execute();
		
		
		MapHelper mapHelper = new MapHelper(context);
		mapHelper.distance("Szczecin", 0);

		getActionBar().setHomeButtonEnabled(true);

		menu = new String[] { "Szukaj", "Ostatnie koncerty", "Twoje koncerty", "Aktualizuj", "Preferencje", "Informacje" };
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		drawerList = (ListView) findViewById(R.id.left_drawer);
		adapterDrawer = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menu);
		drawerList.setAdapter(adapterDrawer);
		drawerList.setSelector(android.R.color.holo_blue_dark);
		drawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				drawerLayout.closeDrawers();
				if (currentFragment != position) {
					Fragment fragment = null;
					if (position == 0)
					{
						fragment = new SearchFragment();
						// przekazuje managera, mozna to w sumie uogolnic ;)
						fragment.setArguments(arguments);
					}
					else if (position == 1)
					{
						fragment = new RecentFragment();
						// przekazuje managera
						fragment.setArguments(arguments);
					}
					else if (position == 2)
						fragment = new FavoriteFragment();
					else if (position == 3)
						new DownloadTask().execute();
					else if (position == 4)
					{
						fragment = new SettingsFragment();
						// przekazuje managera
						fragment.setArguments(arguments);
					}
					else if (position == 5)
						fragment = new AboutFragment();
					currentFragment = position;

					if (fragment != null)
					{
						fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
					}
				}
			}
		});

		mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, // nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle("dpa");
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle("dupa");
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};

		Fragment fragment = new RecentFragment();
		// przekazuje managera
		fragment.setArguments(arguments);
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case 0:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class DownloadTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params)
		{
			JSoupDownloader downloader = new JSoupDownloader(dbMgr);
			try
			{
				downloader.getData();
				Log.i("DB", "Pobieranie");
			} catch (IOException e)
			{
				Log.i("DB", "Nie powiniene� wiedzie� tego tekstu");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected void onPostExecute(String result) {
			Log.i("DB", "Koniec pobierania");
			Toast.makeText(getApplicationContext(), "Zaktualizowano!", Toast.LENGTH_SHORT).show();
			super.onPostExecute(result);
		}
	}
}
