package com.fingerprints.www;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

public class SplashActivity extends Activity {

    // region Public Methods

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        navigateToCameraActivity();
    }

    // endregion Public Methods

    // region Private Methods

    /**
     * Navigate to the Camera activity.
     */
    private void navigateToCameraActivity() {
        /* New Handler to start the Camera Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                Permissions.check(getApplicationContext()/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        // do your task.
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        SplashActivity.this.startActivity(intent);
                        SplashActivity.this.finish();
                    }
                });
                // navigate to Camera activity
            }
        }, 1000);
    }

    // endregion Private Methods
}
