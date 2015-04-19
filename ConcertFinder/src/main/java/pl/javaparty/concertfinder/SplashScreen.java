package pl.javaparty.concertfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import pl.javaparty.prefs.Prefs;

public class SplashScreen extends FragmentActivity {

    FacebookFragment facebookFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
        int prefsUserId = Prefs.getUserID(getApplicationContext());

        Log.i("LOGIN", "ID w Prefs" + prefsUserId);

        if (prefsUserId != -1)
            startActivity(mainActivityIntent);
        else if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            facebookFragment = new FacebookFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, facebookFragment)
                    .commit();
            // Or set the fragment from restored state info
        } else {
            facebookFragment = (FacebookFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }


    }

}
