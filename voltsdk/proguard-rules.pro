# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable
# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Your existing SDK rules
-keep class com.voltmoney.voltsdk.VoltSDKContainer { public protected <methods>; }
-keep class com.voltmoney.voltsdk.models.VOLTENV { public protected <methods>; }
-keep class com.voltmoney.voltsdk.models.PreCreateAppResponse { public protected <methods>; }
-keep class com.voltmoney.voltsdk.VoltAPIResponse{ public protected <methods>; }

# Gson rules - Essential for JSON parsing
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }

# Keep all model/data classes for Gson serialization/deserialization
-keep class com.voltmoney.voltsdk.models.** { *; }

# Keep ResponseData class specifically (add your actual ResponseData class path)
-keep class com.voltmoney.voltsdk.models.ResponseData { *; }

# Generic signature preservation for R8 full mode
-keep,allowshrinking,allowoptimization interface retrofit2.Call
-keep,allowshrinking,allowoptimization class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Keep Gson TypeAdapter classes
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent obfuscation of classes with @SerializedName annotation
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Keep enum classes for Gson
-keepclassmembers enum * { *; }