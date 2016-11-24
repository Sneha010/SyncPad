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

    @Inject
    public ProfileStore(Application context) {

        this.mContext =  context;
    }


    public void saveProfile(Participant participant) {

        Gson gson = new Gson();

        String profile = gson.toJson(participant, Participant.class);

        getSharedPreference().edit().putString("MY_PROFILE", profile).commit();


    }


    public Participant getMyProfile() {

        Gson gson = new Gson();

        String profileString = getSharedPreference().getString("MY_PROFILE", "");

        if (!TextUtils.isEmpty(profileString)) {

            return gson.fromJson(profileString, Participant.class);
        }

        return null;

    }

    public void clearProfileData(){

        getSharedPreference().edit().putString("MY_PROFILE", null).commit();
    }

    private SharedPreferences getSharedPreference() {

        return mContext.getSharedPreferences("ProfileData", Context.MODE_PRIVATE);
    }

}
