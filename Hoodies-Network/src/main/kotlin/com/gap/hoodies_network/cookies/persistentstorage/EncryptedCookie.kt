package com.gap.hoodies_network.cookies.persistentstorage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gap.hoodies_network.utils.Generated

@Entity
@Generated
data class EncryptedCookie(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "host") val host: String?,
    @ColumnInfo(name = "cookie") val cookie: String?,
    @ColumnInfo(name = "iv") val iv: String?,
    @ColumnInfo(name = "hash") val hash: Int,
)