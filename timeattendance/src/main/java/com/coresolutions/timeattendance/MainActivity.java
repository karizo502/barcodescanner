package com.coresolutions.timeattendance;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.spdy.Header;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class MainActivity extends ActionBarActivity {
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        checkLocale();

        Button b1 = (Button) findViewById(R.id.button_start);
        b1.setOnClickListener(myhandler);
        TextView status = (TextView) findViewById(R.id.txt_status);
        if (checkSetting() == true) {
            if (checkOnline() == true) {
                intent = new Intent(this, CaptureActivity.class);
                startActivity(intent);
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
    View.OnClickListener myhandler = new View.OnClickListener() {
        public void onClick(View v) {
            //pb.setVisibility(View.VISIBLE);
            //new MyAsyncTask().execute("http://157.179.24.77/test.php");
            intent = new Intent(MainActivity.this, SimpleScannerActivity.class);
            startActivity(intent);
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        checkLocale();
        //checkState();

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
        SharedPreferences settings = getSharedPreferences("ConfigFile", 0);
        String state = settings.getString("state", "main");
        Toast.makeText(this, state, Toast.LENGTH_SHORT).show();
        if(state.equals("scanner")){
            intent = new Intent(this, CamTestActivity.class);
            startActivity(intent);
        }else if(state.equals("capture")){
            intent = new Intent(this, SimpleScannerActivity.class);
            startActivity(intent);
        }else{
            //intent = new Intent(this, MainActivity.class);
            //startActivity(intent);
        }
    }

    public static void restartActivity(Activity act){

        Intent intent=new Intent();
        intent.setClass(act, act.getClass());
        act.startActivity(intent);
        act.finish();

    }

}


