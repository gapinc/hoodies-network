import com.google.gson.annotations.SerializedName

data class Headers(
    @field:SerializedName("Accept") val accept: String,
    @field:SerializedName("Accept-Encoding") val acceptEncoding: String,
    @field:SerializedName("Postman-Token") val postmanToken: String,
    @field:SerializedName("User-Agent") val userAgent: String,
    @field:SerializedName("X-Amzn-Trace-Id") val xAmznTraceId: String,
)