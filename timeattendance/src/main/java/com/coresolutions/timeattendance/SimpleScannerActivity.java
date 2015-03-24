package com.coresolutions.timeattendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class SimpleScannerActivity extends ActionBarActivity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;
    Intent intent;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        SharedPreferences settings = getSharedPreferences("ConfigFile", 0);
        mScannerView.startCamera( settings.getInt("scanner", 0));
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Toast.makeText(this, "Contents1 = " + rawResult.getContents() +
                ", Format = " + rawResult.getBarcodeFormat().getName(), Toast.LENGTH_SHORT).show();
        //mScannerView.startCamera(1);
        //this.finish();
        //intent = new Intent(getApplicationContext(), CaptureActivity.class);
        //startActivity(intent);
        SharedPreferences settings = getSharedPreferences("ConfigFile", 0);
        SharedPreferences.Editor editor;

        editor = settings.edit();
        editor.putString("userid",rawResult.getContents().toString() );
        editor.putString("state","scanner" );
        editor.commit();
        this.finish();

    }
}
