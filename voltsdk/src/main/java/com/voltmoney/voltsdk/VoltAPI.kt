package com.voltmoney.voltsdk

import com.voltmoney.voltsdk.models.AuthData
import com.voltmoney.voltsdk.models.CreateAppResponse
import com.voltmoney.voltsdk.models.CreateApplicationData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.Objects

interface VoltAPI {
    @POST("/v1/partner/platform/auth/login")
    fun getAuthToken(@Body authData: AuthData):Call<CreateAppResponse>

    @POST("/v1/partner/platform/las/createCreditApplication")
    fun createApplication(@Body createApplicationData: CreateApplicationData, @Header("Authorization") bearerToken:String, @Header("X-AppPlatform") appPlatfrom:String):Call<CreateAppResponse>
}