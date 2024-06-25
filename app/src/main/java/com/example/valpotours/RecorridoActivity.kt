package com.example.valpotours

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.valpotours.MainActivity.Companion.listaFav
import com.example.valpotours.MainActivity.Companion.listaRecorrido
import com.example.valpotours.adapter.CategoriasAdapter
import com.example.valpotours.adapter.LugarTuristicoAdapter
import com.example.valpotours.adapter.RecorridoAdapter
import com.example.valpotours.databinding.ActivityRecorridoBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class RecorridoActivity : AppCompatActivity() {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var binding: ActivityRecorridoBinding
    private lateinit var recorridoArrayList: ArrayList<LugaresTuristico>
    private lateinit var db: FirebaseFirestore
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRecorridoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // INIT RECORRIDO
        binding.rvRecorrido.layoutManager = LinearLayoutManager(this)
        binding.rvRecorrido.setHasFixedSize(true)
        recorridoArrayList = arrayListOf()
        binding.rvRecorrido.adapter = RecorridoAdapter(recorridoArrayList)
        EventChangeListenerRoutes()
        binding.btnStartTour.setOnClickListener {
            initTour()
        }
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun EventChangeListenerRoutes() {
        db = FirebaseFirestore.getInstance()
        db.collection("places").whereNotEqualTo("descripcion", null)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {
                        Log.i("Firestore Error", error.message.toString())
                        return
                    }
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.document.data.get("id") in listaRecorrido) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                val lugar = dc.document.toObject(LugaresTuristico::class.java)
                                recorridoArrayList.add(lugar)
                            }
                        }
                    }
                    (binding.rvRecorrido.adapter as RecorridoAdapter).updateList(recorridoArrayList)
                }
            })
    }

    private fun initTour() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLocation = "${it.latitude},${it.longitude}"
                val locations = listaRecorrido
                openGoogleMapsWithWaypoints(currentLocation, locations)
            }
        }
    }

    private fun openGoogleMapsWithWaypoints(currentLocation: String, locations: List<String>) {
        val baseUrl = "https://www.google.com/maps/dir/?api=1"
        val origin = currentLocation // Usar la ubicación actual como punto de partida
        val destination = encodePlusCode(locations.last())// El último lugar como destino
        val waypoints = locations.subList(0, locations.size - 1)
            .joinToString("|") { encodePlusCode(it) } // Los lugares intermedios

        val url = "$baseUrl&origin=$origin&destination=$destination&waypoints=$waypoints"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.setPackage("com.google.android.apps.maps") // Asegurarse de abrir con Google Maps
        startActivity(intent)
    }

    private fun encodePlusCode(plusCode: String): String {
        return URLEncoder.encode(plusCode, StandardCharsets.UTF_8.toString())
    }


}