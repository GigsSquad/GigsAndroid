package pl.javaparty.concertfinder;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	ArrayAdapter<String> adapterDrawer;
	String[] menu;
	DrawerLayout drawerLayout;
	ListView drawerList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		menu = new String[] { "Szukaj", "Ostatnie koncerty", "Twoje koncerty", "Preferencje", "Informacje" };
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);
		adapterDrawer = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menu);
		drawerList.setAdapter(adapterDrawer);
		drawerList.setSelector(android.R.color.holo_blue_dark);
		drawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				drawerLayout.closeDrawers();

				Bundle args = new Bundle();
				args.putString("Menu", menu[position]);
				

				Fragment fragment = null;
				if (position == 0)
					fragment = new SearchFragment();
				else if (position == 1)
					fragment = new RecentFragment();
				else if (position == 2)
					fragment = new FavoriteFragment();
				else if (position == 3)
					fragment = new SettingsFragment();
				else if (position == 4)
					fragment = new InformationFragment();
				
				fragment.setArguments(args);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

			}
		});
		
		FragmentManager fragmentManager = getFragmentManager();
		Fragment fragment = new RecentFragment();
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
	}

	// Od action bar, ikonka szukaj, ta z 3 kropkami co otwiera menu w którym s¹ ustawienia etc.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
}
