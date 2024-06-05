package com.example.valpotours

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

            val bottomNav = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.settingsFragment -> {
                    callSettingsActivity()
                    true
                }
                R.id.favoriteFragment -> {
                    callHomeActivity()
                    true
                }
                else -> false
            }
        }
    }

    private fun callHomeActivity() {
        val intent = Intent(this, MainActivity::class.java)
        // Opcionalmente, puedes agregar flags para evitar que se creen múltiples instancias de la actividad
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Finaliza la actividad actual para evitar regresar a ella
    }

    private fun callSettingsActivity() {
        val intent = Intent(this, DetalleLugar::class.java)
        // Opcionalmente, puedes agregar flags para evitar que se creen múltiples instancias de la actividad
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Finaliza la actividad actual para evitar regresar a ella
    }
}