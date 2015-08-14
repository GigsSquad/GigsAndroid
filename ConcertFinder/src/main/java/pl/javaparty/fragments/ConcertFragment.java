package pl.javaparty.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.MenuItem;
import pl.javaparty.concertfinder.R;
import pl.javaparty.sql.dbManager;

public class ConcertFragment extends FragmentActivity {

    private FragmentTabHost mTabHost;
    private int ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_concert);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        ID = getIntent().getExtras().getInt("ID", -1);
        boolean happened = dbManager.getInstance(getApplicationContext()).getConcertByID(ID).happened();

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        if (happened) {
            mTabHost.addTab(mTabHost.newTabSpec("infoFragment").setIndicator("Informacje"), TabPastConcertInfo.class, getIntent().getExtras());
            mTabHost.addTab(mTabHost.newTabSpec("commentsFragment").setIndicator("Komentarze"), TabComment.class, getIntent().getExtras());
        } else {
            mTabHost.addTab(mTabHost.newTabSpec("infoFragment").setIndicator("Informacje"), TabConcertInfo.class, getIntent().getExtras());
//            mTabHost.addTab(mTabHost.newTabSpec("mapFragment").setIndicator("Mapa koncertu"), TabConcertMap.class, getIntent().getExtras());
            mTabHost.addTab(mTabHost.newTabSpec("googleMapFragment").setIndicator("Dojazd"), TabConcertGoogleMap.class, getIntent().getExtras());

        }
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