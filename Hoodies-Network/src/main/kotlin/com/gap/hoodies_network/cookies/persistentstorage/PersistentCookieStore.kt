package com.gap.hoodies_network.cookies.persistentstorage

import android.content.Context
import java.net.CookieStore
import java.net.HttpCookie
import java.net.URI

internal class PersistentCookieStore(instanceName: String, context: Context) : CookieStore {
    private val roomDb = EncryptedDaoWrapperForCookies(instanceName, context)

    override fun add(uri: URI, cookie: HttpCookie) {
        roomDb.insert(uri, cookie)
    }

    override fun get(uri: URI): MutableList<HttpCookie> {
        return roomDb.getByHost(uri).toMutableList()
    }

    override fun getCookies(): MutableList<HttpCookie> {
        return roomDb.getAll().toMutableList()
    }

    override fun getURIs(): MutableList<URI> {
        return roomDb.getAllHosts().toMutableList()
    }

    override fun remove(uri: URI, cookie: HttpCookie): Boolean {
        return roomDb.deleteCookie(cookie)
    }

    override fun removeAll(): Boolean {
        roomDb.deleteAll()
        return true
    }
}