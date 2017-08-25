package com.example.admin.googlemapslocation.view.mainactivity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.example.admin.googlemapslocation.data.RemoteDataSource;
import com.example.admin.googlemapslocation.model.AddressResponse;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;


/**
 * Created by Admin on 8/24/2017.
 */

public class MainActivityPresenter {

    MainActivityContract.View view;
    Context context;
    RemoteDataSource remoteSource = new RemoteDataSource(this);

    public void attachView(MainActivityContract.View view){
        this.view = view;

    }

    public void dettachView() {
        this.view = null;
    }

    public void setContext(Context context){
        this.context = context;
    }

    public void getGeocodeFromAddress(String address){
        remoteSource.getGeocodeFromAddress(address);

    }
    public void getGeocodeFromLatLng(String latitude, String longitude){
        remoteSource.getGeocodeFromLatLong(latitude, longitude);
    }

    public void returnNetworkCallAddress(String s) {
        Gson gson = new Gson();
        AddressResponse addressResponse = gson.fromJson(s, AddressResponse.class);
        view.SetAddress(addressResponse.getResults().get(0).getFormattedAddress());
    }

    public void returnNetworkCallLatLong(String s){
        Gson gson = new Gson();
        AddressResponse addressResponse = gson.fromJson(s, AddressResponse.class);
        view.SetLat(Double.toString(addressResponse.getResults().get(0).getGeometry().getLocation().getLat()));
        view.SetLng(Double.toString(addressResponse.getResults().get(0).getGeometry().getLocation().getLng()));
    }

    public void getGeocoderCallFromAddress(String s){
        Geocoder gc = new Geocoder(context);
        if(gc.isPresent()){
            List<Address> list = null;
            try {
                list = gc.getFromLocationName(s, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = list.get(0);
            view.SetLat(Double.toString(address.getLatitude()));
            view.SetLng(Double.toString(address.getLongitude()));
        }
    }

    public void getGeocoderCallFromLatLng(String Lat, String Lng){
        Geocoder gc = new Geocoder(context);
        if(gc.isPresent()){
            List<Address> list = null;
            try {
                list = gc.getFromLocation(Double.parseDouble(Lat), Double.parseDouble(Lng),1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = list.get(0);
            view.SetAddress(address.getAddressLine(0));
        }
    }

}
