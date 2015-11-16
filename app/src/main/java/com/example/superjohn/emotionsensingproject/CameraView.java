package com.example.superjohn.emotionsensingproject;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by superjohn on 15/7/2.
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback{

    private SurfaceHolder mHolder;
    private Camera mCamera;



    public CameraView(Context context, Camera camera){
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.setDisplayOrientation(90);
            setQuality();
            mCamera.startPreview();
        }catch(IOException e){
            Log.d("Camera view creation", "Error setting camera a preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder.getSurface() == null){
            return;
        }

        try{
            mCamera.stopPreview();
        }catch (Exception e){

        }

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e){
            Log.d("Camera view creation", "Error setting camera a preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void setQuality(){
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureFormat(android.graphics.PixelFormat.JPEG);
        List<Camera.Size> imageSizes = parameters.getSupportedPictureSizes();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        //parameters.setPreviewSize(854, 480);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        parameters.setPreviewSize(previewSizes.get(0).width, previewSizes.get(0).height);
        parameters.setPictureSize(imageSizes.get(0).width, imageSizes.get(0).height);
        parameters.setRotation(90);
        mCamera.setParameters(parameters);
    }
}
