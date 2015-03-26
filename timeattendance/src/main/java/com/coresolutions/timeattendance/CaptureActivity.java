package com.coresolutions.timeattendance;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import junit.framework.Test;

public class CaptureActivity extends Activity implements SurfaceHolder.Callback,Camera.ShutterCallback,Camera.PictureCallback{

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Button chk_in,chk_out;
    TextView countdownTextView;
    Handler timerUpdateHandler;
    boolean timerRunning = false;
    int currentTime = 4;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.camera);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView)findViewById(R.id.CameraView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        countdownTextView = (TextView) findViewById(R.id.CountDownTextView);
        countdownTextView.setText("");
        chk_in = (Button) findViewById(R.id.button_chk_in);
        chk_out = (Button) findViewById(R.id.button_chk_out);
        timerUpdateHandler = new Handler();

        SharedPreferences settings = getSharedPreferences("ConfigFile", 0);
        camera = Camera.open(settings.getInt("capture", 0));

        surfaceView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                camera.takePicture(CaptureActivity.this, null, null, CaptureActivity.this);
            }
        });

        chk_in.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!timerRunning) {
                    countdownTextView.setText("" + currentTime);
                    timerRunning = true;
                    timerUpdateHandler.post(timerUpdateTask);
                }
            }});
        chk_out.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!timerRunning) {
                    countdownTextView.setText("" + currentTime);
                    timerRunning = true;
                    timerUpdateHandler.post(timerUpdateTask);
                }
            }});
    }


    @Override
    public void onPause() {
        super.onPause();
        camera.stopPreview();
    }

    private Runnable timerUpdateTask = new Runnable() {
        public void run() {
            if (currentTime > 1) {
                currentTime--;
                timerUpdateHandler.postDelayed(timerUpdateTask, 1000);
            } else {
                camera.takePicture(null, null, CaptureActivity.this);
                timerRunning = false;
                currentTime = 4;
            }
            if(timerRunning){countdownTextView.setText("" + currentTime);}
            else{countdownTextView.setText("");}
        }
    };

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Uri imageFileUri = getContentResolver().insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        try {
          OutputStream imageFileOS = getContentResolver().openOutputStream(
                  imageFileUri);
          imageFileOS.write(data);
          imageFileOS.flush();
          imageFileOS.close();

          Toast t = Toast.makeText(this, "Saved JPEG!", Toast.LENGTH_SHORT);
          t.show();
            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            globalVariable.setState("capture");
            CaptureActivity.this.finish();

        } catch (Exception e) {
          Toast t = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
          t.show();
        }
        camera.startPreview();
      }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Camera.Parameters params = camera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        Camera.Size selected = sizes.get(0);
        params.setPreviewSize(selected.width,selected.height);
        camera.setParameters(params);

        camera.setDisplayOrientation(90);
        camera.startPreview();

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("PREVIEW", "surfaceDestroyed");
    }

    @Override
    public void onShutter() {
        Toast.makeText(this, "Click!", Toast.LENGTH_SHORT).show();
    }
    public void onCancelClick(View v) {
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        camera.release();
        Log.d("CAMERA", "Destroy");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Toast.makeText(this, "555", Toast.LENGTH_SHORT).show();
        this.finish();
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        globalVariable.setState("main");
    }

}
