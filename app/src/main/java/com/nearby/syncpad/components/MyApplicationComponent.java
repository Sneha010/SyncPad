package com.nearby.syncpad.components;

import com.nearby.syncpad.ActiveMeetingActivity;
import com.nearby.syncpad.LoginActivity;
import com.nearby.syncpad.MeetingsSaveActivity;
import com.nearby.syncpad.MyProfileActivity;
import com.nearby.syncpad.fragments.ScanMeetingsDialogFragment;
import com.nearby.syncpad.modules.FirebaseModule;
import com.nearby.syncpad.modules.GoogleApiModule;
import com.nearby.syncpad.modules.MyApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Sneha on 11/19/2016.
 */

@Singleton
@Component(modules = {MyApplicationModule.class, FirebaseModule.class , GoogleApiModule.class})
public interface MyApplicationComponent {

    void inject(LoginActivity loginActivity);

    void inject(MyProfileActivity myProfileActivity);

    void inject(MeetingsSaveActivity meetingsSaveActivity);

    void inject(ActiveMeetingActivity activeMeetingActivity);

    void inject(ScanMeetingsDialogFragment scanMeetingsDialogFragment);
}
