package com.empty_proj;

import android.app.Activity;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class CustomModulePackage implements ReactPackage {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    private TextureViewManager mTextureViewManager;
    private CustomModule mCustomModule;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
//        if (mTextureViewManager == null){
//            mTextureViewManager = new TextureViewManager();
//            //mTextureViewManager.createViewInstance(reactContext);
//        }
            mCustomModule = new CustomModule(reactContext);
            //mTextureViewManager.createViewInstance(reactContext);

       // modules.add(new CustomModule(reactContext,mTextureViewManager));
        modules.add(mCustomModule);
        return modules;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactContext) {
        if(mCustomModule == null){
            mCustomModule = new CustomModule(reactContext);
        }
        return Arrays.<ViewManager>asList(new TextureViewManager(mCustomModule));
//        return Collections.singletonList(new TextureViewManager());
    }
}
