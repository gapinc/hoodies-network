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
 * ViewModel to send a JSONarray to an endpoint
 */
class JsonArrayRequestViewModel : ViewModel() {

    private var mobileHttpClient: HoodiesNetworkClient = HoodiesNetworkClient.Builder().baseUrl("https://httpbin.org/").build()
    private val gson = Gson()
    internal var jsonArrayResponse = MutableLiveData<String>()

    internal fun sendJsonArrayRequest() {
        viewModelScope.launch(Dispatchers.Main) {
            val url = "post" //baseurl + this, so http://localhost:6969/
            val jsonArray = JSONArray(
                "[{\"name\":\"Test 1\", \"age\":25}," +
                        "{\"name\":\"Test 2\", \"age\":22},{\"name\":\"Test 3\", \"age\":21}]"
            ) //We will send this JsonArray
            val result: Result<CallResponse, HoodiesNetworkError> = withContext(Dispatchers.IO) {
                mobileHttpClient.post(url, jsonArray)
            }
            when (result) {
                is Success -> {
                    jsonArrayResponse.postValue(gson.toJson(result.value))
                }
                is Failure -> {
                    jsonArrayResponse.postValue(gson.toJson(result.reason.message))
                }
            }
        }
    }
}