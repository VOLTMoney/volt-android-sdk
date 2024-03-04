package com.voltmoney.voltsdk

import com.voltmoney.voltsdk.models.PreCreateAppResponse

interface VoltAPIResponse {
    fun preCreateAppAPIResponse(preCreateAppResponse: PreCreateAppResponse?, errorMsg: String?)
}