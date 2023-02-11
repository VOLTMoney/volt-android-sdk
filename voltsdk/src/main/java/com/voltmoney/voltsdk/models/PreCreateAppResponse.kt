package com.voltmoney.voltsdk.models

data class PreCreateAppResponse(
    val auth_token: String?,
    val customerAccountId: String?,
    val customerCreditApplicationId: String?,
    val message: String?,
    val statusCode: String?,
    val violations: String?
)