import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @field:SerializedName("coord") val coord: CoordData?,
    @field:SerializedName("weather") val weather: List<WeatherData>?,
    @field:SerializedName("main") val main: MainData?,
    @field:SerializedName("clouds") val clouds: CloudsData?
)

data class WeatherData(
    @field:SerializedName("id") val id: String?,
    @field:SerializedName("main") val main: String?,
    @field:SerializedName("description") val description: String?,
    @field:SerializedName("icon") val clouds: String?
)

data class CoordData(
    @field:SerializedName("lat") val latitude: String?,
    @field:SerializedName("lon") val longitude: String?
)

data class MainData(
    @field:SerializedName("temp") val temp: String?,
    @field:SerializedName("pressure") val pressure: String?,
    @field:SerializedName("humidity") val humidity: String?
)

data class CloudsData(
    @field:SerializedName("all") val all: String?
)