@file:Suppress("UNCHECKED_CAST", "NAME_SHADOWING")
package com.gap.hoodies_network

import androidx.test.platform.app.InstrumentationRegistry
import com.gap.hoodies_network.cookies.CookieJar
import com.gap.hoodies_network.cookies.PersistentCookieJar
import com.gap.hoodies_network.core.Failure
import com.gap.hoodies_network.core.HoodiesNetworkClient
import com.gap.hoodies_network.core.Success
import com.gap.hoodies_network.mockwebserver.ServerManager
import com.gap.hoodies_network.mockwebserver.helper.similar
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.net.CookieManager
import java.net.HttpCookie
import java.net.URI
import java.util.*

class CookieTests {

    @Before
    fun startMockWebServer() {
        ServerManager.setup(InstrumentationRegistry.getInstrumentation().context)
    }

    @After
    fun stopServer() {
        ServerManager.stop()
    }

    @Test
    fun cookieTestPersistent() {
        runBlocking {
            val cookieJar = PersistentCookieJar("testForPersistentCookies", InstrumentationRegistry.getInstrumentation().targetContext)
            cookieJar.removeAllCookies()
            val client = HoodiesNetworkClient.Builder()
                .baseUrl("http://localhost:6969")
                .enableCookiesWithCookieJar(cookieJar)
                .build()

            //Request 3 random cookies from the CookieFactory
            val requestJson = JSONObject()
            requestJson.put(UUID.randomUUID().toString(), UUID.randomUUID().toString())
            requestJson.put(UUID.randomUUID().toString(), UUID.randomUUID().toString())
            requestJson.put(UUID.randomUUID().toString(), UUID.randomUUID().toString())

            when (val result = client.post<String>("/cookie_factory", requestJson)) {
                is Success -> {

                    //Add two extra cookies. One for localhost and one for another URL
                    val newKey = UUID.randomUUID().toString()
                    val newValue = UUID.randomUUID().toString()
                    requestJson.put(newKey, newValue)
                    cookieJar.addCookieForHost(URI("http://localhost"), HttpCookie(newKey, newValue).apply { version = 0 })
                    cookieJar.addCookieForHost(URI("https://gap.com"), HttpCookie("gapCookieName", "gapCookieValue").apply { version = 0 })

                    //Confirm the cookies in the jar for localhost
                    var cookies = cookieJar.getCookiesForHost(URI("http://localhost"))
                    assertEquals(cookies.size, 4)
                    for (i in 0 until 4) {
                        assertEquals(cookies[i].value, requestJson.getString(cookies[i].name))
                    }

                    //Confirm all cookies in the jar
                    cookies = cookieJar.getAllCookies()
                    assertEquals(cookies.size, 5)
                    for (i in 0 until 5) {
                        if (cookies[i].name == "gapCookieName")
                            assertEquals(cookies[i].value, "gapCookieValue")
                        else
                            assertEquals(cookies[i].value, requestJson.getString(cookies[i].name))
                    }

                    //Confirm that the hosts for the cookies are localhost and gap.com
                    val hosts = cookieJar.getAllHosts()
                    assertEquals(hosts.size, 2)
                    assertEquals(hosts[0], URI("http://localhost"))
                    assertEquals(hosts[1], URI("http://gap.com"))


                    //The cookies should now be stored
                    //To verify this, we make a request to CookieInspector in order to get our cookies back
                    when (val inspectorResult = client.post<String>("/cookie_inspector", requestJson)) {
                        is Success -> {
                            val resultJson = JSONObject(inspectorResult.value)

                            //And now we assert that we got our cookies back
                            assert(requestJson.similar(resultJson))

                            //Assert the cookie for gap.com wasn't sent to localhost
                            assert(!resultJson.has("gapCookieName"))

                            //Now, we overwrite all the localhost cookies
                            val newCookies: ArrayList<HttpCookie> = arrayListOf()
                            for (i in 0 until 4) {
                                newCookies.add(HttpCookie(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                            }
                            cookieJar.setCookiesForHost(URI("http://localhost"), newCookies)

                            //And the verify that the cookies were actually overwritten
                            val cookies = cookieJar.getCookiesForHost(URI("http://localhost"))
                            assertEquals(cookies.size, 4)
                            for (i in 0 until 4) {
                                assertEquals(cookies[i].value, newCookies[i].value)
                                assertEquals(cookies[i].name, newCookies[i].name)
                            }

                            //Finally, ensure that cookie deletion works
                            cookieJar.removeAllCookies()
                            assertEquals(cookieJar.getAllCookies().size, 0)

                        }
                        is Failure -> {
                            throw inspectorResult.reason
                        }
                    }
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun cookieTestNonPersistent() {
        runBlocking {
            val cookieJar = CookieJar()
            cookieJar.removeAllCookies()
            val client = HoodiesNetworkClient.Builder()
                .baseUrl("http://localhost:6969")
                .enableCookiesWithCookieJar(cookieJar)
                .build()

            //Request 3 random cookies from the CookieFactory
            val requestJson = JSONObject()
            requestJson.put(UUID.randomUUID().toString(), UUID.randomUUID().toString())
            requestJson.put(UUID.randomUUID().toString(), UUID.randomUUID().toString())
            requestJson.put(UUID.randomUUID().toString(), UUID.randomUUID().toString())

            when (val result = client.post<String>("/cookie_factory", requestJson)) {
                is Success -> {

                    //Add two extra cookies. One for localhost and one for another URL
                    val newKey = UUID.randomUUID().toString()
                    val newValue = UUID.randomUUID().toString()
                    requestJson.put(newKey, newValue)
                    cookieJar.addCookieForHost(URI("http://localhost"), HttpCookie(newKey, newValue).apply { version = 0 })
                    cookieJar.addCookieForHost(URI("https://gap.com"), HttpCookie("gapCookieName", "gapCookieValue").apply { version = 0 })

                    //Confirm the cookies in the jar for localhost
                    var cookies = cookieJar.getCookiesForHost(URI("http://localhost"))
                    assertEquals(cookies.size, 4)
                    for (i in 0 until 4) {
                        assertEquals(cookies[i].value, requestJson.getString(cookies[i].name))
                    }

                    //Confirm all cookies in the jar
                    cookies = cookieJar.getAllCookies()
                    assertEquals(cookies.size, 5)
                    for (i in 0 until 5) {
                        if (cookies[i].name == "gapCookieName")
                            assertEquals(cookies[i].value, "gapCookieValue")
                        else
                            assertEquals(cookies[i].value, requestJson.getString(cookies[i].name))
                    }

                    //Confirm that the hosts for the cookies are localhost and gap.com
                    val hosts = cookieJar.getAllHosts()
                    assertEquals(hosts.size, 2)
                    assertEquals(hosts[0], URI("http://localhost"))
                    assertEquals(hosts[1], URI("http://gap.com"))


                    //The cookies should now be stored
                    //To verify this, we make a request to CookieInspector in order to get our cookies back
                    when (val inspectorResult = client.post<String>("/cookie_inspector", requestJson)) {
                        is Success -> {
                            val resultJson = JSONObject(inspectorResult.value)

                            //And now we assert that we got our cookies back
                            assert(requestJson.similar(resultJson))

                            //Assert the cookie for gap.com wasn't sent to localhost
                            assert(!resultJson.has("gapCookieName"))

                            //Now, we overwrite all the localhost cookies
                            val newCookies: ArrayList<HttpCookie> = arrayListOf()
                            for (i in 0 until 4) {
                                newCookies.add(HttpCookie(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                            }
                            cookieJar.setCookiesForHost(URI("http://localhost"), newCookies)

                            //And the verify that the cookies were actually overwritten
                            val cookies = cookieJar.getCookiesForHost(URI("http://localhost"))
                            assertEquals(cookies.size, 4)
                            for (i in 0 until 4) {
                                assertEquals(cookies[i].value, newCookies[i].value)
                                assertEquals(cookies[i].name, newCookies[i].name)
                            }

                            //Finally, ensure that cookie deletion works
                            cookieJar.removeAllCookies()
                            assertEquals(cookieJar.getAllCookies().size, 0)

                        }
                        is Failure -> {
                            throw inspectorResult.reason
                        }
                    }
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun noCookieTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder()
                .baseUrl("http://localhost:6969")
                .build()

            //Request 3 random cookies from the CookieFactory
            val requestJson = JSONObject()
            requestJson.put(UUID.randomUUID().toString(), UUID.randomUUID().toString())
            requestJson.put(UUID.randomUUID().toString(), UUID.randomUUID().toString())
            requestJson.put(UUID.randomUUID().toString(), UUID.randomUUID().toString())

            when (val result = client.post<String>("/cookie_factory", requestJson)) {
                is Success -> {

                    //The cookies should now be stored
                    //To verify this, we make a request to CookieInspector in order to get our cookies back
                    when (val inspectorResult = client.post<String>("/cookie_inspector", requestJson)) {
                        is Success -> {
                            assertEquals(result.value, "{}")
                        }
                        is Failure -> {
                            throw inspectorResult.reason
                        }
                    }
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

}