package com.voltmoney.voltsdk

data class ResponseData(
    val platformSDKConfig: PlatformSDKConfig?,
)

data class PlatformSDKConfig(
    var showVoltDefaultHeader: Boolean,
    var showVoltLogo: Boolean,
    var customLogoUrl: String,
    var customSupportNumber: String,
    var showVoltBottomNavBar: Boolean,
    var showPoweredByVoltMoney: Boolean,
    var showPostLoanJourney: Boolean,
    var showMyAccountIcon: Boolean,
    var showLogout: Boolean,
    var showHome: Boolean,
    var showDashboardManageFields: Boolean,
    var showDashboardBenefitsForYou: Boolean,
    var showCSPill: Boolean,
    var dashboardManageFieldsData: DashboardManageFieldsData,
    var csPillData: CSPillData
)

data class DashboardManageFieldsData (
    var showAccountDetails: Boolean,
    var showLoanClosure: Boolean,
    var showManageLimit: Boolean,
    var showTransactionHistory: Boolean
)

data class CSPillData (
    var callData: String,
    var customIconUrl: String,
    var emailData: String,
    var showCall: Boolean,
    var showEmail: Boolean,
    var showWA: Boolean,
    var waData: String
)