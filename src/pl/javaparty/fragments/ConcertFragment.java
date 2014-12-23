package pl.javaparty.fragments;

import pl.javaparty.concertfinder.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.MenuItem;

public class ConcertFragment extends FragmentActivity {

	private FragmentTabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_concert);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		mTabHost.addTab(mTabHost.newTabSpec("infoFragment").setIndicator("Informacje"), InfoConcertTab.class, getIntent().getExtras());
		mTabHost.addTab(mTabHost.newTabSpec("mapFragment").setIndicator("Mapa"), MapConcertTab.class, getIntent().getExtras());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}