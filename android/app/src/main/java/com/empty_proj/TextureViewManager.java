package com.empty_proj;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.CamcorderProfile;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.react.ReactActivity;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.PermissionListener;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import static android.app.PendingIntent.getActivity;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class TextureViewManager extends SimpleViewManager<TextureView> implements LifecycleEventListener{


    public static final String REACT_CLASS ="TextureView";
    public ThemedReactContext themedReactContext;
    public TextureView mTextureView;
    public Boolean isTextureViewCreated =  false;
    public CustomModule mCustomModule;

    public TextureViewManager(CustomModule customModule){
        mCustomModule = customModule;
    }

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @NonNull
    @Override
    public TextureView createViewInstance(@NonNull ThemedReactContext reactContext) {
        themedReactContext = reactContext;
        isTextureViewCreated = true;
        //Toast.makeText(reactContext,"TextureViewCreated",Toast.LENGTH_SHORT).show();
        mTextureView = new TextureView(themedReactContext);
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        return mTextureView;
    }

    public TextureView getTextureView() {
        return mTextureView;
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            //Toast.makeText(themedReactContext,"TextureView Surface Texture Available",Toast.LENGTH_LONG).show();
            Log.i(REACT_CLASS,"EnteredSurfaceTextureListener");
            mCustomModule.mySuperDuperFunction(mTextureView);
//            setupCamera(width, height);
//            connectCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

        }
    };


    @Override
    public void onHostResume() {
        if(mTextureView.isAvailable()){
            mCustomModule.mySuperDuperFunction(mTextureView);
        }
        else{
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {

    }

//    @ReactProp(name = "onCreated")
//    public void onCreated(TextureView view){
//        view.
//    }

}
