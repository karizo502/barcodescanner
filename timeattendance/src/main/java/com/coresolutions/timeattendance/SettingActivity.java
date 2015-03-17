package com.coresolutions.timeattendance;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.EditText;

/**
 * Created by Panupong on 17/3/2558.
 */
public class SettingActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        setTitle("Setting");
        final EditText ipaddress = (EditText)findViewById(R.id.txt_ipaddress);
        final EditText port = (EditText)findViewById(R.id.txt_port);

    }

}
