package com.fingerprints.www;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.fingerprints.www.helper.AppJavaCameraView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.lang.annotation.Native;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class MainActivity extends CameraActivity implements CvCameraViewListener2 , CameraBridgeViewBase.OnTouchListener{

    private static final String TAG = "TAG";

    private AppJavaCameraView mOpenCvCameraView;
    private Button takePictureBtn;

    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "success ");
                    mOpenCvCameraView.enableView();
                }break;
                default:{
                    super.onManagerConnected(status);
                }break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Called OnCreate");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.camera);

        mOpenCvCameraView = (AppJavaCameraView) findViewById(R.id.cameraCameraView);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCameraIndex(0);

        mOpenCvCameraView.setCvCameraViewListener(this);

        baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        mOpenCvCameraView.requestFocus();

        mOpenCvCameraView.requestFocusFromTouch();

        mOpenCvCameraView.setFocusable(true);

        mOpenCvCameraView.setFocusableInTouchMode(true);

        mOpenCvCameraView.setOnTouchListener(this);

        mOpenCvCameraView.setFocusMode(getApplicationContext(), 5);

        takePictureBtn = (Button) findViewById(R.id.cameraButtonTakePic);

        mOpenCvCameraView.setFlashMode(getApplicationContext(), 2);

        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Capture btn clicked");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                String currentDateandTime = sdf.format(new Date());
                String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+
                        currentDateandTime + ".jpg";
                mOpenCvCameraView.takePicture(fileName);
                Toast.makeText(getApplicationContext(), fileName + " saved", Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "OnPause Called");
        if(mOpenCvCameraView != null){
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "OnResume Called");
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, baseLoaderCallback);
        }else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "OnDestroyed Called");

        if(mOpenCvCameraView != null){
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }


    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return inputFrame.rgba();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mOpenCvCameraView.focusOnTouch(event);
        return true;
    }
}
