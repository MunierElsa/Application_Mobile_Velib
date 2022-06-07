package fr.epf.projet.dao

import androidx.room.*
import fr.epf.projet.model.Station

@Dao
interface StationDao {
    @Query("SELECT * FROM Station")
    fun getAll(): List<Station>

    @Query("SELECT * FROM Station WHERE name=(:name)")
    fun getStation(name: String): Station

    @Insert
    fun insertAll(vararg stations: Station)

    @Insert
    fun insertOne(station: Station)

    @Delete
    fun delete(station: Station)

    @Query("UPDATE Station SET numDocksAvailable=(:numDocksAvailable), numBikesAvailable=(:numBikesAvailable), is_returning=(:is_returning), is_renting=(:is_renting), is_installed=(:is_installed) WHERE name=(:name)")
    fun update(
        name: String,
        numBikesAvailable: Int,
        numDocksAvailable: Int,
        is_returning: Int,
        is_renting: Int,
        is_installed: Int
    )
}
