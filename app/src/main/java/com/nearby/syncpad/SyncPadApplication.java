package com.nearby.syncpad;

import android.app.Application;

import com.nearby.syncpad.components.DaggerMyApplicationComponent;
import com.nearby.syncpad.components.MyApplicationComponent;
import com.nearby.syncpad.modules.MyApplicationModule;

/**
 * Created by Sneha on 11/19/2016.
 */

public class SyncPadApplication extends Application{

    private MyApplicationComponent mMyApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mMyApplicationComponent = DaggerMyApplicationComponent.builder()
                .myApplicationModule(new MyApplicationModule(this))
                .build();


    }

    public MyApplicationComponent getMyApplicationComponent() {
        return mMyApplicationComponent;
    }

}
