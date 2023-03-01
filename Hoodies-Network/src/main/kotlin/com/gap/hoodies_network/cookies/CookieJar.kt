package com.gap.hoodies_network.cookies

import java.net.CookieManager
import java.net.CookieStore
import java.net.HttpCookie
import java.net.URI

open class CookieJar(cookieStore: CookieStore = CookieManager().cookieStore) : CookieManager(cookieStore, null) {

    /**
     * Gets all the cookies for a specified host
     * This is path-independent - the HttpCookie's Path attribute is ignored
     * So, cookies returned by this function may not be sent to all URLs on a particular host if Path attributes are used
     */
    fun getCookiesForHost(host: URI) : List<HttpCookie> {
        return this.cookieStore.get(host)
    }

    /**
     * Gets all cookies stored in the CookieJar
     */
    fun getAllCookies() : List<HttpCookie> {
        return this.cookieStore.cookies
    }

    /**
     * Gets all hosts that are associated with a cookie stored in the CookieJar
     */
    fun getAllHosts() : List<URI> {
        return this.cookieStore.urIs
    }

    /**
     * Overwrites all of the existing cookie for a specified host with the provided list
     */
    fun setCookiesForHost(host: URI, cookies: List<HttpCookie>) {
        this.cookieStore.get(host).forEach {
            this.cookieStore.remove(host, it)
        }

        cookies.forEach {
            this.cookieStore.add(host, it)
        }
    }

    /**
     * Adds a single cookie to the CookieJar for a host
     */
    fun addCookieForHost(host: URI, cookie: HttpCookie) {
        this.cookieStore.add(host, cookie)
    }

    /**
     * Removes every single cookie in the CookieJar
     */
    fun removeAllCookies() {
        this.cookieStore.removeAll()
    }

}