package com.gap.hoodies_network.testObjects

import com.google.gson.annotations.SerializedName

data class CallResponse(
    @field:SerializedName("headers") val headers: Headers,
    @field:SerializedName("origin") val origin: String,
    @field:SerializedName("data") val data: String,
    @field:SerializedName("url") val url: String
)