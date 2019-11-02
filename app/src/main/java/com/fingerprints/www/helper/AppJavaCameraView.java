package com.fingerprints.www.helper;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Mat;
import org.opencv.photo.CalibrateDebevec;
import org.opencv.photo.Photo;

import java.util.List;

public class AppJavaCameraView extends JavaCameraView implements Camera.AutoFocusCallback, Camera.PictureCallback {

    private String mPictureFileName;


    public AppJavaCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public List<Camera.Size> getResolutionList() {
        return this.mCamera.getParameters().getSupportedPreviewSizes();
    }

    public void setResolution(Size resolution) {
        this.disconnectCamera();
        this.connectCamera(resolution.width, resolution.height);
    }

    public void setFocusMode(Context ctx, int type) {


        try {
            mCamera = android.hardware.Camera.open();
        }catch (RuntimeException ex){}
// Here is your problem. Catching RuntimeException will make camera object null,
// so method 'getParameters();' won't work :)
        android.hardware.Camera.Parameters parameters;
        parameters = mCamera.getParameters();
        Camera.Parameters params = this.mCamera.getParameters();
        List<String> FocusModes = params.getSupportedFocusModes();
        switch(type) {
            case 0:
                if (FocusModes.contains("auto")) {
                    params.setFocusMode("auto");
                } else {
                    Toast.makeText(ctx, "Auto Mode not supported", Toast.LENGTH_LONG).show();
                }
                break;
            case 1:
                if (FocusModes.contains("continuous-video")) {
                    params.setFocusMode("continuous-video");
                } else {
                    Toast.makeText(ctx, "Continuous Mode not supported", Toast.LENGTH_LONG).show();
                }
                break;
            case 2:
                if (FocusModes.contains("edof")) {
                    params.setFocusMode("edof");
                } else {
                    Toast.makeText(ctx, "EDOF Mode not supported", Toast.LENGTH_LONG).show();
                }
                break;
            case 3:
                if (FocusModes.contains("fixed")) {
                    params.setFocusMode("fixed");
                } else {
                    Toast.makeText(ctx, "Fixed Mode not supported", Toast.LENGTH_LONG).show();
                }
                break;
            case 4:
                if (FocusModes.contains("infinity")) {
                    params.setFocusMode("infinity");
                } else {
                    Toast.makeText(ctx, "Infinity Mode not supported", Toast.LENGTH_LONG).show();
                }
                break;
            case 5:
                if (FocusModes.contains("macro")) {
                    params.setFocusMode("macro");
                } else {
                    Toast.makeText(ctx, "Macro Mode not supported", Toast.LENGTH_LONG).show();
                }
        }

        this.mCamera.setParameters(params);
    }

    public void setFlashMode(Context item, int type) {
        Parameters params = this.mCamera.getParameters();
        List<String> FlashModes = params.getSupportedFlashModes();
        switch(type) {
            case 0:
                if (FlashModes.contains("auto")) {
                    params.setFlashMode("auto");
                } else {
                    Toast.makeText(item, "Auto Mode not supported", Toast.LENGTH_LONG).show();
                }
                break;
            case 1:
                if (FlashModes.contains("off")) {
                    params.setFlashMode("off");
                } else {
                    Toast.makeText(item, "Off Mode not supported", Toast.LENGTH_LONG).show();
                }
                break;
            case 2:
                if (FlashModes.contains("on")) {
                    params.setFlashMode("on");
                } else {
                    Toast.makeText(item, "On Mode not supported", Toast.LENGTH_LONG).show();
                }
                break;
            case 3:
                if (FlashModes.contains("red-eye")) {
                    params.setFlashMode("red-eye");
                } else {
                    Toast.makeText(item, "Red Eye Mode not supported", Toast.LENGTH_LONG).show();
                }
                break;
            case 4:
                if (FlashModes.contains("torch")) {
                    params.setFlashMode("torch");
                } else {
                    Toast.makeText(item, "Torch Mode not supported", Toast.LENGTH_LONG).show();
                }
        }

        this.mCamera.setParameters(params);
    }

    public Size getResolution() {
        Parameters params = this.mCamera.getParameters();
        Size s = params.getPreviewSize();
        return s;
    }

    public void focusOnTouch(MotionEvent event) {
        Rect focusRect = calculateTapArea(event.getRawX(), event.getRawY(), 1f);
        Rect meteringRect = calculateTapArea(event.getRawX(), event.getRawY(), 1.5f);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        if (parameters.getMaxNumFocusAreas() > 0) {
            List

                    focusAreas = new ArrayList

                    ();
            focusAreas.add(new Camera.Area(focusRect, 1000));
            parameters.setFocusAreas(focusAreas);
        }
        if (parameters.getMaxNumMeteringAreas() > 0) {
            List

                    meteringAreas = new ArrayList

                    ();
            meteringAreas.add(new Camera.Area(meteringRect, 1000));
            parameters.setMeteringAreas(meteringAreas);
        }
        mCamera.setParameters(parameters);
        mCamera.autoFocus(this);
    }

    /**
     * Convert touch position x:y to {@link Camera.Area} position -1000:-1000 to 1000:1000.
     */
    private Rect calculateTapArea(float x, float y, float coefficient) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerX = (int) (x / getResolution().width - 1000);
        int centerY = (int) (y / getResolution().height - 1000);
        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);
        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {

    }


    public void takePicture(final String fileName) {
        Log.i("TAG", "Taking picture");
        this.mPictureFileName = fileName;
        // Postview and jpeg are sent in the same buffers if the queue is not empty when performing a capture.
        // Clear up buffers to avoid mCamera.takePicture to be stuck because of a memory issue
        mCamera.setPreviewCallback(null);

        mCamera.enableShutterSound(true);

        // PictureCallback is implemented by the current class
        mCamera.takePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.i("TAG", "Saving a bitmap to file");
        // The camera preview was automatically stopped. Start it again.
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);

        // Write the image in a file (in jpeg format)
        try {
            FileOutputStream fos = new FileOutputStream(mPictureFileName);
            fos.write(data);
            fos.close();
            Log.i("TAG", "data written");

        } catch (java.io.IOException e) {
            Log.e("TAG", "Exception in photoCallback", e);
        }

    }


}

