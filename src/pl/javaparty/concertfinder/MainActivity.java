package pl.javaparty.concertfinder;

import android.app.ActionBar.OnMenuVisibilityListener;
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
	DrawerLayout dLayout;
	ListView dList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		getActionBar().addOnMenuVisibilityListener(new OnMenuVisibilityListener() {

			@Override
			public void onMenuVisibilityChanged(boolean isVisible) {
				// TODO Auto-generated method stub

			}
		});

		menu = new String[] { "Szukaj", "Ostatnie koncerty", "Twoje koncerty", "Preferencje", "Informacje" };
		dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		dList = (ListView) findViewById(R.id.left_drawer);
		adapterDrawer = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menu);
		dList.setAdapter(adapterDrawer);
		dList.setSelector(android.R.color.holo_blue_dark);
		dList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				dLayout.closeDrawers();

				Fragment fragment = null;
				if (position == 0)
					fragment = new SearchFragment();
				else if (position == 1)
					fragment = new RecentFragment();

				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
				/*
				 * Bundle args = new Bundle(); args.putString("Menu", menu[position]); Fragment detail = new
				 * RecentFragment(); detail.setArguments(args); FragmentManager fragmentManager = getFragmentManager();
				 * fragmentManager.beginTransaction().replace(R.id.content_frame, detail).commit();
				 */
			}
		});
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
