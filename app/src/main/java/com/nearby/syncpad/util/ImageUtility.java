package com.nearby.syncpad.util;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;

import static com.google.android.gms.internal.zzs.TAG;


public class ImageUtility {

    private static final int maxHeight = 700;
    private static final int maxWidth = 600;

    private Application mContext;

    @Inject
    public ImageUtility(Application context) {

        this.mContext =  context;
    }

    public String getProfileImageBytes(Bitmap photo) {

        int finalWidth,finalHeight;

        if (maxHeight > 0 && maxWidth > 0) {
            int width = photo.getWidth();
            int height = photo.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            finalWidth = maxWidth;
            finalHeight = maxHeight;

            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
        }

        Log.d(TAG, "getProfileImageBytes: " + finalHeight + " W:" + finalWidth);

        Bitmap scaled = Bitmap.createScaledBitmap(photo, finalWidth, finalHeight, true);

        Log.d(TAG, "getProfileImageBytes: " + photo.isRecycled());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);

    }


    public Bitmap getBitmapFromImageBytes(String imageByteString) {

        byte[] imageBytes = Base64.decode(imageByteString, Base64.DEFAULT);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);

        return bmp;

    }
    public Bitmap grabImage(String path) throws FileNotFoundException,
            IOException {
        Bitmap bitmap = null;
        bitmap = GeneralUtils.decodeSampledBitmapFromPath(path, 250, 400);
        return bitmap;
    }

    public String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String res = null;
        Cursor cursor = null;

        if (contentUri != null) {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = mContext.getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                res = cursor.getString(column_index);
            }
            if (cursor == null)
                return null;
            cursor.close();
        }
        return res;
    }

    public String getRealPathFromURIForKitkat(Uri contentUri) {
        String id = contentUri.getLastPathSegment().split(":")[1];
        final String[] imageColumns = {MediaStore.Images.Media.DATA};
        final String imageOrderBy = null;

        Uri uri = getUri();
        String selectedImagePath = "path";

        Cursor imageCursor = mContext.getContentResolver().query(uri, imageColumns, MediaStore.Images.Media._ID + "=" + id, null, imageOrderBy);

        if (imageCursor.moveToFirst()) {
            selectedImagePath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
        }

        return selectedImagePath;
    }

    // By using this method get the Uri of Internal/External Storage for Media
    private Uri getUri() {
        String state = Environment.getExternalStorageState();
        if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }



}
