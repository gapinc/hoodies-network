package com.gap.hoodies_network.cookies.persistentstorage

import android.content.Context
import androidx.room.Room
import com.gap.hoodies_network.cache.EncryptedCache
import com.google.gson.Gson
import java.net.HttpCookie
import java.net.URI
import java.util.*
import javax.crypto.Cipher

class EncryptedDaoWrapperForCookies(instanceName: String, context: Context) {
    private val db = Room.databaseBuilder(context, EncryptedCookieDatabase::class.java, instanceName).build().encryptedCookieDao()

    fun getAll() : List<HttpCookie> {
        return db.getAll().map{ decryptCookie(it).cookie }.toList()
    }

    fun getByHost(host: URI) : List<HttpCookie> {
        return db.getByHost(uriToHost(host)).map{ decryptCookie(it).cookie }.toList()
    }

    private fun decryptCookie(encryptedCookie: EncryptedCookie) : CookieAndId {
        val iv = Base64.getDecoder().decode(encryptedCookie.iv)
        val decryptedCookieJson = EncryptedCache.runAES(Base64.getDecoder().decode(encryptedCookie.cookie), iv, Cipher.DECRYPT_MODE).decodeToString()

        return CookieAndId(Gson().fromJson(decryptedCookieJson, HttpCookie::class.java), encryptedCookie.id)
    }

    fun deleteAll() {
        db.deleteAll()
    }

    fun deleteCookie(cookie: HttpCookie) : Boolean {
        return db.deleteByHash(cookie.hashCode()) > 0
    }

    fun getAllHosts() : List<URI> {
        return db.getAllHosts().map{ URI(it) }.toList()
    }

    fun insert(host: URI, cookie: HttpCookie) {
        val cookieJson = Gson().toJson(cookie)
        var iv = EncryptedCache.genIV()

        //Make sure the IV is unique
        while (db.getByIv(Base64.getEncoder().encodeToString(iv)).isNotEmpty())
            iv = EncryptedCache.genIV()

        val encryptedCookieJson = Base64.getEncoder().encodeToString(EncryptedCache.runAES(cookieJson.encodeToByteArray(), iv, Cipher.ENCRYPT_MODE))

        db.insert(EncryptedCookie(0, uriToHost(host), encryptedCookieJson, Base64.getEncoder().encodeToString(iv), cookie.hashCode()))
    }

    private fun uriToHost(uri: URI) : String {
        return "${uri.scheme.replace("https", "http")}://${uri.host}"
    }

    data class CookieAndId(val cookie: HttpCookie, val id: Int)
}