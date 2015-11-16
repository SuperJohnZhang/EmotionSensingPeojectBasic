package com.example.superjohn.emotionsensingproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CaremaActivity extends Activity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static String imgPathName;
    /* old version
    private static Uri getOutputMediaFileUri(int type) {
        File file = getOutputMediaFile(type);
        return Uri.fromFile(getOutputMediaFile(type));
    }
    */
    private static File getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");

        if (! mediaStorageDir.exists()){
            Log.d("MyCameraApp", "Failed to create directory");
            return null;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            imgPathName = mediaStorageDir.getPath() + File.separator
                    + "IMG" + timeStamp + ".jpg";
            mediaFile = new File(imgPathName);
        }else{
            return null;
        }

        return mediaFile;
    }



    private Camera camera;
    private boolean isPreview = true;
    private CameraView mPreview;

    private Camera.PictureCallback jpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d("ImageSaving", "Error creating media file, check storage permission");
                return;
            }

            try{
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e){
                Log.d("Find file", "File not found: "+e.getMessage());
            } catch (IOException e) {
                Log.d("Access", "Error accessing file: "+e.getMessage());
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  // fullscreen
        setContentView(R.layout.activity_carema);


        /* old version
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        */

        // new version
        // detect camera hardware
        this.checkCameraHardware(this.getApplicationContext());                 // exist doubt
        camera = getCameraInstance();
        this.showCameraParameters();

        mPreview = new CameraView(this, camera);

        if (! Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(this, "please add sd card", Toast.LENGTH_SHORT).show();
        }

        // use framelayout
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        // release button
        Button release =  (Button) findViewById(R.id.release);
        release.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (camera != null){
                    camera.startPreview();
                    isPreview = true;
                }
            }
        });

        // takephoto button
        Button takePhoto = (Button) findViewById(R.id.takephoto);
        takePhoto.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View v) {
                if (camera != null && isPreview){
                    camera.takePicture(null , null, jpeg);
                    isPreview = false;
                }
            }
        });

        // setting button
        Button setConnect = (Button) findViewById(R.id.setting);
        setConnect.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                System.out.println("okokok");
                connectSetting(v);
            }
        });

    }

    protected void onPause(){
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera(){
        if (camera != null){
            camera.release();
            camera = null;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                Toast.makeText(this, "Image saved to:\n" +
                    data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED){

            } else {

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_carema, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // this is used to check the existence of camera
    private boolean checkCameraHardware(Context context){
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        }
        else {
            return true;
        }
    }

    // show camere number on console
    private void showCameraParameters (){
        int num = Camera.getNumberOfCameras();
        // show the number on console
    }

    // get camera instance
    public static Camera getCameraInstance(int cameraNumber){
        Camera c = null;
        try {
            c = Camera.open(cameraNumber);
        }catch (Exception e){
            e.printStackTrace();
        }
        return c;
    }

    // get default camera instance
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        }catch (Exception e){
            e.printStackTrace();
        }
        return c;
    }

    // open connnection setting
    public void connectSetting(View view){
        releaseCamera();
        Intent intent = new Intent(this, ConnectionSetting.class);
        this.startActivity(intent);
    }
}
