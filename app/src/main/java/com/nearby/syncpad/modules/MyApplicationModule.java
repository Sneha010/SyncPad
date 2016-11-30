package com.nearby.syncpad.modules;

import android.app.Application;
import android.content.Context;
import android.os.PowerManager;

import com.nearby.syncpad.util.NotificationHelper;

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

    @Singleton
    @Provides
    PowerManager providePowerManager(Application application) {

        return (PowerManager) application.getSystemService(Context.POWER_SERVICE);

    }

    @Singleton
    @Provides
    PowerManager.WakeLock provideWakeLock(PowerManager powerManager) {

        return powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");

    }

    @Singleton
    @Provides
    NotificationHelper provideNotificationHelper(Application application) {

        return new NotificationHelper(application);
    }
}
