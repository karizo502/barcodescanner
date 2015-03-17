package com.coresolutions.timeattendance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;



public class MainActivity extends ActionBarActivity {
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(checkSetting()==true){
            intent = new Intent(this, SimpleScannerActivity.class);
            startActivity(intent);
        }else{
            intent = new Intent(this, SimpleScannerActivity.class);
            startActivity(intent);
        }

        //settings = this.getPreferences(MODE_PRIVATE);
        //editor = settings.edit();
        //int val = settings.getInt("flag", 0);
        //Toast.makeText(this, "flag = " + val, Toast.LENGTH_SHORT).show();
        //editor.putInt("flag", val+1);
        //editor.commit();
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
    public boolean checkSetting(){
        SharedPreferences settings = getSharedPreferences("ConfigFile", 0);
        String ipAddress = settings.getString("IPAddress", "");

        if(ipAddress==""){
            SharedPreferences.Editor editor;
            editor = settings.edit();
            editor.putInt("port", 80);
            editor.putString("language", "EN");
            editor.putInt("scanner", 0);      // 0 = back camera | 1 = front camera
            editor.putInt("capture", 0);  // 0 = back camera | 1 = front camera
            editor.commit();
            return false;
        }else{
            return true;
        }
    }
    public boolean checkOnline(){
        return true;
    }
}
