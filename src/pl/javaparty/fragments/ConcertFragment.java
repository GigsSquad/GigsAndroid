package pl.javaparty.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;

public class ConcertFragment extends FragmentActivity {

	private FragmentTabHost mTabHost;
	int ID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		Bundle extras = getIntent().getExtras();
		if (extras != null)
			ID = extras.getInt("ID", -1);

		Log.i("KURWA", "ConcertFragment, Przeslane ID: " + ID);

		mTabHost = new FragmentTabHost(getApplication());
		mTabHost.setup(getApplication(), getSupportFragmentManager(), android.R.id.tabhost);

		mTabHost.addTab(mTabHost.newTabSpec("fragmentb").setIndicator("Informacje"), InfoConcertTab.class, extras);
		mTabHost.addTab(mTabHost.newTabSpec("fragmenta").setIndicator("Mapa"), MapConcertTab.class, extras);
		mTabHost.setCurrentTab(0);
	}

	//
	// @Override
	// public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	// inflater.inflate(R.menu.activity_main_actions, menu);
	//
	// // zmienia ikonkê na
	// if (dbm.isConcertFavourite(ID))
	// menu.getItem(0).setIcon(R.drawable.ic_action_favorite_on);
	//
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case R.id.favorite_icon:
	// dbm.addFavouriteConcert(ID);
	// // TODO: tutaj taki sam IF jak w onCreateOptionsMenu()
	// if (dbm.isConcertFavourite(ID))
	// item.setIcon(R.drawable.ic_action_favorite_on);
	// return true;
	// default:
	// return super.onOptionsItemSelected(item);
	// }
	// }

}