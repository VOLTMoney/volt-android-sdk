# VoltMoney Android SDK - Example App.

This repo contains a sample andriod app, which consumes the Volt Money andriod SDK (published over jitpack). This code can be used as a reference code to integrate Volt Money SDK in your andriod app.

## Contents
* [Set up an environment](#set-up-an-environment)
* [Create an instance of VoltSDK](#create-voltsdk-instance)
* [Pre-create customer application](#precreate-application)
* [Init Volt Money Journey](#init-volt-money-journey)

## 1. Set up an environment

* Download Latest Android Studio and open this cloned repo as a new project.
* This sdk is published on jitpack, follow below steps to get it in your project:

Step 1. Add it in your root build.gradle at the end of repositories:
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Step 2. Add the dependency for VOLT SDK.
```
implementation 'com.github.VOLTMoney:volt-android-sdk:1.0'

```

## 2. Create an instance of VoltSDK

The first and mandatory step to integrate volt sdk is to create an instance of VoltSDKContainer class. The constructor to the class takes in configuration parameters which would be used while rendering content, these are:

1. **app_key (mandatory)**: Application's private key provided by the Volt Money team. If you don't have the key please contact the team at support@voltmoney.in.
2. **app_secret (mandatory)**: Application's private hash key provided by the Volt Money team. If you don't have the key please contact the team at support@voltmoney.in.
3. **partner_platform (mandatory)**: partner_platform, partner platform is the name/id provided to you by the Volt Money team to identify the SDK user. This is a mandatory field and will be passed as header in all API calls. If you don't have this 'id' please reach out to volt team @ support@voltmoney.in
4. **primary_color(optional)** : Primary color, hex code of the color to be used as primary color for Volt Money sdk. The UI will get automatically customized to use this color as primary color (ex. all CTA, Icons etc.)
5. **secondary_color(optional)** : Secondary color, hex code of the color to be used as secondary color for Volt Money sdk. The UI will get automatically customized to use this color. ex. all svgs, progress bar etc.)
6. **ref (optional)**: Ref, is short for referral code, referral code can be specific to partner/platform based on the use-case. If provided the user signing up would be associated with the partner/platform.

VoltSDKContainer instance can be created as follows:

  ```
  var voltSDKContainer = VoltSDKContainer(contex,
                "app_key",
                "app_secret",
                "partner_platform",
                "primary_color",
                "secondary_color",
                "ref")
  ```

## 3. Pre-create customer application (Optional)

Most of the applications which are consuming Volt Money SDK would already have customer details like mobile, phone, email and date of birth, which form the first 3 steps of the loan application process. We provide a way to simplify the user experience by pre-creating the application, this will skip the first 3 steps and will take the customers directly to the portfolio fetch steps. This is based on the  APIs provided by  Volt Money  to partners (documented here: https://volt-docs.readme.io/reference/createcreditapplicationusingpost). Hence the VOLTSDKContainer, provides a simple function which takes care of calling the API.

***Please note:*** you only need to call this API once for a customer, in case of duplicate API call it will throw an exception saying application is already created.

The api takes in the 4 customer details :

1. **PAN** : Customer's PAN number
2. **Mobile Number** : Customer's mobile number.
3. **email**: Customer's email address.
4. **dob**: Customer's date of birth.


```
// API to pre-create the application. 
voltSDKContainer.preCreateApplication(pan, mobile_number, email_address, date_of_birth);
```

The API will respond with standard http response statuses like 200 for success and 4xx for bad requests/auth failures etc.

* If you want to know the response of the create user api call, all you have to do is add the interface  `VoltAPIResponse` to calling activity and implement the `VoltAPIResponse`'s `preCreateAppAPIResponse()` method.
```
// Sample Implementation where we use preCreateAppResponse to create a toast based on API response. 

override fun preCreateAppAPIResponse(preCreateAppResponse: PreCreateAppResponse?, errorMsg: String?) {

        this.preCreateAppResponse =preCreateAppResponse
        if (preCreateAppResponse?.customerAccountId !=null) {
                Toast.makeText(
                    this,
                    "Customer Id is: "+this.createAppResponse?.customerAccountId.toString(),
                    Toast.LENGTH_SHORT
                ).show()
        }else{
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
        }
    }
```
Here createAppResponse dataclass is :
```
data class PreCreateAppResponse(
    val customerAccountId: String?,
    val customerCreditApplicationId: String?,
    val message: String?,
    val statusCode: String?,
    val violations: String?
)
```


## 4. Init Volt Money Journey.

Initialize/Resume customer journey by calling this API. The same API will be called every time customer want to open up the volt journey/dashboard, which step or state customer will be shown will be decided by Volt Money backend services, which manage the customer state hence the client app would not have to do it. You can simply call this API everytime the customer visit's VOLT SDK entry point, this API will open up VOLT Money's customized UI based on the parameters passed during the setup of sdk instance.

The API takes one optional parameter as input :

1. **Mobile Number** : Customer's mobile number. If passed it would autofill the mobile number in the mobile number text box on the login screen.


```
voltSDKContainer.initVoltSDK(mobileNumber)
```
 
