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


class VoltSDKContainer(
    private val context: Context,
    private val app_key: String,
    private val app_secret: String,
    private val partner_platform: String,
    private val primary_color: String?,
    private val secondary_color: String?,
    private val ref: String?,
    private val voltenv: VOLTENV=VOLTENV.STAGING
) {
    private var authToken:String?=null
    private var voltAPI: VoltAPI
    init {
        voltAPI = RetrofitHelper.getInstance().create(VoltAPI::class.java)
    }
    var webView_url:String = "${voltenv.baseurl}?" +
            "ref=$ref" +
            "&platform=$partner_platform" +
            "&primaryColor=$primary_color"
    fun preCreateApplication(dob:String,email:String,mobileNumber: Long,pan:String){
        val createApplicationData = CreateApplicationData(CustomerDetails(dob,email, mobileNumber,pan))
        voltAPI.getAuthToken(AuthData(app_key,app_secret)).enqueue(object:Callback<PreCreateAppResponse>{
            override fun onResponse(call: Call<PreCreateAppResponse>, response: Response<PreCreateAppResponse>) {
                if (response.body() !=null && response.code() ==200){
                    authToken = response.body()!!.auth_token.toString()
                   /* val createAppResponse = response.body() as PreCreateAppResponse
                    Log.d("ResVolt", createAppResponse.auth_token!!)
                    (context as VoltAPIResponse).createAppAPIResponse(createAppResponse,null)*/
                    voltAPI.createApplication(createApplicationData, "Bearer $authToken",partner_platform).enqueue(object:Callback<PreCreateAppResponse>{
                        override fun onResponse(
                            call: Call<PreCreateAppResponse>,
                            response: Response<PreCreateAppResponse>
                        ) {
                            if (response.body() != null) {
                                val preCreateAppResponse = response.body() as PreCreateAppResponse
                                Log.d("ResVolt", response.code().toString())
                                (context as VoltAPIResponse).preCreateAppAPIResponse(preCreateAppResponse,null)
                            }else{
                                if (response.errorBody() !=null) {
                                    val jObjError = JSONObject(response.errorBody()!!.string())
                                    val errorRes = jObjError.getString("message")
                                    (context as VoltAPIResponse).preCreateAppAPIResponse(null,errorRes)
                                }
                            }
                        }
                        override fun onFailure(call: Call<PreCreateAppResponse>, t: Throwable) {
                            Log.d("ResVolt", t.toString())
                        }
                    })
                }else {
                    if (response.errorBody() !=null) {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorRes = jObjError.getString("message")
                        (context as VoltAPIResponse).preCreateAppAPIResponse(null,errorRes)
                    }else{
                        (context as VoltAPIResponse).preCreateAppAPIResponse(null,"Invalid Credentials")
                    }
                }
            }
            override fun onFailure(call: Call<PreCreateAppResponse>, t: Throwable) {
                t.localizedMessage?.let { Log.d("ResVolt", it) }
            }
        })
        //write logic for creating application and upon success response from api update webView_url and open VoltWebViewActivity
    }
    fun initVoltSdk(mobileNumber: Long?) {
        if(mobileNumber.toString().length==10){
            webView_url+="&user=$mobileNumber"
        }
        val intent = Intent(context, VoltWebViewActivity::class.java)
        intent.putExtra("webViewUrl",webView_url)
        intent.putExtra("primaryColor",primary_color)
        startActivity(context,intent,null)
    }
}
