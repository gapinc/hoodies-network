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
    private var mobileHttpClient: HoodiesNetworkClient = HoodiesNetworkClient.Builder().baseUrl("https://httpbin.org/").build()
    val getImageResponse = MutableLiveData<Bitmap>()

    fun sendGetImageRequest() {
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