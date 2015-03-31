package com.coresolutions.timeattendance;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import junit.framework.Test;

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

public class CaptureActivity extends Activity implements SurfaceHolder.Callback,Camera.ShutterCallback,Camera.PictureCallback{

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Button chk_in,chk_out;
    TextView countdownTextView;
    Handler timerUpdateHandler;
    boolean timerRunning = false;
    int currentTime = 4;
    String[] data;
    String datastring;
    Bitmap bitmap;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.camera);

        TextView txt_name = (TextView) findViewById(R.id.txt_name);
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        txt_name.setText(globalVariable.getName());

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
                //camera.takePicture(CaptureActivity.this, null, null, CaptureActivity.this);
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
    public void onResume() {
        super.onResume();
        camera.startPreview();
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
            //OutputStream imageFileOS = getContentResolver().openOutputStream(imageFileUri);
            //imageFileOS.write(data);
            //imageFileOS.flush();
            //imageFileOS.close();
            FileOutputStream outStream = null;
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath() + "/timeattendance");
            dir.mkdirs();

            File outFile = new File(dir, "tmp.png");

            outStream = new FileOutputStream(outFile);
            outStream.write(data);
            outStream.flush();
            outStream.close();

            String callname = "/sdcard/timeattendance/tmp.png";
            bitmap = BitmapFactory.decodeFile(callname);
            bitmap = ResizeBitmap(bitmap);
            bitmap = RotateBitmap(bitmap,-90);
            ByteArrayOutputStream baos=new  ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
            byte [] b=baos.toByteArray();
            datastring=Base64.encodeToString(b, Base64.DEFAULT);

            Toast.makeText(this, "Saved JPEG!", Toast.LENGTH_SHORT).show();
            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            globalVariable.setState("capture");
            String emp_id = globalVariable.getEmpID();
            //Toast.makeText(this, globalVariable.getEmpID().toString(), Toast.LENGTH_SHORT).show();
            new MyTask().execute(emp_id);

        } catch (Exception e) {
          Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            //new uploadImage().execute(data[1]);
            new UploadTask(data[1]).execute(bitmap);
            //CaptureActivity.this.finish();
        }
        protected void onProgressUpdate(Integer... progress){
            //pb.setProgress(progress[0]);
        }

        public void postData(String valueIWantToSend) {
            // Create a new HttpClient and Post Header
            SharedPreferences settings = getSharedPreferences("ConfigFile", 0);
            String ipAddress = settings.getString("ipaddress", "");
            int port = settings.getInt("port", 80);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://"+ipAddress+":"+port+"/timeattendance/connect_base.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("action", "time_attendance"));
                nameValuePairs.add(new BasicNameValuePair("emp_id", valueIWantToSend));
                nameValuePairs.add(new BasicNameValuePair("image", datastring));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                String actual = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
                Log.v("test", "Your capture data: " + actual); //response data
                data = actual.split("[|]");

            } catch (ClientProtocolException e) {
                Log.v("test", "Error: " + e.getMessage());
            } catch (IOException e) {
                Log.v("test", "Error: " + e.getMessage());
            }
        }

    }

    private class UploadTask extends AsyncTask<Bitmap, Void, Void> {
        String filename;
        UploadTask(String filename) {
            this.filename = filename;
        }

        protected Void doInBackground(Bitmap... bitmaps) {
            if (bitmaps[0] == null)
                return null;
            setProgress(0);

            Bitmap bitmap = bitmaps[0];
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // convert Bitmap to ByteArrayOutputStream
            InputStream in = new ByteArrayInputStream(stream.toByteArray()); // convert ByteArrayOutputStream to ByteArrayInputStream
            SharedPreferences settings = getSharedPreferences("ConfigFile", 0);
            String ipAddress = settings.getString("ipaddress", "");
            int port = settings.getInt("port", 80);
            DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
                HttpPost httppost = new HttpPost(
                        "http://"+ipAddress+":"+port+"/timeattendance/saveimage.php"); // server

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("myFile",filename+ ".jpg", in);
                //reqEntity.addPart("emp_id",emp_id,in);
                httppost.setEntity(reqEntity);

                Log.i("test", "request " + httppost.getRequestLine());
                HttpResponse response = null;
                try {
                    response = httpclient.execute(httppost);
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    if (response != null)
                        Log.i("test", "response " + response.getStatusLine().toString());
                } finally {

                }
            } finally {

            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            CaptureActivity.this.finish();
            //Toast.makeText(MainActivity.this, R.string.uploaded, Toast.LENGTH_LONG).show();
        }
    }


    public Bitmap ResizeBitmap(Bitmap source)
    {
        SharedPreferences settings = getSharedPreferences("ConfigFile", 0);
        int resolution = settings.getInt("resolution", 0);
        if(resolution==0) {
            return Bitmap.createScaledBitmap(source, 160, 120, false);
        }else{
            return Bitmap.createScaledBitmap(source, 320, 240, false);
        }
    }
    public Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

}
