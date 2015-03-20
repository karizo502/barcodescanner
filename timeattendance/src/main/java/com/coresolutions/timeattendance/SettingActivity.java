package com.coresolutions.timeattendance;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by Panupong on 17/3/2558.
 */
public class SettingActivity extends ActionBarActivity {
    EditText ipaddress,port;
    Spinner language,scanner,capture;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        setTitle("Setting");

        int spinnerPostion;
        SharedPreferences settings = getSharedPreferences("ConfigFile", 0);
        ipaddress = (EditText)findViewById(R.id.txt_ipaddress);
        ipaddress.setText(settings.getString("ipaddress", ""));

        port = (EditText)findViewById(R.id.txt_port);
        port.setText(String.valueOf(settings.getInt("port", 80)));

        language = (Spinner) findViewById(R.id.spn_language);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language.setAdapter(adapter);
        String tmp_language= getLanguageDesc(settings.getString("language", "en"));
        //Toast.makeText(this, "language = "+settings.getString("language", "en")+" | "+tmp_language, Toast.LENGTH_SHORT).show();
        if (!tmp_language.equals(null)) {
            spinnerPostion = adapter.getPosition(tmp_language);
            language.setSelection(spinnerPostion);
            spinnerPostion = 0;
        }

        scanner = (Spinner) findViewById(R.id.spn_scanner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.camera_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scanner.setAdapter(adapter1);
        String tmp_scanner= getCameraDesc(settings.getInt("scanner", 0));
        //Toast.makeText(this, "language = "+settings.getInt("scanner", 0)+" | "+tmp_scanner, Toast.LENGTH_SHORT).show();
        if (!tmp_scanner.equals(null)) {
            spinnerPostion = adapter1.getPosition(tmp_scanner);
            scanner.setSelection(spinnerPostion);
            spinnerPostion = 0;
        }

        capture = (Spinner) findViewById(R.id.spn_capture);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.camera_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        capture.setAdapter(adapter2);
        String tmp_capture= getCameraDesc(settings.getInt("capture", 0));
        if (!tmp_capture.equals(null)) {
            spinnerPostion = adapter2.getPosition(tmp_capture);
            capture.setSelection(spinnerPostion);
            spinnerPostion = 0;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.icon_bar_right, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            SharedPreferences settings = getSharedPreferences("ConfigFile", 0);
            SharedPreferences.Editor editor;

            editor = settings.edit();
            editor.putString("ipaddress", ipaddress.getText().toString());
            editor.putInt("port", Integer.valueOf(port.getText().toString()));
            editor.putString("language", getLanguageCode(language.getSelectedItem().toString()));
            //Toast.makeText(this, "scanner = "+getCameraType(scanner.getSelectedItem().toString()), Toast.LENGTH_SHORT).show();
            editor.putInt("scanner", getCameraType(scanner.getSelectedItem().toString()));;      // 0 = back camera | 1 = front camera
            editor.putInt("capture", getCameraType(capture.getSelectedItem().toString()));  // 0 = back camera | 1 = front camera
            editor.commit();
            this.finish();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public String getLanguageCode(String language){
        String[] language_array = getResources().getStringArray(R.array.language_array);
        if(language.equals(language_array[0])) {
            return "en";
        }else if(language.equals(language_array[1])) {
            return "th";
        }else{
            return "en";
        }

    }
    public String getLanguageDesc(String language){
        String[] language_array = getResources().getStringArray(R.array.language_array);
        if(language.equals("en")) {
            return language_array[0];
        }else if(language.equals("th")) {
            return language_array[1];
        }else{
            return language_array[0];
        }

    }
    public int getCameraType(String camera){
        String[] camera_array = getResources().getStringArray(R.array.camera_array);
        if(camera.equals(camera_array[0])) {
            return 0;
        }else if(camera.equals(camera_array[1])) {
            return 1;
        }else{
            return 0;
        }

    }
    public String getCameraDesc(int camera){
        String[] camera_array = getResources().getStringArray(R.array.camera_array);
        //Toast.makeText(this, "camera = "+camera+" | "+camera_array[0]+" | "+camera_array[1], Toast.LENGTH_SHORT).show();
        if(camera == 0) {
            return camera_array[0];
        }else if(camera == 1) {
            return camera_array[1];
        }else{
            return camera_array[0];
        }
    }

}
