package com.gap.hoodies_network.cache.persistentstorage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CacheDao {
    @Insert
    fun insert(vararg item: CachedData)

    @Query("SELECT * FROM cacheddata WHERE iv = :iv")
    fun getByIv(iv: String): List<CachedData>

    @Query("SELECT * FROM cacheddata WHERE url = :url AND bodyHash = :bodyHash")
    fun get(url: String, bodyHash: Int): CachedData?

    @Query("DELETE FROM cacheddata WHERE url = :url AND bodyHash = :bodyHash")
    fun delete(url: String, bodyHash: Int)
}