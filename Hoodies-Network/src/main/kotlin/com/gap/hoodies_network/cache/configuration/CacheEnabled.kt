package com.gap.hoodies_network.cache.configuration

import android.content.Context
import java.time.Duration

class CacheEnabled(
    val staleDataThreshold: Duration = Duration.ofHours(1),
    val encryptionEnabled: Boolean = false,
    val applicationContext: Context
) : CacheConfiguration