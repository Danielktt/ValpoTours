package com.example.valpotours

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.valpotours.MainActivity.Companion.idUser
import com.example.valpotours.databinding.ActivityChangeDatoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChangeDatoActivity : AppCompatActivity() {
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etPassword2: EditText
    private lateinit var btnChangeDatos: Button

    private lateinit var mAuth: FirebaseAuth

    private lateinit var binding: ActivityChangeDatoBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_dato)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding = ActivityChangeDatoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnCambiarDatos.setOnClickListener{
            updateUserData()
        }
    }



    private fun updateUserData() {
        val currentUser = idUser
        Log.i("usuarioes ", "Current user: $currentUser")

        if (currentUser != null) {
            val newFullName = binding.etFullName.text.toString().trim()
            val newPassword = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            val userDocRef = db.collection("usuario").document(currentUser)
            val updates = mutableMapOf<String, Any>()

            if (newFullName.isNotEmpty()) {
                updates["nombre"] = newFullName
            }

            if (newPassword.isNotEmpty() && newPassword == confirmPassword) {
                updates["contraseña"] = newPassword  // Aquí debes ajustar el campo correspondiente en Firestore
            } else if (newPassword.isNotEmpty() && newPassword != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return
            }

            if (updates.isNotEmpty()) {
                userDocRef.update(updates)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully updated!")
                        Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating document", e)
                        Toast.makeText(this, "Error al actualizar datos", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "No se han ingresado datos para actualizar", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.w(TAG, "No user logged in")
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }



}
