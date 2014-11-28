package pl.javaparty.map;

import pl.javaparty.concertfinder.R;
import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapTest extends Activity {
	static final LatLng SZCZECIN = new LatLng(51.558, 16.927);
	private GoogleMap map;
	
	 @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.map_activity);
	    map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
	        .getMap();
	    Marker szczecin = map.addMarker(new MarkerOptions().position(SZCZECIN).title("sdfasd"));

	    // Move the camera instantly to hamburg with a zoom of 15.
	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(SZCZECIN, 15));

	    // Zoom in, animating the camera.
	    map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
	  }
}
