package fr.epf.projet.database

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.epf.projet.dao.StationDao
import fr.epf.projet.model.Station

@Database(entities = [Station::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stationDao(): StationDao
}