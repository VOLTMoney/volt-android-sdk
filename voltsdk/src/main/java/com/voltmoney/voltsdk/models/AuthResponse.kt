package com.voltmoney.voltsdk.models

data class AuthResponse(
    val auth_token: String?,
    val message: String?,
    val statusCode: String?
)