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

#Specify the classes needed to keep in the app while shrinking


-dontwarn java.lang.invoke**
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
-keepattributes *Annotation*
-keepattributes EnclosingMethod

-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

-dontwarn java.lang.invoke**

#-keep public class * extends com.gap.network.** { *; }
-keep class !com.gap.hoodies_network.**,!com.gap.hoodies_network.** { <fields>;}
-keep  class  com.gap.hoodies_network.config.** { <fields>;}
-keep  class  com.gap.hoodies_network.core.** { <fields>; }
-keep  class  com.gap.hoodies_network.cache.** { <fields>;}
-keep  class  com.gap.hoodies_network.connection.** { <fields>;}
-keep  class  com.gap.hoodies_network.delivery.** { <fields>;}
-keep  class  com.gap.hoodies_network.header.** { <fields>;}
-keep  class  com.gap.hoodies_network.interceptor.** { <fields>;}
-keep  class  com.gap.hoodies_network.request.** { <fields>;}
-keep  class  com.gap.hoodies_network.connection.queue.** { <fields>;}
-keep  class  com.gap.hoodies_network.request.json.** { <fields>;}
-keep  class  com.gap.hoodies_network.request.query.** { <fields>;}

-keep class com.gap.hoodies_network.core.GapHttpClient$** {  *;}
-keep class com.gap.hoodies_network.config.HttpClientConfig$** {  *;}

-keep  class * {
    public protected *;
}

-keep class com.gap.hoodies_network.core.HoodiesNetworkClient { *; }


-keep class com.gap.hoodies_network.request.json.JsonObjectRequest$** { *;}

-keep interface * { <methods>;}
-keep class com.gap.hoodies_network.request.json.JsonObjectRequest {

}
-keep class com.gap.hoodies_network.config.HttpClientConfig$Companion
-keep class com.gap.hoodies_network.config.UrlResolver$Companion
-keep class com.gap.hoodies_network.connection.queue.RequestQueue$Companion
-keep class com.gap.hoodies_network.cache.DiskBasedCache$Companion
-keep class com.gap.hoodies_network.connection.BaseNetwork$Companion
-keep class com.gap.hoodies_network.core.Response$Companion
-keep class com.gap.hoodies_network.header.HttpHeaderParser$Companion
-keep class com.gap.hoodies_network.request.FormUrlEncodedRequest$Companion
-keep class com.gap.hoodies_network.request.ImageRequest$Companion
-keep class com.gap.hoodies_network.request.Request$Method
-keep class com.gap.hoodies_network.request.query.UrlQueryParamEncodedRequest$Companion

-keep class org.json.JSONArray** { *;}
-keep class org.json.JSONObject** { *;}
-keep class org.json.** { *; }

-keepclassmembernames class * {
    public protected <methods>;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { <fields>; }

-keep class org.json.JSONException.** {*;}
-keep class org.json.JSONObject.** {*;}
-keep class java.io.UnsupportedEncodingException.** {*;}

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

## Prevent proguard from stripping interface information from TypeAdapterFactory,


## Coroutines
-keep class kotlinx.coroutines.android.** {*;}
-keep class kotlin.coroutines.Continuation.** { *;}
-keep class kotlin.coroutines.CoroutineContext.**{ *;}
-keep class kotlinx.coroutines.CoroutineStart.** { *;}
-keep class kotlin.coroutines.SuspendFunction.** { *;}
-keep class kotlinx.coroutines.Job.** { *;}


-dontwarn kotlinx.coroutines.flow.**

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keep class com.d.c.** {
*;
}
-keep class com.a.f.** {
*;
}


-assumenosideeffects class android.util.Log {
public static int d(...);
public static int v(...);
public static int i(...);
public static int w(...);
public static int e(...);
public static int wtf(...);
}