package com.coresolutions.timeattendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

/**
 * Created by Panupong on 18/3/2558.
 */
public class CaptureActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        SharedPreferences settings = getSharedPreferences("ConfigFile", 0);
        SharedPreferences.Editor editor;

        editor = settings.edit();
        editor.putString("state","capture");
        editor.commit();
        IntentLauncher launcher = new IntentLauncher();
        launcher.start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private class IntentLauncher extends Thread {

        @Override
        /**
         * Sleep for some time and than start new activity.
         */
        public void run() {
            try {
                // Sleeping
                Thread.sleep(10*1000);
            } catch (Exception e) {
                //Log.e(TAG, e.getMessage());
            }

            // Start main activity
            Intent intent = new Intent(CaptureActivity.this, MainActivity.class);
            CaptureActivity.this.startActivity(intent);
            CaptureActivity.this.finish();
        }
    }
}
