package com.nearby.syncpad.util;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.nearby.syncpad.R;

import java.util.List;
import java.util.Map;


public class MyUtil {

    public static void displayCustomToast(Context context, String message) {

        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View toastView = toast.getView(); //This'll return the default View of the Toast.

        TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);
        toastMessage.setTextSize(17);
        toastMessage.setPadding(35,10,35,10);
        toastMessage.setTextColor(ContextCompat.getColor(context , R.color.white));
        toastMessage.setGravity(Gravity.CENTER);
        toastView.setBackground(ContextCompat.getDrawable(context , R.drawable.toast_bg));
        toast.show();
    }

    public static void showSnackbar(View view ,String message){

        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    public static boolean isEmpty(Object obj) {
        boolean result = true;
        if (obj != null) {
            if (obj instanceof String) {

                if (obj.toString().trim().length() != 0
                        && !obj.toString().trim().equalsIgnoreCase("null"))
                    result = false;
            } else if (obj instanceof List) {
                if (((List) obj).size() > 0)
                    result = false;
            } else if (obj instanceof Map) {
                if (((Map) obj).size() > 0)
                    result = false;
            }
        }

        return result;

    }
}
