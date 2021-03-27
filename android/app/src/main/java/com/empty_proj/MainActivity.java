package com.empty_proj;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

public class MainActivity extends ReactActivity {

  private static MainActivity mInstanceActivity;
  public static MainActivity getmInstanceActivity() {
    return mInstanceActivity;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mInstanceActivity = this;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mInstanceActivity = null;
  }

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  @Override
  protected String getMainComponentName() {
    return "empty_proj";
  }

}
