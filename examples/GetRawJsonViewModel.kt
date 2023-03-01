package com.gap.hoodies_network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gap.hoodies_network.config.*
import com.gap.hoodies_network.core.HoodiesNetworkError
import com.gap.hoodies_network.core.HoodiesNetworkClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.MalformedURLException
import java.util.HashMap

class GetRawJsonViewModel : ViewModel() {

    private var mobileHttpClient: HoodiesNetworkClient = HoodiesNetworkClient.Builder().baseUrl("https://httpbin.org/").build()

    internal var getRawJsonResponse = MutableLiveData<String>()

    internal fun sendGetJsonRequest() {
        viewModelScope.launch(Dispatchers.Main) {
            val result: Result<String, HoodiesNetworkError> = withContext(Dispatchers.IO) {
                val url = "get"
                mobileHttpClient.getRaw(url)
            }
            when (result) {
                is Success -> {
                    getRawJsonResponse.postValue(result.value)
                }
                is Failure -> {
                    getRawJsonResponse.postValue(getError(result.reason))
                }
            }
        }
    }

    fun getError(HoodiesNetworkError: HoodiesNetworkError): String {
        return "Code Error: " + HoodiesNetworkError.code.toString() + " Message: " + HoodiesNetworkError.message
    }

}