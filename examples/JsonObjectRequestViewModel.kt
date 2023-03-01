import androidx.lifecycle.*
import com.gap.hoodies_network.config.*
import com.gap.hoodies_network.core.HoodiesNetworkError
import com.gap.hoodies_network.core.HoodiesNetworkClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * ViewModel to send a JSON object to an endpoint
 */
class JsonObjectRequestViewModel : ViewModel() {

    private var mobileHttpClient: HoodiesNetworkClient = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").build()
    private val gson = Gson()
    internal var jsonObjectResponse = MutableLiveData<String>()

    internal fun sendJsonObjectRequest(): LiveData<String> {
        viewModelScope.launch(Dispatchers.Main) {
            val url = "post" //baseurl + this, so http://localhost:6969/post
            val jsonObject = JSONObject("{\"name\":\"Test\", \"age\":25}") //We will send this JsonObject
            val result: Result<CallResponse, HoodiesNetworkError> = withContext(Dispatchers.IO) {
                mobileHttpClient.post(url, jsonObject)
            }
            when (result) {
                is Success -> {
                    jsonObjectResponse.postValue(gson.toJson(result.value))
                }
                is Failure -> {
                    jsonObjectResponse.postValue(gson.toJson(result.reason.message))
                }
            }
        }
        return jsonObjectResponse
    }
}