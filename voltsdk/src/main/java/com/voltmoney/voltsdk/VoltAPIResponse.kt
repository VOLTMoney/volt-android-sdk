package com.voltmoney.voltsdk

import com.voltmoney.voltsdk.models.CreateAppResponse

interface VoltAPIResponse {
    fun createAppAPIResponse(createAppResponse: CreateAppResponse?, errorMsg: String?)
}