package com.gap.hoodies_network.cache.persistentstorage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CachedData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "bodyHash") val bodyHash: Int,
    @ColumnInfo(name = "cachedAt") val cachedAt: Long,
    @ColumnInfo(name = "data") var data: String,
    @ColumnInfo(name = "iv") var iv: String?,
)