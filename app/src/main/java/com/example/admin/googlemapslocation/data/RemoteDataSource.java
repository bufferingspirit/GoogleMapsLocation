package com.example.admin.googlemapslocation.data;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.admin.googlemapslocation.view.mainactivity.MainActivityPresenter;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Admin on 8/24/2017.
 */

public class RemoteDataSource{
    //add api calls here
    public static final String TAG = "RemoteDataSource";
    public static final String GEO_KEY = "AIzaSyAFC0lf_vI-ilwHKMNxvbrBhaZifDYeZT0";

    MainActivityPresenter presenter;
    Handler handler = new Handler(Looper.getMainLooper());

    public RemoteDataSource(MainActivityPresenter presenter){
        this.presenter = presenter;
    }

    private HttpUrl BuildQuery(String queryType, String queryArg){
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("maps.googleapis.com")
                .addPathSegment("maps")
                .addPathSegment("api")
                .addPathSegment("geocode")
                .addPathSegment("json")
                .addQueryParameter(queryType, queryArg)
                .addQueryParameter("key", GEO_KEY)
                .build();
        return url;
    }

    public void getGeocodeFromAddress(String address){
        String queryType = "address";
        HttpUrl url = BuildQuery("address", address.replaceAll(" ", "+"));
        doGeoQuery(url, queryType);
    }

    public void getGeocodeFromLatLong(String latitude, String longitude) {
        String queryType = "latlng";
        String currentLatLang = latitude + "," + longitude;
        HttpUrl url = BuildQuery("latlng", currentLatLang);
        doGeoQuery(url, queryType);
    }

    public void doGeoQuery(HttpUrl url, final String queryType){
        OkHttpClient client = new OkHttpClient();
        Log.d(TAG, "doGeoQuery: " + url);
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                Gson gson = new Gson();
                final String stuff = response.body().string();
                Log.d(TAG, "onResponse: " + stuff);

                switch (queryType){
                    case "latlng":
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                presenter.returnNetworkCallAddress(stuff);
                            }
                        });
                        break;
                    case "address":
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                presenter.returnNetworkCallLatLong(stuff);
                            }
                        });
                        break;
                }
            }
        });

    }

}
