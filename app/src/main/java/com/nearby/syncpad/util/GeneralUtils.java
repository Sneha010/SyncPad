package com.nearby.syncpad.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.nearby.syncpad.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.Map;


public class GeneralUtils {

    public static String getProfileImageBytes(Context context, Bitmap photo) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);

    }

    public static boolean checkSDCard() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return false;
        } else {
            return false;
        }

    }

    public static Drawable getImageFromByteArray(String imageBytes) {

        byte[] bytes = Base64.decode(imageBytes, Base64.DEFAULT);

        return new BitmapDrawable(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

    }

    public Bitmap decodeSampledBitmapFromPath(String path, int reqWidth,
                                              int reqHeight) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            ExifInterface exif = new ExifInterface(
                    new File(path).getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 100);
            int rotate = 0;
            Matrix matrix = new Matrix();

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    rotate = 0;
                    break;
            /*case ExifInterface.ORIENTATION_NORMAL:
                rotate = -90;*/
            }
            matrix.postRotate(rotate);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            return null;
        }
    }
    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

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
