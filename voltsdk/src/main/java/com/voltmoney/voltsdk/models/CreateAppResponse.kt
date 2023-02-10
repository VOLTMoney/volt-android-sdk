package com.voltmoney.voltsdk.models

data class CreateAppResponse(
    val customerAccountId: String?,
    val customerCreditApplicationId: String?,
    val message: String?,
    val statusCode: String?,
    val violations: String?
)