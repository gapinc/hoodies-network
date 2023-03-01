import android.graphics.Bitmap
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gap.hoodies_network.config.Failure
import com.gap.hoodies_network.config.HoodiesNetworkClient
import com.gap.hoodies_network.config.Success
import com.gap.hoodies_network.core.HoodiesNetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel to fetch an image from an endpoint
 * A Bitmap is returned, various settings are configurable
 */
class ImageRequestViewModel : ViewModel() {
    val getImageResponse = MutableLiveData<Bitmap>()

    fun sendGetImageRequest() {
        //Setting interceptor and default headers
        private val sessionInterceptor: SessionInterceptor = SessionInterceptor()
        val defaultHeaders = hashMapOf(
           CONTENT_TYPE_KEY to APPLICATION_JSON, MULTIDB_ENABLED to MULTIDB_ENABLED_VALUE,
           CLIENT_ID to CLIENT_ID_VALUE,
           CLIENT_OS to CLIENT_OS_VALUE
        )
        var mobileHttpClient: HoodiesNetworkClient = HoodiesNetworkClient.Builder().baseUrl("https://httpbin.org/").addHeaders(defaultHeaders).addInterceptor(sessionInterceptor).build()

        //Set timeouts to 1000ms
        HttpClientConfig.setConnectTimeOut(1000)
        HttpClientConfig.setReadTimeOut(1000)

        //Reset timeout to default values like this:
        //HttpClientConfig.setFactoryDefaultConfiguration()

        viewModelScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                mobileHttpClient.getImage(
                    "image", //https://httpbin.org/image
                    null,
                    0,
                    0,
                    ImageView.ScaleType.CENTER,
                    Bitmap.Config.ALPHA_8
                )
            }
            when (result) {
                is Success -> {
                    getImageResponse.setValue(result.value)
                }
                is Failure -> {
                    getImageResponse.setValue(null)

                }
            }
        }
    }

}