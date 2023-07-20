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
    private val partner_platform: String,
    private val primary_color: String?,
    private var environment: ENVIRONMENT,
    private var headingTextColor: String = "",
    private var mobileNumber: String?,
    private var target: String?,
    private var customerSSToken: String?,
    private var customerCode: String?,
    private var voltPlatformCode: String?,
    private var platformAuthToken: String?,
    ) {
    var url =
        if (environment == ENVIRONMENT.STAGING) "https://app.staging.voltmoney.in/?partnerplatform" else "https://app.voltmoney.in/?partnerplatform"

    init {
        if (platformAuthToken?.trim() == null || platformAuthToken?.trim() == "") {
            Log.e("TAG", "Please enter Platform Auth Token")
        } else if (customerSSToken != "") {
            if (customerCode == "") {
                Log.e("TAG", "Please enter Customer Code")
            } else {
                if (target?.trim() == "") {
                    var getDetailsURL =
                        if (environment === ENVIRONMENT.STAGING) "https://api.staging.voltmoney.in/app/pf/details/" else "https://api.voltmoney.in/app/pf/details/"
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
                                if (environment === ENVIRONMENT.STAGING) "https://api.staging.voltmoney.in/api/client/validate/ssoToken/${customerCode}" else "https://api.voltmoney.in/api/client/validate/ssoToken/${customerCode}"
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
                            if (environment === ENVIRONMENT.STAGING) "https://api.staging.voltmoney.in/app/pf/details/" else "https://api.voltmoney.in/app/pf/details/"
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
                                    if (environment == ENVIRONMENT.STAGING) "https://api.staging.voltmoney.in/api/client/validate/ssoToken/${customerCode}" else "https://api.voltmoney.in/api/client/validate/ssoToken/${customerCode}"
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
                    if (environment == ENVIRONMENT.STAGING) "https://api.staging.voltmoney.in/app/pf/details/" else "https://api.voltmoney.in/app/pf/details/"
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
                        if (environment == ENVIRONMENT.STAGING) "https://api.staging.voltmoney.in/app/pf/details/" else "https://api.voltmoney.in/app/pf/details/"
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
        Log.e("TAG", "BVH entered VoltSDKContainer", )
    }

    var webView_url: String = "$url" +
            "&platform=$partner_platform" +
            "&primaryColor=$primary_color" +
            "&target=${target?.trim()}" +
            "&ssoToken=$customerSSToken" +
            "&voltPlatformCode=$voltPlatformCode"


    fun logoutSDK() {
        WebStorage.getInstance().deleteAllData()
    }
}
