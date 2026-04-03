package edu.nd.pmcburne.hello

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class VisualCenterDto(
    val latitude: Double,
    val longitude: Double
)

data class PlacemarkDto(
    val id: Int,
    val name: String,
    val tag_list: List<String>,
    val description: String,
    val visual_center: VisualCenterDto
)

interface PlacemarkApiService {
    @GET("~wxt4gm/placemarks.json")
    suspend fun getPlacemarks(): List<PlacemarkDto>
}

object PlacemarkApi {
    private const val BASE_URL = "https://www.cs.virginia.edu/"

    val retrofitService: PlacemarkApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlacemarkApiService::class.java)
    }
}