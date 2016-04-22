package utsav.childsecurity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LoggedIn extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        this.mMap.setMyLocationEnabled(true);
        this.mMap.getUiSettings().setZoomControlsEnabled(true);
        Location location = null;
        try {
            location = getLastKnownLocation();
        } catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        if (location != null) {
            double clati = location.getLatitude();
            double clongi = location.getLongitude();
            LatLng source = new LatLng(clati, clongi);
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(clati, clongi, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert addresses != null;
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            Toast.makeText(getApplicationContext(), address + "\n" + city + "\n" + state + "\n" + country + "\n" + postalCode, Toast.LENGTH_LONG).show();
            this.mMap.moveCamera(CameraUpdateFactory.newLatLng(source));
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(source, 15.0f);
            this.mMap.animateCamera(update);
        }
    }
    private Location getLastKnownLocation() throws SecurityException {
        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Location bestLocation = null;
        for (String provider : mLocationManager.getProviders(true)) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l != null && (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy())) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
