package com.taskfoundation.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;

    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.i("Location: ", location.toString());
                updateLocationInfo(location);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                updateLocationInfo(lastKnownLocation);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        }
    }

    public void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    public void updateLocationInfo(Location location) {
        Log.i("Location: ", location.toString());
        TextView latTextView = findViewById(R.id.lat);
        TextView lngTextView = findViewById(R.id.lng);
        TextView altTextView = findViewById(R.id.alt);
        TextView accuracyTextView = findViewById(R.id.accuracy);
        TextView addressTextView = findViewById(R.id.address);

        latTextView.setText("Latitude: " + Double.toString(location.getLatitude()));
        lngTextView.setText("Longitude: " + Double.toString(location.getLongitude()));
        altTextView.setText("Altitude: " + Double.toString(location.getAltitude()));
        accuracyTextView.setText("Accuracy: " + Double.toString(location.getAccuracy()));

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String address = "couldn't find an address!";
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addressList != null && addressList.size() > 0) {
                address = "Address: \n";
                if (addressList.get(0).getThoroughfare() != null) {
                    address += addressList.get(0).getThoroughfare() + "\n";
                }

                if (addressList.get(0).getLocality() != null) {
                    address += addressList.get(0).getLocality() + "\n";
                }

                if (addressList.get(0).getPostalCode() != null) {
                    address += addressList.get(0).getPostalCode() + "\n";
                }

                if (addressList.get(0).getAdminArea() != null) {
                    address += addressList.get(0).getAdminArea();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        addressTextView.setText("Address: " + address);
    }
}