package com.coresolutions.timeattendance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.NetworkOnMainThreadException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
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
                intent = new Intent(this, SimpleScannerActivity.class);
                startActivity(intent);
                status.setText("Online");
            } else {
                status.setText("Offline");
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

            try{ // Loading the MySQL Connector/J driver
                Class.forName("com.mysql.jdbc.Driver");
                System.out.println("com.mysql.jdbc.Driver");
            }catch(ClassNotFoundException e){
                System.out.println("Error while loading the Driver: " + e.getMessage());
            }
            System.out.println("MySQL Connect Example.");
            Connection conn = null;
            String url = "jdbc:mysql://157.179.24.77:3306/";
            String dbName = "game";
            String driver = "com.mysql.jdbc.Driver";
            String userName = "root";
            String password = "root";
            try {
                Class.forName(driver).newInstance();
                conn = DriverManager.getConnection(url+dbName,userName,password);
                Toast.makeText(getBaseContext(),
                        "Connected to the database.", Toast.LENGTH_LONG)
                        .show();
                conn.close();
                Toast.makeText(getBaseContext(),
                        "Disconnected form the database.", Toast.LENGTH_LONG)
                        .show();
            } catch (Exception e) {
                Toast.makeText(getBaseContext(),
                        "Exception e = " , Toast.LENGTH_LONG)
                        .show();
                e.printStackTrace();
            }
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
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }
    private boolean isOnTheInternet() {
        try {
            URLConnection urlConnection = new URL("http://157.179.24.77").openConnection();
            urlConnection.setConnectTimeout(400);
            urlConnection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isHostRechable(String hostUrl) {
        try {
            URL url = new URL("http://" + hostUrl);
            Toast.makeText(this, "ipaddress = " + url, Toast.LENGTH_SHORT).show();
            final HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            urlc.setRequestProperty("User-Agent", "Android Application");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(10 * 1000);
            urlc.connect();
            if (urlc.getResponseCode() == 200) {
                return true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isConnectedToServer(String url) {
        try {
            URL myUrl = new URL("http://" +url);
            //Toast.makeText(this, "ipaddress = " + url, Toast.LENGTH_SHORT).show();
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();
            return true;
        } catch (Exception e) {
            // Handle your exceptions
            return false;

        }
    }

    public static boolean isHostReachable(String serverAddress, int serverTCPport, int timeoutMS){
        boolean connected = false;
        String sentence = "TCP Test #1n";
        String modifiedSentence;
        try {

            Socket clientSocket = new Socket("192.168.18.116", 8080);
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new
                    InputStreamReader(clientSocket.getInputStream()));
            outToServer.writeBytes(sentence + 'n');
            connected = true;
            clientSocket.close();

        } catch (Exception e) {
           // printScr("TCP Error: " + e.toString());
        }

        return connected;
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
    }

    public  void checkState(){
        SharedPreferences settings = getSharedPreferences("ConfigFile", 0);
        String state = settings.getString("state", "main");
        if(state=="scanner"){
            intent = new Intent(this, CaptureActivity.class);
            startActivity(intent);
        }else if(state=="capture"){
            intent = new Intent(this, SimpleScannerActivity.class);
            startActivity(intent);
        }else{
            //intent = new Intent(this, MainActivity.class);
            //startActivity(intent);
        }
    }
}