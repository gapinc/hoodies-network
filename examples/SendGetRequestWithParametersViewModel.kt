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

class SendGetRequestWithParametersViewModel : ViewModel() {

    private var weatherHttpClient: HoodiesNetworkClient = HoodiesNetworkClient.Builder().baseUrl("https://samples.openweathermap.org").build()

    internal var getURLQueryResponse = MutableLiveData<String>()

    internal fun sendGetUrlQueryRequest() {
        try {
            viewModelScope.launch(Dispatchers.Main) {
                val result: Result<WeatherResponse, HoodiesNetworkError> = withContext(Dispatchers.IO) {
                    val queryParams: HashMap<String, String> = HashMap()
                    queryParams["q"] = "London,uk"
                    queryParams["appid"] = "2b1fd2d7f77ccf1b7de9b441571b39b8"
                    val api = "/data/2.5/weather"
                    weatherHttpClient.getUrlQueryParam(
                        queryParams = queryParams,
                        api = api
                    )
                }
                when (result) {
                    is Success -> {
                        getURLQueryResponse.postValue(gson.toJson(result.value))
                    }
                    is Failure -> {
                        getURLQueryResponse.postValue(result.reason.message)
                    }
                }
            }
        } catch (e: MalformedURLException) {
            Log.e("ERROR", e.toString())
        }
    }


    fun getError(HoodiesNetworkError: HoodiesNetworkError): String {
        return "Code Error: " + HoodiesNetworkError.code.toString() + " Message: " + HoodiesNetworkError.message
    }

}