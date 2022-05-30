package fr.epf.projet.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Station(
    @PrimaryKey val station_id: Long,
    @ColumnInfo(name = "name") val name: String,

    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "lon") val lon: Double,
    @ColumnInfo(name = "capacity") val capacity: Int,
    @ColumnInfo(name = "stationCode") val stationCode: String,

    @ColumnInfo(name = "is_installed") val is_installed: Int,
    @ColumnInfo(name = "is_renting") val is_renting: Int,
    @ColumnInfo(name = "is_returning") val is_returning: Int,
    @ColumnInfo(name = "numBikesAvailable") val numBikesAvailable: Int,
    @ColumnInfo(name = "numDocksAvailable") val numDocksAvailable: Int,
    ) {
    companion object {
        fun all(nb: Int = 20) = (1..20).map {
            Station(
                1,
                "$it",
                3.00,
                3.00,
                4,
                "$it",
                1,
                0,
                1,
                12,
                3
            )
        }
    }
}
