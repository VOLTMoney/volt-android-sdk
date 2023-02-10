# VoltMoney Android SDK

This repo contains code that will generate artifacts which can be consumed by client android mobile apps

## Contents
* [Set up an environment](#set-up-an-environment)
* [Project Structure](#project-structure)
* [VoltSDK Android library generation](#voltsdk-android-library-generation)
* [Invoke VoltSDK](#invoke-voltsdk)
* [Get VoltSDK CreateApp response](#get-voltsdk-createapp-response)
### Set up an environment

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
Step 2. Add the dependency
```
implementation 'com.github.VOLTMoney:volt-android-sdk:1.0'

```
### Project Structure
* This sample project contains two module
* voltsdk  -> This the module which contains aar(sdk) code base, if current open repo is volt-android-sdk-example then you wont see this module since an aar file has already been created and placed in there in libs folder of app module.
* app -> This dir contain volt sample app code

### VoltSDK Android library generation

* In order to generate aar lib for android we need to build the project from root using below command
  module:
  Note-: if you are in volt-android-sdk-example then you wont need this step as aar is already been generated and placed in libs dir of app module
```
  ./gradlw clean build
```
After successful run of above command the aar and apk(VoltMoneySample) will be generated on below location:

```
   volt-release.aar -> voltsdk/build/outputs/aar/volt-release.aar
   app-debug.apk -> app/build/outputs/apk/debug/app-debug.apk
```

**Note** - As `aar` file doesn't include dependency from shared module, while create a new android
project and adding this `aar` as lib, don't forget to add all dependency from from android section
of common build.gradle.

In this case we need to include below dependencies in project  `build.gradle` file :

```
    implementation files('libs/voltsdk-release.aar')
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'androidx.browser:browser:1.5.0-alpha02'
```
### Invoke VoltSDK
* In order to open voltSDK client must have to create Volt SDK instance by passing certain value(see below):
  ```
  var voltInstance = VoltSDKInstance(contex,
                "app_key",
                "app_secret",
                "ref",
                "primary_color",
                "secondary_color",
               "partner_platform " )
  ```
After creating voltsdk instance there are below two way by which VoltMoney app can be invoke
1. Invoke VoltSDK by passing user info(create app fun call)
```
voltInstance.startApplication(dob,email,mobileNumber,pan)
voltInstance.invokeVoltSdk(mobileNumber)

```
2. Invoke VoltSDK without creating user
```
voltInstance.invokeVoltSdk(mobileNumber)
```

### Get VoltSDK CreateApp response
* If you want to know the response of the create user api call, all you have to do is add the interface  `VoltAPIResponse` to calling activity and implement the `VoltAPIResponse`'s `createAppAPIResponse()` method.
```
override fun createAppAPIResponse(createAppResponse: CreateAppResponse?, errorMsg: String?) {

        this.createAppResponse =createAppResponse
        if (createAppResponse?.customerAccountId !=null) {
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
data class CreateAppResponse(
    val auth_token: String?,
    val customerAccountId: String?,
    val customerCreditApplicationId: String?,
    val message: String?,
    val statusCode: String?,
    val violations: String?
)
```
Note: auth_token will be null as we are not passing auth_token to client

