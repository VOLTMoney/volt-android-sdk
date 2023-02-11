package com.voltmoney.voltmoneySdkSample

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.voltmoney.voltmoneySdkSample.databinding.ActivityMainBinding
import com.voltmoney.voltsdk.VoltAPIResponse
import com.voltmoney.voltsdk.VoltSDKContainer
import com.voltmoney.voltsdk.models.PreCreateAppResponse
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity(), VoltAPIResponse {
    private lateinit var voltButton: Button
    private lateinit var authButton: Button
    private lateinit var createAppButton:Button
    private lateinit var invokeVoltSdk:Button
    private var voltSDKContainer:VoltSDKContainer?=null
    private var preCreateAppResponse: PreCreateAppResponse?=null
    private var authToken:String?=null
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btVolt.setOnClickListener {
           // var intent:Intent = Intent(this,VoltWebViewActivity::class.java)
            //startActivity(Intent(this,VoltWebViewActivity::class.java))
            voltSDKContainer = VoltSDKContainer(this,
                "volt-sdk-staging@voltmoney.in",
                "e10b6eaf2e334d1b955434e25fcfe2d8",
                binding.etRef.text.toString(),
                binding.etPrimaryColor.text.toString(),
                null,
                binding.etPlatform.text.toString()
            )

        }
        binding.btCreateApp.setOnClickListener {
            if (voltSDKContainer == null){
                Toast.makeText(this, "Please create VoltInstance first", Toast.LENGTH_SHORT).show()
            }
            if(binding.etMobile.text.toString().length <10){
                Toast.makeText(this, "Please input correct mobile number", Toast.LENGTH_SHORT).show()
            }else {
                voltSDKContainer?.preCreateApplication(
                    binding.etDob.text.toString(),
                    binding.etEmail.text.toString(),
                    binding.etMobile.text.toString().toLong(),
                    binding.etPan.text.toString()
                )
            }
        }

        binding.btInvokeVoltSdk.setOnClickListener {
            if (voltSDKContainer == null){
                Toast.makeText(this, "Please create VoltInstance first", Toast.LENGTH_SHORT).show()
            }
            voltSDKContainer.let {
                    it?.initVoltSdk(binding.etMobile.text.toString().toLong())
            }
        }
        binding.btDeleteUser.setOnClickListener {
            val thread:Thread = Thread(object : Runnable{
                override fun run() {
                    val url: URL =
                        URL("https://api.staging.voltmoney.in/api/client/auth/test/delete/+91" + binding.etMobile.text.toString())
                    val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    try {
                        // setting the  Request Method Type
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setChunkedStreamingMode(0);
                        // to log the response code of your request
                        Log.d(
                            "ApplicationConstant.TAG", urlConnection.responseCode.toString())
                        // to log the response message from your server after you have tried the request.
                        Log.d(
                            "ApplicationConstant.TAG", urlConnection.responseMessage.toString())

                    } finally {
                        // this is done so that there are no open connections left when this task is going to complete
                        urlConnection.disconnect();
                    }
                }
            })
            thread.start()
            Toast.makeText(this, "User deleted :"+ binding.etMobile.text.toString(), Toast.LENGTH_SHORT).show()
        }
    }
    override fun preCreateAppAPIResponse(preCreateAppResponse: PreCreateAppResponse?, errorMsg: String?) {

        this.preCreateAppResponse =preCreateAppResponse
        if (preCreateAppResponse?.customerAccountId !=null) {
                Toast.makeText(
                    this,
                    "Customer Id is: "+this.preCreateAppResponse?.customerAccountId.toString(),
                    Toast.LENGTH_SHORT
                ).show()
        }else{
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
        }
    }
}