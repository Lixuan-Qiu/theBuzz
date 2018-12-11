package cloud9.cse216.lehigh.edu.cloud9;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivityForInput extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Marker marker = null;
    String savedMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_for_input);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent sentIntent = getIntent();
        if(sentIntent.hasExtra("savedMessage")) {
            savedMessage = sentIntent.getStringExtra("savedMessage");
            Log.i("MapsActivityForInput", "receive message: '" + savedMessage + "'");
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(marker != null) {
                    marker.remove();
                    marker = null;
                }
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title("your entered location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }
        });

    }

    public void backFromLocationInput (View v) {
        Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
        if(marker != null) {
            LatLng latLng = marker.getPosition();
            intent.putExtra("latitude", latLng.latitude);
            intent.putExtra("longitude", latLng.longitude);

        }
        intent.putExtra("savedMessage", savedMessage);
        startActivity(intent);

    }
}
