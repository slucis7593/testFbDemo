package com.vuduc.android.fblogindemo;

import android.app.Application;

import com.facebook.FacebookSdk;

/**
 * Created by vuduc on 8/28/15.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
