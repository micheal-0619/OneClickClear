package com.axb.oneclickclear;

import android.app.Application;


/**
 * Created by Catherine on 2016/8/18.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class MainApplication extends Application {
    public static MainApplication INSTANCE;

    public static MainApplication getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        INSTANCE = this;
        //Branch.getAutoInstance(this);
        //AccountKit.initialize(this);
        super.onCreate();
    }

}
