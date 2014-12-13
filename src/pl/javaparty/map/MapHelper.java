package pl.javaparty.map;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.LocationListener;

public class MapHelper {

	Context context;
	private Geocoder geoCoder;
	private List<Address> address;
	LocationManager locationManager;

	public MapHelper(Context context) {
		this.context = context;
		geoCoder = new Geocoder(context);
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}

	public void distance(final String city, int rage)
	{
		try {
			address = geoCoder.getFromLocationName(city, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				float[] distance = new float[4];

				Address loc = address.get(0); // pierwsze co znajdzie i bedzie najlepiej dopasowane
				Location.distanceBetween(location.getLatitude(), location.getLongitude(), loc.getLatitude(), loc.getLongitude(), distance);
				for (float f : distance)
					Log.i("DIST", "distance: " + f);

				Log.i("DIST", "Dystans w km: " + distance[0] / 1000);
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		// locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}

}
