package com.example.flutter_braintree;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.braintreepayments.api.BraintreeClient;
import com.braintreepayments.api.Card;
import com.braintreepayments.api.CardClient;
import com.braintreepayments.api.CardNonce;
import com.braintreepayments.api.CardTokenizeCallback;
import com.braintreepayments.api.PayPalCheckoutRequest;
import com.braintreepayments.api.PayPalRequest;
import com.braintreepayments.api.PayPalVaultRequest;
import com.braintreepayments.api.PaymentMethodNonce;
import com.braintreepayments.api.UserCanceledException;

import java.util.HashMap;

public class FlutterBraintreeCustom extends AppCompatActivity {
  private BraintreeClient braintreeClient;
  private Boolean started = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.d("FlutterBraintreePlugin", "Llamando a onCreatessss");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_flutter_braintree_custom);
    try {
      Intent intent = getIntent();
      braintreeClient = new BraintreeClient(this, intent.getStringExtra("authorization"));
      String type = intent.getStringExtra("type");
      if (type.equals("tokenizeCreditCard")) {
        tokenizeCreditCard();
      } else {
        throw new Exception("Invalid request type: " + type);
      }
    } catch (Exception e) {
      Intent result = new Intent();
      result.putExtra("error", e);
      setResult(2, result);
      finish();
      return;
    }
  }

  @Override
  protected void onStart() {
    Log.d("FlutterBraintreePlugin", "Llamando a onStartsasasasa");
    super.onStart();
  }

  @Override
  protected void onResume() {
    Log.d("FlutterBraintreePlugin", "Llamando a onResume");
    super.onResume();
  }

  protected void tokenizeCreditCard() {
    Log.d("FlutterBraintreePlugin", "Llamando a tooooootokenizeCreditCard");
    Intent intent = getIntent();
    Card card = new Card();
    card.setExpirationMonth(intent.getStringExtra("expirationMonth"));
    card.setExpirationYear(intent.getStringExtra("expirationYear"));
    card.setCvv(intent.getStringExtra("cvv"));
    card.setCardholderName(intent.getStringExtra("cardholderName"));
    card.setNumber(intent.getStringExtra("cardNumber"));

    CardClient cardClient = new CardClient(braintreeClient);
    CardTokenizeCallback callback = (cardNonce, error) -> {
      if(cardNonce != null){
        onPaymentMethodNonceCreated(cardNonce);
      }
      if(error != null){
        onError(error);
      }
    };
    cardClient.tokenize(card, callback);
  }

  public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
    Log.d("FlutterBraintreePlugin", "Llamando a onPaymentMethodNonceCreated");
    HashMap<String, Object> nonceMap = new HashMap<String, Object>();
    nonceMap.put("nonce", paymentMethodNonce.getString());
    nonceMap.put("isDefault", paymentMethodNonce.isDefault());
    if (paymentMethodNonce instanceof CardNonce){
      CardNonce cardNonce = (CardNonce) paymentMethodNonce;
      nonceMap.put("typeLabel", cardNonce.getCardType());
      nonceMap.put("description", "ending in ••" + cardNonce.getLastTwo());
    }
    Intent result = new Intent();
    result.putExtra("type", "paymentMethodNonce");
    result.putExtra("paymentMethodNonce", nonceMap);
    setResult(RESULT_OK, result);
    finish();
  }

  public void onError(Exception error) {}
  
  @Override
  protected void onNewIntent(Intent newIntent) {}

}