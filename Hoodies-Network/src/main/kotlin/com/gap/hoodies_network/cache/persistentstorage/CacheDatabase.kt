package com.gap.hoodies_network.cache.persistentstorage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gap.hoodies_network.utils.Generated

@Generated
@Database(entities = [CachedData::class], version = 1, exportSchema = false)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun cacheDao(): CacheDao
}