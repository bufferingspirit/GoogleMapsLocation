package com.example.admin.googlemapslocation.view.mainactivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.admin.googlemapslocation.view.mapactivity.MapsActivity;
import com.example.admin.googlemapslocation.R;
import com.example.admin.googlemapslocation.model.AddressResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements MainActivityContract.View{

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 10;
    public static final String TAG = "MainActivity";
    EditText etLattitude, etLongitude, etAddress;
    Location currentLocation;

    MainActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etLattitude = (EditText) findViewById(R.id.etLattitude);
        etLongitude = (EditText) findViewById(R.id.etLongitude);
        etAddress = (EditText) findViewById(R.id.etAddress);
        presenter = new MainActivityPresenter();
        presenter.attachView(this);
        presenter.setContext(this);
        checkPermissions();
        checkLocationEnabled();

    }

    public void SetLat(String s){
        etLattitude.setText(s);
    }

    public void SetLng(String s){
        etLongitude.setText(s);
    }

    public void SetAddress(String s){
        etAddress.setText(s);
    }


    public void startMapActivity(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("location", currentLocation);
        startActivity(intent);
    }

    public void getLocationData(View view){

        //TODO add user input checks
        switch (view.getId()){
            case R.id.btnGetAddress:
                presenter.getGeocodeFromLatLng(etLattitude.getText().toString(), etLongitude.getText().toString());
                break;
            case R.id.btnGetLatLng:
                presenter.getGeocodeFromAddress(etAddress.getText().toString());
                break;
            case R.id.btnGetLatLngGeo:
                presenter.getGeocoderCallFromAddress(etAddress.getText().toString());
                break;
            case R.id.btnGetAddressGeo:
                presenter.getGeocoderCallFromLatLng(etLattitude.getText().toString(), etLongitude.getText().toString());
        }
    }

    FusedLocationProviderClient fusedLocationProviderClient;
    public void getLocation(final View view){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d(TAG, "onSuccess: " + location.toString());

                        currentLocation = location;
                        etLattitude.setText(Double.toString(location.getLatitude()));
                        etLongitude.setText(Double.toString(location.getLongitude()));
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });

    }

    public void checkLocationEnabled(){
        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Activate GPS");
            dialog.setPositiveButton(("Activate GPS"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Do Not Activate", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
    }


    private void checkPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

                // MY_PERMISSIONS_REQUEST_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else{
            //getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void showError(String s) {
        Log.d(TAG, "showError: ");
    }
}
