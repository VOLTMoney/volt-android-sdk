package com.voltmoney.voltsdk.models

data class CustomerDetails(
    val dob: String,
    val email: String,
    val mobileNumber: Long,
    val pan: String
)