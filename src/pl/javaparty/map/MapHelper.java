package pl.javaparty.map;

import java.io.IOException;
import java.util.List;

import pl.javaparty.prefs.Prefs;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class MapHelper {

	private Context context;
	private Geocoder geoCoder;
	private List<Address> addressList;
	private Address cityAddress; // to zadane
	private Address myCityAddress; // to z ustawieñ
	private String myCity;

	public MapHelper(Context context) {
		this.context = context;
		geoCoder = new Geocoder(context);
		myCity = Prefs.getCity(context);
		myCityAddress = getAddress(myCity);
	}

	/**
	 * Powinno byc wywolywane po zmianie miasta w ustawieniach
	 */
	public void updateMyCity()
	{
		myCityAddress = getAddress(Prefs.getCity(context));
		Prefs.setLatLng(context, myCityAddress.getLatitude(), myCityAddress.getLongitude());
	}

	/**
	 * Oblicza odleglosc miedzy zadanym miastem w stringu a miastem ktore zostalo zapisane w ustawieniach.
	 * 
	 * @param city
	 *            - nazwa miasta
	 * @return zwraca odleglosc w kilometrach
	 * @throws IOException
	 */

	public int distanceTo(final String city)
	{
		cityAddress = getAddress(city);

		float[] distanceFloat = new float[3];
		Location.distanceBetween(myCityAddress.getLatitude(), myCityAddress.getLongitude(), cityAddress.getLatitude(), cityAddress.getLongitude(),
				distanceFloat);

		Log.i("MAP", "Dystans w km: " + (int) (distanceFloat[0] / 1000));
		return (int) (distanceFloat[0] / 1000);
	}

	public Address getAddress(String city)
	{
		Address address = null;
		try {
			addressList = geoCoder.getFromLocationName(city, 1);
		} catch (IOException e) {
			Toast.makeText(context, "Nie mo¿na za³adowaæ, brak internetu", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

		try {
			address = addressList.get(0);
		} catch (IndexOutOfBoundsException e)
		{
			getAddress("Warszawa"); // TODO: tymczasowo
			Log.e("MAP", "Nie znaleziono ¿adneog miasta dla stringa: " + city);
		}
		return address;
	}

	public LatLng getLatLng(String city)
	{
		Log.i("MAP", "MapHelper getLatLng City: " + city);
		Address address = getAddress(city);
		return (new LatLng(address.getLatitude(), address.getLongitude()));
	}
}
