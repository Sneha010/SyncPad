package com.nearby.syncpad.storedata;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.nearby.syncpad.models.Participant;

import javax.inject.Inject;


public class ProfileStore {


    private Application mContext;
    public static final String MY_PROFILE = "MY_PROFILE";
    public static final String IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH";

    @Inject
    public ProfileStore(Application context) {

        this.mContext =  context;
    }


    public void saveProfile(Participant participant) {

        Gson gson = new Gson();

        String profile = gson.toJson(participant, Participant.class);

        getSharedPreference().edit().putString(MY_PROFILE, profile).commit();


    }


    public Participant getMyProfile() {

        Gson gson = new Gson();

        String profileString = getSharedPreference().getString(MY_PROFILE, "");

        if (!TextUtils.isEmpty(profileString)) {

            return gson.fromJson(profileString, Participant.class);
        }

        return null;

    }

    public void clearProfileData(){

        getSharedPreference().edit().putString(MY_PROFILE, null).commit();
    }

    public void firstLaunchDone(boolean data){

        getSharedPreference().edit().putBoolean(IS_FIRST_LAUNCH, data).commit();
    }

    public boolean isFirstLaunchDone(){
        return getSharedPreference().getBoolean(IS_FIRST_LAUNCH, false);
    }


    private SharedPreferences getSharedPreference() {

        return mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
    }

}
