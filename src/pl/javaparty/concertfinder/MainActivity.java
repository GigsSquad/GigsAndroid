package pl.javaparty.concertfinder;

import java.io.File;
import java.io.IOException;

import pl.javaparty.concertmanager.ConcertManager;
import pl.javaparty.jsoup.JSoupDownloader;
import pl.javaparty.sql.dbManager;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	ArrayAdapter<String> adapterDrawer;
	String[] menu;
	DrawerLayout drawerLayout;
	ListView drawerList;
	Context context;
	int currentFragment = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = getApplicationContext();

		File db = getDatabasePath(dbManager.DATABASE_NAME);
		Log.i("DB", "Sprawdzam czy baza istnieje");
		// if (!db.exists()) TODO: nie zawsze dziala
		new DownloadTask().execute();

		menu = new String[] { "Szukaj", "Ostatnie koncerty", "Twoje koncerty", "Aktualizuj", "Preferencje", "Informacje" };
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);
		adapterDrawer = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menu);
		drawerList.setAdapter(adapterDrawer);
		drawerList.setSelector(android.R.color.holo_blue_dark);
		drawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				// Bundle args = new Bundle();
				// args.putString("Menu", menu[position]);
				drawerLayout.closeDrawers();

				if (currentFragment != position) {
					Fragment fragment = null;
					if (position == 0)
						fragment = new SearchFragment();
					else if (position == 1)
						fragment = new RecentFragment();
					else if (position == 2)
						fragment = new FavoriteFragment();
					else if (position == 3)
						new DownloadTask().execute();
					else if (position == 4)
						fragment = new SettingsFragment();
					else if (position == 5)
						fragment = new InformationFragment();
					currentFragment = position;

					if (fragment != null)
					{
						// fragment.setArguments(args);
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
					}
				}
			}
		});

		FragmentManager fragmentManager = getFragmentManager();
		Fragment fragment = new RecentFragment();
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private class DownloadTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params)
		{
			dbManager database = new dbManager(context);
			JSoupDownloader downloader = new JSoupDownloader(database);
			if(isOnline())
			{
				Toast.makeText(getApplicationContext(), "Jestem podlaczony internetem", Toast.LENGTH_SHORT).show();
				try
				{
					downloader.getData();
					new ConcertManager(database).collect();
					Log.i("DB", "Tworzenie nowej bazy i pobieranie");
					System.out.println("Pobieranie...");
				} catch (IOException e)
				{
					Log.i("DB", "Nie powiniene� wiedzie� tego tekstu");
					e.printStackTrace();
				}
			}else
				Toast.makeText(getApplicationContext(), "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
			return null;
		}
		
		public boolean isOnline() {
		    ConnectivityManager cm =
		        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();
		    return netInfo != null && netInfo.isConnectedOrConnecting();
		}
		
		@Override
		protected void onPreExecute() {
			Log.i("DB", "Baza nie istnieje");
			super.onPreExecute();
		}

		protected void onPostExecute(String result) {
			Log.i("DB", "Koniec pobierania");
			Toast.makeText(getApplicationContext(), "Zaaktualizowano!", Toast.LENGTH_SHORT).show();
			super.onPostExecute(result);
		}
	}
}
