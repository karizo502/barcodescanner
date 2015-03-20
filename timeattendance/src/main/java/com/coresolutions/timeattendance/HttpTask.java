package com.coresolutions.timeattendance;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpTask extends AsyncTask<String, Integer, String> {

    @Override
    protected void onPreExecute() {
        // Create Show ProgressBar
    }

    protected String doInBackground(String... urls)   {
        String result = "";
        try {

            HttpGet httpGet = new HttpGet(urls[0]);
            HttpClient client = new DefaultHttpClient();

            HttpResponse response = client.execute(httpGet);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                InputStream inputStream = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader
                        (new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    result += line;
                }
            }

        } catch (ClientProtocolException e) {

        } catch (IOException e) {

        }
        return result;
    }

    protected void onPostExecute(String result)  {
        // Dismiss ProgressBar
        // updateWebView(result);
        Log.e("aaa",result);
    }

}