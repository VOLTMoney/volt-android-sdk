package com.voltmoney.voltmoneySdkSample

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Website.URL
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.voltmoney.voltmoneySdkSample.databinding.ActivityMainBinding
import com.voltmoney.voltsdk.VoltAPIResponse
import com.voltmoney.voltsdk.VoltSDKInstance
import com.voltmoney.voltsdk.models.CreateAppResponse
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity(), VoltAPIResponse {
    private lateinit var voltButton: Button
    private lateinit var authButton: Button
    private lateinit var createAppButton:Button
    private lateinit var invokeVoltSdk:Button
    private var voltInstance:VoltSDKInstance?=null
    private var createAppResponse: CreateAppResponse?=null
    private var authToken:String?=null
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btVolt.setOnClickListener {
           // var intent:Intent = Intent(this,VoltWebViewActivity::class.java)
            //startActivity(Intent(this,VoltWebViewActivity::class.java))
            voltInstance = VoltSDKInstance(this,
                "volt-sdk-staging@voltmoney.in",
                "e10b6eaf2e334d1b955434e25fcfe2d8",
                binding.etRef.text.toString(),
                binding.etPrimaryColor.text.toString(),
                null,
                binding.etPlatform.text.toString()
            )

        }
       /* binding.btGetAuthToken.setOnClickListener {
            voltInstance?.generateToken()

        }*/

        binding.btCreateApp.setOnClickListener {
            if (voltInstance == null){
                Toast.makeText(this, "Please create VoltInstance first", Toast.LENGTH_SHORT).show()
            }
            voltInstance?.startApplication(
                binding.etDob.text.toString(),
                binding.etEmail.text.toString(),
                binding.etMobile.text.toString().toLong(),
                binding.etPan.text.toString()
            )
        }

        binding.btInvokeVoltSdk.setOnClickListener {
            if (voltInstance == null){
                Toast.makeText(this, "Please create VoltInstance first", Toast.LENGTH_SHORT).show()
            }
            voltInstance.let {
                    it?.invokeVoltSdk(binding.etMobile.text.toString().toLong())
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
                        // adding the headers for request
                       // urlConnection.setRequestProperty("Content-Type", "application/json");
                        //to tell the connection object that we will be wrting some data on the server and then will fetch the output result
                       // urlConnection.setDoOutput(true);
                        // this is used for just in case we don't know about the data size associated with our request
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
    override fun createAppAPIResponse(createAppResponse: CreateAppResponse?, errorMsg: String?) {

        this.createAppResponse =createAppResponse
        if (createAppResponse?.customerAccountId !=null) {
                Toast.makeText(
                    this,
                    "Customer Id is: "+this.createAppResponse?.customerAccountId.toString(),
                    Toast.LENGTH_SHORT
                ).show()
        }else{
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
        }
    }
}