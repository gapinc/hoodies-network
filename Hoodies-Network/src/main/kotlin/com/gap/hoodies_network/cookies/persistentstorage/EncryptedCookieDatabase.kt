package com.gap.hoodies_network.cookies.persistentstorage

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [EncryptedCookie::class], version = 1, exportSchema = false)
abstract class EncryptedCookieDatabase : RoomDatabase() {
    abstract fun encryptedCookieDao(): EncryptedCookieDao
}