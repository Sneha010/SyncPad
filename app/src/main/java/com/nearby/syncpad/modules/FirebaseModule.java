package com.nearby.syncpad.modules;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Sneha Khadatare on 11/20/2016.
 */

@Module
public class FirebaseModule {


    @Provides
    @Singleton
    DatabaseReference provideFirebaseDatabase(){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        return firebaseDatabase.getReference();
    }

    @Provides
    @Singleton
    FirebaseAuth provideFirebaseAuth(){
        return FirebaseAuth.getInstance();
    }
}
