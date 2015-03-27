package com.coresolutions.timeattendance;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.util.Locale;



public class MainActivity extends ActionBarActivity {
    Intent intent;
    TextView status;
    String state;

    public String getState() {
        return state;
    }

    public void setState(String c_state) {
        this.state = c_state;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        globalVariable.setState("main");

        checkLocale();

        Button b0 = (Button) findViewById(R.id.button_start);

        b0.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        intent = new Intent(MainActivity.this, SimpleScannerActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
        //b0.setOnClickListener(myhandler0);

        Button b1 = (Button) findViewById(R.id.button_scan);
        b1.setOnClickListener(myhandler1);

        Button b2 = (Button) findViewById(R.id.button_camera);
        b2.setOnClickListener(myhandler2);

        Button b3 = (Button) findViewById(R.id.button_task);
        b3.setOnClickListener(myhandler3);

        status = (TextView) findViewById(R.id.txt_status);
        if (checkSetting() == true) {
            if (checkOnline() == true) {
                status.setText(R.string.status_online);
            } else {
                status.setText(R.string.status_offline);
            }
        } else {
            intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        }

        //settings = this.getPreferences(MODE_PRIVATE);
        //editor = settings.edit();
        //int val = settings.getInt("flag", 0);
        //Toast.makeText(this, "flag = " + val, Toast.LENGTH_SHORT).show();
        //editor.putInt("flag", val+1);
        //editor.commit();
    }
    View.OnClickListener myhandler0 = new View.OnClickListener() {
        public void onClick(View v) {
            intent = new Intent(MainActivity.this, SimpleScannerActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {
            intent = new Intent(MainActivity.this, SimpleScannerActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener myhandler2 = new View.OnClickListener() {
        public void onClick(View v) {
            intent = new Intent(MainActivity.this, CaptureActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener myhandler3 = new View.OnClickListener() {
        public void onClick(View v) {
            intent = new Intent(MainActivity.this, HttpTaskActivity.class);
            startActivity(intent);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        checkLocale();
        checkState();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean checkSetting() {
        SharedPreferences settings = getSharedPreferences("ConfigFile", 0);
        String ipAddress = settings.getString("ipaddress", "");
        if (ipAddress == "") {
            SharedPreferences.Editor editor;
            editor = settings.edit();
            editor.putString("ipaddress", "157.179.24.77");
            editor.putInt("port", 80);
            editor.putString("language", "en");
            editor.putInt("scanner", 0);      // 0 = back camera | 1 = front camera
            editor.putInt("capture", 0);  // 0 = back camera | 1 = front camera
            editor.commit();
            return false;
        } else {
            return true;
        }
    }

    public boolean checkOnline() {
        SharedPreferences settings = getSharedPreferences("ConfigFile", 0);
        String ipAddress = settings.getString("ipaddress", "");
        //status.setText("Your NAME: "+data[1]+" "+data[3]);
        return true;
    }

    public  void checkLocale(){
        //check locale
        SharedPreferences settings = getSharedPreferences("ConfigFile", 0);
        //Toast.makeText(this, Locale.getDefault().getLanguage(), Toast.LENGTH_SHORT).show();
        if (Locale.getDefault().getLanguage()== settings.getString("language", "en"))
        {
            //Toast.makeText(this, "same", Toast.LENGTH_SHORT).show();
            return ;
        }
        Locale locale = new Locale(settings.getString("language", "en"));
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        //Toast.makeText(this, "complete", Toast.LENGTH_SHORT).show();
        //restartActivity(this);
    }

    public  void checkState(){

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        //Toast.makeText(this, globalVariable.getState(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, globalVariable.getEmpID(), Toast.LENGTH_SHORT).show();
        if(globalVariable.getState().equals("scan")){
            intent = new Intent(getApplicationContext(), CaptureActivity.class);
            startActivity(intent);
        }else if(globalVariable.getState().equals("capture")){
            globalVariable.setState("main");
        }
    }

    public static void restartActivity(Activity act){

        Intent intent=new Intent();
        intent.setClass(act, act.getClass());
        act.startActivity(intent);
        act.finish();

    }


}


