package com.voltmoney.voltmoneySdkSample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.voltmoney.voltsdk.VoltSDKContainer;
import com.voltmoney.voltsdk.models.ENVIRONMENT;

public class MainActivityForJava extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        VoltSDKContainer container = new VoltSDKContainer(this,"","", ENVIRONMENT.PRODUCTION,"","", "","", "");

    }
}