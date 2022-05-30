package fr.epf.projet

import android.app.Application
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import fr.epf.projet.database.AppDatabase
import fr.epf.projet.model.Station
import kotlin.coroutines.coroutineContext

class StationAdapter(val stations: List<Station>) : RecyclerView.Adapter<StationAdapter.PlanteViewHolder>() {

    class PlanteViewHolder(val view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val stationView = inflater.inflate(R.layout.adapter_stations,parent,false)
        return PlanteViewHolder(stationView)
    }

    override fun onBindViewHolder(holder: PlanteViewHolder, position: Int) {
        val plante = stations[position]

        holder.view.setOnClickListener{
            val db = Room.databaseBuilder(
                it.context,
                AppDatabase::class.java, "database-name"
            ).allowMainThreadQueries().build()

            val builder = AlertDialog.Builder(it.context).setTitle("Suppression")
                .setMessage(R.string.confirm_delete_message).setPositiveButton(R.string.yes) { _, _ ->
                    db.stationDao().delete(db.stationDao().getStation(plante.getNom()))
                    it.context.startActivity(Intent(it.context, FavorisActivity::class.java))
                    Toast.makeText(it.context, "Station supprimée des favoris", Toast.LENGTH_LONG).show()
                }.setNegativeButton(R.string.no) { _, _ ->
                    Log.d(ContentValues.TAG, "dialog fermé")
                }
            builder.show()

        }

        val nametextview = holder.view.findViewById<TextView>(R.id.name_textView)
        nametextview.setTextColor(Color.parseColor("#FFFFFFFF"))
        nametextview.text = plante.getNom()

        val is_installedtextview = holder.view.findViewById<TextView>(R.id.is_installed_textView)
        is_installedtextview.setTextColor(Color.parseColor("#FFFFFFFF"))
        is_installedtextview.text = if(plante.getIs_installed().equals("1")) "Déployée" else if(plante.getIs_installed().equals("0"))"En cours de déploiement" else "Information non disponible"

        val is_rentingtextview = holder.view.findViewById<TextView>(R.id.is_renting_textView)
        is_rentingtextview.setTextColor(Color.parseColor("#FFFFFFFF"))
        is_rentingtextview.text = if(plante.getIs_renting().equals("1")) "Location possible" else if(plante.getIs_renting().equals("0")) "Location impossible" else "Information non disponible"

        val is_returningtextview = holder.view.findViewById<TextView>(R.id.is_returning_textView)
        is_returningtextview.setTextColor(Color.parseColor("#FFFFFFFF"))
        is_returningtextview.text = if(plante.getIs_returning().equals("1")) "Retour possible" else if(plante.getIs_returning().equals("0"))"Retour impossible" else "Information non disponible"

        val numBikestextview = holder.view.findViewById<TextView>(R.id.numBikesAvailable_textView)
        numBikestextview.setTextColor(Color.parseColor("#FFFFFFFF"))
        numBikestextview.text = plante.getNumBikes()

        val numDockstextview = holder.view.findViewById<TextView>(R.id.numDocksAvailable_textView)
        numDockstextview.setTextColor(Color.parseColor("#FFFFFFFF"))
        numDockstextview.text = plante.getNumDocks()

    }

    override fun getItemCount() = stations.size

}

fun Station.getNom() : String {
    return "${name}"
}

fun Station.getIs_installed() : String {
    return "${is_installed}"
}

fun Station.getIs_renting() : String {
    return "${is_renting}"
}

fun Station.getIs_returning() : String {
    return "${is_returning}"
}

fun Station.getNumBikes() : String {
    return "${numBikesAvailable}"
}

fun Station.getNumDocks() : String {
    return "${numDocksAvailable}"
}


