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
import com.voltmoney.voltsdk.models.PreCreateAppResponse
import com.voltmoney.voltsdk.models.VOLTENV
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity(), VoltAPIResponse {
    private var voltSDKContainer: VoltSDKContainer? = null
    private var preCreateAppResponse: PreCreateAppResponse? = null
    private lateinit var requestQueue: RequestQueue


    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var showVoltDefaultHeader: Boolean
        var showVoltLogo: Boolean
        var customLogoUrl: String
        var customSupportNumber: String
        var isStagingChecked: Boolean = true
        var isProductionChecked: Boolean = false
        requestQueue = Volley.newRequestQueue(this)


        binding.stagingRB.setOnCheckedChangeListener { button, b ->
            if (binding.stagingRB.isChecked) {
                isStagingChecked = true
                isProductionChecked = false
                binding.productionRB.isChecked = false
            }
        }
        binding.productionRB.setOnCheckedChangeListener { button, b ->
            if (binding.productionRB.isChecked) {
                isStagingChecked = false
                isProductionChecked = true
                binding.stagingRB.isChecked = false
            }
        }


        binding.btVolt.setOnClickListener {
            voltSDKContainer = binding.etPrimaryColor.text.toString().let { it ->
                if (it.length == 6) {
                    var target = binding.etTarget.text.toString()
                    var customerSSOToken = binding.etSsoToken.text.toString()
                    var voltPlatformCode = binding.etPlatform.text.toString()
                    var utmSource = binding.etUtmSource.text.toString()
                    var utmCampaign = binding.etUtmCampaign.text.toString()
                    var utmToken = binding.etUtmToken.text.toString()
                    var platformAuthToken = binding.etPlatformAuthToken.text.toString()
                    var customerCode = binding.etCustomerCode.text.toString()

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
                                            "volt-sdk-staging@voltmoney.in",
                                            "e10b6eaf2e334d1b955434e25fcfe2d8",
                                            iter,
                                            it,
                                            null,
                                            binding.etRef.text.toString(),
                                            VOLTENV.valueOf("STAGING"),
                                            "FFFFFF",
                                            target,
                                            customerSSOToken,
                                            voltPlatformCode,
                                            utmSource,
                                            utmCampaign,
                                            utmToken,
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
                                                "volt-sdk-staging@voltmoney.in",
                                                "e10b6eaf2e334d1b955434e25fcfe2d8",
                                                iter,
                                                it,
                                                null,
                                                binding.etRef.text.toString(),
                                                VOLTENV.valueOf("STAGING"),
                                                "FFFFFF",
                                                target,
                                                customerSSOToken,
                                                voltPlatformCode,
                                                utmSource,
                                                utmCampaign,
                                                utmToken,
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
                                "volt-sdk-staging@voltmoney.in",
                                "e10b6eaf2e334d1b955434e25fcfe2d8",
                                it1,
                                it,
                                null,
                                binding.etRef.text.toString(),
                                VOLTENV.valueOf("STAGING"),
                                "FFFFFF",
                                target,
                                customerSSOToken,
                                voltPlatformCode,
                                utmSource,
                                utmCampaign,
                                utmToken,
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
        binding.btCreateApp.setOnClickListener {
            if (voltSDKContainer == null) {
                Log.e("TAG", "Please create VoltInstance first")
                Toast.makeText(this, "Please create VoltInstance first", Toast.LENGTH_SHORT).show()
            }
            if (binding.etMobile.text.toString().length < 10) {
                Log.e("TAG", "Please input correct mobile number")
                Toast.makeText(this, "Please input correct mobile number", Toast.LENGTH_SHORT)
                    .show()
            } else {
                voltSDKContainer?.preCreateApplication(
                    binding.etDob.text.toString(),
                    binding.etEmail.text.toString(),
                    binding.etMobile.text.toString().toLong(),
                    binding.etPan.text.toString()
                )
            }
        }

        binding.logoutButton.setOnClickListener {
            voltSDKContainer?.logoutSDK()
        }
        binding.btInvokeVoltSdk.setOnClickListener {
            if (voltSDKContainer == null) {
                Log.e("TAG", "Please create VoltInstance first")
                Toast.makeText(this, "Please create VoltInstance first", Toast.LENGTH_SHORT).show()
            } else if (binding.etPlatformAuthToken.text.toString() == null || binding.etPlatformAuthToken.text.toString()
                    .trim() == ""
            ) {
                Log.e("TAG", "Please enter Platform Auth Token")
                Toast.makeText(this, "Please enter Platform Auth token", Toast.LENGTH_SHORT).show()
            } else if (binding.etSsoToken.text.toString().trim() != "") {
                if (binding.etCustomerCode.text.toString().trim() == "") {
                    Log.e("TAG", "Please enter Customer Code")
                    Toast.makeText(this, "Please enter Customer Code", Toast.LENGTH_SHORT).show()
                } else {
                    if (binding.etTarget.text.toString().trim() == "") {
                        var getDetailsURL =
                            if (isStagingChecked) "https://api.staging.voltmoney.in/app/pf/details/" else "https://api.voltmoney.in/app/pf/details/"

                        val stringRequest = object : StringRequest(Request.Method.GET,
                            getDetailsURL,
                            Response.Listener { response ->
                                // Handle the response here
                                val gson = Gson()
                                val responseData = gson.fromJson(response, ResponseData::class.java)
                                val platformSDKConfig = responseData.platformSDKConfig
                                Log.d("TAG", "Response: $platformSDKConfig")
                                if (platformSDKConfig != null) {
                                    showVoltDefaultHeader = platformSDKConfig.showDefaultVoltHeader
                                }
                                if (platformSDKConfig != null) {
                                    showVoltLogo = platformSDKConfig.showVoltLogo
                                }
                                if (platformSDKConfig != null) {
                                    customLogoUrl = platformSDKConfig.customLogoUrl
                                }
                                if (platformSDKConfig != null) {
                                    customSupportNumber = platformSDKConfig.customSupportNumber
                                }
                                var customerCode = binding.etCustomerCode.text.toString()
                                var validateSSOTokenURL =
                                    if (isStagingChecked) "https://api.staging.voltmoney.in/api/client/validate/ssoToken/${customerCode}" else "https://api.voltmoney.in/api/client/validate/ssoToken/${customerCode}"
                                val requestQueue: RequestQueue =
                                    Volley.newRequestQueue(this@MainActivity)

                                // Prepare the JSON object data
                                val jsonBody = JSONObject()
                                jsonBody.put(
                                    "ssoToken", binding.etSsoToken.text
                                )

                                // Create a Volley JSON Object Request
                                val jsonObjectRequest =
                                    object : JsonObjectRequest(Request.Method.POST,
                                        validateSSOTokenURL,
                                        jsonBody,
                                        Response.Listener { response ->
                                            voltSDKContainer.let {
                                                if (binding.etMobile.text.toString().length == 10) {
                                                    if (platformSDKConfig != null) {
                                                        it?.initVoltSdk(
                                                            binding.etMobile.text.toString()
                                                                .toLong(),
                                                            platformSDKConfig.showDefaultVoltHeader,
                                                            platformSDKConfig.showVoltLogo,
                                                            platformSDKConfig.customLogoUrl,
                                                            platformSDKConfig.customSupportNumber
                                                        )
                                                    } else {
                                                        it?.initVoltSdk(
                                                            binding.etMobile.text.toString()
                                                                .toLong(),
                                                            false,
                                                           false,
                                                            "",
                                                            ""
                                                        )
                                                    }
                                                } else {
                                                    if (platformSDKConfig != null) {
                                                        it?.initVoltSdk(
                                                            null,
                                                            platformSDKConfig.showDefaultVoltHeader,
                                                            platformSDKConfig.showVoltLogo,
                                                            platformSDKConfig.customLogoUrl,
                                                            platformSDKConfig.customSupportNumber
                                                        )
                                                    } else {
                                                        it?.initVoltSdk(
                                                           null,
                                                            false,
                                                            false,
                                                            "",
                                                            ""
                                                        )
                                                    }
                                                }
                                            }
                                        },
                                        Response.ErrorListener { error ->
                                            Log.e("TAG", "Customer SSO Token is incorrect")
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Customer SSO token is incorrect",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }) {

                                        // Add headers to the request
                                        @Throws(AuthFailureError::class)
                                        override fun getHeaders(): MutableMap<String, String> {
                                            val headers = HashMap<String, String>()
                                            headers["X-AppPlatform"] = "VOLT_API_UAT"
                                            headers["requestReferenceId"] = "5eufmnf6phj"
                                            headers["Content-Type"] = "application/json"
                                            headers["Authorization"] =
                                                "Bearer ${binding.etPlatformAuthToken.text}"
                                            return headers
                                        }
                                    }
                                requestQueue.add(jsonObjectRequest)
                            },
                            Response.ErrorListener { error ->
                                // Handle error cases here
                                Log.e("TAG", "Error occurred: ${error.message}")
                            }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                var authToken = binding.etPlatformAuthToken.text.toString()
                                // Set your custom headers here
                                val headers = HashMap<String, String>()
                                headers["Authorization"] = "Bearer $authToken"
                                headers["X-AppPlatform"] = binding.etPlatform.text.toString()
                                // Add more headers as needed
                                return headers
                            }
                        }
                        requestQueue.add(stringRequest)
                    } else {
                        var target = binding.etTarget.text.toString().trim()
                        if (target != "" && target != "manageLimit" && target != "account" && target != "payment" && target != "withdraw") {
                            Log.e("TAG", "the target page does not exist")
                            Toast.makeText(
                                this, "The target page does not exist", Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            var getDetailsURL =
                                if (isStagingChecked) "https://api.staging.voltmoney.in/app/pf/details/" else "https://api.voltmoney.in/app/pf/details/"

                            val stringRequest = object : StringRequest(Request.Method.GET,
                                getDetailsURL,
                                Response.Listener { response ->
                                    // Handle the response here
                                    val gson = Gson()
                                    val responseData =
                                        gson.fromJson(response, ResponseData::class.java)
                                    val platformSDKConfig = responseData.platformSDKConfig
                                    Log.d("TAG", "Response: $platformSDKConfig")
                                    if (platformSDKConfig != null) {
                                        showVoltDefaultHeader =
                                            platformSDKConfig.showDefaultVoltHeader
                                    }
                                    if (platformSDKConfig != null) {
                                        showVoltLogo = platformSDKConfig.showVoltLogo
                                    }
                                    if (platformSDKConfig != null) {
                                        customLogoUrl = platformSDKConfig.customLogoUrl
                                    }
                                    if (platformSDKConfig != null) {
                                        customSupportNumber = platformSDKConfig.customSupportNumber
                                    }
                                    voltSDKContainer.let {
                                        if (binding.etMobile.text.toString().length == 10) {
                                            if (platformSDKConfig != null) {
                                                it?.initVoltSdk(
                                                    binding.etMobile.text.toString().toLong(),
                                                    platformSDKConfig.showDefaultVoltHeader,
                                                    platformSDKConfig.showVoltLogo,
                                                    platformSDKConfig.customLogoUrl,
                                                    platformSDKConfig.customSupportNumber,
                                                )
                                            } else {
                                                it?.initVoltSdk(
                                                    binding.etMobile.text.toString().toLong(),
                                                    false,
                                                    false,
                                                    "",
                                                    "",
                                                )
                                            }
                                        } else {
                                            if (platformSDKConfig != null) {
                                                it?.initVoltSdk(
                                                    null,
                                                    platformSDKConfig.showDefaultVoltHeader,
                                                    platformSDKConfig.showVoltLogo,
                                                    platformSDKConfig.customLogoUrl,
                                                    platformSDKConfig.customSupportNumber,
                                                )
                                            } else {
                                                it?.initVoltSdk(
                                                    null,
                                                    false,
                                                    false,
                                                    "",
                                                    "",
                                                )
                                            }                                        }
                                    }
                                },
                                Response.ErrorListener { error ->
                                    // Handle error cases here
                                    Log.e("TAG", "Error occurred: ${error.message}")
                                }) {
                                override fun getHeaders(): MutableMap<String, String> {
                                    var authToken = binding.etPlatformAuthToken.text.toString()
                                    // Set your custom headers here
                                    val headers = HashMap<String, String>()
                                    headers["Authorization"] = "Bearer $authToken"
                                    headers["X-AppPlatform"] = binding.etPlatform.text.toString()
                                    // Add more headers as needed
                                    return headers
                                }
                            }
                            requestQueue.add(stringRequest)
                        }
                    }
                }
            } else {
                var target = binding.etTarget.text.toString().trim()
                if (target != "" && target != "manageLimit" && target != "account" && target != "payment" && target != "withdraw") {
                    Log.e("TAG", "The target page does not exist")
                    Toast.makeText(
                        this, "The target page does not exist", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    var getDetailsURL =
                        if (isStagingChecked) "https://api.staging.voltmoney.in/app/pf/details/" else "https://api.voltmoney.in/app/pf/details/"

                    val stringRequest = object : StringRequest(Request.Method.GET,
                        getDetailsURL,
                        Response.Listener { response ->
                            // Handle the response here
                            val gson = Gson()
                            val responseData = gson.fromJson(response, ResponseData::class.java)
                            val platformSDKConfig = responseData.platformSDKConfig
                            Log.d("TAG", "Response: $platformSDKConfig")
                            voltSDKContainer.let {
                                if (binding.etMobile.text.toString().length == 10) {
                                    if (platformSDKConfig != null) {
                                        it?.initVoltSdk(
                                            binding.etMobile.text.toString().toLong(),
                                            platformSDKConfig.showDefaultVoltHeader,
                                            platformSDKConfig.showVoltLogo,
                                            platformSDKConfig.customLogoUrl,
                                            platformSDKConfig.customSupportNumber,
                                        )
                                    } else {
                                        it?.initVoltSdk(
                                            binding.etMobile.text.toString().toLong(),
                                            false,
                                            false,
                                            "",
                                            "",
                                        )
                                    }
                                } else {
                                    if (platformSDKConfig != null) {
                                        it?.initVoltSdk(
                                            null,
                                            platformSDKConfig.showDefaultVoltHeader,
                                            platformSDKConfig.showVoltLogo,
                                            platformSDKConfig.customLogoUrl,
                                            platformSDKConfig.customSupportNumber,
                                        )
                                    } else {
                                        it?.initVoltSdk(
                                            null,
                                            false,
                                            false,
                                            "",
                                            "",
                                        )
                                    }
                                }
                            }
                        },
                        Response.ErrorListener { error ->
                            // Handle error cases here
                            Log.e("TAG", "Error occurred: ${error.message}")
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            var authToken = binding.etPlatformAuthToken.text.toString()
                            // Set your custom headers here
                            val headers = HashMap<String, String>()
                            headers["Authorization"] = "Bearer $authToken"
                            headers["X-AppPlatform"] = binding.etPlatform.text.toString()
                            // Add more headers as needed
                            return headers
                        }
                    }
                    requestQueue.add(stringRequest)
                }
            }
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