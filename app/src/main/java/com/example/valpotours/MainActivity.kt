package com.example.valpotours

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.valpotours.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    companion object{
        var listaFav:ArrayList<String> = arrayListOf()
        lateinit var idUser: String
        const val REQUEST_CODE_LOCATION = 0

    }


    private val LocationService : LocationService = LocationService()
    private lateinit var navController: NavController
    private lateinit var db : FirebaseFirestore
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        enableMyLocation()
        llenarListaFav()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        val  bottonnav = findViewById<BottomNavigationView>(R.id.nav_view)
        bottonnav.setupWithNavController(navController)

    }


    // PERMISOS DE LOCACION
    private fun isPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    private fun enableMyLocation() {
        if (isPermissionsGranted()) {
            return
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){

            }else{
                Toast.makeText(this, "Para activar la localización ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }
    override fun onResumeFragments() {
        super.onResumeFragments()
        if(!isPermissionsGranted()){
            Toast.makeText(this, "Para activar la localización ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }
    }
    //OTRAS FUNCIONES
    private fun llenarListaFav() {
        db = FirebaseFirestore.getInstance()
        Log.i("PedroEsparrago","Hola")
        db.collection("usuario").whereEqualTo("email", LoginActivity.userMail)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.data.contains("favoritos")) {
                        listaFav = document.data.get("favoritos") as ArrayList<String>
                    } else {
                        listaFav = ArrayList()
                    }
                    idUser = document.id
                }
            }
            .addOnFailureListener { exception ->
                Log.i("Error getting documents: ", exception.toString())
            }
    }

}