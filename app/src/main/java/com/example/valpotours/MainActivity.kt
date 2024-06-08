package com.example.valpotours

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.valpotours.LoginActivity.Companion.EMAIL_KEY
import com.example.valpotours.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    companion object{
        var listaFav:ArrayList<String> = arrayListOf()
        fun logOut(){
            //singOf()
        }
    }

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

        llenarListaFav()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        val  bottonnav = findViewById<BottomNavigationView>(R.id.nav_view)
        bottonnav.setupWithNavController(navController)
    }

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
                }
            }
            .addOnFailureListener { exception ->
                Log.i("Error getting documents: ", exception.toString())
            }
    }

    fun singOf() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}