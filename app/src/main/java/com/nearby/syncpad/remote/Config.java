package com.nearby.syncpad.remote;

import com.nearby.syncpad.util.Constants;

import java.net.MalformedURLException;
import java.net.URL;

public class Config {
    public static final URL BASE_URL;

    static {
        URL url = null;
        try {
            url = new URL(Constants.MEETINGS_BASE_URL);
        } catch (MalformedURLException ignored) {
            // TODO: throw a real error
        }

        BASE_URL = url;
    }
}
