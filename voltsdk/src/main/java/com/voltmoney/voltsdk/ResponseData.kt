package com.voltmoney.voltsdk

data class ResponseData(
    val platformSDKConfig: PlatformSDKConfig?,
)

data class PlatformSDKConfig(
    var showDefaultVoltHeader: Boolean,
    var showVoltLogo: Boolean,
    var customLogoUrl: String,
    var customSupportNumber: String
)