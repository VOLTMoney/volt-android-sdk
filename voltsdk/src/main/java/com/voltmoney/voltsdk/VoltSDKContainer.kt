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
import com.android.volley.Response as VResponse

public var STAGING = "staging"

class VoltSDKContainer(
    private val context: Context,
    private val partner_platform: String,
    private var platformAuthToken: String?,
    private var environment: String?,
    private val primary_color: String?,
    private var headingTextColor: String = "",
    private var target: String?,
    private var customerSSToken: String?,
    private var customerCode: String?,
    private var showHeader: String?,
    private var secondary_color: String?,
    private var onExitSDK: (() -> Unit)? = null
) {
    var url = if (environment == STAGING)  "https://app.staging.voltmoney.in/?partnerplatform" else "https://app.voltmoney.in/?partnerplatform"

    init {
        if (platformAuthToken?.trim() == null || platformAuthToken?.trim() == "") {
            Log.e("TAG", "Please enter Platform Auth Token")
        } else if (customerSSToken != "") {
            if (customerCode == "") {
                Log.e("TAG", "Please enter Customer Code")
            } else {
                if (target?.trim() == "") {
                    var getDetailsURL =
                        if (environment == STAGING) "https://api.staging.voltmoney.in/app/pf/details/" else "https://api.voltmoney.in/app/pf/details/"
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
                            var validateSSOTokenURL =
                                if (environment == STAGING) "https://api.staging.voltmoney.in/api/client/validate/ssoToken/${customerCode}" else "https://api.voltmoney.in/api/client/validate/ssoToken/${customerCode}"
                            val requestQueue: RequestQueue =
                                Volley.newRequestQueue(context)

                            // Prepare the JSON object data
                            val jsonBody = JSONObject()
                            jsonBody.put(
                                "ssoToken", customerSSToken
                            )


                            val dashboardManageFieldsData =
                                platformSDKConfig?.dashboardManageFieldsData
                            val csPillData = platformSDKConfig?.csPillData
                            val showVoltBottomNavBar = platformSDKConfig?.showVoltBottomNavBar
                            val showPoweredByVoltMoney = platformSDKConfig?.showPoweredByVoltMoney
                            val showPostLoanJourney = platformSDKConfig?.showPostLoanJourney
                            val showMyAccountIcon = platformSDKConfig?.showMyAccountIcon
                            val showLogout = platformSDKConfig?.showLogout
                            val showHome = platformSDKConfig?.showHome
                            val showDashboardManageFields =
                                platformSDKConfig?.showDashboardManageFields
                            val showDashboardBenefitsForYou =
                                platformSDKConfig?.showDashboardBenefitsForYou
                            val showCSPill = platformSDKConfig?.showCSPill
                            val customLogoUrl = platformSDKConfig?.customLogoUrl
                            val customSupportNumber = platformSDKConfig?.customSupportNumber
                            val showTransactionHistory =
                                dashboardManageFieldsData?.showTransactionHistory
                            val showManageLimit = dashboardManageFieldsData?.showManageLimit
                            val showLoanClosure = dashboardManageFieldsData?.showLoanClosure
                            val showAccountDetails = dashboardManageFieldsData?.showAccountDetails
                            val waData = csPillData?.waData
                            val showWA = csPillData?.showWA
                            val showEmail = csPillData?.showEmail
                            val showCall = csPillData?.showCall
                            val emailData = csPillData?.emailData
                            val customIconUrl = csPillData?.customIconUrl
                            val callData = csPillData?.callData


                            val jsonObjectRequest =
                                object : JsonObjectRequest(Request.Method.POST,
                                    validateSSOTokenURL,
                                    jsonBody,
                                    VResponse.Listener { response ->
                                        if (platformSDKConfig != null) {
                                            webView_url +=
                                                "&showDefaultVoltHeader=${platformSDKConfig.showDefaultVoltHeader}" +
                                                        "&showVoltLogo=${platformSDKConfig.showVoltLogo}" +
                                                        "&customLogoUrl=${customLogoUrl}" +
                                                        "&customSupportNumber=${platformSDKConfig.customSupportNumber}" +
                                                        "&showVoltBottomNavBar=$showVoltBottomNavBar" +
                                                        "&showPoweredByVoltMoney=$showPoweredByVoltMoney" +
                                                        "&showPostLoanJourney=$showPostLoanJourney" +
                                                        "&showMyAccountIcon=$showMyAccountIcon" +
                                                        "&showLogout=$showLogout" +
                                                        "&showDashboardManageFields=$showDashboardManageFields" +
                                                        "&showHome=$showHome" +
                                                        "&showDashboardBenefitsForYou=$showDashboardBenefitsForYou" +
                                                        "&showCSPill=$showCSPill" +
                                                        "&customSupportNumber=$customSupportNumber" +
                                                        "&showTransactionHistory=$showTransactionHistory" +
                                                        "&showManageLimit=$showManageLimit" +
                                                        "&showLoanClosure=$showLoanClosure" +
                                                        "&showAccountDetails=$showAccountDetails" +
                                                        "&waData=$waData" +
                                                        "&showWA=$showWA" +
                                                        "&showEmail=$showEmail" +
                                                        "&showCall=$showCall" +
                                                        "&emailData=$emailData" +
                                                        "&customIconUrl=$customIconUrl" +
                                                        "&callData=$callData"
                                            val intent =
                                                Intent(context, VoltWebViewActivity::class.java)
                                            intent.putExtra("webViewUrl", webView_url)
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
                                                onExitSDK?.invoke()
                                            }
                                        } else {
                                            val intent =
                                                Intent(context, VoltWebViewActivity::class.java)
                                            intent.putExtra("webViewUrl", webView_url)
                                            intent.putExtra("primaryColor", primary_color)
                                            intent.putExtra("textColor", headingTextColor)
                                            intent.putExtra("showHeader", showHeader)
                                            intent.putExtra("secondaryColor", secondary_color)

                                            intent.putExtra(
                                                "voltPlatformCode",
                                                partner_platform
                                            )
                                            intent.putExtra("onExitCallback", myCallback)

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
                                                onExitSDK?.invoke()
                                            }
                                        }
                                    },
                                    VResponse.ErrorListener { error ->
                                        Log.e("TAG", "Customer SSO Token is incorrect")
                                    }) {
                                    @Throws(AuthFailureError::class)
                                    override fun getHeaders(): MutableMap<String, String> {
                                        val headers = HashMap<String, String>()
                                        headers["X-AppPlatform"] = "${partner_platform}"
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
                            headers["X-AppPlatform"] = "$partner_platform"
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
                            if (environment == STAGING) "https://api.staging.voltmoney.in/app/pf/details/" else "https://api.voltmoney.in/app/pf/details/"
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
                                var validateSSOTokenURL =
                                    if (environment == STAGING) "https://api.staging.voltmoney.in/api/client/validate/ssoToken/${customerCode}" else "https://api.voltmoney.in/api/client/validate/ssoToken/${customerCode}"
                                val requestQueue: RequestQueue =
                                    Volley.newRequestQueue(context)

                                // Prepare the JSON object data
                                val jsonBody = JSONObject()
                                jsonBody.put(
                                    "ssoToken", customerSSToken
                                )

                                val dashboardManageFieldsData =
                                    platformSDKConfig?.dashboardManageFieldsData
                                val csPillData = platformSDKConfig?.csPillData
                                val showVoltBottomNavBar = platformSDKConfig?.showVoltBottomNavBar
                                val showPoweredByVoltMoney =
                                    platformSDKConfig?.showPoweredByVoltMoney
                                val showPostLoanJourney = platformSDKConfig?.showPostLoanJourney
                                val showMyAccountIcon = platformSDKConfig?.showMyAccountIcon
                                val showLogout = platformSDKConfig?.showLogout
                                val showHome = platformSDKConfig?.showHome
                                val showDashboardManageFields =
                                    platformSDKConfig?.showDashboardManageFields
                                val showDashboardBenefitsForYou =
                                    platformSDKConfig?.showDashboardBenefitsForYou
                                val showCSPill = platformSDKConfig?.showCSPill
                                val customLogoUrl = platformSDKConfig?.customLogoUrl
                                val customSupportNumber = platformSDKConfig?.customSupportNumber
                                val showTransactionHistory =
                                    dashboardManageFieldsData?.showTransactionHistory
                                val showManageLimit = dashboardManageFieldsData?.showManageLimit
                                val showLoanClosure = dashboardManageFieldsData?.showLoanClosure
                                val showAccountDetails =
                                    dashboardManageFieldsData?.showAccountDetails
                                val waData = csPillData?.waData
                                val showWA = csPillData?.showWA
                                val showEmail = csPillData?.showEmail
                                val showCall = csPillData?.showCall
                                val emailData = csPillData?.emailData
                                val customIconUrl = csPillData?.customIconUrl
                                val callData = csPillData?.callData

                                val jsonObjectRequest =
                                    object : JsonObjectRequest(Request.Method.POST,
                                        validateSSOTokenURL,
                                        jsonBody,
                                        VResponse.Listener { response ->
                                            if (platformSDKConfig != null) {
                                                webView_url +=
                                                    "&showDefaultVoltHeader=${platformSDKConfig.showDefaultVoltHeader}" +
                                                            "&showVoltLogo=${platformSDKConfig.showVoltLogo}" +
                                                            "&customLogoUrl=${customLogoUrl}" +
                                                            "&customSupportNumber=${platformSDKConfig.customSupportNumber}" +
                                                            "&showVoltBottomNavBar=$showVoltBottomNavBar" +
                                                            "&showPoweredByVoltMoney=$showPoweredByVoltMoney" +
                                                            "&showPostLoanJourney=$showPostLoanJourney" +
                                                            "&showMyAccountIcon=$showMyAccountIcon" +
                                                            "&showLogout=$showLogout" +
                                                            "&showDashboardManageFields=$showDashboardManageFields" +
                                                            "&showHome=$showHome" +
                                                            "&showDashboardBenefitsForYou=$showDashboardBenefitsForYou" +
                                                            "&showCSPill=$showCSPill" +
                                                            "&customSupportNumber=$customSupportNumber" +
                                                            "&showTransactionHistory=$showTransactionHistory" +
                                                            "&showManageLimit=$showManageLimit" +
                                                            "&showLoanClosure=$showLoanClosure" +
                                                            "&showAccountDetails=$showAccountDetails" +
                                                            "&waData=$waData" +
                                                            "&showWA=$showWA" +
                                                            "&showEmail=$showEmail" +
                                                            "&showCall=$showCall" +
                                                            "&emailData=$emailData" +
                                                            "&customIconUrl=$customIconUrl" +
                                                            "&callData=$callData"
                                                val intent =
                                                    Intent(
                                                        context,
                                                        VoltWebViewActivity::class.java
                                                    )
                                                intent.putExtra("webViewUrl", webView_url)
                                                intent.putExtra("primaryColor", primary_color)
                                                intent.putExtra("textColor", headingTextColor)
                                                intent.putExtra("showHeader", showHeader)
                                                intent.putExtra("secondaryColor", secondary_color)
                                                intent.putExtra(
                                                    "voltPlatformCode",
                                                    partner_platform
                                                )
                                                intent.putExtra("onExitCallback", myCallback)

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
                                                VoltWebViewActivity.onExitVolt = {
                                                    onExitSDK?.invoke()
                                                }
                                            } else {
                                                val intent =
                                                    Intent(
                                                        context,
                                                        VoltWebViewActivity::class.java
                                                    )
                                                intent.putExtra("webViewUrl", webView_url)
                                                intent.putExtra("primaryColor", primary_color)
                                                intent.putExtra("textColor", headingTextColor)
                                                intent.putExtra("showHeader", showHeader)
                                                intent.putExtra("secondaryColor", secondary_color)
                                                intent.putExtra(
                                                    "voltPlatformCode",
                                                    partner_platform
                                                )
                                                intent.putExtra("onExitCallback", myCallback)

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
                                                VoltWebViewActivity.onExitVolt = {
                                                    onExitSDK?.invoke()
                                                }
                                            }
                                        },
                                        VResponse.ErrorListener { error ->
                                            Log.e("TAG", "Customer SSO Token is incorrect")
                                        }) {
                                        @Throws(AuthFailureError::class)
                                        override fun getHeaders(): MutableMap<String, String> {
                                            val headers = HashMap<String, String>()
                                            headers["X-AppPlatform"] = "${partner_platform}"
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
                                headers["X-AppPlatform"] = "$partner_platform"
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
                    if (environment == STAGING) "https://api.staging.voltmoney.in/app/pf/details/" else "https://api.voltmoney.in/app/pf/details/"
                val requestQueue: RequestQueue =
                    Volley.newRequestQueue(context)
                val stringRequest = object : StringRequest(Request.Method.GET,
                    getDetailsURL,
                    VResponse.Listener { response ->
                        // Handle the response here
                        val gson = Gson()
                        val responseData = gson.fromJson(response, ResponseData::class.java)

                        val platformSDKConfig = responseData.platformSDKConfig
                        val dashboardManageFieldsData = platformSDKConfig?.dashboardManageFieldsData
                        val csPillData = platformSDKConfig?.csPillData
                        val showVoltBottomNavBar = platformSDKConfig?.showVoltBottomNavBar
                        val showPoweredByVoltMoney = platformSDKConfig?.showPoweredByVoltMoney
                        val showPostLoanJourney = platformSDKConfig?.showPostLoanJourney
                        val showMyAccountIcon = platformSDKConfig?.showMyAccountIcon
                        val showLogout = platformSDKConfig?.showLogout
                        val showHome = platformSDKConfig?.showHome
                        val showDashboardManageFields = platformSDKConfig?.showDashboardManageFields
                        val showDashboardBenefitsForYou =
                            platformSDKConfig?.showDashboardBenefitsForYou
                        val showCSPill = platformSDKConfig?.showCSPill
                        val customLogoUrl = platformSDKConfig?.customLogoUrl
                        val customSupportNumber = platformSDKConfig?.customSupportNumber
                        val showTransactionHistory =
                            dashboardManageFieldsData?.showTransactionHistory
                        val showManageLimit = dashboardManageFieldsData?.showManageLimit
                        val showLoanClosure = dashboardManageFieldsData?.showLoanClosure
                        val showAccountDetails = dashboardManageFieldsData?.showAccountDetails
                        val waData = csPillData?.waData
                        val showWA = csPillData?.showWA
                        val showEmail = csPillData?.showEmail
                        val showCall = csPillData?.showCall
                        val emailData = csPillData?.emailData
                        val customIconUrl = csPillData?.customIconUrl
                        val callData = csPillData?.callData


                        if (platformSDKConfig != null) {
                            webView_url +=
                                "&showDefaultVoltHeader=${platformSDKConfig.showDefaultVoltHeader}" +
                                        "&showVoltLogo=${platformSDKConfig.showVoltLogo}" +
                                        "&customLogoUrl=${customLogoUrl}" +
                                        "&customSupportNumber=${platformSDKConfig.customSupportNumber}" +
                                        "&showVoltBottomNavBar=$showVoltBottomNavBar" +
                                        "&showPoweredByVoltMoney=$showPoweredByVoltMoney" +
                                        "&showPostLoanJourney=$showPostLoanJourney" +
                                        "&showMyAccountIcon=$showMyAccountIcon" +
                                        "&showLogout=$showLogout" +
                                        "&showDashboardManageFields=$showDashboardManageFields" +
                                        "&showHome=$showHome" +
                                        "&showDashboardBenefitsForYou=$showDashboardBenefitsForYou" +
                                        "&showCSPill=$showCSPill" +
                                        "&customSupportNumber=$customSupportNumber" +
                                        "&showTransactionHistory=$showTransactionHistory" +
                                        "&showManageLimit=$showManageLimit" +
                                        "&showLoanClosure=$showLoanClosure" +
                                        "&showAccountDetails=$showAccountDetails" +
                                        "&waData=$waData" +
                                        "&showWA=$showWA" +
                                        "&showEmail=$showEmail" +
                                        "&showCall=$showCall" +
                                        "&emailData=$emailData" +
                                        "&customIconUrl=$customIconUrl" +
                                        "&callData=$callData"

                            val intent = Intent(context, VoltWebViewActivity::class.java)
                            intent.putExtra("webViewUrl", webView_url)
                            intent.putExtra("primaryColor", primary_color)
                            intent.putExtra("textColor", headingTextColor)
                            intent.putExtra("voltPlatformCode", partner_platform)
                            intent.putExtra("showHeader", showHeader)
                            intent.putExtra("onExitCallback", myCallback)
                            intent.putExtra("secondaryColor", secondary_color)

                            if (target != "") intent.putExtra("target", target)
                            if (customerSSToken != "") intent.putExtra(
                                "customerSSToken",
                                customerSSToken
                            )

                            intent.putExtra("platformAuthToken", platformAuthToken)
                            startActivity(context, intent, null)
                            VoltWebViewActivity.onExitVolt = {
                                onExitSDK?.invoke()
                            }

                        } else {
                            val intent = Intent(context, VoltWebViewActivity::class.java)
                            intent.putExtra("webViewUrl", webView_url)
                            intent.putExtra("primaryColor", primary_color)
                            intent.putExtra("textColor", headingTextColor)
                            intent.putExtra("voltPlatformCode", partner_platform)
                            intent.putExtra("showHeader", showHeader)
                            intent.putExtra("secondaryColor", secondary_color)

                            if (target != "") intent.putExtra("target", target)
                            if (customerSSToken != "") intent.putExtra(
                                "customerSSToken",
                                customerSSToken
                            )
                            intent.putExtra("platformAuthToken", platformAuthToken)
                            startActivity(context, intent, null)
                            VoltWebViewActivity.onExitVolt = {
                                onExitSDK?.invoke()
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
                        headers["X-AppPlatform"] = "$partner_platform"
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
                        if (environment == STAGING) "https://api.staging.voltmoney.in/app/pf/details/" else "https://api.voltmoney.in/app/pf/details/"
                    val requestQueue: RequestQueue =
                        Volley.newRequestQueue(context)
                    val stringRequest = object : StringRequest(Request.Method.GET,
                        getDetailsURL,
                        VResponse.Listener { response ->
                            // Handle the response here
                            val gson = Gson()
                            val responseData = gson.fromJson(response, ResponseData::class.java)
                            val platformSDKConfig = responseData.platformSDKConfig

                            val dashboardManageFieldsData =
                                platformSDKConfig?.dashboardManageFieldsData
                            val csPillData = platformSDKConfig?.csPillData
                            val showVoltBottomNavBar = platformSDKConfig?.showVoltBottomNavBar
                            val showPoweredByVoltMoney = platformSDKConfig?.showPoweredByVoltMoney
                            val showPostLoanJourney = platformSDKConfig?.showPostLoanJourney
                            val showMyAccountIcon = platformSDKConfig?.showMyAccountIcon
                            val showLogout = platformSDKConfig?.showLogout
                            val showHome = platformSDKConfig?.showHome
                            val showDashboardManageFields =
                                platformSDKConfig?.showDashboardManageFields
                            val showDashboardBenefitsForYou =
                                platformSDKConfig?.showDashboardBenefitsForYou
                            val showCSPill = platformSDKConfig?.showCSPill
                            val customLogoUrl = platformSDKConfig?.customLogoUrl
                            val customSupportNumber = platformSDKConfig?.customSupportNumber
                            val showTransactionHistory =
                                dashboardManageFieldsData?.showTransactionHistory
                            val showManageLimit = dashboardManageFieldsData?.showManageLimit
                            val showLoanClosure = dashboardManageFieldsData?.showLoanClosure
                            val showAccountDetails = dashboardManageFieldsData?.showAccountDetails
                            val waData = csPillData?.waData
                            val showWA = csPillData?.showWA
                            val showEmail = csPillData?.showEmail
                            val showCall = csPillData?.showCall
                            val emailData = csPillData?.emailData
                            val customIconUrl = csPillData?.customIconUrl
                            val callData = csPillData?.callData


                            if (platformSDKConfig != null) {
                                webView_url +=
                                    "&showDefaultVoltHeader=${platformSDKConfig.showDefaultVoltHeader}" +
                                            "&showVoltLogo=${platformSDKConfig.showVoltLogo}" +
                                            "&customLogoUrl=${platformSDKConfig.customLogoUrl}" +
                                            "&customSupportNumber=${platformSDKConfig.customSupportNumber}" +
                                            "&showVoltBottomNavBar=$showVoltBottomNavBar" +
                                            "&showPoweredByVoltMoney=$showPoweredByVoltMoney" +
                                            "&showPostLoanJourney=$showPostLoanJourney" +
                                            "&showMyAccountIcon=$showMyAccountIcon" +
                                            "&showLogout=$showLogout" +
                                            "&showDashboardManageFields=$showDashboardManageFields" +
                                            "&showHome=$showHome" +
                                            "&showDashboardBenefitsForYou=$showDashboardBenefitsForYou" +
                                            "&showCSPill=$showCSPill" +
                                            "&customSupportNumber=$customSupportNumber" +
                                            "&showTransactionHistory=$showTransactionHistory" +
                                            "&showManageLimit=$showManageLimit" +
                                            "&showLoanClosure=$showLoanClosure" +
                                            "&showAccountDetails=$showAccountDetails" +
                                            "&waData=$waData" +
                                            "&showWA=$showWA" +
                                            "&showEmail=$showEmail" +
                                            "&showCall=$showCall" +
                                            "&emailData=$emailData" +
                                            "&customIconUrl=$customIconUrl" +
                                            "&callData=$callData"

                                val intent = Intent(context, VoltWebViewActivity::class.java)
                                intent.putExtra("webViewUrl", webView_url)
                                intent.putExtra("primaryColor", primary_color)
                                intent.putExtra("textColor", headingTextColor)
                                intent.putExtra("voltPlatformCode", partner_platform)
                                intent.putExtra("showHeader", showHeader)
                                intent.putExtra("onExitCallback", myCallback)
                                intent.putExtra("secondaryColor", secondary_color)

                                if (target != "") intent.putExtra("target", target)
                                if (customerSSToken != "") intent.putExtra(
                                    "customerSSToken",
                                    customerSSToken
                                )
                                intent.putExtra("platformAuthToken", platformAuthToken)
                                startActivity(context, intent, null)
                                VoltWebViewActivity.onExitVolt = {
                                    onExitSDK?.invoke()
                                }
                            } else {
                                val intent = Intent(context, VoltWebViewActivity::class.java)
                                intent.putExtra("webViewUrl", webView_url)
                                intent.putExtra("primaryColor", primary_color)
                                intent.putExtra("textColor", headingTextColor)
                                intent.putExtra("voltPlatformCode", partner_platform)
                                intent.putExtra("showHeader", showHeader)
                                intent.putExtra("onExitCallback", myCallback)
                                intent.putExtra("secondaryColor", secondary_color)

                                if (target != "") intent.putExtra("target", target)
                                if (customerSSToken != "") intent.putExtra(
                                    "customerSSToken",
                                    customerSSToken
                                )
                                intent.putExtra("platformAuthToken", platformAuthToken)
                                startActivity(context, intent, null)
                                VoltWebViewActivity.onExitVolt = {
                                    onExitSDK?.invoke()
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
                            headers["X-AppPlatform"] = "$partner_platform"
                            // Add more headers as needed
                            return headers
                        }
                    }
                    requestQueue.add(stringRequest)
                }
            }
        }
    }

    var webView_url: String = "$url" +
            "&platform=$partner_platform" +
            "&primaryColor=$primary_color" +
            "&target=${target?.trim()}" +
            "&isFromNativeSdk=${true}" +
            "&ssoToken=$customerSSToken" +
            "&voltPlatformCode=$partner_platform" +
            "&secondaryColor=$secondary_color"

    private var myCallback: MyCallback? = null

    fun setCallback(myCallback: MyCallback) {
        this.myCallback = myCallback
    }

    fun logoutSDK() {
        WebStorage.getInstance().deleteAllData()
    }
}