package com.voltmoney.voltsdk

import com.voltmoney.voltsdk.models.AuthResponse
import com.voltmoney.voltsdk.models.CreateAppResponse

interface VoltAPIResponse {
    fun authAPIResponse(authResponse: AuthResponse?, errorMsg:String?)
    fun createAppAPIResponse(createAppResponse: CreateAppResponse?, errorMsg: String?)
}