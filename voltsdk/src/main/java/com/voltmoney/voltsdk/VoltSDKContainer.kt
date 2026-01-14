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
import java.util.UUID
import com.android.volley.Response as VResponse

class VoltSDKContainer(
    private val context: Context,
    private val partner_platform: String,
    private var platformAuthToken: String?,
    private var environment: ENVIRONMENT?,
    private val primary_color: String?,
    private var headingTextColor: String = "",
    private var target: String?,
    private var customerSSToken: String?,
    private var customerCode: String?,
    private var showHeader: String?,
    private var secondary_color: String?,
    private var onExitSDK: ((String) -> Unit)? = null
) {

    init {
        Log.d("VOLT", "Init Volt SDK")

        if (platformAuthToken?.trim() == null || platformAuthToken?.trim() == "") {
            Log.e("VOLT", "Please enter Platform Auth Token")
        }
        else if (environment == null) {
            Log.e("VOLT", "Please enter Environment")
        }
        else {
            val getSDKUrl = if (environment == ENVIRONMENT.PRODUCTION) "https://api.voltmoney.in/v1/partner/platform/generate/sdk/url" else "https://api.staging.voltmoney.in/v1/partner/platform/generate/sdk/url"

            val jsonBody = JSONObject()
            jsonBody.put("sdkType" , "ANDROID_SDK")
            jsonBody.put("pColor" , primary_color?.replace("#",""))
            jsonBody.put("sColor" , secondary_color?.replace("#",""))
            jsonBody.put("customerSsoToken" , customerSSToken ?: "")
            jsonBody.put("target" , target ?: "")
            jsonBody.put("voltCustomerCode" , customerCode ?: "")




            val request = object : JsonObjectRequest(Method.POST, getSDKUrl, jsonBody,
                VResponse.Listener { response ->
                    // Handle successful response
                    val url = response.optString("url", "")  // Safe extraction from JSONObject
                    Log.d("VOLT", "Response: $url")

                    val intent = Intent(context , VoltWebViewActivity::class.java)
                    intent.putExtra("webViewUrl", url)
                                            intent.putExtra("primaryColor", primary_color)
                                            intent.putExtra("textColor", headingTextColor)
                                            intent.putExtra("showHeader", showHeader)
                                            intent.putExtra("onExitCallback", myCallback)
                                            intent.putExtra("secondaryColor", secondary_color)
                                            intent.putExtra(
                                                "voltPlatformCode",
                                                partner_platform
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
                                            VoltWebViewActivity.onExitVolt = {
                                                onExitSDK?.invoke(it)
                                            }

                },
                VResponse.ErrorListener { error ->
                    Log.e("VOLT", "Error: ${error.message}")
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $platformAuthToken"
                    headers["Content-Type"] = "application/json"
                    headers["requestReferenceId"] = UUID.randomUUID().toString()
                    headers["X-AppPlatform"] = partner_platform
                    return headers
                }
            }

            Volley.newRequestQueue(context).add(request)
        }
    }

    private var myCallback: MyCallback? = null


}