package com.nearby.syncpad;

import android.support.v7.app.AppCompatActivity;

import butterknife.Unbinder;

/**
 * Created by Sneha Khadatare on 11/30/2016.
 */

public abstract class BaseActivity extends AppCompatActivity{

    protected Unbinder mUnbinder;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}
