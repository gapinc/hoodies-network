# Hoodies-Network library

Hoodies-Network is a modern, type-safe and high-performance Android HTTP library. 

## Table of Contents
* [Introduction](#introduction) 
* [Features](#features) 
* [Getting Started](#getting-started) 
  - [Instructions for building the library](#instructions-for-building-the-library)
* [Usage Examples](#usage-examples) 
* [Steps to make HTTP Calls](#steps-to-make-http-calls) 
* [Configure Timeouts for the HoodiesNetworkClient](#configure-timeouts-for-the-hoodiesnetworkclient) 
* [Instructions for using Caching Functionality](#instructions-for-using-caching-functionality) 
* [Instructions for using Encryption/Decryption](#instructions-for-using-encryptiondecryption) 
* [Using the MockWebServer](#using-the-mockwebserver) 
* [Instructions for testing the library](#instructions-for-testing-the-library) 
* [Environment](#environment) 
* [Conduct](#conduct) 
* [Contributing](#contributing) 

## Introduction
Hoodies-Network was originally designed for use in Gap's Instant Apps - it offers a highly competitive feature set while being smaller than other libraries. It is also Kotlin-native and designed using a philosophy of simple and clean design/architecture. Though its feature set has expanded over time to match and sometimes exceed other libraries, its Instant App roots remain and the post-minification apk size is still the smallest.
  
Typically, another dependency is required for loading images from a url into an ImageView. Hoodies-Network provides this feature out of the box.
```kotlin
suspend fun getImage(
      api: String,
      additionalHeaders: HashMap<String, String>?,
      maxWidth: Int,  
      maxHeight: Int,
      scaleType: ImageView.ScaleType,
      config: Bitmap.Config,
  ): Result<Bitmap?, HoodiesNetworkError>
```

Hoodies-Network provides a powerful MockWebServer to test API requests. It allows developers to easily mock API endpoints and thoroughly test all API calls.
```kotlin
MockServerMaker.Builder()
      .acceptMethod(HoodiesNetworkClient.HttpMethod.POST)
      .expect(bodyJson) //Also supports URL-encoded params
      .expectHeaders(reqHeaders)
      .returnThisJsonIfInputMatches(responseJson)
      .applyToMockWebServerBuilder("/test", serverBuilder)
```

Hoodies-Network offers encrypted caching and encrypted-by-default persistent local cookie storage. Both of these features can easily be turned on with less than a single line of code.
```kotlin
val cacheConfiguration = CacheEnabled(encryptionEnabled = true, staleDataThreshold = Duration.ofSeconds(60), this.context)
```
```kotlin
.enableCookiesWithCookieJar(PersistentCookieJar("I'm encrypted by default!", context))
```

## Features 
* URL parameter replacement and query parameter support 
* Object conversion to request body (e.g., JSON, protocol buffers)
* Object conversion from response body (e.g., JSON, protocol buffers, images)
* Custom header support
* Multiple and multipart file upload
* Support for POST, GET, PATCH, PUT, and DELETE methods
* Success and failure Callbacks
* Request, error, network, response, request body encryption, and response body decryption interceptors
* Retry on failure 
* Use of SSLSocketFactory
* Advanced Mock web server
* Advanced optionally encrypted cache system
* Built-in cookie handling including encrypted persistent local storage

## Getting Started
To integrate this library as a dependency in your project, clone this project and publish it to your local Maven repository.
Then, use Gradle to add it as a dependency in your project.

### Instructions for building the library
1. (In Android Studio top menu) Build -> Clean Build
2. Build -> Make Module 'Hoodies-Network.Hoodies-Network'

Alternatively, you can run a build and the tests in a single step by running the following command in the CLI: 

```./gradlew clean build```

**Note: Having your android emulator or physical device connected is required for this step**

Currently this library support Android version between 9 and 13 (API level between 28 and 32)

Gradle version 7.5.1 <br />
Kotlin version 1.6.10

## Usage Examples

Please refer to our [Examples Folder](https://github.com/gapinc/hoodies-network/tree/main/examples) for example usages of the library.

## Steps to make HTTP Calls

1. Import the library code     -> <i>import com.gap.hoodies_network.core.*</i> (additional packages may need to be imported in order to use advanced library features)
2. HoodiesNetworkClient() is used as a Service Manager to make network calls.
It must be instantiated via its Builder.

<b>Optionally, you can create an Interceptor Class which inherits from com.gap.network.interceptor.Interceptor</b>
Interceptors allow you read/modify all properties (headers, body, etc) of requests and responses before they are executed/delivered

CancellableMutableRequests can be cancelled by calling ```cancellableMutableRequest.cancelRequest(Success(object to return))``` or ```cancellableMutableRequest.cancelRequest(Failure(message, code, throwable))``` - depending on what you want to return

RetryableCancellableMutableRequests can be cancelled as well as retried. If the request has its body or headers changed, the retry attempt will execute the request with those changes intact
```kotlin
class SessionInterceptor(context: Context) : Interceptor(context) {

    override fun interceptRequest(identifier: String, cancellableMutableRequest: CancellableMutableRequest) {
        //Called after interceptNetwork, just before a request is executed
    }

    override fun interceptError(error: GapError, retryableCancellableMutableRequest: RetryableCancellableMutableRequest, autoRetryAttempts: Int) {
        //This is invoked before the failure callback is called, so you can retry the request if, for example, it fails because of expired authorization data:
	//if (error.code == 403) {
	//	val headers = retryableCancellableMutableRequest.request.getHeaders().toMutableMap()        	
	//	headers["Authorization"] = getNewAuthorization()
        //	retryableCancellableMutableRequest.request.setRequestHeaders(headers)
	//
	//  	retryableCancellableMutableRequest.retryRequest()
	//}
    }

    override fun interceptNetwork(isOnline: Boolean, cancellableMutableRequest: CancellableMutableRequest) {
	//This is the first interceptor that is invoked. Here, you can define what happens if the device knows that it is offline
        //If you don't want the request to be executed if there is no network connection, you can do something like this:
	//if (!isOnline) { cancellableMutableRequest.cancelRequest(Failure(GapError("No connection!", 0, SocketTimeoutException("No connection!")))) }
    }

    override fun interceptResponse(result: Result<*, GapError>, request: Request<Any>?) {
        //This is invoked upon the successful completion of a request before the success object is returned
    }
}
```

<b>Instantiate Service Manager</b>
```kotlin
private val sessionInterceptor: SessionInterceptor
val mobileHttpClient: HoodiesNetworkClient
val baseUrl = BuildConfig.BASE_URL

sessionInterceptor = SessionInterceptor(context)
val defaultHeaders = hashMapOf(
        CONTENT_TYPE_KEY to APPLICATION_JSON, MULTIDB_ENABLED to MULTIDB_ENABLED_VALUE,
        CLIENT_ID to CLIENT_ID_VALUE,
        CLIENT_OS to CLIENT_OS_VALUE
    )

mobileHttpClient = HoodiesNetworkClient.Builder()
    .baseUrl(baseUrl)
    .addHeaders(defaultHeaders)
    .addInterceptor(sessionInterceptor)
    .retryOnConnectionFailure(false, HoodiesNetworkClient.RetryCount.RETRY_NEVER)
    .build()
```

 <b> Configure Retry on connection failure</b>

  Use ```retryOnConnectionFailure(true,HoodiesNetworkClient.RetryCount.RETRY_MAX) ``` builder method of HoodiesNetworkClient to configure its Retry On Connection Failure Feature. The HoodiesNetworkClient will attempt retry on connection failure due to SocketTimeout Exception and IOException only.

  1. Enable Retry on connection failure by setting first argument to true.
  2. Set max allowed retry count by passing second argument of type RetryCount enum class of HoodiesNetworkClient.
  3. All Retry Requests will be processed in sequence, using a FIFO Queue.
  4. By Default, retryOnConnectionFailure() is set to false and count is set to HoodiesNetworkClient.RetryCount.RETRY_NEVER.

```HoodiesNetworkClient.RetryCount.RETRY_NEVER``` will Retry 0 Times

```HoodiesNetworkClient.RetryCount.RETRY_ONCE``` will Retry 1 Times

```HoodiesNetworkClient.RetryCount.RETRY_TWICE``` will Retry 2 Times

```HoodiesNetworkClient.RetryCount.RETRY_THRICE``` will Retry 3 Times

```HoodiesNetworkClient.RetryCount.RETRY_MAX``` will Retry 5 Times


```kotlin
mobileHttpClient = HoodiesNetworkClient.Builder()
    .baseUrl(baseUrl)
    .addHeaders(defaultHeaders)
    .addInterceptor(sessionInterceptor)
    .retryOnConnectionFailure(true, HoodiesNetworkClient.RetryCount.RETRY_MAX)
    .build()
 ```

 ## Configure Timeouts for the HoodiesNetworkClient

 Use this code to configure timeouts:
 ```kotlin
HttpClientConfig.setConnectTimeOut(Duration.ofSeconds(seconds))
HttpClientConfig.setReadTimeOut(Duration.ofSeconds(seconds))
  ```
* You must pass a Duration object
* Socket will wait for the connection to complete/connect if the value is set to 0
* If Time out is set to more than 0, Sockets will close/timeout connect operations after the given time
* Call this code only once to make changes and it will apply to all the Http Requests after that


  This function can be used to reset the config settings to the default:

 ```kotlin
HttpClientConfig.setFactoryDefaultConfiguration()
 ```

## Instructions for cookie handling
By default, all cookies are ignored. In order to enable retention/sending of cookies, a CookieManager object must be passed to the builder as shown:
By default, all cookies are ignored. In order to enable retention/sending of cookies, a CookieJar object must be passed to the builder as shown:
```kotlin
val client = GapHttpClient.Builder()
    .baseUrl("http://localhost")
    .enableCookiesWithManager(CookieManager())
    .enableCookiesWithCookieJar(CookieJar())
    .build()
```

Advanced custom functionality for cookie handling/retention can be achieved by making a custom class that inherits from CookieManager or by passing a custom CookieStore to the CookieManager
For more details, please see the official java.net.CookieManager documentation
A PersistentCookieJar is also offered. It saves the cookies in a Room DB so they are persistent across app launches.
For the PersistentCookieJar, an instance name is required. The data in the Room DB is encrypted - the key is stored securely in the KeyStore and managed by the library for you.
```kotlin
val client = GapHttpClient.Builder()
    .baseUrl("http://localhost")
    .enableCookiesWithCookieJar(PersistentCookieJar("myPersistentCookieJar", context))
    .build()
```

The CookieJar offers the following methods:

```getCookiesForHost(host: URI) : List<HttpCookie>``` gets all the cookies for a specified host. Note that this may include cookies that aren't sent to all paths for the host - the HttpCookie object has a "path" parameter for this purpose
```getAllCookies() : List<HttpCookie>``` gets all cookies stored in the CookieJar
```getAllHosts() : List<URI>``` gets a list of all hosts that have stored cookies in the CookieJar
```setCookiesForHost(host: URI, cookies: List<HttpCookie>)``` overwrites all the cookies for the specified host with those in the passed list
```addCookieForHost(host: URI, cookie: HttpCookie)``` adds the passed cookie for the specified host
```removeAllCookies()``` deletes all cookies in the CookieJar

## Instructions for using Caching Functionality

<b>1. Create a CacheEnabled() object </b> <br />
 Parameters : <br>
 **encryptionEnabled**: Boolean - Specifies that data is encrypted. The encryption key is stored securely in the KeyStore and managed by the library for you <br />
 **applicationContext**: Context - context <br />
 **staleDataThreshold**: Duration - to specify stale data threshold.


```kotlin
val cacheConfiguration = CacheEnabled(encryptionEnabled = true, staleDataThreshold = Duration.ofSeconds(60), this.context)
```


<b>1. Pass CacheEnabled() object to cacheConfiguration parameter when making Network Request </b>

```kotlin
return@withContext client.getUrlQueryParam<LocationAttribute>(
        queryParams = queryParams,
        api = pathParams,
        cacheConfiguration = cacheConfiguration
    )
```

## Instructions for using Encryption/Decryption

<b>1. Create a Class that implements our EncryptionDecryptionInterceptor Interface and instantiate it - </b>

```kotlin
val encDecInterceptor = EncDecInterceptor(this.context)

class EncDecInterceptor(override val context: Context) : EncryptionDecryptionInterceptor {

    override fun decryptResponse(response: ByteArray): ByteArray {
        // add your decryption logic here
        return  ByteArray(1)
    }

    override fun encryptAdditionalHeaders(additionalHeaderValue: ByteArray): ByteArray {
        // add your encryption logic here
        return  ByteArray(1)
    }

    override fun encryptRequest(requestBodyOrUrlQueryParamKeyValue: ByteArray): ByteArray {
        // add your encryption logic here
        return  ByteArray(1)
    }
}
```

<b>2. Add encryption/decryption interceptor to HoodiesNetworkClient - </b>

```kotlin
val client = HoodiesNetworkClient.Builder()
    .baseUrl(host)
    .addInterceptor(interceptor)
    .addEncryptionDecryptionInterceptor(encDecInterceptor)
    .build()
```

## Using the MockWebServer
Hoodies-Network includes an advanced mock webserver that you can use to easily replicate your API endpoints. 

1. Create a Builder and set the port
```kotlin
val serverBuilder = MockWebServerManager.Builder().usePort(5000)
```

2. a) Make your endpoints using the simple MockServerMaker DSL
```kotlin
//Make request body
val body = JSONObject()
body.put("name", "test_1")
body.put("salary", "1234")
body.put("age", "123")

//Make request headers
val reqHeaders: MutableMap<String, String> = HashMap()
reqHeaders["key"] = "value"

//Mock response
val response = "{\"status\":\"success\",\"data\":{\"name\":\"test_1\",\"salary\":\"1234\",\"age\":\"123\",\"id\":9221},\"message\":\"Successfully! Record has been added.\"}"

//Set up MockWebServer builder with port
val serverBuilder = MockWebServerManager.Builder().usePort(5000)

//Set up handler on MockWebServer to accept the request body and headers from above
MockServerMaker.Builder()
    .acceptMethod(HoodiesNetworkClient.HttpMethod.POST)
    .expect(body) //Can also be a HashMap<String, String> to validate URL-encoded params
    .expectHeaders(reqHeaders)
    .returnThisJsonIfInputMatches(JSONObject(response))
    .applyToMockWebServerBuilder("/test", serverBuilder)
```
The server will verify that your request includes what you passed to expect() and that your headers include what you passed to expectHeaders()

If everything matched, the success response will be returned. Otherwise, a customizable error response will be returned

2. b) For advanced behavior, make a WebServerHandler() for your endpoint
```kotlin
val handler = object : WebServerHandler() {
    override fun handleRequest(call: HttpCall) {
        when (method) {
            //KTor-like syntax
            get {
                val delayLength = call.httpExchange.requestURI.toString().split("/").last()
                Thread.sleep(delayLength.toLong() * 1000L)
                call.respond(200, "{\"delay\":\"$delayLength\"}")
            }
            post {
                val delayLength = call.httpExchange.requestURI.toString().split("/").last()
                Thread.sleep(delayLength.toLong() * 1000L)
                call.respond(200, "{\"delay\":\"$delayLength\"}")
            }
        }
    }
}

serverBuilder.addContext("/echodelay", handler)
```
3. Start your server
```kotlin
val server = serverBuilder.start()
```

4. Stop your server when your tests are finished
```kotlin
server.stop()
```

## Instructions for testing the library

The test classes package path is at com.gap.hoodies_network(androidTest).
The test classes use test libraries Mockito and Junit, and run on an Android device.
The MockWebServer is used to host the endpoints for the tests.
The Test Classes are as follows:

- CachingAndCryptographyTest
- FormUrlEncodedRequestTest
- EncryptionDecryptionTest
- FileUploadRequestTests
- HoodiesNetworkClientTest
- HeaderTest
- ImageRequestMockTest
- ImageTests
- JsonRequestTest
- MultiRequestTest
- NetworkConnectionTest
- ResponseDeliveryInstant
- ResponseTest
- RetryTest
- SocketTimeOutTest
- StringRequestTest
- UrlResolverTest
- CookieTests

You can run the tests by right-clicking on the androidTestFolder and then clicking "Run Tests"

**Note: Having your android emulator or physical device connected is required**
<br><br>


# Environment
This is a Gradle project. You can use any Android and Gradle-compatible IDE. Use of Android Studio is highly suggested

Android Studio Bumblebee and above are supported

It it also highly recommended that you have an emulator or physical device connected - this allows you to run the unit tests

# Conduct
This is a professional environment and you are expected to conduct yourself in a professional and courteous manner. If you fail to exhibit appropriate conduct, your contributions and interactions will no longer be welcome here.

# Contributing
All are welcome and encouraged to contribute. If you are looking for a place to start, try working on an unassigned issue with the #good-first-issue tag. All contributions are expected to conform to standard Kotlin code style and be covered by unit tests. If you open a pull request with failing tests, your PR will not be merged and you will be asked to resolve the issue.
If you would like to contribute code you can do so through GitHub by forking the repository and sending a pull request.
When submitting code, please make every effort to follow existing conventions and style in order to keep the code as readable as possible.
Please also make sure your code compiles and passes all tests by running ./gradlew clean build (or gradlew.bat clean build for Windows).




