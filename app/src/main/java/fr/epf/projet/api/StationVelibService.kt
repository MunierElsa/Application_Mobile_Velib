package fr.epf.projet.api

import com.squareup.moshi.Json
import fr.epf.projet.model.Station
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface StationVelibService {

    @GET("station_information.json")
    suspend fun getStations(): GetStationsResult

}
data class GetStationsResult(val lastUpdatedOther: Int, val ttl: Int, val data: Stations)
data class Stations(val stations : List<Coord>)
data class Coord(
    val station_id: Long,
    val name: String,
    val lat: Double,
    val lon: Double,
    val capacity: Int,
    val stationCode: String,
)