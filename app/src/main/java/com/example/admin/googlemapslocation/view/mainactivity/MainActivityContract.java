package com.example.admin.googlemapslocation.view.mainactivity;

import android.content.Context;

import com.example.admin.googlemapslocation.BasePresenter;
import com.example.admin.googlemapslocation.BaseView;

/**
 * Created by Admin on 8/24/2017.
 */

public interface MainActivityContract {

    interface View extends BaseView {
        void SetAddress(String s);
        void SetLat(String s);
        void SetLng(String s);

    }

    interface Presenter extends BasePresenter<View>{

        void setContext(Context context);
        void getGeocodeFromAddress(String s);
        void getGeocodeFromLatLng(String s, String q);
    }
}
