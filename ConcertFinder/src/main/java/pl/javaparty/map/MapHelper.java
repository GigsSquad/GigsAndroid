package pl.javaparty.map;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import pl.javaparty.prefs.Prefs;

import java.io.IOException;
import java.util.List;

public class MapHelper {
	private Geocoder geoCoder;

	private List<Address> addressList;
	private Address destinationAddress; // to zadane
	private Address hometownAddress; // to z ustawie�
	private String hometownString;

	public MapHelper(Context context) {
		geoCoder = new Geocoder(context);
		hometownString = Prefs.getCity(context);
	}

	/**
	 * Oblicza odleglosc miedzy zadanym miastem w stringu a miastem ktore zostalo zapisane w ustawieniach.
	 *
	 * @param city nazwa miasta
	 * @return zwraca odleglosc w kilometrach
	 * @throws java.io.IOException shit happens
	 */

	public int distanceTo(final String city) {
		destinationAddress = getAddress(city);
		hometownAddress = getAddress(hometownString);

		Log.i("MAP", "Miasto:" + city);
		float[] distanceFloat = new float[3];

		try {
			Location.distanceBetween(
					hometownAddress.getLatitude(), hometownAddress.getLongitude(),
					destinationAddress.getLatitude(), destinationAddress.getLongitude(),
					distanceFloat);
		} catch (NullPointerException ne) {
			return 0;
		}
		Log.i("MAP", "Dystans w km: " + (int) (distanceFloat[0] / 1000));
		return (int) (distanceFloat[0] / 1000);
	}

	public Address getAddress(String place) {
		Address address = null;
		try {
			addressList = geoCoder.getFromLocationName(place, 1);
			while (addressList.size() == 0)
				addressList = geoCoder.getFromLocationName(place, 1);
			if (addressList.size() > 0)
				address = addressList.get(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return address;
	}

	public LatLng getLatLng(String place) {
		return (new LatLng(getAddress(place).getLatitude(), getAddress(place).getLongitude()));

	}
}
