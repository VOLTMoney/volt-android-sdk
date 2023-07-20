package com.voltmoney.voltmoneySdkSample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.voltmoney.voltmoneySdkSample.databinding.ActivityMainBinding
import com.voltmoney.voltsdk.VoltAPIResponse
import com.voltmoney.voltsdk.VoltSDKContainer
import com.voltmoney.voltsdk.models.ENVIRONMENT
import com.voltmoney.voltsdk.models.PreCreateAppResponse
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.security.auth.login.LoginException


class MainActivity : AppCompatActivity(), VoltAPIResponse {
    private var voltSDKContainer: VoltSDKContainer? = null
    private var preCreateAppResponse: PreCreateAppResponse? = null
    private lateinit var requestQueue: RequestQueue
    var selectedEnvironment: ENVIRONMENT = ENVIRONMENT.STAGING


    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestQueue = Volley.newRequestQueue(this)


        binding.stagingRB.setOnCheckedChangeListener { button, b ->
            if (binding.stagingRB.isChecked) {
                selectedEnvironment = ENVIRONMENT.STAGING
                binding.productionRB.isChecked = false
            }
        }
        binding.productionRB.setOnCheckedChangeListener { button, b ->
            if (binding.productionRB.isChecked) {
                selectedEnvironment = ENVIRONMENT.PRODUCTION
                binding.stagingRB.isChecked = false
            }
        }


        binding.btVolt.setOnClickListener {
            voltSDKContainer = binding.etPrimaryColor.text.toString().let { it ->
                if (it.length == 6) {
                    val target = binding.etTarget.text.toString()
                    val customerSSOToken = binding.etSsoToken.text.toString()
                    val voltPlatformCode = binding.etPlatform.text.toString()
                    val platformAuthToken = binding.etPlatformAuthToken.text.toString()
                    val customerCode = binding.etCustomerCode.text.toString()
                    val mobileNumber = binding.etMobile.text.toString()

                    if (platformAuthToken == null) {
                        Log.e("TAG", "Please enter Platform Auth Token")
                        Toast.makeText(this, "Please enter Platform Auth Token", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        if (customerSSOToken == null) {
                            binding.etPlatform.text.toString().let { iter ->
                                {
                                    if (iter.length > 2) {
                                        VoltSDKContainer(
                                            this,
                                            iter,
                                            it,
                                            selectedEnvironment,
                                            "FFFFFF",
                                            mobileNumber,
                                            target,
                                            customerSSOToken,
                                            customerCode,
                                            voltPlatformCode,
                                            platformAuthToken,
                                        )
                                    }
                                }
                            }
                        } else {
                            if (customerCode !== null) {
                                binding.etPlatform.text.toString().let { iter ->
                                    {
                                        if (iter.length > 2) {
                                            VoltSDKContainer(
                                                this,
                                                iter,
                                                it,
                                                selectedEnvironment,
                                                "FFFFFF",
                                                mobileNumber,
                                                target,
                                                customerSSOToken,
                                                customerCode,
                                                voltPlatformCode,
                                                platformAuthToken,
                                            )
                                        }
                                    }
                                }
                            } else {
                                Log.e("TAG", "Please enter Customer Code")
                                Toast.makeText(
                                    this, "Please enter Customer Code", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    binding.etPlatform.text.toString().let { it1 ->
                        if (it1.length > 2) {
                            VoltSDKContainer(
                                this,
                                it1,
                                it,
                                selectedEnvironment,
                                "FFFFFF",
                                mobileNumber,
                                target,
                                customerSSOToken,
                                customerCode,
                                voltPlatformCode,
                                platformAuthToken,
                            )

                        } else {
                            Log.e("TAG", "Please enter correct platform")
                            Toast.makeText(
                                this, "Please enter correct Platform", Toast.LENGTH_SHORT
                            ).show()
                            null
                        }

                    }
                } else {
                    Log.e("TAG", "Please enter primary color")
                    Toast.makeText(this, "Please enter primary color", Toast.LENGTH_SHORT).show()
                    null
                }
            }
        }

        binding.logoutButton.setOnClickListener {
            voltSDKContainer?.logoutSDK()
        }

        binding.btDeleteUser.setOnClickListener {
            if (binding.etMobile.text.toString().length < 10) {
                Log.e("TAG", "Please input correct mobile number")
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
                            urlConnection.requestMethod = "GET";
                            urlConnection.setChunkedStreamingMode(0);
                        } finally {
                            // this is done so that there are no open connections left when this task is going to complete
                            urlConnection.disconnect();
                        }
                    }
                })
                thread.start()
                Toast.makeText(
                    this, "User deleted :" + binding.etMobile.text.toString(), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun preCreateAppAPIResponse(
        preCreateAppResponse: PreCreateAppResponse?, errorMsg: String?
    ) {
        this.preCreateAppResponse = preCreateAppResponse
        if (preCreateAppResponse?.customerAccountId != null) {
            Toast.makeText(
                this,
                "Customer Id is: " + this.preCreateAppResponse?.customerAccountId.toString(),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
        }
    }
}