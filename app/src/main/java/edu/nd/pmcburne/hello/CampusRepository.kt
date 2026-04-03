package edu.nd.pmcburne.hello

import android.content.Context

class CampusRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val dao = db.locationDao()
    private val api = PlacemarkApi.retrofitService

    suspend fun syncPlacemarkData() {
        val networkLocations = api.getPlacemarks()

        val entities = networkLocations.map { placemark ->
            LocationEntity(
                id = placemark.id,
                name = placemark.name,
                description = placemark.description,
                latitude = placemark.visual_center.latitude,
                longitude = placemark.visual_center.longitude,
                tagList = placemark.tag_list
            )
        }

        dao.insertAll(entities)
    }

    suspend fun getAllLocations(): List<LocationEntity> {
        return dao.getAllLocations()
    }
}