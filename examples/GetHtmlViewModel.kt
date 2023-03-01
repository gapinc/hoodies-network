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

class GetHtmlViewModel : ViewModel() {

    private var mobileHttpClient: HoodiesNetworkClient = HoodiesNetworkClient.Builder().baseUrl("https://httpbin.org/").build()

    internal var getHtmlResponse = MutableLiveData<String>()

    internal fun sendGetHtmlRequest() {
        viewModelScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                val url = "html"
                mobileHttpClient.getHtml(url)
            }
            when (result) {
                is Success -> {
                    getHtmlResponse.postValue(result.value)
                }
                is Failure -> {
                    getHtmlResponse.postValue(result.reason.message)
                }
            }
        }
    }

    fun getError(HoodiesNetworkError: HoodiesNetworkError): String {
        return "Code Error: " + HoodiesNetworkError.code.toString() + " Message: " + HoodiesNetworkError.message
    }

}