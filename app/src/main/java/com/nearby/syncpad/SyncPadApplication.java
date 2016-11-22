package com.nearby.syncpad;

import android.app.Application;

import com.nearby.syncpad.components.DaggerMyApplicationComponent;
import com.nearby.syncpad.components.MyApplicationComponent;
import com.nearby.syncpad.modules.MyApplicationModule;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Sneha on 11/19/2016.
 */

public class SyncPadApplication extends Application{

    private MyApplicationComponent mMyApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/gothamblack.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        mMyApplicationComponent = DaggerMyApplicationComponent.builder()
                .myApplicationModule(new MyApplicationModule(this))
                .build();


    }

    public MyApplicationComponent getMyApplicationComponent() {
        return mMyApplicationComponent;
    }

}
