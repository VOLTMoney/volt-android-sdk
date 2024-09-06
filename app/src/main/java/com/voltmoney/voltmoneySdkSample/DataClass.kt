package com.voltmoney.voltmoneySdkSample

data class ResponseData(
    val platformSDKConfig: PlatformSDKConfig?,
)

data class PlatformSDKConfig(
    var showVoltDefaultHeader: Boolean,
    var showVoltLogo: Boolean,
    var customLogoUrl: String,
    var customSupportNumber: String
)