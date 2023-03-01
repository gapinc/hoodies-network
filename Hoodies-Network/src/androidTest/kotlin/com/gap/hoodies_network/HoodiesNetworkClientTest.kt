@file:Suppress("UNCHECKED_CAST")

package com.gap.hoodies_network

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gap.hoodies_network.cache.configuration.CacheDisabled
import com.gap.hoodies_network.config.*
import com.gap.hoodies_network.core.*
import com.gap.hoodies_network.mockwebserver.ServerManager
import com.gap.hoodies_network.request.Request
import com.gap.hoodies_network.testObjects.CallResponse
import com.gap.hoodies_network.testObjects.WeatherResponse
import com.gap.hoodies_network.testObjects.testInterceptor
import com.google.gson.Gson
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HoodiesNetworkClientTest {

    private val baseURL = "http://localhost:6969/"
    private val gson = Gson()
    private val interceptor = testInterceptor(InstrumentationRegistry.getInstrumentation().context)
    private val defaultHeaders = hashMapOf(
        CLIENT_ID to CLIENT_ID_VALUE,
        CLIENT_OS to CLIENT_OS_VALUE
    )

    @Before
    fun startMockWebServer() {
        ServerManager.setup(interceptor.context)
    }

    @After
    fun stopServer() {
        ServerManager.stop()
    }

    @Test
    fun getBuilderTest() {
        val requestMap = HashMap<String, String>()
        requestMap["key1"] = "value1"
        requestMap["key2"] = "value2"
        requestMap["key3"] = "value3"
        val builder = HoodiesNetworkClient.Builder()
            .baseUrl(baseURL)
            .addHeaders(requestMap)
            .addHeader("key4", "value4")
            .addInterceptor(interceptor)
            .build()


        assertEquals(
            builder.nonInlinedClient.baseUrl,
            "http://localhost:6969/"
        )
        requestMap["key4"] = "value4"
        assertEquals(builder.nonInlinedClient.defaultHeaders, requestMap)
        assertEquals(builder.nonInlinedClient.interceptors.size, 1)
    }

    @Test
    fun getRequestHeadersTest() {
        val builder = HoodiesNetworkClient.Builder()
        builder.addHeaders(defaultHeaders)
        val getRequestHeaders1 = builder.defaultHeaders
        assertEquals(
            getRequestHeaders1["clientOS"],
            "IOS/Android"
        ) //clientOS -> IOS/Android
        assertEquals(getRequestHeaders1["clientAppId"], "BRONGA") //clientAppId -> BRONGA
    }

    @Test
    fun httpMethodTest() {
        assertEquals(HoodiesNetworkClient.HttpMethod.GET.value, "GET")
        assertEquals(HoodiesNetworkClient.HttpMethod.POST.value, "POST")
        assertEquals(HoodiesNetworkClient.HttpMethod.PUT.value, "PUT")
        assertEquals(HoodiesNetworkClient.HttpMethod.DELETE.value, "DELETE")

        assertEquals(HoodiesNetworkClient.HttpMethod.GET.ordinal, 0)
        assertEquals(HoodiesNetworkClient.HttpMethod.POST.ordinal, 1)
        assertEquals(HoodiesNetworkClient.HttpMethod.PUT.ordinal, 2)
        assertEquals(HoodiesNetworkClient.HttpMethod.DELETE.ordinal, 3)
    }

    @Test
    fun retryCountTest() {
        assertEquals(HoodiesNetworkClient.RetryCount.RETRY_MAX.name, "RETRY_MAX")
        assertEquals(HoodiesNetworkClient.RetryCount.RETRY_NEVER.name, "RETRY_NEVER")
        assertEquals(HoodiesNetworkClient.RetryCount.RETRY_ONCE.name, "RETRY_ONCE")
        assertEquals(HoodiesNetworkClient.RetryCount.RETRY_THRICE.name, "RETRY_THRICE")
        assertEquals(HoodiesNetworkClient.RetryCount.RETRY_TWICE.name, "RETRY_TWICE")

        assertEquals(HoodiesNetworkClient.RetryCount.RETRY_MAX.ordinal, 4)
        assertEquals(HoodiesNetworkClient.RetryCount.RETRY_NEVER.ordinal, 0)
        assertEquals(HoodiesNetworkClient.RetryCount.RETRY_ONCE.ordinal, 1)
        assertEquals(HoodiesNetworkClient.RetryCount.RETRY_THRICE.ordinal, 3)
        assertEquals(HoodiesNetworkClient.RetryCount.RETRY_TWICE.ordinal, 2)

        assertEquals(HoodiesNetworkClient.RetryCount.parseToInt(HoodiesNetworkClient.RetryCount.RETRY_NEVER), 0)
        assertEquals(HoodiesNetworkClient.RetryCount.parseToInt(HoodiesNetworkClient.RetryCount.RETRY_ONCE), 1)
        assertEquals(HoodiesNetworkClient.RetryCount.parseToInt(HoodiesNetworkClient.RetryCount.RETRY_TWICE), 2)
        assertEquals(HoodiesNetworkClient.RetryCount.parseToInt(HoodiesNetworkClient.RetryCount.RETRY_THRICE), 3)
        assertEquals(HoodiesNetworkClient.RetryCount.parseToInt(HoodiesNetworkClient.RetryCount.RETRY_MAX), 5)
    }

    @Test
    fun getRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()

            when (val result = client.get<CallResponse>("get")) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals(result.value.url, "http://localhost:6969/get")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getHtmlRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()

            when (val result = client.getHtml("html", cacheConfiguration = CacheDisabled())) {
                is Success -> {
                    //Assert that we got the HTML that we are expecting back
                    assertEquals(result.value, "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "  <head>\n" +
                            "  </head>\n" +
                            "  <body>\n" +
                            "      <h1>Herman Melville - Moby-Dick</h1>\n" +
                            "\n" +
                            "      <div>\n" +
                            "        <p>\n" +
                            "          Availing himself of the mild, summer-cool weather that now reigned in these latitudes, and in preparation for the peculiarly active pursuits shortly to be anticipated, Perth, the begrimed, blistered old blacksmith, had not removed his portable forge to the hold again, after concluding his contributory work for Ahab's leg, but still retained it on deck, fast lashed to ringbolts by the foremast; being now almost incessantly invoked by the headsmen, and harpooneers, and bowsmen to do some little job for them; altering, or repairing, or new shaping their various weapons and boat furniture. Often he would be surrounded by an eager circle, all waiting to be served; holding boat-spades, pike-heads, harpoons, and lances, and jealously watching his every sooty movement, as he toiled. Nevertheless, this old man's was a patient hammer wielded by a patient arm. No murmur, no impatience, no petulance did come from him. Silent, slow, and solemn; bowing over still further his chronically broken back, he toiled away, as if toil were life itself, and the heavy beating of his hammer the heavy beating of his heart. And so it was.—Most miserable! A peculiar walk in this old man, a certain slight but painful appearing yawing in his gait, had at an early period of the voyage excited the curiosity of the mariners. And to the importunity of their persisted questionings he had finally given in; and so it came to pass that every one now knew the shameful story of his wretched fate. Belated, and not innocently, one bitter winter's midnight, on the road running between two country towns, the blacksmith half-stupidly felt the deadly numbness stealing over him, and sought refuge in a leaning, dilapidated barn. The issue was, the loss of the extremities of both feet. Out of this revelation, part by part, at last came out the four acts of the gladness, and the one long, and as yet uncatastrophied fifth act of the grief of his life's drama. He was an old man, who, at the age of nearly sixty, had postponedly encountered that thing in sorrow's technicals called ruin. He had been an artisan of famed excellence, and with plenty to do; owned a house and garden; embraced a youthful, daughter-like, loving wife, and three blithe, ruddy children; every Sunday went to a cheerful-looking church, planted in a grove. But one night, under cover of darkness, and further concealed in a most cunning disguisement, a desperate burglar slid into his happy home, and robbed them all of everything. And darker yet to tell, the blacksmith himself did ignorantly conduct this burglar into his family's heart. It was the Bottle Conjuror! Upon the opening of that fatal cork, forth flew the fiend, and shrivelled up his home. Now, for prudent, most wise, and economic reasons, the blacksmith's shop was in the basement of his dwelling, but with a separate entrance to it; so that always had the young and loving healthy wife listened with no unhappy nervousness, but with vigorous pleasure, to the stout ringing of her young-armed old husband's hammer; whose reverberations, muffled by passing through the floors and walls, came up to her, not unsweetly, in her nursery; and so, to stout Labor's iron lullaby, the blacksmith's infants were rocked to slumber. Oh, woe on woe! Oh, Death, why canst thou not sometimes be timely? Hadst thou taken this old blacksmith to thyself ere his full ruin came upon him, then had the young widow had a delicious grief, and her orphans a truly venerable, legendary sire to dream of in their after years; and all of them a care-killing competency.\n" +
                            "        </p>\n" +
                            "      </div>\n" +
                            "  </body>\n" +
                            "</html>")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getUrlQueryParamRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()
            val queryParams: HashMap<String, String> = HashMap()
            queryParams["q"] = "London,uk"
            queryParams["appid"] = "2b1fd2d7f77ccf1b7de9b441571b39b8"

            when (val result = client.getUrlQueryParam<WeatherResponse>(queryParams, "weather")) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals(result.value.coord?.latitude, "51.51")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getUrlQueryParamEncodedRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()
            val queryParams: HashMap<String, String> = HashMap()
            queryParams["q"] = "London,uk"
            queryParams["appid"] = "2b1fd2d7f77ccf1b7de9b441571b39b8"

            when (val result = client.getUrlQueryParamEncoded<WeatherResponse>(queryParams, "weather")) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals(result.value.coord?.latitude, "51.51")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun basicPostRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()

            when (val result = client.post<CallResponse>("post")) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals(result.value.url, "http://localhost:6969/post")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun postObjectRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()

            when (val result = client.post<JSONObject, CallResponse>("post", JSONObject("{\"name\":\"Test\", \"age\":25}"))) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals(result.value.data, "{\"nameValuePairs\":{\"name\":\"Test\",\"age\":25}}")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun postJsonObjectRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()

            when (val result = client.post<CallResponse>("post", JSONObject("{\"name\":\"Test\", \"age\":25}"))) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals(result.value.data, "{\"name\":\"Test\",\"age\":25}")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun postJsonArrayRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()

            when (val result = client.post<CallResponse>("post", JSONArray(
                    "[{\"name\":\"Test 1\", \"age\":25}," +
                            "{\"name\":\"Test 2\", \"age\":22},{\"name\":\"Test 3\", \"age\":21}]"
                    )
            )) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals(result.value.data, "[{\"name\":\"Test 1\",\"age\":25},{\"name\":\"Test 2\",\"age\":22},{\"name\":\"Test 3\",\"age\":21}]")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

   @Test
    fun putRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()

            when (val result = client.put<JSONObject,CallResponse>("put", JSONObject("{\"name\":\"Test\", \"age\":25}"))) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals(result.value.data, "{\"nameValuePairs\":{\"name\":\"Test\",\"age\":25}}")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun deleteRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()

            when (val result = client.delete<CallResponse>("delete")) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals(result.value.url, "http://localhost:6969/delete")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun cannotReachServerTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("https://127.0.0.1/").addInterceptor(interceptor).build()

            when (val result = client.get<CallResponse>("get")) {
                is Success -> {
                    throw Exception()
                }
                is Failure -> {
                    assertEquals(result.reason.code, -1)
                }
            }
        }
    }
    @Test
    fun getRawTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()

            when (val result = client.getRaw("get")) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals(gson.fromJson(result.value, CallResponse::class.java).url, "http://localhost:6969/get")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getRawUrlQueryParamRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()
            val queryParams: HashMap<String, String> = HashMap()
            queryParams["q"] = "London,uk"
            queryParams["appid"] = "2b1fd2d7f77ccf1b7de9b441571b39b8"

            when (val result = client.getRawUrlQueryParam(queryParams, "weather")) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals(gson.fromJson(result.value, WeatherResponse::class.java).coord?.latitude, "51.51")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getRawUrlQueryParamEncodedRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()
            val queryParams: HashMap<String, String> = HashMap()
            queryParams["q"] = "London,uk"
            queryParams["appid"] = "2b1fd2d7f77ccf1b7de9b441571b39b8"

            when (val result = client.getRawUrlQueryParamEncoded(queryParams, "weather")) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals(gson.fromJson(result.value, WeatherResponse::class.java).coord?.latitude, "51.51")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun basicRawPostRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()

            when (val result = client.postRaw("post")) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals(gson.fromJson(result.value, CallResponse::class.java).url, "http://localhost:6969/post")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun withStringRawPostRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()

            when (val result = client.postRaw("post", "test")) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals(gson.fromJson(result.value, CallResponse::class.java).data, "\"test\"")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun withHashMapRawPostRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()

            when (val result = client.postRaw("post", hashMapOf("param1" to "value"))) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals(gson.fromJson(result.value, CallResponse::class.java).url, "http://localhost:6969/post")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun withHashMapRawAndHeadersPostRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()

            when (val result = client.postRaw("post", hashMapOf("param1" to "value"), hashMapOf("param1" to "value"))) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals(gson.fromJson(result.value, CallResponse::class.java).url, "http://localhost:6969/post")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun putRawRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()

            when (val result = client.putRaw("put", "{\"name\":\"Test\", \"age\":25}")) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals(gson.fromJson(result.value, CallResponse::class.java).data, "\"{\\\"name\\\":\\\"Test\\\", \\\"age\\\":25}\"")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun deleteRawRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()

            when (val result = client.deleteRaw("delete")) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals(gson.fromJson(result.value, CallResponse::class.java).url, "http://localhost:6969/delete")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun cannotReachServerRawTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("https://127.0.0.1/").addInterceptor(interceptor).build()

            when (val result = client.getRaw("get")) {
                is Success -> {
                    throw Exception()
                }
                is Failure -> {
                    assertEquals(result.reason.code, -1)
                }
            }
        }
    }

    @Test
    fun getRequestTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient

            when (val result = client.get("get", resultType = CallResponse::class.java)) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals((result.value as CallResponse).url, "http://localhost:6969/get")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getHtmlRequestTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient

            when (val result = client.getHtml("html")) {
                is Success -> {
                    //Assert that we got the HTML that we are expecting back
                    assertEquals(result.value, "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "  <head>\n" +
                            "  </head>\n" +
                            "  <body>\n" +
                            "      <h1>Herman Melville - Moby-Dick</h1>\n" +
                            "\n" +
                            "      <div>\n" +
                            "        <p>\n" +
                            "          Availing himself of the mild, summer-cool weather that now reigned in these latitudes, and in preparation for the peculiarly active pursuits shortly to be anticipated, Perth, the begrimed, blistered old blacksmith, had not removed his portable forge to the hold again, after concluding his contributory work for Ahab's leg, but still retained it on deck, fast lashed to ringbolts by the foremast; being now almost incessantly invoked by the headsmen, and harpooneers, and bowsmen to do some little job for them; altering, or repairing, or new shaping their various weapons and boat furniture. Often he would be surrounded by an eager circle, all waiting to be served; holding boat-spades, pike-heads, harpoons, and lances, and jealously watching his every sooty movement, as he toiled. Nevertheless, this old man's was a patient hammer wielded by a patient arm. No murmur, no impatience, no petulance did come from him. Silent, slow, and solemn; bowing over still further his chronically broken back, he toiled away, as if toil were life itself, and the heavy beating of his hammer the heavy beating of his heart. And so it was.—Most miserable! A peculiar walk in this old man, a certain slight but painful appearing yawing in his gait, had at an early period of the voyage excited the curiosity of the mariners. And to the importunity of their persisted questionings he had finally given in; and so it came to pass that every one now knew the shameful story of his wretched fate. Belated, and not innocently, one bitter winter's midnight, on the road running between two country towns, the blacksmith half-stupidly felt the deadly numbness stealing over him, and sought refuge in a leaning, dilapidated barn. The issue was, the loss of the extremities of both feet. Out of this revelation, part by part, at last came out the four acts of the gladness, and the one long, and as yet uncatastrophied fifth act of the grief of his life's drama. He was an old man, who, at the age of nearly sixty, had postponedly encountered that thing in sorrow's technicals called ruin. He had been an artisan of famed excellence, and with plenty to do; owned a house and garden; embraced a youthful, daughter-like, loving wife, and three blithe, ruddy children; every Sunday went to a cheerful-looking church, planted in a grove. But one night, under cover of darkness, and further concealed in a most cunning disguisement, a desperate burglar slid into his happy home, and robbed them all of everything. And darker yet to tell, the blacksmith himself did ignorantly conduct this burglar into his family's heart. It was the Bottle Conjuror! Upon the opening of that fatal cork, forth flew the fiend, and shrivelled up his home. Now, for prudent, most wise, and economic reasons, the blacksmith's shop was in the basement of his dwelling, but with a separate entrance to it; so that always had the young and loving healthy wife listened with no unhappy nervousness, but with vigorous pleasure, to the stout ringing of her young-armed old husband's hammer; whose reverberations, muffled by passing through the floors and walls, came up to her, not unsweetly, in her nursery; and so, to stout Labor's iron lullaby, the blacksmith's infants were rocked to slumber. Oh, woe on woe! Oh, Death, why canst thou not sometimes be timely? Hadst thou taken this old blacksmith to thyself ere his full ruin came upon him, then had the young widow had a delicious grief, and her orphans a truly venerable, legendary sire to dream of in their after years; and all of them a care-killing competency.\n" +
                            "        </p>\n" +
                            "      </div>\n" +
                            "  </body>\n" +
                            "</html>")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getUrlQueryParamRequestTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient
            val queryParams: HashMap<String, String> = HashMap()
            queryParams["q"] = "London,uk"
            queryParams["appid"] = "2b1fd2d7f77ccf1b7de9b441571b39b8"

            when (val result = client.getUrlQueryParam(queryParams, "weather", resultType = WeatherResponse::class.java)) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals((result.value as WeatherResponse).coord?.latitude, "51.51")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getUrlQueryParamEncodedRequestTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient
            val queryParams: HashMap<String, String> = HashMap()
            queryParams["q"] = "London,uk"
            queryParams["appid"] = "2b1fd2d7f77ccf1b7de9b441571b39b8"

            when (val result = client.getUrlQueryParamEncoded(queryParams, "weather", resultType = WeatherResponse::class.java)) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals((result.value as WeatherResponse).coord?.latitude, "51.51")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun basicPostRequestTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient

            when (val result = client.post("post", resultType = CallResponse::class.java)) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals((result.value as CallResponse).url, "http://localhost:6969/post")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun postObjectRequestTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient

            when (val result = client.post("post", JSONObject("{\"name\":\"Test\", \"age\":25}"), resultType = CallResponse::class.java)) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals((result.value as CallResponse).data, "{\"name\":\"Test\",\"age\":25}")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun postJsonObjectRequestTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient

            when (val result = client.post("post", JSONObject("{\"name\":\"Test\", \"age\":25}"), resultType = CallResponse::class.java)) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals((result.value as CallResponse).data, "{\"name\":\"Test\",\"age\":25}")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun postNullJsonObjectRequestTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient

            when (val result = client.sendJsonObjectRequest("post", HoodiesNetworkClient.HttpMethod.POST, additionalHeaders = null, resultType = CallResponse::class.java, cacheConfiguration = CacheDisabled())) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals((result.value as CallResponse).data, "null")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun postJsonArrayRequestTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient

            when (val result = client.post("post", JSONArray(
                "[{\"name\":\"Test 1\", \"age\":25}," +
                        "{\"name\":\"Test 2\", \"age\":22},{\"name\":\"Test 3\", \"age\":21}]"
            ), resultType = CallResponse::class.java
            )) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals((result.value as CallResponse).data, "[{\"name\":\"Test 1\",\"age\":25},{\"name\":\"Test 2\",\"age\":22},{\"name\":\"Test 3\",\"age\":21}]")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun postNullJsonArrayRequestTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient

            when (val result = client.sendJsonArrayRequest("post", HoodiesNetworkClient.HttpMethod.POST, additionalHeaders = null, resultType = CallResponse::class.java, cacheConfiguration = CacheDisabled())) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals((result.value as CallResponse).data, "null")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun putRequestTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient

            when (val result = client.put("put", JSONObject("{\"name\":\"Test\", \"age\":25}"), resultType = CallResponse::class.java)) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals((result.value as CallResponse).data, "{\"nameValuePairs\":{\"name\":\"Test\",\"age\":25}}")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun deleteRequestTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient

            when (val result = client.delete("delete", resultType = CallResponse::class.java)) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals((result.value as CallResponse).url, "http://localhost:6969/delete")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getRequestRegularTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient
            val requestMap = HashMap<String, String>()
            requestMap["key1"] = "value1"
            requestMap["key2"] = "value2"
            requestMap["key3"] = "value3"
            val result = client.sendRequest("post", HoodiesNetworkClient.HttpMethod.POST, "test", null, resultType = CallResponse::class.java, cacheConfiguration = CacheDisabled())

            when (result) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals((result.value as CallResponse).data, "\"test\"")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun sendUrlQueryParamEncodedRequestNullHeaderTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient

            when (val result = client.sendUrlQueryParamEncodedRequest(HoodiesNetworkClient.HttpMethod.GET, hashMapOf(), "get", resultType = CallResponse::class.java, cacheConfiguration = CacheDisabled())) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals((result.value as CallResponse).url, "http://localhost:6969/get")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun sendUrlQueryRequestNullHeaderTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient

            when (val result = client.sendUrlQueryRequest(HoodiesNetworkClient.HttpMethod.GET, hashMapOf(), "get", resultType = CallResponse::class.java, cacheConfiguration = CacheDisabled())) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals((result.value as CallResponse).url, "http://localhost:6969/get")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getRequestAdditionalHeadersEmptyFormUrlEncodedTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient
            val result = suspendCancellableCoroutine { continuation: CancellableContinuation<Result<*, HoodiesNetworkError>> ->
                val request = client.getRequest(
                    "id", "post", HoodiesNetworkClient.HttpMethod.POST, additionalHeaders = null, continuation = continuation, resultType = CallResponse::class.java)
                client.sendRequest("id", request as Request<Any>, null, cacheConfiguration = CacheDisabled())
            }

            when (result) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals((result.value as CallResponse).data, "")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getRequestHtmlWithHeadersTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient
            val result = suspendCancellableCoroutine { continuation: CancellableContinuation<Result<*, HoodiesNetworkError>> ->
                val request = client.getRequestHtml(
                    "id", "html", HoodiesNetworkClient.HttpMethod.GET,
                    additionalHeaders = null, continuation = continuation
                )
                client.sendRequest("id", request as Request<Any>, null, cacheConfiguration = CacheDisabled())
            }

            when (result) {
                is Success -> {
                    //Assert that we got the HTML that we are expecting back
                    assertEquals(result.value, "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "  <head>\n" +
                            "  </head>\n" +
                            "  <body>\n" +
                            "      <h1>Herman Melville - Moby-Dick</h1>\n" +
                            "\n" +
                            "      <div>\n" +
                            "        <p>\n" +
                            "          Availing himself of the mild, summer-cool weather that now reigned in these latitudes, and in preparation for the peculiarly active pursuits shortly to be anticipated, Perth, the begrimed, blistered old blacksmith, had not removed his portable forge to the hold again, after concluding his contributory work for Ahab's leg, but still retained it on deck, fast lashed to ringbolts by the foremast; being now almost incessantly invoked by the headsmen, and harpooneers, and bowsmen to do some little job for them; altering, or repairing, or new shaping their various weapons and boat furniture. Often he would be surrounded by an eager circle, all waiting to be served; holding boat-spades, pike-heads, harpoons, and lances, and jealously watching his every sooty movement, as he toiled. Nevertheless, this old man's was a patient hammer wielded by a patient arm. No murmur, no impatience, no petulance did come from him. Silent, slow, and solemn; bowing over still further his chronically broken back, he toiled away, as if toil were life itself, and the heavy beating of his hammer the heavy beating of his heart. And so it was.—Most miserable! A peculiar walk in this old man, a certain slight but painful appearing yawing in his gait, had at an early period of the voyage excited the curiosity of the mariners. And to the importunity of their persisted questionings he had finally given in; and so it came to pass that every one now knew the shameful story of his wretched fate. Belated, and not innocently, one bitter winter's midnight, on the road running between two country towns, the blacksmith half-stupidly felt the deadly numbness stealing over him, and sought refuge in a leaning, dilapidated barn. The issue was, the loss of the extremities of both feet. Out of this revelation, part by part, at last came out the four acts of the gladness, and the one long, and as yet uncatastrophied fifth act of the grief of his life's drama. He was an old man, who, at the age of nearly sixty, had postponedly encountered that thing in sorrow's technicals called ruin. He had been an artisan of famed excellence, and with plenty to do; owned a house and garden; embraced a youthful, daughter-like, loving wife, and three blithe, ruddy children; every Sunday went to a cheerful-looking church, planted in a grove. But one night, under cover of darkness, and further concealed in a most cunning disguisement, a desperate burglar slid into his happy home, and robbed them all of everything. And darker yet to tell, the blacksmith himself did ignorantly conduct this burglar into his family's heart. It was the Bottle Conjuror! Upon the opening of that fatal cork, forth flew the fiend, and shrivelled up his home. Now, for prudent, most wise, and economic reasons, the blacksmith's shop was in the basement of his dwelling, but with a separate entrance to it; so that always had the young and loving healthy wife listened with no unhappy nervousness, but with vigorous pleasure, to the stout ringing of her young-armed old husband's hammer; whose reverberations, muffled by passing through the floors and walls, came up to her, not unsweetly, in her nursery; and so, to stout Labor's iron lullaby, the blacksmith's infants were rocked to slumber. Oh, woe on woe! Oh, Death, why canst thou not sometimes be timely? Hadst thou taken this old blacksmith to thyself ere his full ruin came upon him, then had the young widow had a delicious grief, and her orphans a truly venerable, legendary sire to dream of in their after years; and all of them a care-killing competency.\n" +
                            "        </p>\n" +
                            "      </div>\n" +
                            "  </body>\n" +
                            "</html>")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getRequestFormUrlEncodedAdditionalHeadersEmptyTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient
            val result = suspendCancellableCoroutine { continuation: CancellableContinuation<Result<*, HoodiesNetworkError>> ->
                val request = client.getRequestFormUrlEncoded(
                    "id", "post", HoodiesNetworkClient.HttpMethod.POST, hashMapOf("testKey" to "testVal") ,
                    continuation = continuation, resultType = CallResponse::class.java
                )
                client.sendRequest("id", request as Request<Any>, null, cacheConfiguration = CacheDisabled())
            }

            when (result) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals((result.value as CallResponse).data, "testKey=testVal")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getRequestUrlQueryParamAdditionalHeadersEmptyTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient
            val result = suspendCancellableCoroutine { continuation: CancellableContinuation<Result<*, HoodiesNetworkError>> ->
                val request = client.getRequestUrlQueryParam(
                    "post", "id", HoodiesNetworkClient.HttpMethod.POST, hashMapOf("testKey" to "testVal") ,
                    continuation = continuation, resultType = CallResponse::class.java
                )
                client.sendRequest("id", request as Request<Any>, null, cacheConfiguration = CacheDisabled())
            }

            when (result) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals((result.value as CallResponse).data, "http://localhost:6969/post?testKey=testVal")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getRequestUrlQueryParamEncodedAdditionalHeadersEmptyTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient
            val result = suspendCancellableCoroutine { continuation: CancellableContinuation<Result<*, HoodiesNetworkError>> ->
                val request = client.getRequestUrlQueryParamEncoded(
                    "post","id", HoodiesNetworkClient.HttpMethod.POST,hashMapOf("testKey" to "testVal"),
                    continuation = continuation, resultType = CallResponse::class.java
                )
                client.sendRequest("id", request as Request<Any>, null, cacheConfiguration = CacheDisabled())
            }

            when (result) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals((result.value as CallResponse).data, "http://localhost:6969/post?testKey=testVal")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getRequestRegularAllNullTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient
            val result = suspendCancellableCoroutine { continuation: CancellableContinuation<Result<*, HoodiesNetworkError>> ->
                val request = client.getRequestRegular(
                    "id", "post", HoodiesNetworkClient.HttpMethod.POST,
                    continuation = continuation, resultType = CallResponse::class.java
                )
                client.sendRequest("id", request as Request<Any>, null, cacheConfiguration = CacheDisabled())
            }

            when (result) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals((result.value as CallResponse).data, "")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getRegularRequestHtmlWithHeadersNoBodyTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient
            val result = suspendCancellableCoroutine { continuation: CancellableContinuation<Result<*, HoodiesNetworkError>> ->
                val request = client.getRegularRequestHtml(
                    "html","id",  HoodiesNetworkClient.HttpMethod.GET,
                    continuation = continuation, additionalHeaders = hashMapOf("testKey" to "testVal")
                )
                client.sendRequest("id", request as Request<Any>, null, cacheConfiguration = CacheDisabled())
            }

            when (result) {
                is Success -> {
                    //Assert that we got the HTML that we are expecting back
                    assertEquals(result.value, "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "  <head>\n" +
                            "  </head>\n" +
                            "  <body>\n" +
                            "      <h1>Herman Melville - Moby-Dick</h1>\n" +
                            "\n" +
                            "      <div>\n" +
                            "        <p>\n" +
                            "          Availing himself of the mild, summer-cool weather that now reigned in these latitudes, and in preparation for the peculiarly active pursuits shortly to be anticipated, Perth, the begrimed, blistered old blacksmith, had not removed his portable forge to the hold again, after concluding his contributory work for Ahab's leg, but still retained it on deck, fast lashed to ringbolts by the foremast; being now almost incessantly invoked by the headsmen, and harpooneers, and bowsmen to do some little job for them; altering, or repairing, or new shaping their various weapons and boat furniture. Often he would be surrounded by an eager circle, all waiting to be served; holding boat-spades, pike-heads, harpoons, and lances, and jealously watching his every sooty movement, as he toiled. Nevertheless, this old man's was a patient hammer wielded by a patient arm. No murmur, no impatience, no petulance did come from him. Silent, slow, and solemn; bowing over still further his chronically broken back, he toiled away, as if toil were life itself, and the heavy beating of his hammer the heavy beating of his heart. And so it was.—Most miserable! A peculiar walk in this old man, a certain slight but painful appearing yawing in his gait, had at an early period of the voyage excited the curiosity of the mariners. And to the importunity of their persisted questionings he had finally given in; and so it came to pass that every one now knew the shameful story of his wretched fate. Belated, and not innocently, one bitter winter's midnight, on the road running between two country towns, the blacksmith half-stupidly felt the deadly numbness stealing over him, and sought refuge in a leaning, dilapidated barn. The issue was, the loss of the extremities of both feet. Out of this revelation, part by part, at last came out the four acts of the gladness, and the one long, and as yet uncatastrophied fifth act of the grief of his life's drama. He was an old man, who, at the age of nearly sixty, had postponedly encountered that thing in sorrow's technicals called ruin. He had been an artisan of famed excellence, and with plenty to do; owned a house and garden; embraced a youthful, daughter-like, loving wife, and three blithe, ruddy children; every Sunday went to a cheerful-looking church, planted in a grove. But one night, under cover of darkness, and further concealed in a most cunning disguisement, a desperate burglar slid into his happy home, and robbed them all of everything. And darker yet to tell, the blacksmith himself did ignorantly conduct this burglar into his family's heart. It was the Bottle Conjuror! Upon the opening of that fatal cork, forth flew the fiend, and shrivelled up his home. Now, for prudent, most wise, and economic reasons, the blacksmith's shop was in the basement of his dwelling, but with a separate entrance to it; so that always had the young and loving healthy wife listened with no unhappy nervousness, but with vigorous pleasure, to the stout ringing of her young-armed old husband's hammer; whose reverberations, muffled by passing through the floors and walls, came up to her, not unsweetly, in her nursery; and so, to stout Labor's iron lullaby, the blacksmith's infants were rocked to slumber. Oh, woe on woe! Oh, Death, why canst thou not sometimes be timely? Hadst thou taken this old blacksmith to thyself ere his full ruin came upon him, then had the young widow had a delicious grief, and her orphans a truly venerable, legendary sire to dream of in their after years; and all of them a care-killing competency.\n" +
                            "        </p>\n" +
                            "      </div>\n" +
                            "  </body>\n" +
                            "</html>")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getJsonObjectRequestNoBodyTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient
            val result = suspendCancellableCoroutine { continuation: CancellableContinuation<Result<*, HoodiesNetworkError>> ->
                val request = client.getJsonObjectRequest(
                    "id", "post", HoodiesNetworkClient.HttpMethod.POST,
                    continuation = continuation, additionalHeaders = hashMapOf("testKey" to "testVal"), resultType = CallResponse::class.java
                )
                client.sendRequest("id", request as Request<Any>, null, cacheConfiguration = CacheDisabled())
            }

            when (result) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals((result.value as CallResponse).data, "null")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getJsonArrayRequestNoBodyTestNonInlined() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient
            val result = suspendCancellableCoroutine { continuation: CancellableContinuation<Result<*, HoodiesNetworkError>> ->
                val request = client.getJsonArrayRequest(
                    "id", "post", HoodiesNetworkClient.HttpMethod.POST,
                    continuation = continuation, additionalHeaders = hashMapOf("testKey" to "testVal"), resultType = CallResponse::class.java
                )
                client.sendRequest("id", request as Request<Any>, null, cacheConfiguration = CacheDisabled())
            }

            when (result) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals((result.value as CallResponse).data, "null")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun basicPatchRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient

            when (val result = client.patch("patch", resultType = CallResponse::class.java)) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals((result.value as CallResponse).url, "http://localhost:6969/patch")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun patchObjectRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient

            when (val result = client.patch("patch", JSONObject("{\"name\":\"Test\", \"age\":25}"), resultType = CallResponse::class.java)) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals((result.value as CallResponse).data, "{\"name\":\"Test\",\"age\":25}")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun patchJsonObjectRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient

            when (val result = client.patch("patch", JSONObject("{\"name\":\"Test\", \"age\":25}"), resultType = CallResponse::class.java)) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals((result.value as CallResponse).data, "{\"name\":\"Test\",\"age\":25}")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun patchStringRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient

            when (val result = client.patch("patch", "testBody", resultType = CallResponse::class.java)) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals((result.value as CallResponse).data, "\"testBody\"")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun patchJsonArrayRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build().nonInlinedClient

            when (val result = client.patch("patch", JSONArray(
                "[{\"name\":\"Test 1\", \"age\":25}," +
                        "{\"name\":\"Test 2\", \"age\":22},{\"name\":\"Test 3\", \"age\":21}]"
            ), resultType = CallResponse::class.java
            )) {
                is Success -> {
                    //Assert that the data was sent successfully and we got what we were expecting back
                    assertEquals((result.value as CallResponse).data, "[{\"name\":\"Test 1\",\"age\":25},{\"name\":\"Test 2\",\"age\":22},{\"name\":\"Test 3\",\"age\":21}]")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun basicRawPatchRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()

            when (val result = client.patchRaw("patch")) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals(gson.fromJson(result.value, CallResponse::class.java).url, "http://localhost:6969/patch")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun withStringRawPatchRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()

            when (val result = client.patchRaw("patch", "test")) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals(gson.fromJson(result.value, CallResponse::class.java).data, "\"test\"")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun withHashMapRawPatchRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()

            when (val result = client.patchRaw("patch", hashMapOf("param1" to "value"))) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    assertEquals(gson.fromJson(result.value, CallResponse::class.java).url, "http://localhost:6969/patch")
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }
}