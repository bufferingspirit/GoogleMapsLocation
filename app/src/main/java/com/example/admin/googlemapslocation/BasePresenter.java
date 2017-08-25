package com.example.admin.googlemapslocation;

/**
 * Created by Admin on 8/24/2017.
 */

public interface BasePresenter <V extends BaseView> {

    void attachView(V view);
    void dettachView();
}
