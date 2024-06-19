package com.example.flutter_braintree;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.braintreepayments.api.DropInListener;
import com.braintreepayments.api.DropInResult;
import com.braintreepayments.api.UserCanceledException;

public class DropInActivity extends AppCompatActivity implements DropInListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {}

    @Override
    protected void onStart() {}

    @Override
    public void onDropInSuccess(@NonNull DropInResult dropInResult) {}

    @Override
    public void onDropInFailure(@NonNull Exception error) {}
}
