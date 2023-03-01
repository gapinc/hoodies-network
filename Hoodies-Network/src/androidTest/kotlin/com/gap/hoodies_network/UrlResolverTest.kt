package com.gap.hoodies_network

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gap.hoodies_network.config.UrlResolver
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.net.URL

@RunWith(AndroidJUnit4::class)
class UrlResolverTest {
    @Test
    fun resolveHttpProtocolHttpsTest() {
        val url = URL("https://gap.com/order")
       val urlString= UrlResolver.resolveHttpProtocol(url)
        assertEquals("https", urlString)
    }

    @Test
    fun resolveHttpProtocolHttpTest(){
        val url = URL("http://gap.com/order")
        val urlString = UrlResolver.resolveHttpProtocol(url)
        assertEquals("http", urlString)
    }

    @Test
    fun resolveUrlWithoutHttpTest(){
        val url = UrlResolver.resolveUrl("gap.com/order")
        assertEquals("gap.com/order", url)
    }
    @Test
    fun resolveUrlWithHttpTest(){
        val url = UrlResolver.resolveUrl("http://gap.com/order")
        assertEquals("gap.com/order", url)
    }

    @Test
    fun validateUrlTest(){
        val url = UrlResolver.validateUrl("https://gap.com/order")
        val url1 = UrlResolver.validateUrl("http://gap.com/order")
        val url2 = UrlResolver.validateUrl("gap.com/order")
        Assert.assertEquals("https://gap.com/order",url)
        Assert.assertEquals("http://gap.com/order",url1)
        Assert.assertEquals("https://gap.com/order",url2)
    }

    @Test
    fun getProtocolTest(){
        val url = UrlResolver.getProtocol("https://gap.com/order")
        Assert.assertEquals("https", url)
    }

    @Test
    fun wrongProtocolFixed() {
        val url = UrlResolver.resolveHttpProtocol(URL("ftp://gap.com"))
        Assert.assertEquals(url, "https")
    }

    @Test
    fun rawIP() {
        val res = UrlResolver.resolveUrl("8.8.8.8")
        assertEquals(res, "8.8.8.8")
    }

    @Test
    fun validateURL() {
        var url = UrlResolver.validateUrl("https:/gap.com")
        Assert.assertEquals(url, "https://gap.com")

        url = UrlResolver.validateUrl("http:/gap.com")
        Assert.assertEquals(url, "http://gap.com")

        url = UrlResolver.validateUrl("://gap.com")
        Assert.assertEquals(url, "https://gap.com")

        url = UrlResolver.validateUrl("//gap.com")
        Assert.assertEquals(url, "https://gap.com")
    }
}