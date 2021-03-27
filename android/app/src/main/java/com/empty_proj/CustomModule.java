package com.empty_proj;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import android.media.MediaActionSound;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.PermissionListener;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

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
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CustomModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
    private static ReactApplicationContext reactContext;
    private static String Tag = "CustomModule";
    private static String REACT_CLASS = "NativeCameraModule";
    public TextureView mTextureView;
    public TextureViewManager mtvmInstance;



    CustomModule(ReactApplicationContext context){
        super(context);
        reactContext = context;
        mMediaRecorder = new MediaRecorder();
    }

    private static int count = 0;
    public void mySuperDuperFunction(TextureView view) {
            mTextureView = view;
            mMediaRecorder=new MediaRecorder();
            Toast.makeText(reactContext,"In Super Duper Function",Toast.LENGTH_LONG).show();
            createImageFolder();
            try{
            createVideoFolder();
            Toast.makeText(reactContext,"Folder Created",Toast.LENGTH_LONG).show();
            }catch(Exception e){
                Toast.makeText(reactContext, e.getMessage()+"Folder not created" ,Toast.LENGTH_LONG).show();
            }
            startBackgroundThread();
            startBackgroundThread2();
            if (mTextureView.isAvailable()) {
                setupCamera(mTextureView.getWidth(), mTextureView.getHeight());
                connectCamera();
                count = count + 1;
            }

            else {

               // Toast.makeText(reactContext,"TextureView Surface Texture Not Available",Toast.LENGTH_LONG).show();
                Log.i(REACT_CLASS,"TextureView Surface Texture Not Available");
                mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);

            }
        //Toast.makeText(reactContext, "Created", Toast.LENGTH_LONG).show();
    }

    @ReactMethod
    public void show(){
        Toast.makeText(reactContext, "Hi From Android", Toast.LENGTH_LONG).show();
    }



    @NonNull
    @Override
    public String getName() {
        Log.i("CustomModule", "getName: Entered");
        return REACT_CLASS;
    }


    private void createImageFolder(){
        File imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        mImageFolder = new File(imageFile, "FramesImages");
        if(!mImageFolder.exists()){
            mImageFolder.mkdirs();
        }
    }


    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
           // Toast.makeText(reactContext,"TextureView Surface Texture Available",Toast.LENGTH_LONG).show();
            Log.i(REACT_CLASS,"EnteredSurfaceTextureListener");
            setupCamera(width, height);
            connectCamera();
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

    public void checkWriteStoragePermission(){
        //Toast.makeText(MainActivity.getmInstanceActivity(), "Entered Permission Check", Toast.LENGTH_SHORT).show();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(MainActivity.getmInstanceActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                mIsRecording = true;
                try {
                    Toast.makeText(reactContext, "Entered CheckWrite Try", Toast.LENGTH_SHORT).show();
                    createVideoFileName();
                    Log.i(REACT_CLASS,"Permission For Storage Granted and File Created");
                    Toast.makeText(reactContext,"Permission For Storage Granted and File Created", Toast.LENGTH_SHORT).show();
                } catch (IOException e){
                    // Toast.makeText(reactContext,"Permission For Storage Granted Error", Toast.LENGTH_SHORT).show();
                    // if(MainActivity.getmInstanceActivity().shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    //     Toast.makeText(MainActivity.getmInstanceActivity(), "app needs to be able to save videos", Toast.LENGTH_SHORT).show();
                    // }
    
                    // MainActivity.getmInstanceActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT);
                    // Log.i(REACT_CLASS,"Permission For Storage Asked");
                    // Toast.makeText(reactContext, "Request Permission asked", Toast.LENGTH_SHORT).show();
                    Toast.makeText(reactContext, e.getMessage()+"Catch 1", Toast.LENGTH_SHORT).show();
                    //e.printStackTrace();
                }
                try{
                startRecord();
                }catch (Exception e){
                    Toast.makeText(reactContext, e.getMessage()+"Catch 2", Toast.LENGTH_SHORT).show();
                    //e.printStackTrace();
                }
                try{
                    mMediaRecorder.start();
                }catch (Exception e){
                    Toast.makeText(reactContext, e.getMessage()+"Catch 3", Toast.LENGTH_SHORT).show();
                    // e.printStackTrace();
                }
                
                //.makeText(MainActivity.getmInstanceActivity(), "RECORD Started", Toast.LENGTH_SHORT).show();
            } else {
                if(MainActivity.getmInstanceActivity().shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(MainActivity.getmInstanceActivity(), "app needs to be able to save videos", Toast.LENGTH_SHORT).show();
                }

                MainActivity.getmInstanceActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT);
                Log.i(REACT_CLASS,"Permission For Storage Asked");
                Toast.makeText(reactContext, "Request Permission asked", Toast.LENGTH_SHORT).show();

            }

        }else{
            mIsRecording = true;
            Toast.makeText(reactContext,"Permission For Storage Granted Else Toast", Toast.LENGTH_SHORT).show();
            try {
                createVideoFileName();
            } catch (IOException e){
                e.printStackTrace();
            }
            startRecord();
            mMediaRecorder.start();
            //.makeText(MainActivity.getmInstanceActivity(), "RECORD Started", Toast.LENGTH_SHORT).show();

        }
    }



    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 0;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT = 1;
    private void startRecord(){
       Toast.makeText(reactContext, "Entered Start Record1", Toast.LENGTH_SHORT).show();
        try {
           Toast.makeText(reactContext, "Entered Start Record2", Toast.LENGTH_SHORT).show();
        //    fileOutputStreamRecordImageBuffer = new FileOutputStream(mVideoFileName);
            setupMediaRecorder();
            //setupCodec();
            Log.i(REACT_CLASS,"MediaRecorderSetup");
            SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            Surface recordSurface = mMediaRecorder.getSurface();
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            Log.i(REACT_CLASS,"ZSL Setup");
            //m2CaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            mCaptureRequestBuilder.addTarget(previewSurface);
            //mCaptureRequestBuilder.addTarget(mImageReader2.getSurface());
            mCaptureRequestBuilder.addTarget(recordSurface);
            //m2CaptureRequestBuilder.addTarget(previewSurface);
            //mCameraDevice.createCaptureSession(Arrays.asList(previewSurface, recordSurface, mImageReader.getSurface(), mImageReader2.getSurface()),
            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface,recordSurface,mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            mRecordCaptureSession = session;
                            try {
                                //mRecordCaptureSession.capture(m2CaptureRequestBuilder.build(), null, mBackgroundHandler);
                                mRecordCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, mBackgroundHandler);
                                //mRecordCaptureSession.capture(m2CaptureRequestBuilder.build(), null, mBackgroundHandler);
                            } catch (CameraAccessException e){
                                Toast.makeText(reactContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }


                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                        }
                    }, mBackgroundHandler2);
        } catch (Exception e){
            Toast.makeText(reactContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            // e.printStackTrace();
        }


    }


    private File createVideoFileName() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String prepend = "VIDEO_" +timestamp+"_";
        File videoFile = File.createTempFile(prepend, ".mp4", mVideoFolder);
        mVideoFileName = videoFile.getAbsolutePath();
        Toast.makeText(MainActivity.getmInstanceActivity(), "File name created", Toast.LENGTH_SHORT).show();
        return videoFile;
    }
    private void createVideoFolder(){
        Toast.makeText(reactContext,"In Video Folder",Toast.LENGTH_SHORT).show();
        File movieFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        mVideoFolder = new File(movieFile, "Frames");
        if(!mVideoFolder.exists()){
            Toast.makeText(reactContext,"Creating Frames Directory",Toast.LENGTH_SHORT).show();
            mVideoFolder.mkdirs();
        }
        else{
            Toast.makeText(reactContext,"Frames Directory not created",Toast.LENGTH_SHORT).show();
        }
    }

    private File mVideoFolder;
    public String mVideoFileName;
    public MediaRecorder mMediaRecorder;
    private void setupMediaRecorder() throws IOException{
        //Log.i(Tag,"Entered MediaRecorder");
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        //mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_PERFORMANCE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        //mMediaRecorder.setAudioSamplingRate(44100);
       // mMediaRecorder.setAudioEncodingBitRate(320000);
        //Log.i(Tag,"Entered QUALITY HIGH");
        //mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mMediaRecorder.setOutputFile(mVideoFileName);
        mMediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);
        mMediaRecorder.setVideoFrameRate(profile.videoFrameRate);
        //mMediaRecorder.setCaptureRate(60);
        //Log.i(Tag,"Entered FrameRate"+profile.videoFrameRate);
        //mMediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
        mMediaRecorder.setVideoSize((int)(profile.videoFrameHeight*videoratio), profile.videoFrameHeight);
        //mMediaRecorder.setVideoSize(profile.videoFrameWidth, (int)(profile.videoFrameWidth*videoratio));
        //Log.i(Tag,"ProfileFrameWidth:"+(int)(profile.videoFrameHeight*videoratio)+"ProfileFrameHeight:"+profile.videoFrameHeight);
        //mMediaRecorder.setVideoSize(mTextureView.getWidth(), mTextureView.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.HEVC);
        //mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setOrientationHint(mtotalRotation);
        //Log.i(Tag,"Entered Orientation");
        mMediaRecorder.prepare();
       Toast.makeText(reactContext,"MediaRecorderSetup",Toast.LENGTH_SHORT).show();
        //Log.i(Tag,"Prepared");

    }


    @Override
    public void onHostResume() {
        startBackgroundThread();
        startBackgroundThread2();
        if (mTextureView.isAvailable()){
            Toast.makeText(reactContext,"TextureView Surface Texture Available",Toast.LENGTH_LONG).show();
            setupCamera(mTextureView.getWidth(), mTextureView.getHeight());
            connectCamera();

        }else {

            Toast.makeText(reactContext,"TextureView Surface Texture Not Available",Toast.LENGTH_LONG).show();
            Log.i(REACT_CLASS,"TextureView Surface Texture Not Available");
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);

        }
    }
    private void closeCamera(){
        if(mCameraDevice != null){
            mCameraDevice.close();
            mCameraDevice = null;
            Log.i(REACT_CLASS,"Camera Closed");
        }
    }

    @Override
    public void onHostPause() {
        stopBackgroundThread();
        stopBackgroundThread2();
        closeCamera();
    }

    @Override
    public void onHostDestroy() {

    }


    private HandlerThread mBackgroundHandlerThread;
    private Handler mBackgroundHandler;
    private Handler mBackgroundHandler2;
    private HandlerThread mBackgroundHandlerThread2;

    private void startBackgroundThread(){
        mBackgroundHandlerThread = new HandlerThread("CameraNativeModule");
        mBackgroundHandlerThread.start();
        mBackgroundHandler = new Handler(mBackgroundHandlerThread.getLooper());
    }

    private void startBackgroundThread2(){
        mBackgroundHandlerThread2 = new HandlerThread("CameraNativeModule");
        mBackgroundHandlerThread2.start();
        mBackgroundHandler2 = new Handler(mBackgroundHandlerThread2.getLooper());
    }
    private void stopBackgroundThread(){
        mBackgroundHandlerThread.quitSafely();
        try {
            mBackgroundHandlerThread.join();
            mBackgroundHandlerThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void stopBackgroundThread2(){
        mBackgroundHandlerThread2.quitSafely();
        try {
            mBackgroundHandlerThread2.join();
            mBackgroundHandlerThread2 = null;
            mBackgroundHandler2 = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String mCameraId;
    private int mtotalRotation;
    private Size mPreviewSize;
    private Size mVideoSize;
    private Size mImageSize;
    private ImageReader mImageReader;
    private static double videoratio = 4.0/3;
    private static SparseIntArray ORIENTATIONS = new SparseIntArray();

    private static class CompareSizeByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) (lhs.getWidth() * lhs.getHeight()) - (long) (rhs.getWidth() * rhs.getHeight()));

        }

    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height) {
        List<Size> bigEnough = new ArrayList<Size>();
        for (Size option : choices) {
            Log.i(REACT_CLASS,"Preview, Height:"+option.getHeight() + "Width:" + option.getWidth());
            if ((option.getHeight() == option.getWidth() * height / width) && (option.getWidth() >= width) && (option.getHeight() >= height) ) {
                bigEnough.add(option);
            }
        }
        if (bigEnough.size() > 0) {
            Toast.makeText(reactContext,"Bigger "+ Collections.max(bigEnough, new CompareSizeByArea()),Toast.LENGTH_SHORT).show();
            return Collections.max(bigEnough, new CompareSizeByArea());
        } else {
            Toast.makeText(reactContext,"Smaller",Toast.LENGTH_SHORT).show();
            return choices[0];
        }
    }

    private static int sensorToDeviceRotation(CameraCharacteristics cameraCharacteristics, int deviceOrientation) {
        int sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        deviceOrientation = ORIENTATIONS.get(deviceOrientation);
        return ((sensorOrientation + deviceOrientation + 360) % 360);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupCamera(int width, int height) {
        CameraManager cameraManager = (CameraManager) reactContext.getSystemService(Context.CAMERA_SERVICE);
        //mWindowManager = (WindowManager) reactContext.getSystemService(Context.WINDOW_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
                mCameraId = cameraId;
                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                //ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
                //WindowManager mWindowManager = (WindowManager) reactContext.getSystemService(Context.WINDOW_SERVICE);

                int deviceOrientation = MainActivity.getmInstanceActivity().getWindowManager().getDefaultDisplay().getRotation();
                mtotalRotation = sensorToDeviceRotation(cameraCharacteristics, deviceOrientation);
                boolean swapRotation = mtotalRotation == 90 || mtotalRotation == 270;
                int rotatedWidth = width;
                int rotatedHeight = height;
                if (swapRotation) {
                    Log.i(REACT_CLASS,"mTotalRotation is 90 or 270");
                    rotatedWidth = height;
                    rotatedHeight = width;
                    Log.i(REACT_CLASS,"RotatedHeight:"+rotatedHeight+"   RotatedWidth:"+rotatedWidth);
                }
                CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);
                mVideoSize = chooseOptimalSize(map.getOutputSizes(MediaRecorder.class), rotatedWidth, rotatedHeight);
                //mImageSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);
                mImageSize = chooseOptimalSize(map.getOutputSizes(ImageFormat.JPEG), rotatedWidth, rotatedHeight);
                mImageReader = ImageReader.newInstance(mImageSize.getWidth(), mImageSize.getHeight(), ImageFormat.JPEG,1);
                //mImageReader2 = ImageReader.newInstance((int)videoratio*(profile.videoFrameHeight), profile.videoFrameHeight, ImageFormat.YUV_420_888,2);
                videoratio = (double)mImageSize.getWidth()/mImageSize.getHeight();
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListner, mBackgroundHandler);
                //mImageReader2.setOnImageAvailableListener(mOnImageGetPreviewListener, mBackgroundHandler);
                //fpsRanges = map.getHighSpeedVideoFpsRanges();
                Log.i(REACT_CLASS,"PreviewSize"+ mPreviewSize);
                Toast.makeText(reactContext,"RotatedHeight:"+rotatedHeight+"   RotatedWidth:"+rotatedWidth,Toast.LENGTH_LONG).show();

            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListner = new
            ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    mBackgroundHandler.post(new ImageSaver(reader.acquireLatestImage()));

                }
            };

    private class ImageSaver implements Runnable{

        private final Image mImage;
        public ImageSaver(Image image){
            mImage = image;
        }

        @Override
        public void run() {
            ByteBuffer byteBuffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(mImageFileName);
                fileOutputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                Log.i(REACT_CLASS,"Image Close Started");
//                Intent mediaStoreUpdateIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                mediaStoreUpdateIntent.setData(Uri.fromFile(new File(mImageFileName)));
//                sendBroadcast(mediaStoreUpdateIntent);
                if(fileOutputStream != null){
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.i(REACT_CLASS,"Image Capture Closed");
            }

        }
    }


    private CaptureRequest.Builder mCaptureRequestBuilder;
    private CameraCaptureSession mPreviewCaptureSession;
    private static Range<Integer>[] fpsRanges;
    private int maxFps = 0;
    public void startPreview() {
        SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface previewSurface = new Surface(surfaceTexture);
        CameraManager cameraManager = (CameraManager) reactContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(mCameraId);
            try {
                mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                fpsRanges = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
                Log.i(REACT_CLASS, "SYNC_MAX_LATENCY_PER_FRAME_CONTROL: " + Arrays.toString(fpsRanges));
                Log.i(REACT_CLASS,"FPS_RANGE_TYPE"+fpsRanges[0].getClass());

                int index = 0;
                int MaxLower = 0 ,MaxHigher = 0;
                //int MaxHigher = 0 ;
                for ( int i=0; i<fpsRanges.length; i++ ){
                    int fpsLower = fpsRanges[i].getLower();
                    int fpsHigher = fpsRanges[i].getUpper();
                    if ((fpsLower > MaxLower) && (fpsHigher > MaxHigher) ){
                        MaxLower = fpsLower;
                        MaxHigher = fpsHigher;
                        index = i;
                    }
                    //Log.i(Tag,"lowerValueFps"+fps2 +":"+i);
                }
                Toast.makeText(reactContext,"MAXFPS Index:" + index + " MinValue:" + MaxLower + " MaxValue:" + MaxHigher, Toast.LENGTH_SHORT).show();
                //Log.i(REACT_CLASS,"MAXFPS Index:" + index + " MinValue:" + MaxLower + " MaxValue:" + MaxHigher );
                mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE,fpsRanges[index]);
                maxFps = fpsRanges[index].getUpper();
                mCaptureRequestBuilder.addTarget(previewSurface);
                Log.i(REACT_CLASS,"PreviewSurfaceAdded");
                mCaptureRequestBuilder.setTag("START_PREVIEW_TAG");
                // mCaptureRequestBuilder.setPhysicalCameraKey();

//                m2CaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
//                m2CaptureRequestBuilder.set(CaptureRequest.JPEG_THUMBNAIL_QUALITY, (byte) 100);
//                m2CaptureRequestBuilder.addTarget(mImageReader.getSurface());
//                m2CaptureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, mtotalRotation);
//                //m2CaptureRequestBuilder.set(CaptureRequest.CONTROL_SCENE_MODE,CaptureRequest.CONTROL_SCENE_MODE_ACTION);
//                //m2CaptureRequestBuilder.set(CaptureRequest.CONTROL_CAPTURE_INTENT, CaptureRequest.CONTROL_CAPTURE_INTENT_ZERO_SHUTTER_LAG);
//                m2CaptureRequestBuilder.setTag("STILL_CAPTURE_TAG");

                mCameraDevice.createCaptureSession(Arrays.asList(previewSurface, mImageReader.getSurface()),
                        new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(CameraCaptureSession session) {
                                Log.d(REACT_CLASS, "onConfigured: startPreview");
                                mPreviewCaptureSession = session;
                                try {
                                    mPreviewCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(),null,mBackgroundHandler);
                                    //mPreviewCaptureSession.capture(m2CaptureRequestBuilder.build(),stillCaptureCallback,mBackgroundHandler2);
                                } catch (CameraAccessException e) {
                                    Log.d(REACT_CLASS, "Entered Try Catch error");
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(CameraCaptureSession session) {
                                Log.d(REACT_CLASS, "onConfigureFailed: startPreview");
                                Toast.makeText(reactContext, "Camera Preview Failed", Toast.LENGTH_SHORT).show();

                            }
                        }, mBackgroundHandler);

                Log.i(REACT_CLASS,"CaptureSessionCreated");
                //lockFocus();
            } catch (CameraAccessException e) {
                Log.i(REACT_CLASS,"Error in Try1");
                e.printStackTrace();
            }
        } catch (CameraAccessException e) {
            Log.i(REACT_CLASS,"Error in Try2");
            e.printStackTrace();
        }

    }






    public boolean mIsRecording = false;

    private CameraDevice mCameraDevice;
    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            if(mIsRecording){
                     try {
                createVideoFileName();
                     } catch (IOException e){
                        e.printStackTrace();
                    }
                startRecord();
                mMediaRecorder.start();
            }
            else {
                Toast.makeText(reactContext, "Preview To be Played", Toast.LENGTH_SHORT).show();
                startPreview();
            }
            Log.i(REACT_CLASS,"Entered onOpen Camera");
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.i(REACT_CLASS,"Camera Disconnected");
            camera.close();
            mCameraDevice = null;

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.i(REACT_CLASS,"Camera State Callback Error");
            camera.close();
            mCameraDevice = null;
        }
    };

    private void startStillCaptureRequest(){
        try {
            Log.i(REACT_CLASS,"Entered Still Capture Request Mode");
            if (mIsRecording){
                mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_VIDEO_SNAPSHOT);
                //mCaptureRequestBuilder.set(CaptureRequest.CONTROL_SCENE_MODE_HDR)
                mCaptureRequestBuilder.set(CaptureRequest.JPEG_THUMBNAIL_QUALITY, (byte) 100);
                mCaptureRequestBuilder.addTarget(mImageReader.getSurface());
                mCaptureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, mtotalRotation);
                mCaptureRequestBuilder.set(CaptureRequest.CONTROL_SCENE_MODE,CaptureRequest.CONTROL_SCENE_MODE_NIGHT);
                //m2CaptureRequestBuilder.set(CaptureRequest.CONTROL_CAPTURE_INTENT, CaptureRequest.CONTROL_CAPTURE_INTENT_ZERO_SHUTTER_LAG);
                mCaptureRequestBuilder.setTag("STILL_CAPTURE_TAG");
                //m2CaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                Log.i(REACT_CLASS,"Still Capture Params Set");
            }else{
                mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                mCaptureRequestBuilder.set(CaptureRequest.JPEG_THUMBNAIL_QUALITY, (byte) 100);
                mCaptureRequestBuilder.addTarget(mImageReader.getSurface());
                mCaptureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, mtotalRotation);
                //mCaptureRequestBuilder.set(CaptureRequest.CONTROL_SCENE_MODE,CaptureRequest.CONTROL_SCENE_MODE_ACTION);
                //mCaptureRequestBuilder.set(CaptureRequest.CONTROL_CAPTURE_INTENT, CaptureRequest.CONTROL_CAPTURE_INTENT_ZERO_SHUTTER_LAG);
                mCaptureRequestBuilder.setTag("STILL_CAPTURE_TAG");
                //mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                Log.i(REACT_CLASS,"Still Capture Params Set");
            }

            if(mIsRecording){
                mRecordCaptureSession.capture(mCaptureRequestBuilder.build(), stillCaptureCallback, mBackgroundHandler2);
                mTimer = new Timer();
                mTimer.schedule(new RemindTask(),2000);
                Log.i(REACT_CLASS, "Timer Scheduled");
            }else{
                mPreviewCaptureSession.capture(mCaptureRequestBuilder.build(), stillCaptureCallback, mBackgroundHandler);
            }
            Log.i(REACT_CLASS,"Capture Session Call back called");
            //mCaptureRequestBuilder =  null;
            //startPreview();

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private static CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
    private static void genVideoUsingMuxer(String srcPath, String dstPath,
                                           int startMs, int endMs, boolean useAudio, boolean
                                                   useVideo)
            throws IOException {
        // Set up MediaExtractor to read from the source.
        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(srcPath);
        int trackCount = extractor.getTrackCount();
        // Set up MediaMuxer for the destination.
        MediaMuxer muxer;
        muxer = new MediaMuxer(dstPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        // Set up the tracks and retrieve the max buffer size for selected
        // tracks.
        HashMap<Integer, Integer> indexMap = new HashMap<>(trackCount);
        int bufferSize = -1;
        for (int i = 0; i < trackCount; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            boolean selectCurrentTrack = false;
            if (mime.startsWith("audio/") && useAudio) {
                selectCurrentTrack = true;
            } else if (mime.startsWith("video/") && useVideo) {
                selectCurrentTrack = true;
            }
            if (selectCurrentTrack) {
                extractor.selectTrack(i);
                int dstIndex = muxer.addTrack(format);
                indexMap.put(i, dstIndex);
                if (format.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
                    int newSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
                    bufferSize = newSize > bufferSize ? newSize : bufferSize;
                }
            }
        }
        if (bufferSize < 0) {
            bufferSize = 1 * (int)(profile.videoFrameHeight*videoratio) * profile.videoFrameHeight;
        }
        // Set up the orientation and starting time for extractor.
        MediaMetadataRetriever retrieverSrc = new MediaMetadataRetriever();
        retrieverSrc.setDataSource(srcPath);
        String time = retrieverSrc.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time);
        if ((int)timeInMillisec >= 4000) {
            startMs = (int) timeInMillisec - 4000;
        }

        Log.i(Tag, "genVideoUsingMuxer: start Duration "+startMs);
        Log.i(Tag, "genVideoUsingMuxer: Total Duration "+timeInMillisec);
        String degreesString = retrieverSrc.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        if (degreesString != null) {
            int degrees = Integer.parseInt(degreesString);
            if (degrees >= 0) {
                muxer.setOrientationHint(degrees);
            }
        }
        if (startMs > 0) {
            extractor.seekTo(startMs * 1000, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        }
        // Copy the samples from MediaExtractor to MediaMuxer. We will loop
        // for copying each sample and stop when we get to the end of the source
        // file or exceed the end time of the trimming.
        int offset = 0;
        int trackIndex = -1;
        ByteBuffer dstBuf = ByteBuffer.allocate(bufferSize);
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        try {
            muxer.start();
            while (true) {
                bufferInfo.offset = offset;
                bufferInfo.size = extractor.readSampleData(dstBuf, offset);
                if (bufferInfo.size < 0) {
                    Log.d(Tag,"Saw input EOS.");
                    bufferInfo.size = 0;
                    break;
                } else {
                    bufferInfo.presentationTimeUs = extractor.getSampleTime();
                    if (endMs > 0 && bufferInfo.presentationTimeUs > (endMs * 1000)) {
                        Log.d(Tag, "The current sample is over the trim end time.");
                        break;
                    } else {
                        bufferInfo.flags = extractor.getSampleFlags();
                        trackIndex = extractor.getSampleTrackIndex();
                        muxer.writeSampleData(indexMap.get(trackIndex), dstBuf,
                                bufferInfo);
                        extractor.advance();
                    }
                }
            }
            muxer.stop();

            //deleting the old file
            File file = new File(srcPath);
            file.delete();
        } catch (IllegalStateException e) {
            // Swallow the exception due to malformed source.
            Log.w(Tag, "The source video file is malformed");
        } finally {
            muxer.release();
        }
        return;
    }

    class RemindTask extends TimerTask {
        @RequiresApi(api = Build.VERSION_CODES.N)
        public void run() {
            Log.i(Tag, "2 secs Times up");//Terminate the timer thread
            mIsRecording = false;
            mMediaRecorder.pause();
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            String Tempname = mVideoFileName;
            //videoStopped = 1;
            try {
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String prepend = "VIDEO2_" +timestamp+"_";
                File videoFile = File.createTempFile(prepend, ".mp4", mVideoFolder);
                String mVideoFileName2 = videoFile.getAbsolutePath();
                genVideoUsingMuxer(Tempname,mVideoFileName2,0,-1,false,true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mIsRecording = true;
            startRecord();
            mMediaRecorder.start();
        }
    }
    private Timer mTimer;
    private File mImageFolder;
    private String mImageFileName;
    private File createImageFileName() throws IOException {

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String prepend = "IMG_" +timestamp+"_";
        Log.i(REACT_CLASS,"ImageFileParams Set");
        File imageFile = File.createTempFile(prepend, ".jpg", mImageFolder);
        Log.i(REACT_CLASS,"ImageFileCreated");
        mImageFileName = imageFile.getAbsolutePath();
        return imageFile;
    }

    CameraCaptureSession.CaptureCallback stillCaptureCallback = new
            CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);
                    try {
                        Log.i(REACT_CLASS,"Starting to Create File name");
                        createImageFileName();
                        Log.i(REACT_CLASS,"Filename created");
                        new MediaActionSound().play(MediaActionSound.SHUTTER_CLICK);

                    } catch (IOException e) {
                        Log.i(REACT_CLASS,"Error in Filename creation");
                        e.printStackTrace();
                    }
                }
            };


    private CameraCaptureSession mRecordCaptureSession;
    private CameraCaptureSession.CaptureCallback mRecordCaptureCallback = new
            CameraCaptureSession.CaptureCallback() {

                private void process(CaptureResult captureResult){
                    switch(mCaptureState){
                        case STATE_PREVIEW:
                            //Nothing da Onnumae illa
                            break;

                        case STATE_WAIT_LOCK:
                            mCaptureState = STATE_PREVIEW;
                            Integer afState = captureResult.get(CaptureResult.CONTROL_AF_STATE);
                            Log.i(REACT_CLASS,"Camera Shutter Clicked");
                            //if(afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED || afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED){
                            Toast.makeText(MainActivity.getmInstanceActivity(), "Image Saved", Toast.LENGTH_SHORT).show();
                            startStillCaptureRequest();
                            //}

                            break;
                    }
                }
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);

                    process(result);

                }
            };


    private static final int STATE_PREVIEW=0;
    private static final int STATE_WAIT_LOCK=1;
    private int mCaptureState = STATE_PREVIEW;

    private static boolean KeyframeCaptured = false;
    private void lockFocus(){
        mCaptureState = STATE_WAIT_LOCK;
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);
        //m2CaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);
        try{
            if(mIsRecording){
                mRecordCaptureSession.capture(mCaptureRequestBuilder.build(), mRecordCaptureCallback, mBackgroundHandler2);
                KeyframeCaptured = true;
            }else{
                mPreviewCaptureSession.capture(mCaptureRequestBuilder.build(), mPreviewCaptureCallback, mBackgroundHandler);
            }
        }catch (CameraAccessException e){
            e.printStackTrace();
        }

    }

    private CameraCaptureSession.CaptureCallback mPreviewCaptureCallback = new
            CameraCaptureSession.CaptureCallback() {

                private void process(CaptureResult captureResult){
                    switch(mCaptureState){
                        case STATE_PREVIEW:
                            //Nothing da Onnumae illa
                            break;

                        case STATE_WAIT_LOCK:
                            mCaptureState = STATE_PREVIEW;
                            Integer afState = captureResult.get(CaptureResult.CONTROL_AF_STATE);
                            Log.i(REACT_CLASS,"Camera Shutter Clicked");
                            //if(afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED || afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED){
                            Toast.makeText(MainActivity.getmInstanceActivity(), "Image Saved", Toast.LENGTH_SHORT).show();
                            startStillCaptureRequest();
                            //}

                            break;
                    }
                }
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);

                    process(result);

                }
            };


    private void connectCamera(){
        CameraManager cameraManager = (CameraManager) reactContext.getSystemService(Context.CAMERA_SERVICE);
        Toast.makeText(reactContext,"Connect Camera Toast", Toast.LENGTH_SHORT).show();
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(ContextCompat.checkSelfPermission(reactContext, android.Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED) {
                    // Log.i(REACT_CLASS,"Permission Granted");
                    Toast.makeText(reactContext,"Permission Granted", Toast.LENGTH_SHORT).show();
                    try {
                        cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
                    } catch (Exception e){
                        Log.i(REACT_CLASS,"Error Camera");
                        Toast.makeText(reactContext,"Error Camera", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    if(MainActivity.getmInstanceActivity().shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                        Toast.makeText(reactContext, "Video app required access to camera", Toast.LENGTH_SHORT).show();
                    }

                    MainActivity.getmInstanceActivity().requestPermissions(new String[] {android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, REQUEST_CAMERA_PERMISSION_RESULT);
                    // Log.i(REACT_CLASS,"Permission Pop Up Shown");
                    Toast.makeText(reactContext,"Permission Pop Up Shown", Toast.LENGTH_SHORT).show();
                }

            } else {
                cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
                Toast.makeText(reactContext,"Permission Pop else condition", Toast.LENGTH_SHORT).show();
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @ReactMethod
    public void videoStartCapture(final Promise promise){
        checkWriteStoragePermission();
//        Toast.makeText(reactContext,
//                mVideoFileName, Toast.LENGTH_SHORT).show();
//        promise.resolve(mVideoFileName);
    }

    public class Livephoto {
        public String video;
        public String keyframe;

        public Livephoto(String mvideo, String mkeyframe){
            this.video = mvideo;
            this.keyframe = mkeyframe;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @ReactMethod
    public void videoStopCapture(final Promise promise){
        // lockFocus();
        // promise.resolve(mImageFileName+"@/@"+mVideoFileName);

       Log.i(REACT_CLASS, "Call Back on Button click");
       try{
            mMediaRecorder.pause();
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mIsRecording = false;
            startPreview();
       }catch (Exception e){
            Toast.makeText(reactContext,e.getMessage()+" STOP NOT WORKING", Toast.LENGTH_SHORT).show();
           
       }
    }



    private final PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == REQUEST_CAMERA_PERMISSION_RESULT) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(reactContext,
                            "Application will not run without camera services", Toast.LENGTH_SHORT).show();
                }
            }
            if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mIsRecording = true;
                    try {
                        createVideoFileName();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.getmInstanceActivity(),
                            "Permission Granted for Storage", Toast.LENGTH_SHORT).show();
                    Log.i(REACT_CLASS, "Permission Granted for Storage");

                } else {
                    Toast.makeText(MainActivity.getmInstanceActivity(),
                            "App needs Storage Permissions", Toast.LENGTH_SHORT).show();
                }
            }  
            return false;
        }
    };
}
