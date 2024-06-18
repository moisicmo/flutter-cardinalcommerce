package com.example.flutter_braintree;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import android.app.Activity;
import android.content.Intent;


import androidx.annotation.Nullable;

import com.braintreepayments.api.DropInRequest;
import com.braintreepayments.api.DropInResult;
import com.braintreepayments.api.PayPalCheckoutRequest;
import com.braintreepayments.api.PaymentMethodNonce;
import com.braintreepayments.api.ThreeDSecureAdditionalInformation;
import com.braintreepayments.api.ThreeDSecurePostalAddress;
import com.braintreepayments.api.ThreeDSecureRequest;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;

import java.io.Serializable;
import java.util.HashMap;

public class FlutterBraintreeDropIn  implements FlutterPlugin, ActivityAware, MethodCallHandler, ActivityResultListener, Serializable {
  private static final int DROP_IN_REQUEST_CODE = 0x1337;

  private Activity activity;
  private Result activeResult;


  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_braintree.drop_in");
    FlutterBraintreeDropIn plugin = new FlutterBraintreeDropIn();
    plugin.activity = registrar.activity();
    registrar.addActivityResultListener(plugin);
    channel.setMethodCallHandler(plugin);
  }

  @Override
  public void onAttachedToEngine(FlutterPluginBinding binding) {
    final MethodChannel channel = new MethodChannel(binding.getBinaryMessenger(), "flutter_braintree.drop_in");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onDetachedFromEngine(FlutterPluginBinding binding) {

  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
    activity = binding.getActivity();
    binding.addActivityResultListener(this);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    activity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
    activity = binding.getActivity();
    binding.addActivityResultListener(this);
  }

  @Override
  public void onDetachedFromActivity() {
    activity = null;
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
      result.notImplemented();
  }

  @Override
  public boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (this.activeResult == null)
      return false;

    switch (requestCode) {
      case DROP_IN_REQUEST_CODE:
        if (resultCode == Activity.RESULT_OK) {
          DropInResult dropInResult = data.getParcelableExtra("dropInResult");
          PaymentMethodNonce paymentMethodNonce = dropInResult.getPaymentMethodNonce();
          HashMap<String, Object> result = new HashMap<String, Object>();

          HashMap<String, Object> nonceResult = new HashMap<String, Object>();
          nonceResult.put("nonce", paymentMethodNonce.getString());
          nonceResult.put("typeLabel", dropInResult.getPaymentMethodType().name());
          nonceResult.put("description", dropInResult.getPaymentDescription());
          nonceResult.put("isDefault", paymentMethodNonce.isDefault());

          result.put("paymentMethodNonce", nonceResult);
          result.put("deviceData", dropInResult.getDeviceData());
          this.activeResult.success(result);
        } else if (resultCode == Activity.RESULT_CANCELED) {
          activeResult.success(null);
        } else {
          String error = data.getStringExtra("error");
          activeResult.error("braintree_error", error, null);
        }
        activeResult = null;
        return true;
      default:
        return false;
    }
  }
}