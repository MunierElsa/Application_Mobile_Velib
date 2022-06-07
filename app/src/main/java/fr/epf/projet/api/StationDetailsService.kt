package fr.epf.projet.api

import retrofit2.http.GET

interface StationDetailsService {

    @GET("station_status.json")
    suspend fun getDetails(): GetDetailsResult

}

data class GetDetailsResult(val data: StationsDetails)
data class StationsDetails(val stations: List<Details>)
data class Details(
    val station_id: Long,
    val is_installed: Int,
    val is_renting: Int,
    val is_returning: Int,
    val numBikesAvailable: Int,
    val numDocksAvailable: Int,
)