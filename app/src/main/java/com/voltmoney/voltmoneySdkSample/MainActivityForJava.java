package com.voltmoney.voltmoneySdkSample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.voltmoney.voltsdk.VoltSDKCallback;
import com.voltmoney.voltsdk.VoltSDKContainer;
import com.voltmoney.voltsdk.models.ENVIRONMENT;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class MainActivityForJava extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        VoltSDKContainer container = new VoltSDKContainer(this, "", "", "production", "", "", "", "", "", "Yes", new Function0<Unit>() {
            @Override
            public Unit invoke() {
                return Unit.INSTANCE;
            }
        });

    }
}