package com.voltmoney.voltsdk

import android.content.Context
import android.content.Intent
import android.util.Log
import android.webkit.WebStorage
import androidx.core.content.ContextCompat.startActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.voltmoney.voltsdk.models.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.android.volley.Response as VResponse


class VoltSDKContainer(
    private val context: Context,
    private val app_key: String,
    private val app_secret: String,
    private val partner_platform: String,
    private val primary_color: String?,
    private val secondary_color: String?,
    private val ref: String?,
    private var isStagingChecked: Boolean,
    private var headingTextColor: String = "",
    private var target: String?,
    private var customerSSToken: String?,
    private var customerCode: String?,
    private var voltPlatformCode: String?,
    private var utmSource: String?,
    private var utmCampaign: String?,
    private var utmToken: String?,
    private var platformAuthToken: String?,


    ) {
    private var authToken: String? = null
    private var voltAPI: VoltAPI = RetrofitHelper.getInstance().create(VoltAPI::class.java)

    var url =
        if (isStagingChecked) "https://app.staging.voltmoney.in/?partnerplatform" else "https://app.voltmoney.in/?partnerplatform"

    var webView_url: String = "$url" +
            "ref=$ref" +
            "&platform=$partner_platform" +
            "&primaryColor=$primary_color" +
            "&target=${target?.trim()}" +
            "&ssoToken=$customerSSToken" +
            "&voltPlatformCode=$voltPlatformCode" +
            "&utmSource=${if (utmSource == null) "" else utmSource}" +
            "&utmCampaign=${if (utmCampaign == null) "" else utmCampaign}" +
            "&utmToken=${if (utmToken == null) "" else utmToken}"

    fun preCreateApplication(dob: String, email: String, mobileNumber: Long, pan: String) {
        val createApplicationData =
            CreateApplicationData(CustomerDetails(dob, email, mobileNumber, pan))
        voltAPI.getAuthToken(AuthData(app_key, app_secret))
            .enqueue(object : Callback<PreCreateAppResponse> {
                override fun onResponse(
                    call: Call<PreCreateAppResponse>,
                    response: Response<PreCreateAppResponse>
                ) {
                    if (response.body() != null && response.code() == 200) {
                        authToken = response.body()!!.auth_token.toString()
                        /* val createAppResponse = response.body() as PreCreateAppResponse
                         Log.d("ResVolt", createAppResponse.auth_token!!)
                         (context as VoltAPIResponse).createAppAPIResponse(createAppResponse,null)*/
                        voltAPI.createApplication(
                            createApplicationData,
                            "Bearer $authToken",
                            partner_platform
                        ).enqueue(object : Callback<PreCreateAppResponse> {
                            override fun onResponse(
                                call: Call<PreCreateAppResponse>,
                                response: Response<PreCreateAppResponse>
                            ) {
                                if (response.body() != null) {
                                    val preCreateAppResponse =
                                        response.body() as PreCreateAppResponse
                                    Log.d("ResVolt", response.code().toString())
                                    (context as VoltAPIResponse).preCreateAppAPIResponse(
                                        preCreateAppResponse,
                                        null
                                    )
                                } else {
                                    if (response.errorBody() != null) {
                                        val jObjError = JSONObject(response.errorBody()!!.string())
                                        val errorRes = jObjError.getString("message")
                                        (context as VoltAPIResponse).preCreateAppAPIResponse(
                                            null,
                                            errorRes
                                        )
                                    }
                                }
                            }

                            override fun onFailure(call: Call<PreCreateAppResponse>, t: Throwable) {
                                Log.d("ResVolt", t.toString())
                            }
                        })
                    } else {
                        if (response.errorBody() != null) {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            val errorRes = jObjError.getString("message")
                            (context as VoltAPIResponse).preCreateAppAPIResponse(null, errorRes)
                        } else {
                            (context as VoltAPIResponse).preCreateAppAPIResponse(
                                null,
                                "Invalid Credentials"
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<PreCreateAppResponse>, t: Throwable) {
                    t.localizedMessage?.let { Log.d("ResVolt", it) }
                }
            })
        //write logic for creating application and upon success response from api update webView_url and open VoltWebViewActivity
    }

    fun initVoltSdk(
        mobileNumber: Long?,
    ) {
        if (platformAuthToken?.trim() == null || platformAuthToken?.trim() == "") {
            Log.e("TAG", "Please enter Platform Auth Token")
        } else if (customerSSToken != "") {
            if (customerCode == "") {
                Log.e("TAG", "Please enter Customer Code")
            } else {
                if (target?.trim() == "") {
                    var getDetailsURL =
                        if (isStagingChecked) "https://api.staging.voltmoney.in/app/pf/details/" else "https://api.voltmoney.in/app/pf/details/"
                    val requestQueue: RequestQueue =
                        Volley.newRequestQueue(context)
                    val stringRequest = object : StringRequest(
                        Request.Method.GET,
                        getDetailsURL,
                        VResponse.Listener { response ->
                            // Handle the response here
                            val gson = Gson()
                            val responseData = gson.fromJson(response, ResponseData::class.java)
                            val platformSDKConfig = responseData.platformSDKConfig
                            Log.d("TAG", "Response: $platformSDKConfig")
                            var validateSSOTokenURL =
                                if (isStagingChecked) "https://api.staging.voltmoney.in/api/client/validate/ssoToken/${customerCode}" else "https://api.voltmoney.in/api/client/validate/ssoToken/${customerCode}"
                            val requestQueue: RequestQueue =
                                Volley.newRequestQueue(context)

                            // Prepare the JSON object data
                            val jsonBody = JSONObject()
                            jsonBody.put(
                                "ssoToken", customerSSToken
                            )
                            val jsonObjectRequest =
                                object : JsonObjectRequest(Request.Method.POST,
                                    validateSSOTokenURL,
                                    jsonBody,
                                    VResponse.Listener { response ->
                                        if (mobileNumber.toString().length == 10) {
                                            if (platformSDKConfig != null) {
                                                if (mobileNumber.toString().length == 10) {
                                                    webView_url += "&user=$mobileNumber&showDefaultVoltHeader=${platformSDKConfig.showDefaultVoltHeader}&showVoltLogo=${platformSDKConfig.showVoltLogo}&customLogoUrl=${platformSDKConfig.customLogoUrl}&customSupportNumber=${platformSDKConfig.customSupportNumber}"
                                                } else {
                                                    webView_url += "showDefaultVoltHeader=${platformSDKConfig.showDefaultVoltHeader}&showVoltLogo=${platformSDKConfig.showVoltLogo}&customLogoUrl=${platformSDKConfig.customLogoUrl}&customSupportNumber=${platformSDKConfig.customSupportNumber}"
                                                }
                                                Log.d(
                                                    "TAG",
                                                    "BVH initVoltSdk Customer SSO $webView_url"
                                                )
                                                val intent =
                                                    Intent(context, VoltWebViewActivity::class.java)
                                                intent.putExtra("webViewUrl", webView_url)
                                                intent.putExtra("primaryColor", primary_color)
                                                intent.putExtra("textColor", headingTextColor)
                                                intent.putExtra(
                                                    "voltPlatformCode",
                                                    voltPlatformCode
                                                )
                                                if (target != "") intent.putExtra("target", target)
                                                if (customerSSToken != "") intent.putExtra(
                                                    "customerSSToken",
                                                    customerSSToken
                                                )
                                                if (utmSource != "") intent.putExtra(
                                                    "utmSource",
                                                    utmSource
                                                )
                                                if (utmCampaign != "") intent.putExtra(
                                                    "utmCampaign",
                                                    utmCampaign
                                                )
                                                if (utmToken != "") intent.putExtra(
                                                    "utmToken",
                                                    utmToken
                                                )
                                                intent.putExtra(
                                                    "platformAuthToken",
                                                    platformAuthToken
                                                )
                                                startActivity(context, intent, null)
                                            } else {
                                                if (mobileNumber.toString().length == 10) {
                                                    webView_url += "&user=$mobileNumber"
                                                }
                                                Log.d(
                                                    "TAG",
                                                    "BVH initVoltSdk Customer SSO $webView_url"
                                                )
                                                val intent =
                                                    Intent(context, VoltWebViewActivity::class.java)
                                                intent.putExtra("webViewUrl", webView_url)
                                                intent.putExtra("primaryColor", primary_color)
                                                intent.putExtra("textColor", headingTextColor)
                                                intent.putExtra(
                                                    "voltPlatformCode",
                                                    voltPlatformCode
                                                )
                                                if (target != "") intent.putExtra("target", target)
                                                if (customerSSToken != "") intent.putExtra(
                                                    "customerSSToken",
                                                    customerSSToken
                                                )
                                                if (utmSource != "") intent.putExtra(
                                                    "utmSource",
                                                    utmSource
                                                )
                                                if (utmCampaign != "") intent.putExtra(
                                                    "utmCampaign",
                                                    utmCampaign
                                                )
                                                if (utmToken != "") intent.putExtra(
                                                    "utmToken",
                                                    utmToken
                                                )
                                                intent.putExtra(
                                                    "platformAuthToken",
                                                    platformAuthToken
                                                )
                                                startActivity(context, intent, null)
                                            }
                                        } else {
                                            if (platformSDKConfig != null) {
                                                if (mobileNumber.toString().length == 10) {
                                                    webView_url += "&user=$mobileNumber&showDefaultVoltHeader=${platformSDKConfig.showDefaultVoltHeader}&showVoltLogo=${platformSDKConfig.showVoltLogo}&customLogoUrl=${platformSDKConfig.customLogoUrl}&customSupportNumber=${platformSDKConfig.customSupportNumber}"
                                                } else {
                                                    webView_url += "showDefaultVoltHeader=${platformSDKConfig.showDefaultVoltHeader}&showVoltLogo=${platformSDKConfig.showVoltLogo}&customLogoUrl=${platformSDKConfig.customLogoUrl}&customSupportNumber=${platformSDKConfig.customSupportNumber}"
                                                }
                                                val intent =
                                                    Intent(context, VoltWebViewActivity::class.java)
                                                intent.putExtra("webViewUrl", webView_url)
                                                intent.putExtra("primaryColor", primary_color)
                                                intent.putExtra("textColor", headingTextColor)
                                                intent.putExtra(
                                                    "voltPlatformCode",
                                                    voltPlatformCode
                                                )
                                                if (target != "") intent.putExtra("target", target)
                                                if (customerSSToken != "") intent.putExtra(
                                                    "customerSSToken",
                                                    customerSSToken
                                                )
                                                if (utmSource != "") intent.putExtra(
                                                    "utmSource",
                                                    utmSource
                                                )
                                                if (utmCampaign != "") intent.putExtra(
                                                    "utmCampaign",
                                                    utmCampaign
                                                )
                                                if (utmToken != "") intent.putExtra(
                                                    "utmToken",
                                                    utmToken
                                                )
                                                intent.putExtra(
                                                    "platformAuthToken",
                                                    platformAuthToken
                                                )
                                                startActivity(context, intent, null)
                                            } else {
                                                if (mobileNumber.toString().length == 10) {
                                                    webView_url += "&user=$mobileNumber"
                                                }
                                                val intent =
                                                    Intent(context, VoltWebViewActivity::class.java)
                                                intent.putExtra("webViewUrl", webView_url)
                                                intent.putExtra("primaryColor", primary_color)
                                                intent.putExtra("textColor", headingTextColor)
                                                intent.putExtra(
                                                    "voltPlatformCode",
                                                    voltPlatformCode
                                                )
                                                if (target != "") intent.putExtra("target", target)
                                                if (customerSSToken != "") intent.putExtra(
                                                    "customerSSToken",
                                                    customerSSToken
                                                )
                                                if (utmSource != "") intent.putExtra(
                                                    "utmSource",
                                                    utmSource
                                                )
                                                if (utmCampaign != "") intent.putExtra(
                                                    "utmCampaign",
                                                    utmCampaign
                                                )
                                                if (utmToken != "") intent.putExtra(
                                                    "utmToken",
                                                    utmToken
                                                )
                                                intent.putExtra(
                                                    "platformAuthToken",
                                                    platformAuthToken
                                                )
                                                startActivity(context, intent, null)
                                            }
                                        }
                                    },
                                    VResponse.ErrorListener { error ->
                                        Log.e("TAG", "Customer SSO Token is incorrect")
                                    }) {
                                    @Throws(AuthFailureError::class)
                                    override fun getHeaders(): MutableMap<String, String> {
                                        val headers = HashMap<String, String>()
                                        headers["X-AppPlatform"] = "VOLT_API_UAT"
                                        headers["requestReferenceId"] = "5eufmnf6phj"
                                        headers["Content-Type"] = "application/json"
                                        headers["Authorization"] =
                                            "Bearer ${platformAuthToken}"
                                        return headers
                                    }
                                }
                            requestQueue.add(jsonObjectRequest)
                        },
                        VResponse.ErrorListener { error ->
                            Log.e("TAG", "Volt Platform Auth Token is incorrect")
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            var authToken = platformAuthToken
                            // Set your custom headers here
                            val headers = HashMap<String, String>()
                            headers["Authorization"] = "Bearer $authToken"
                            headers["X-AppPlatform"] = "$voltPlatformCode"
                            // Add more headers as needed
                            return headers
                        }
                    }
                    requestQueue.add(stringRequest)
                } else {
                    if (target?.trim() != "manageLimit" && target?.trim() != "account" && target?.trim() != "payment" && target?.trim() != "withdraw") {
                        Log.e("TAG", "The target page does not exist")
                    } else {
                        var getDetailsURL =
                            if (isStagingChecked) "https://api.staging.voltmoney.in/app/pf/details/" else "https://api.voltmoney.in/app/pf/details/"
                        val requestQueue: RequestQueue =
                            Volley.newRequestQueue(context)
                        val stringRequest = object : StringRequest(
                            Request.Method.GET,
                            getDetailsURL,
                            VResponse.Listener { response ->
                                // Handle the response here
                                val gson = Gson()
                                val responseData = gson.fromJson(response, ResponseData::class.java)
                                val platformSDKConfig = responseData.platformSDKConfig
                                Log.d("TAG", "Response: $platformSDKConfig")
                                var validateSSOTokenURL =
                                    if (isStagingChecked) "https://api.staging.voltmoney.in/api/client/validate/ssoToken/${customerCode}" else "https://api.voltmoney.in/api/client/validate/ssoToken/${customerCode}"
                                val requestQueue: RequestQueue =
                                    Volley.newRequestQueue(context)

                                // Prepare the JSON object data
                                val jsonBody = JSONObject()
                                jsonBody.put(
                                    "ssoToken", customerSSToken
                                )
                                val jsonObjectRequest =
                                    object : JsonObjectRequest(Request.Method.POST,
                                        validateSSOTokenURL,
                                        jsonBody,
                                        VResponse.Listener { response ->
                                            if (mobileNumber.toString().length == 10) {
                                                if (platformSDKConfig != null) {
                                                    if (mobileNumber.toString().length == 10) {
                                                        webView_url += "&user=$mobileNumber&showDefaultVoltHeader=${platformSDKConfig.showDefaultVoltHeader}&showVoltLogo=${platformSDKConfig.showVoltLogo}&customLogoUrl=${platformSDKConfig.customLogoUrl}&customSupportNumber=${platformSDKConfig.customSupportNumber}"
                                                    } else {
                                                        webView_url += "showDefaultVoltHeader=${platformSDKConfig.showDefaultVoltHeader}&showVoltLogo=${platformSDKConfig.showVoltLogo}&customLogoUrl=${platformSDKConfig.customLogoUrl}&customSupportNumber=${platformSDKConfig.customSupportNumber}"
                                                    }
                                                    val intent =
                                                        Intent(
                                                            context,
                                                            VoltWebViewActivity::class.java
                                                        )
                                                    intent.putExtra("webViewUrl", webView_url)
                                                    intent.putExtra("primaryColor", primary_color)
                                                    intent.putExtra("textColor", headingTextColor)
                                                    intent.putExtra(
                                                        "voltPlatformCode",
                                                        voltPlatformCode
                                                    )
                                                    if (target != "") intent.putExtra(
                                                        "target",
                                                        target
                                                    )
                                                    if (customerSSToken != "") intent.putExtra(
                                                        "customerSSToken",
                                                        customerSSToken
                                                    )
                                                    if (utmSource != "") intent.putExtra(
                                                        "utmSource",
                                                        utmSource
                                                    )
                                                    if (utmCampaign != "") intent.putExtra(
                                                        "utmCampaign",
                                                        utmCampaign
                                                    )
                                                    if (utmToken != "") intent.putExtra(
                                                        "utmToken",
                                                        utmToken
                                                    )
                                                    intent.putExtra(
                                                        "platformAuthToken",
                                                        platformAuthToken
                                                    )
                                                    startActivity(context, intent, null)
                                                } else {
                                                    if (mobileNumber.toString().length == 10) {
                                                        webView_url += "&user=$mobileNumber"
                                                    }
                                                    val intent =
                                                        Intent(
                                                            context,
                                                            VoltWebViewActivity::class.java
                                                        )
                                                    intent.putExtra("webViewUrl", webView_url)
                                                    intent.putExtra("primaryColor", primary_color)
                                                    intent.putExtra("textColor", headingTextColor)
                                                    intent.putExtra(
                                                        "voltPlatformCode",
                                                        voltPlatformCode
                                                    )
                                                    if (target != "") intent.putExtra(
                                                        "target",
                                                        target
                                                    )
                                                    if (customerSSToken != "") intent.putExtra(
                                                        "customerSSToken",
                                                        customerSSToken
                                                    )
                                                    if (utmSource != "") intent.putExtra(
                                                        "utmSource",
                                                        utmSource
                                                    )
                                                    if (utmCampaign != "") intent.putExtra(
                                                        "utmCampaign",
                                                        utmCampaign
                                                    )
                                                    if (utmToken != "") intent.putExtra(
                                                        "utmToken",
                                                        utmToken
                                                    )
                                                    intent.putExtra(
                                                        "platformAuthToken",
                                                        platformAuthToken
                                                    )
                                                    startActivity(context, intent, null)
                                                }
                                            } else {
                                                if (platformSDKConfig != null) {
                                                    if (mobileNumber.toString().length == 10) {
                                                        webView_url += "&user=$mobileNumber&showDefaultVoltHeader=${platformSDKConfig.showDefaultVoltHeader}&showVoltLogo=${platformSDKConfig.showVoltLogo}&customLogoUrl=${platformSDKConfig.customLogoUrl}&customSupportNumber=${platformSDKConfig.customSupportNumber}"
                                                    } else {
                                                        webView_url += "showDefaultVoltHeader=${platformSDKConfig.showDefaultVoltHeader}&showVoltLogo=${platformSDKConfig.showVoltLogo}&customLogoUrl=${platformSDKConfig.customLogoUrl}&customSupportNumber=${platformSDKConfig.customSupportNumber}"
                                                    }
                                                    val intent =
                                                        Intent(
                                                            context,
                                                            VoltWebViewActivity::class.java
                                                        )
                                                    intent.putExtra("webViewUrl", webView_url)
                                                    intent.putExtra("primaryColor", primary_color)
                                                    intent.putExtra("textColor", headingTextColor)
                                                    intent.putExtra(
                                                        "voltPlatformCode",
                                                        voltPlatformCode
                                                    )
                                                    if (target != "") intent.putExtra(
                                                        "target",
                                                        target
                                                    )
                                                    if (customerSSToken != "") intent.putExtra(
                                                        "customerSSToken",
                                                        customerSSToken
                                                    )
                                                    if (utmSource != "") intent.putExtra(
                                                        "utmSource",
                                                        utmSource
                                                    )
                                                    if (utmCampaign != "") intent.putExtra(
                                                        "utmCampaign",
                                                        utmCampaign
                                                    )
                                                    if (utmToken != "") intent.putExtra(
                                                        "utmToken",
                                                        utmToken
                                                    )
                                                    intent.putExtra(
                                                        "platformAuthToken",
                                                        platformAuthToken
                                                    )
                                                    startActivity(context, intent, null)
                                                } else {
                                                    if (mobileNumber.toString().length == 10) {
                                                        webView_url += "&user=$mobileNumber"
                                                    }
                                                    val intent =
                                                        Intent(
                                                            context,
                                                            VoltWebViewActivity::class.java
                                                        )
                                                    intent.putExtra("webViewUrl", webView_url)
                                                    intent.putExtra("primaryColor", primary_color)
                                                    intent.putExtra("textColor", headingTextColor)
                                                    intent.putExtra(
                                                        "voltPlatformCode",
                                                        voltPlatformCode
                                                    )
                                                    if (target != "") intent.putExtra(
                                                        "target",
                                                        target
                                                    )
                                                    if (customerSSToken != "") intent.putExtra(
                                                        "customerSSToken",
                                                        customerSSToken
                                                    )
                                                    if (utmSource != "") intent.putExtra(
                                                        "utmSource",
                                                        utmSource
                                                    )
                                                    if (utmCampaign != "") intent.putExtra(
                                                        "utmCampaign",
                                                        utmCampaign
                                                    )
                                                    if (utmToken != "") intent.putExtra(
                                                        "utmToken",
                                                        utmToken
                                                    )
                                                    intent.putExtra(
                                                        "platformAuthToken",
                                                        platformAuthToken
                                                    )
                                                    startActivity(context, intent, null)
                                                }
                                            }
                                        },
                                        VResponse.ErrorListener { error ->
                                            Log.e("TAG", "Customer SSO Token is incorrect")
                                        }) {
                                        @Throws(AuthFailureError::class)
                                        override fun getHeaders(): MutableMap<String, String> {
                                            val headers = HashMap<String, String>()
                                            headers["X-AppPlatform"] = "VOLT_API_UAT"
                                            headers["requestReferenceId"] = "5eufmnf6phj"
                                            headers["Content-Type"] = "application/json"
                                            headers["Authorization"] =
                                                "Bearer ${platformAuthToken}"
                                            return headers
                                        }
                                    }
                                requestQueue.add(jsonObjectRequest)
                            },
                            VResponse.ErrorListener { error ->
                                Log.e("TAG", "Volt Platform Auth token is incorrect")
                            }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                var authToken = platformAuthToken
                                // Set your custom headers here
                                val headers = HashMap<String, String>()
                                headers["Authorization"] = "Bearer $authToken"
                                headers["X-AppPlatform"] = "$voltPlatformCode"
                                // Add more headers as needed
                                return headers
                            }
                        }
                        requestQueue.add(stringRequest)
                    }
                }
            }
        } else {
            if (target == "") {
                var getDetailsURL =
                    if (isStagingChecked) "https://api.staging.voltmoney.in/app/pf/details/" else "https://api.voltmoney.in/app/pf/details/"
                Log.d("TAG", "BHAV initVoltSdk: ${getDetailsURL}")
                val requestQueue: RequestQueue =
                    Volley.newRequestQueue(context)
                val stringRequest = object : StringRequest(Request.Method.GET,
                    getDetailsURL,
                    VResponse.Listener { response ->
                        // Handle the response here
                        val gson = Gson()
                        val responseData = gson.fromJson(response, ResponseData::class.java)
                        val platformSDKConfig = responseData.platformSDKConfig
                        Log.d("TAG", "Response: $platformSDKConfig")
                        if (mobileNumber.toString().length == 10) {
                            if (platformSDKConfig != null) {
                                if (mobileNumber.toString().length == 10) {
                                    webView_url += "&user=$mobileNumber&showDefaultVoltHeader=${platformSDKConfig.showDefaultVoltHeader}&showVoltLogo=${platformSDKConfig.showVoltLogo}&customLogoUrl=${platformSDKConfig.customLogoUrl}&customSupportNumber=${platformSDKConfig.customSupportNumber}"
                                } else {
                                    webView_url += "showDefaultVoltHeader=${platformSDKConfig.showDefaultVoltHeader}&showVoltLogo=${platformSDKConfig.showVoltLogo}&customLogoUrl=${platformSDKConfig.customLogoUrl}&customSupportNumber=${platformSDKConfig.customSupportNumber}"
                                }
                                val intent = Intent(context, VoltWebViewActivity::class.java)
                                intent.putExtra("webViewUrl", webView_url)
                                intent.putExtra("primaryColor", primary_color)
                                intent.putExtra("textColor", headingTextColor)
                                intent.putExtra("voltPlatformCode", voltPlatformCode)
                                if (target != "") intent.putExtra("target", target)
                                if (customerSSToken != "") intent.putExtra(
                                    "customerSSToken",
                                    customerSSToken
                                )
                                if (utmSource != "") intent.putExtra("utmSource", utmSource)
                                if (utmCampaign != "") intent.putExtra(
                                    "utmCampaign",
                                    utmCampaign
                                )
                                if (utmToken != "") intent.putExtra("utmToken", utmToken)
                                intent.putExtra("platformAuthToken", platformAuthToken)
                                startActivity(context, intent, null)
                            } else {
                                if (mobileNumber.toString().length == 10) {
                                    webView_url += "&user=$mobileNumber"
                                }
                                val intent = Intent(context, VoltWebViewActivity::class.java)
                                intent.putExtra("webViewUrl", webView_url)
                                intent.putExtra("primaryColor", primary_color)
                                intent.putExtra("textColor", headingTextColor)
                                intent.putExtra("voltPlatformCode", voltPlatformCode)
                                if (target != "") intent.putExtra("target", target)
                                if (customerSSToken != "") intent.putExtra(
                                    "customerSSToken",
                                    customerSSToken
                                )
                                if (utmSource != "") intent.putExtra("utmSource", utmSource)
                                if (utmCampaign != "") intent.putExtra(
                                    "utmCampaign",
                                    utmCampaign
                                )
                                if (utmToken != "") intent.putExtra("utmToken", utmToken)
                                intent.putExtra("platformAuthToken", platformAuthToken)
                                startActivity(context, intent, null)
                            }
                        } else {
                            if (platformSDKConfig != null) {
                                if (mobileNumber.toString().length == 10) {
                                    webView_url += "&user=$mobileNumber&showDefaultVoltHeader=${platformSDKConfig.showDefaultVoltHeader}&showVoltLogo=${platformSDKConfig.showVoltLogo}&customLogoUrl=${platformSDKConfig.customLogoUrl}&customSupportNumber=${platformSDKConfig.customSupportNumber}"
                                } else {
                                    webView_url += "showDefaultVoltHeader=${platformSDKConfig.showDefaultVoltHeader}&showVoltLogo=${platformSDKConfig.showVoltLogo}&customLogoUrl=${platformSDKConfig.customLogoUrl}&customSupportNumber=${platformSDKConfig.customSupportNumber}"
                                }
                                val intent = Intent(context, VoltWebViewActivity::class.java)
                                intent.putExtra("webViewUrl", webView_url)
                                intent.putExtra("primaryColor", primary_color)
                                intent.putExtra("textColor", headingTextColor)
                                intent.putExtra("voltPlatformCode", voltPlatformCode)
                                if (target != "") intent.putExtra("target", target)
                                if (customerSSToken != "") intent.putExtra(
                                    "customerSSToken",
                                    customerSSToken
                                )
                                if (utmSource != "") intent.putExtra("utmSource", utmSource)
                                if (utmCampaign != "") intent.putExtra(
                                    "utmCampaign",
                                    utmCampaign
                                )
                                if (utmToken != "") intent.putExtra("utmToken", utmToken)
                                intent.putExtra("platformAuthToken", platformAuthToken)
                                startActivity(context, intent, null)
                            } else {
                                if (mobileNumber.toString().length == 10) {
                                    webView_url += "&user=$mobileNumber"
                                }
                                val intent = Intent(context, VoltWebViewActivity::class.java)
                                intent.putExtra("webViewUrl", webView_url)
                                intent.putExtra("primaryColor", primary_color)
                                intent.putExtra("textColor", headingTextColor)
                                intent.putExtra("voltPlatformCode", voltPlatformCode)
                                if (target != "") intent.putExtra("target", target)
                                if (customerSSToken != "") intent.putExtra(
                                    "customerSSToken",
                                    customerSSToken
                                )
                                if (utmSource != "") intent.putExtra("utmSource", utmSource)
                                if (utmCampaign != "") intent.putExtra(
                                    "utmCampaign",
                                    utmCampaign
                                )
                                if (utmToken != "") intent.putExtra("utmToken", utmToken)
                                intent.putExtra("platformAuthToken", platformAuthToken)
                                startActivity(context, intent, null)
                            }
                        }
                    },
                    VResponse.ErrorListener { error ->
                        Log.e("TAG", "Volt Platform Auth token is incorrect")
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        var authToken = platformAuthToken
                        // Set your custom headers here
                        val headers = HashMap<String, String>()
                        headers["Authorization"] = "Bearer $authToken"
                        headers["X-AppPlatform"] = "$voltPlatformCode"
                        // Add more headers as needed
                        return headers
                    }
                }
                requestQueue.add(stringRequest)

            } else {
                if (target?.trim() != "manageLimit" && target?.trim() != "account" && target?.trim() != "payment" && target?.trim() != "withdraw") {
                    Log.e("TAG", "The target page does not exist")
                } else {
                    var getDetailsURL =
                        if (isStagingChecked) "https://api.staging.voltmoney.in/app/pf/details/" else "https://api.voltmoney.in/app/pf/details/"
                    Log.d("TAG", "BHAV initVoltSdk: ${getDetailsURL}")
                    val requestQueue: RequestQueue =
                        Volley.newRequestQueue(context)
                    val stringRequest = object : StringRequest(Request.Method.GET,
                        getDetailsURL,
                        VResponse.Listener { response ->
                            // Handle the response here
                            val gson = Gson()
                            val responseData = gson.fromJson(response, ResponseData::class.java)
                            val platformSDKConfig = responseData.platformSDKConfig
                            Log.d("TAG", "Response: $platformSDKConfig")
                            if (mobileNumber.toString().length == 10) {
                                if (platformSDKConfig != null) {
                                    if (mobileNumber.toString().length == 10) {
                                        webView_url += "&user=$mobileNumber&showDefaultVoltHeader=${platformSDKConfig.showDefaultVoltHeader}&showVoltLogo=${platformSDKConfig.showVoltLogo}&customLogoUrl=${platformSDKConfig.customLogoUrl}&customSupportNumber=${platformSDKConfig.customSupportNumber}"
                                    } else {
                                        webView_url += "showDefaultVoltHeader=${platformSDKConfig.showDefaultVoltHeader}&showVoltLogo=${platformSDKConfig.showVoltLogo}&customLogoUrl=${platformSDKConfig.customLogoUrl}&customSupportNumber=${platformSDKConfig.customSupportNumber}"
                                    }
                                    val intent = Intent(context, VoltWebViewActivity::class.java)
                                    intent.putExtra("webViewUrl", webView_url)
                                    intent.putExtra("primaryColor", primary_color)
                                    intent.putExtra("textColor", headingTextColor)
                                    intent.putExtra("voltPlatformCode", voltPlatformCode)
                                    if (target != "") intent.putExtra("target", target)
                                    if (customerSSToken != "") intent.putExtra(
                                        "customerSSToken",
                                        customerSSToken
                                    )
                                    if (utmSource != "") intent.putExtra("utmSource", utmSource)
                                    if (utmCampaign != "") intent.putExtra(
                                        "utmCampaign",
                                        utmCampaign
                                    )
                                    if (utmToken != "") intent.putExtra("utmToken", utmToken)
                                    intent.putExtra("platformAuthToken", platformAuthToken)
                                    startActivity(context, intent, null)
                                } else {
                                    if (mobileNumber.toString().length == 10) {
                                        webView_url += "&user=$mobileNumber"
                                    }
                                    val intent = Intent(context, VoltWebViewActivity::class.java)
                                    intent.putExtra("webViewUrl", webView_url)
                                    intent.putExtra("primaryColor", primary_color)
                                    intent.putExtra("textColor", headingTextColor)
                                    intent.putExtra("voltPlatformCode", voltPlatformCode)
                                    if (target != "") intent.putExtra("target", target)
                                    if (customerSSToken != "") intent.putExtra(
                                        "customerSSToken",
                                        customerSSToken
                                    )
                                    if (utmSource != "") intent.putExtra("utmSource", utmSource)
                                    if (utmCampaign != "") intent.putExtra(
                                        "utmCampaign",
                                        utmCampaign
                                    )
                                    if (utmToken != "") intent.putExtra("utmToken", utmToken)
                                    intent.putExtra("platformAuthToken", platformAuthToken)
                                    startActivity(context, intent, null)
                                }
                            } else {
                                if (platformSDKConfig != null) {
                                    if (mobileNumber.toString().length == 10) {
                                        webView_url += "&user=$mobileNumber&showDefaultVoltHeader=${platformSDKConfig.showDefaultVoltHeader}&showVoltLogo=${platformSDKConfig.showVoltLogo}&customLogoUrl=${platformSDKConfig.customLogoUrl}&customSupportNumber=${platformSDKConfig.customSupportNumber}"
                                    } else {
                                        webView_url += "showDefaultVoltHeader=${platformSDKConfig.showDefaultVoltHeader}&showVoltLogo=${platformSDKConfig.showVoltLogo}&customLogoUrl=${platformSDKConfig.customLogoUrl}&customSupportNumber=${platformSDKConfig.customSupportNumber}"
                                    }
                                    val intent = Intent(context, VoltWebViewActivity::class.java)
                                    intent.putExtra("webViewUrl", webView_url)
                                    intent.putExtra("primaryColor", primary_color)
                                    intent.putExtra("textColor", headingTextColor)
                                    intent.putExtra("voltPlatformCode", voltPlatformCode)
                                    if (target != "") intent.putExtra("target", target)
                                    if (customerSSToken != "") intent.putExtra(
                                        "customerSSToken",
                                        customerSSToken
                                    )
                                    if (utmSource != "") intent.putExtra("utmSource", utmSource)
                                    if (utmCampaign != "") intent.putExtra(
                                        "utmCampaign",
                                        utmCampaign
                                    )
                                    if (utmToken != "") intent.putExtra("utmToken", utmToken)
                                    intent.putExtra("platformAuthToken", platformAuthToken)
                                    startActivity(context, intent, null)
                                } else {
                                    if (mobileNumber.toString().length == 10) {
                                        webView_url += "&user=$mobileNumber"
                                    }
                                    val intent = Intent(context, VoltWebViewActivity::class.java)
                                    intent.putExtra("webViewUrl", webView_url)
                                    intent.putExtra("primaryColor", primary_color)
                                    intent.putExtra("textColor", headingTextColor)
                                    intent.putExtra("voltPlatformCode", voltPlatformCode)
                                    if (target != "") intent.putExtra("target", target)
                                    if (customerSSToken != "") intent.putExtra(
                                        "customerSSToken",
                                        customerSSToken
                                    )
                                    if (utmSource != "") intent.putExtra("utmSource", utmSource)
                                    if (utmCampaign != "") intent.putExtra(
                                        "utmCampaign",
                                        utmCampaign
                                    )
                                    if (utmToken != "") intent.putExtra("utmToken", utmToken)
                                    intent.putExtra("platformAuthToken", platformAuthToken)
                                    startActivity(context, intent, null)
                                }
                            }
                        },
                        VResponse.ErrorListener { error ->
                            Log.e("TAG", "Volt Platform Auth token is incorrect")
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            var authToken = platformAuthToken
                            // Set your custom headers here
                            val headers = HashMap<String, String>()
                            headers["Authorization"] = "Bearer $authToken"
                            headers["X-AppPlatform"] = "$voltPlatformCode"
                            // Add more headers as needed
                            return headers
                        }
                    }
                    requestQueue.add(stringRequest)
                }
            }
        }

    }

    fun logoutSDK() {
        WebStorage.getInstance().deleteAllData()
    }
}
