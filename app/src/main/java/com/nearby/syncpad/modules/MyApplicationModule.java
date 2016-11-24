package com.nearby.syncpad.modules;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HP on 11/19/2016.
 */


@Module
public class MyApplicationModule {

    private Application mApplication;

    public MyApplicationModule(Application mApplication) {
        this.mApplication = mApplication;
    }

    @Provides
    @Singleton
    Application provideApplication(){
        return mApplication;
    }
}
