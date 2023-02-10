package com.voltmoney.voltsdk

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.voltmoney.voltsdk.models.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class VoltSDKInstance(
    private val context: Context,
    private val app_key: String,
    private val app_secret: String,
    private val ref: String?,
    private val primaryColor: String?,
    private val secondaryColor: String?,
    private val partnerPlatform: String
) {

    private var authToken:String?=null
    private var voltAPI: VoltAPI
    companion object
    {
        const val BASE_URL = "https://app.staging.voltmoney.in/partnerplatform"
    }

    init {
        voltAPI = RetrofitHelper.getInstance().create(VoltAPI::class.java)
    }
    var webView_url:String = "$BASE_URL?" +
            "ref=$ref" +
            "&primaryColor=$primaryColor" +
            "&platform=$partnerPlatform"

    fun generateToken() {
        //write logic for generating token using retrofit
        voltAPI.getAuthToken(AuthData(app_key,app_secret)).enqueue(object:Callback<AuthResponse>{
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    if (response.body() !=null && response.code() ==200){
                        authToken = response.body()!!.auth_token.toString()
                        val authResponse = response.body() as AuthResponse
                        Log.d("ResVolt", authResponse.auth_token!!)
                        (context as VoltAPIResponse).authAPIResponse(authResponse,null)
                    }else {
                        if (response.errorBody() !=null) {
                            val jObjError = JSONObject(response.errorBody()!!.string())
                            val errorRes = jObjError.getString("message")
                            (context as VoltAPIResponse).authAPIResponse(null,errorRes)
                        }
                    }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Log.d("ResVolt",t.localizedMessage)
            }
        })
    }

    fun startApplication(dob:String,email:String,mobileNumber: Long,pan:String){
        //write logic for creating application and upon success response from api update webView_url and open VoltWebViewActivity
        val createApplicationData = CreateApplicationData(CustomerDetails(dob,email, mobileNumber,pan))
        voltAPI.createApplication(createApplicationData, "Bearer $authToken",partnerPlatform).enqueue(object:Callback<CreateAppResponse>{
            override fun onResponse(
                call: Call<CreateAppResponse>,
                response: Response<CreateAppResponse>
            ) {
                if (response.body() != null) {
                        val createAppResponse = response.body() as CreateAppResponse
                        Log.d("ResVolt", response.code().toString())
                        (context as VoltAPIResponse).createAppAPIResponse(createAppResponse,null)
                }else{
                    if (response.errorBody() !=null) {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorRes = jObjError.getString("message")
                        (context as VoltAPIResponse).createAppAPIResponse(null,errorRes)
                    }
                }
            }
            override fun onFailure(call: Call<CreateAppResponse>, t: Throwable) {
                Log.d("ResVolt", t.toString())
            }
        })

    }
    fun invokeVoltSdk(mobileNumber: Long) {
        webView_url+="&user=$mobileNumber"
        val intent = Intent(context, VoltWebViewActivity::class.java)
        intent.putExtra("webViewUrl",webView_url)
        startActivity(context!!,intent,null)
    }
}