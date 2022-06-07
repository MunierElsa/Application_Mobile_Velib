package fr.epf.projet

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import fr.epf.projet.api.StationDetailsService
import fr.epf.projet.api.StationVelibService
import fr.epf.projet.database.AppDatabase
import fr.epf.projet.databinding.ActivityMapsBinding
import fr.epf.projet.model.Station
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    val stationslist: MutableList<Station> = mutableListOf()
    val listcoord: MutableList<Coord> = mutableListOf()
    val listdetails: MutableList<Details> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (checkForInternet(this)) {
            synchroApi()
        } else {
            Toast.makeText(this, "Pas de connexion à internet", Toast.LENGTH_LONG).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).allowMainThreadQueries().build()
        mMap = googleMap
        mMap.setMinZoomPreference(10F)
        for (i in stationslist) {
            val station = LatLng("${i.lat}".toDouble(), "${i.lon}".toDouble())
            if (db.stationDao().getStation(i.name) != null) {
                db.stationDao().update(
                    i.name,
                    i.numBikesAvailable,
                    i.numDocksAvailable,
                    i.is_returning,
                    i.is_renting,
                    i.is_installed
                )
                mMap.addMarker(
                    MarkerOptions().position(station).title("${i.name}")
                        .icon(BitmapDescriptorFactory.defaultMarker(150F))
                )
            } else mMap.addMarker(
                MarkerOptions().position(station).title("${i.name}")
                    .icon(BitmapDescriptorFactory.defaultMarker(290F))
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLng(station))
            mMap.setOnInfoWindowClickListener {
                val intent = Intent(this, DetailsActivity::class.java)
                intent.putExtra("name", it.title)
                for (j in stationslist) {
                    if (it.title.equals(j.name)) {
                        intent.putExtra("station_id", j.station_id)
                        intent.putExtra("is_installed", j.is_installed)
                        intent.putExtra("is_renting", j.is_renting)
                        intent.putExtra("is_returning", j.is_returning)
                        intent.putExtra("numBikes", j.numBikesAvailable)
                        intent.putExtra("numDocks", j.numDocksAvailable)
                        intent.putExtra("lat", j.lat)
                        intent.putExtra("lon", j.lon)
                        intent.putExtra("capacity", j.capacity)
                        intent.putExtra("stationCode", j.stationCode)
                    }
                }
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.favoris_list_actions -> {
                startActivity(Intent(this, FavorisActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun synchroApi() {

        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val station = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://velib-metropole-opendata.smoove.pro/opendata/Velib_Metropole/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(station)
            .build()

        val service = retrofit.create(StationVelibService::class.java)
        val service2 = retrofit.create(StationDetailsService::class.java)

        runBlocking {
            val result = service.getStations()
            val result2 = service2.getDetails()
            val stations = result.data.stations
            val stations2 = result2.data.stations

            stations.map {
                val (station_id, name, lat, lon, capacity, stationCode) = it
                Coord(
                    station_id, name, lat, lon, capacity, stationCode
                )
            }.map {
                listcoord.add(it)
            }

            stations2.map {
                val (station_id, is_installed, is_renting, is_returning, numBikesAvailable, numDocksAvailable) = it

                Details(
                    station_id,
                    is_installed,
                    is_renting,
                    is_returning,
                    numBikesAvailable,
                    numDocksAvailable
                )


            }.map {
                listdetails.add(it)
            }
        }

        for (i in listcoord) {
            for (j in listdetails)
                if (i.station_id == j.station_id) {
                    stationslist.add(
                        Station(
                            i.station_id,
                            i.name,
                            i.lat,
                            i.lon,
                            i.capacity,
                            i.stationCode,
                            j.is_installed,
                            j.is_renting,
                            j.is_returning,
                            j.numBikesAvailable,
                            j.numDocksAvailable
                        )
                    )
                }
        }
        listdetails.clear()
        listcoord.clear()
    }

    @SuppressLint("MissingPermission")
    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

}

data class Coord(
    val station_id: Long,
    val name: String,
    val lat: Double,
    val lon: Double,
    val capacity: Int,
    val stationCode: String,
)

data class Details(
    val station_id: Long,
    val is_installed: Int,
    val is_renting: Int,
    val is_returning: Int,
    val numBikesAvailable: Int,
    val numDocksAvailable: Int,
)

