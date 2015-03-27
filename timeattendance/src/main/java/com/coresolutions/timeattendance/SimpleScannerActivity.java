package com.coresolutions.timeattendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class SimpleScannerActivity extends ActionBarActivity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;
    Intent intent;
    String[] data;
    String state;
    //private ProgressBar pb;

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
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        globalVariable.setState("scan");
        globalVariable.setEmpID(rawResult.getContents());
        new MyTask().execute(rawResult.getContents());
        //this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Toast.makeText(this, "555", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    private class MyTask extends AsyncTask<String, Integer, Double> {

        @Override
        protected Double doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0]);
            return null;
        }

        protected void onPostExecute(Double result) {
            //pb.setVisibility(View.GONE);
            //Toast.makeText(getApplicationContext(), ""+data[1]+" "+data[3], Toast.LENGTH_LONG).show();
            //status.setText(""+data[1]+" "+data[3]);
            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            globalVariable.setName(data[1] + " " + data[3]);
            SimpleScannerActivity.this.finish();
        }
        protected void onProgressUpdate(Integer... progress){
            //pb.setProgress(progress[0]);
        }

        public void postData(String valueIWantToSend) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://157.179.24.77/timeattendance/connect_base.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("action", "emp_profile"));
                nameValuePairs.add(new BasicNameValuePair("emp_id", valueIWantToSend));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                String actual = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
                Log.v("test", "Your scanner data: " + actual); //response data
                data = actual.split("[|]");
                Log.v("test", "Your NAME: " +data[1]+" "+data[3]); //response data

            } catch (ClientProtocolException e) {
                Log.v("test", "Error: " + e.getMessage());
            } catch (IOException e) {
                Log.v("test", "Error: " + e.getMessage());
            }
        }

    }
}
