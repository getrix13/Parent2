package info.androidhive.loginandregistration.activity;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import info.androidhive.loginandregistration.R;

import static info.androidhive.loginandregistration.R.id.map;
import static info.androidhive.loginandregistration.activity.MainActivity.EXTRA_LAT;
import static info.androidhive.loginandregistration.activity.MainActivity.EXTRA_LONG;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager lm;
    TextView tvLat;
    TextView tvLong;
    double platitude;
    double plongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();

        String flat = intent.getStringExtra(EXTRA_LAT);
        String flong = intent.getStringExtra(EXTRA_LONG);



        platitude=Double.parseDouble(flat.toString());
        plongitude=Double.parseDouble(flong.toString());

        //DecimalFormat twoDForm = new DecimalFormat("#.###");
        //double plat = Double.valueOf(twoDForm.format(platitude));
        //double plon = Double.valueOf(twoDForm.format(plongitude));


        //tvLat = (TextView) findViewById(R.id.latitude);
        //tvLong = (TextView) findViewById(R.id.longitude);

        affichage(platitude, plongitude);

    }


    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(platitude, plongitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        double zoomLevel = 15.0; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, (float) zoomLevel));
    }


    private void affichage(double platitude, double plongitude) {

        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

    }

}
