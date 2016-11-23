package com.nearby.syncpad.storedata;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;


public class ProfileStore {


    public static final String USER_NAME_KEY = "user_name";
    public static final String USER_ORG_KEY = "user_org";
    public static final String USER_ROLE_KEY = "user_role";
    public static final String USER_EMAIL_KEY = "user_email";
    public static final String USER_IMAGE_BYTE = "user_image";
    public static final String USER_IMAGE_PATH = "user_image_path";


    private static SharedPreferences getMyPreferences(Context context) {
        return context.getSharedPreferences("profile", Context.MODE_PRIVATE);

    }


    public static void saveUserName(Context context, String name) {
        getMyPreferences(context).edit().putString(USER_NAME_KEY, name).commit();
    }

    public static void saveUserOrganisation(Context context, String org) {
        getMyPreferences(context).edit().putString(USER_ORG_KEY, org).commit();
    }

    public static void saveUserRole(Context context, String role) {
        getMyPreferences(context).edit().putString(USER_ROLE_KEY, role).commit();

    }

    public static void  saveEmailKey(Context context, String email) {
        getMyPreferences(context).edit().putString(USER_EMAIL_KEY, email).commit();
    }

    public static void  saveImagePath(Context context, String image_path) {
        getMyPreferences(context).edit().putString(USER_IMAGE_PATH, image_path).commit();
    }

    public static String getUserName(Context context) {

        return getMyPreferences(context).getString(USER_NAME_KEY, "");

    }
    public static String getUserOrgnisation(Context context) {

        return getMyPreferences(context).getString(USER_ORG_KEY, "");

    }

    public static String getEmailAddress(Context context) {

        return getMyPreferences(context).getString(USER_EMAIL_KEY, "");

    }


    public static String getUserRole(Context context) {
        return getMyPreferences(context).getString(USER_ROLE_KEY, "");
    }

    public static String getImagePath(Context context) {
        return getMyPreferences(context).getString(USER_IMAGE_PATH, "");
    }


    public static boolean isProfilePending(Context context) {
        return (TextUtils.isEmpty(getUserName(context)) ||
                TextUtils.isEmpty(getEmailAddress(context)) ||
                TextUtils.isEmpty(getUserRole(context)));


    }




}
