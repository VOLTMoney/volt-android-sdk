package com.voltmoney.voltmoneySdkSample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.voltmoney.voltmoneySdkSample.databinding.ActivityMainBinding
import com.voltmoney.voltsdk.VoltAPIResponse
import com.voltmoney.voltsdk.VoltSDKContainer
import com.voltmoney.voltsdk.models.PreCreateAppResponse
import com.voltmoney.voltsdk.models.VOLTENV

import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity(), VoltAPIResponse {
    private var voltSDKContainer:VoltSDKContainer?=null
    private var preCreateAppResponse: PreCreateAppResponse?=null
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btVolt.setOnClickListener {
           voltSDKContainer = binding.etPrimaryColor.text.toString().let { it ->
               if (it.length==6){
                    binding.etPlatform.text.toString().let { it1->
                        if (it1.length > 2){
                            VoltSDKContainer(this,
                                "volt-sdk-staging@voltmoney.in",
                                "e10b6eaf2e334d1b955434e25fcfe2d8",
                                it1,
                                it,
                                null,
                                binding.etRef.text.toString(),
                               VOLTENV.valueOf("STAGING"),
                                "FFFFFF"
                            )
                        }else{
                            Toast.makeText(this, "Please enter correct Platform", Toast.LENGTH_SHORT).show()
                            null
                        }

                    }
                }else{
                    Toast.makeText(this, "Please enter primary color", Toast.LENGTH_SHORT).show()
                    null
                }
            }
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

        binding.logoutButton.setOnClickListener{
            voltSDKContainer?.logoutSDK()
        }
        binding.btInvokeVoltSdk.setOnClickListener {
            if (voltSDKContainer == null){
                Toast.makeText(this, "Please create VoltInstance first", Toast.LENGTH_SHORT).show()
            }
            voltSDKContainer.let {
                if (binding.etMobile.text.toString().length==10){
                    it?.initVoltSdk(binding.etMobile.text.toString().toLong())
                }else{
                   it?.initVoltSdk(null);
                }

            }
        }
        binding.btDeleteUser.setOnClickListener {
            if (binding.etMobile.text.toString().length < 10) {
                Toast.makeText(this, "Please input correct mobile number", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val thread: Thread = Thread(object : Runnable {
                    override fun run() {
                        val url: URL =
                            URL("https://api.staging.voltmoney.in/api/client/auth/test/delete/+91" + binding.etMobile.text.toString())
                        val urlConnection: HttpURLConnection =
                            url.openConnection() as HttpURLConnection
                        try {
                            // setting the  Request Method Type
                            urlConnection.setRequestMethod("GET");
                            urlConnection.setChunkedStreamingMode(0);
                            // to log the response code of your request
                            Log.d(
                                "ApplicationConstant.TAG", urlConnection.responseCode.toString()
                            )
                            // to log the response message from your server after you have tried the request.
                            Log.d(
                                "ApplicationConstant.TAG", urlConnection.responseMessage.toString()
                            )

                        } finally {
                            // this is done so that there are no open connections left when this task is going to complete
                            urlConnection.disconnect();
                        }
                    }
                })
                thread.start()
                Toast.makeText(
                    this,
                    "User deleted :" + binding.etMobile.text.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
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