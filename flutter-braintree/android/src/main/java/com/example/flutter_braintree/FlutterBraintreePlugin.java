package com.example.flutter_braintree;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener;

public class FlutterBraintreePlugin implements FlutterPlugin, ActivityAware, MethodCallHandler, ActivityResultListener {
  private static final int CUSTOM_ACTIVITY_REQUEST_CODE = 0x420;

  private Activity activity;
  private Result activeResult;

  @Override
  public void onAttachedToEngine(FlutterPluginBinding binding) {
    Log.d("FlutterBraintreePlugin", "Llamando a onAttachedToEngine");
    final MethodChannel channel = new MethodChannel(binding.getBinaryMessenger(), "flutter_braintree.custom");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
    Log.d("FlutterBraintreePlugin", "Llamando a onAttachedToActivity");
    activity = binding.getActivity();
    binding.addActivityResultListener(this);
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    Log.d("FlutterBraintreePlugin", "Llamando a onMethodCall");
    if (activeResult != null) {
      result.error("already_running", "Cannot launch another custom activity while one is already running.", null);
      return;
    }
    activeResult = result;

    if (call.method.equals("tokenizeCreditCard")) {
      Intent intent = new Intent(activity, FlutterBraintreeCustom.class);
      intent.putExtra("type", "tokenizeCreditCard");
      intent.putExtra("authorization", (String) call.argument("authorization"));
      assert(call.argument("request") instanceof Map);
      Map request = (Map) call.argument("request");
      intent.putExtra("cardNumber", (String) request.get("cardNumber"));
      intent.putExtra("expirationMonth", (String) request.get("expirationMonth"));
      intent.putExtra("expirationYear", (String) request.get("expirationYear"));
      intent.putExtra("cvv", (String) request.get("cvv"));
      intent.putExtra("cardholderName", (String) request.get("cardholderName"));

      activity.startActivityForResult(intent, CUSTOM_ACTIVITY_REQUEST_CODE);
    } else {
      result.notImplemented();
      activeResult = null;
    }
  }

  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d("FlutterBraintreePlugin", "Llamando a onActivityResult");
    if (activeResult == null)
      return false;
    
    switch (requestCode) {
      case CUSTOM_ACTIVITY_REQUEST_CODE:
        if (resultCode == Activity.RESULT_OK) {
          String type = data.getStringExtra("type");
          if (type.equals("paymentMethodNonce")) {
            activeResult.success(data.getSerializableExtra("paymentMethodNonce"));
          } else {
            Exception error = new Exception("Invalid activity result type.");
            activeResult.error("error", error.getMessage(), null);
          }
        } else if (resultCode == Activity.RESULT_CANCELED) {
          activeResult.success(null);
        }  else {
          Exception error = (Exception) data.getSerializableExtra("error");
          activeResult.error("error", error.getMessage(), null);
        }
        activeResult = null;
        return true;
      default:
        return false;
    }
  }

  @Override
  public void onDetachedFromEngine(FlutterPluginBinding binding) {}

  @Override
  public void onDetachedFromActivityForConfigChanges() {}

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {}

  @Override
  public void onDetachedFromActivity() {}

}
