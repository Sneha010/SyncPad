package com.nearby.syncpad.modules;

import android.util.Log;

import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.ContentValues.TAG;

/**
 * Created by Sneha Khadatare on 11/20/2016.
 */

@Module
public class GoogleApiModule {


    @Provides
    @Singleton
    @Named("Scan")
    Strategy provideSubscribeStrategy() {
        return new Strategy.Builder()
                .setTtlSeconds(Strategy.TTL_SECONDS_MAX)
                .setDiscoveryMode(Strategy.DISCOVERY_MODE_SCAN)
                .setDistanceType(Strategy.DISTANCE_TYPE_EARSHOT).build();
    }

    @Provides
    @Singleton
    @Named("Publish")
    Strategy providePublishStrategy() {
        return new Strategy.Builder()
                .setTtlSeconds(Strategy.TTL_SECONDS_MAX)
                .setDiscoveryMode(Strategy.DISCOVERY_MODE_BROADCAST)
                .setDistanceType(Strategy.DISTANCE_TYPE_EARSHOT).build();
    }

    @Provides
    @Singleton
    SubscribeOptions provideSubscribeOptions(@Named("Scan") Strategy subscriptionStrategy) {
        return new SubscribeOptions.Builder()
                .setStrategy(subscriptionStrategy)
                .setCallback(new SubscribeCallback() {
                    @Override
                    public void onExpired() {
                        super.onExpired();
                        Log.i(TAG, "no longer subscribing");
                    }
                }).build();
    }

    @Provides
    @Singleton
    PublishOptions providePublishOptions(@Named("Publish") Strategy publishStrategy) {
        return new PublishOptions.Builder()
                .setStrategy(publishStrategy)
                .setCallback(new PublishCallback() {
                    @Override
                    public void onExpired() {
                        super.onExpired();
                        Log.i(TAG, "no longer publishing");
                    }
                }).build();
    }
}
