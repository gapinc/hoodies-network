package com.gap.hoodies_network.cookies

import android.content.Context
import com.gap.hoodies_network.cookies.persistentstorage.PersistentCookieStore

class PersistentCookieJar(instanceName: String, context: Context) : CookieJar(PersistentCookieStore(instanceName, context))