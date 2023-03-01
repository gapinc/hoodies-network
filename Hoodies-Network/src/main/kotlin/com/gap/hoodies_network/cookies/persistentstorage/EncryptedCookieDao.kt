package com.gap.hoodies_network.cookies.persistentstorage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EncryptedCookieDao {
    @Query("SELECT * FROM encryptedcookie")
    fun getAll(): List<EncryptedCookie>

    @Query("SELECT * FROM encryptedcookie WHERE host = :host")
    fun getByHost(host: String): List<EncryptedCookie>

    @Query("SELECT * FROM encryptedcookie WHERE iv = :iv")
    fun getByIv(iv: String): List<EncryptedCookie>

    @Query("DELETE FROM encryptedcookie")
    fun deleteAll()

    @Query("DELETE FROM encryptedcookie WHERE hash = :hash")
    fun deleteByHash(hash: Int): Int

    @Query("SELECT DISTINCT host FROM encryptedcookie")
    fun getAllHosts(): List<String>

    @Insert
    fun insert(vararg encryptedcookie: EncryptedCookie)
}