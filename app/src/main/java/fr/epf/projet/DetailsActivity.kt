package fr.epf.projet

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.internal.ContextUtils.getActivity
import fr.epf.projet.database.AppDatabase
import fr.epf.projet.model.Station
import okhttp3.internal.notify
import java.security.AccessController.getContext

class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).allowMainThreadQueries().build()

        val name = intent.getStringExtra("name")
        val station_id = intent.getLongExtra("station_id",-1)
        val is_installed = intent.getIntExtra("is_installed", -1)
        val is_renting = intent.getIntExtra("is_renting",-1)
        val is_returning = intent.getIntExtra("is_returning",-1)
        val numBikes = intent.getIntExtra("numBikes", 0)
        val numDocks = intent.getIntExtra("numDocks", 0)
        val lat = intent.getDoubleExtra("station_id",-1.0)
        val lon = intent.getDoubleExtra("station_id",-1.0)
        val capacity = intent.getIntExtra("station_id",-1)
        val stationCode = intent.getStringExtra("stationCode")

        val nameTextView = findViewById<TextView>(R.id.name_textView)
        val is_installedTextView = findViewById<TextView>(R.id.is_installed_textView)
        val is_rentingTextView = findViewById<TextView>(R.id.is_renting_textView)
        val is_returningTextView = findViewById<TextView>(R.id.is_returning_textView)
        val numBikesAvailableTextView = findViewById<TextView>(R.id.numBikesAvailable_textView)
        val numDocksAvailableTextView = findViewById<TextView>(R.id.numDocksAvailable_textView)

        nameTextView.text = name
        is_installedTextView.text = if(is_installed == 1) "Déployée" else if(is_installed == 0)"En cours de déploiement" else "Information non disponible"
        is_rentingTextView.text = if(is_renting == 1) "Location possible" else if(is_renting == 0) "Location impossible" else "Information non disponible"
        is_returningTextView.text = if(is_returning == 1) "Retour possible" else if(is_returning == 0)"Retour impossible" else "Information non disponible"
        numBikesAvailableTextView.text = numBikes.toString()
        numDocksAvailableTextView.text = numDocks.toString()

        findViewById<Button>(R.id.add_favori).setOnClickListener{
            db.stationDao().insertOne(Station(station_id,name.toString(),lat,lon,capacity,stationCode.toString(),is_installed,is_renting,is_returning, numBikes, numDocks))
            Toast.makeText(this,"La station a été ajoutée aux favoris",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_details, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.back_maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
            R.id.favoris_list_actions -> {
                startActivity(Intent(this, FavorisActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

