//package com.empty_proj;
//
//import android.app.Activity;
//import android.os.Build;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.RequiresApi;
//
//import com.facebook.react.ReactPackage;
//import com.facebook.react.bridge.NativeModule;
//import com.facebook.react.bridge.ReactApplicationContext;
//import com.facebook.react.uimanager.ViewManager;
//
//import java.util.Collections;
//import java.util.List;
//
//public class TextureViewPackage implements ReactPackage {
//
//
//    @NonNull
//    @Override
//    public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactContext) {
//        return Collections.emptyList();
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @NonNull
//    @Override
//    public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactContext) {
//        return Collections.<ViewManager>singletonList(new TextureViewManager());
//    }
//}
