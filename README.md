## Capstone project : SyncPad

It's a smart digital alternative that lets you write your notes and share across the all meeting participants.
It uses Google's Nearby Message solution that combines meeting notes captured by all the members in the meeting.
Easy to use and allows cloud syncing that lets you access your meeting notes anytime anywhere.
With SyncPad, help your meetings to become more productive and effective.

Save paper, Go digital with SyncPad!

#### ScreenShots

![Login Screen](/screenshots/LoginScreen.png?raw=true "Login Screen")
![Profile Screen](/screenshots/ProfileScreen.png?raw=true "Profile Screen")
![Landing Screen](/screenshots/LandingScreen.png?raw=true "Landing Screen")
![Add Meeting Dialog](/screenshots/AddMeetingDialog.png?raw=true "Add Meeting Dialog")
![Active Participants Screen](/screenshots/ActiveParticipantsScreen.png?raw=true "Active Participants Screen")
![Meeting Save](/screenshots/MeetingSave.png?raw=true "Meeting Save")


#### Tech

This App uses following open source libraries to make it work and also makes life easy:

* [Butterknife] - To bind the views with annotations. Forget findViewById lines with this!
* [Calligraphy] - Helps to decorate the entire app with custom font in a just few lines of code.
* [Clans FAB] - To display animated Floating Action Menu.
* [Google GSON] - To convert Json to java POJO's or vice versa.
* [Dependency Injection with Dagger 2] - Manages dependencies and removes boilerplate.
* [Firebase Auth and Database] - To authenticate user and to saves their meeting notes.
* [InstantRuntimePermission] - My library to add run-time Marshmallow permissions with a single line of code.
* Delightful Material Design Support, FAB, RecyclerView Cards and CoordinatorLayout.

### Features

1. Sign-up with SyncPad

  User can login and write the notes, all meeting notes will be saved safely in the his SyncPad account.

2. Write and Exchange

  User can write his notes and also can exchange among the members participated in the meeting or
  discussion having this app. Also, at the end of the meeting, SyncPad combines the notes from
  all participants and forms the single note/MoM which will be available to all participants.

3. View meeting info and notes anytime, anywhere

  User can access his notes and meeting agenda information anytime offline.

4. Seemless UI and Material Design overloaded

  App has bunch of material design elements with attractive UI.



### Build and Run Requirements

* Oracle JDK 1.8
* Support Android 4.1 and Above (API 16)


### Tools used to develop
* Android Studio 2.2


[Butterknife]: <http://jakewharton.github.io/butterknife/>
[Calligraphy]: <https://github.com/chrisjenx/Calligraphy>
[Clans FAB]: <https://github.com/Clans/FloatingActionButton>
[Google GSON]: <https://github.com/google/gson>
[Dependency Injection with Dagger 2]: <https://github.com/codepath/android_guides/wiki/Dependency-Injection-with-Dagger-2>
[Firebase Auth and Database]: <https://firebase.google.com/docs/android/setup>
[InstantRuntimePermission]: <https://github.com/Sneha010/InstantRuntimePermissions>



### License

```
Copyright 2016 Sneha Khadatare

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

