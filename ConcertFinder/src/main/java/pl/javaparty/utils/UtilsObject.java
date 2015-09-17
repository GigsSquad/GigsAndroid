package pl.javaparty.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import pl.javaparty.concertfinder.R;

/**
 * Created by jakub on 8/14/15.
 */
public class UtilsObject {

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

//        boolean offline = netInfo != null && netInfo.isConnectedOrConnecting();
//        if (offline) {
//            Toast.makeText(context, "Brak połączenia", Toast.LENGTH_SHORT).show();
//        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
