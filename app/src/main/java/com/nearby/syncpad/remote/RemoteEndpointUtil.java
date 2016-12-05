package com.nearby.syncpad.remote;

import android.util.Log;

import com.nearby.syncpad.util.GeneralUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RemoteEndpointUtil {
    private static final String TAG = "RemoteEndpointUtil";

    private RemoteEndpointUtil() {
    }

    public static JSONArray fetchJsonArray(String user_id) {
        String itemsJson = null;
        try {
            itemsJson = fetchPlainText(Config.BASE_URL, user_id);
        } catch (IOException e) {
            Log.e(TAG, "Error fetching items JSON", e);
            return null;
        }

        // Parse JSON
        try {
            JSONObject jsonObject = new JSONObject(itemsJson);
            return GeneralUtils.convertToArray(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "fetchJsonArray: Error fetching json object");
        }

        return null;
    }

    static String fetchPlainText(URL url, String user_id) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url + user_id + ".json")
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
