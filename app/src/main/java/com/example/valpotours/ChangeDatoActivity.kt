package com.example.valpotours

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.valpotours.LoginActivity.Companion.userMail
import com.example.valpotours.MainActivity.Companion.idUser
import com.example.valpotours.databinding.ActivityChangeDatoBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class ChangeDatoActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityChangeDatoBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeDatoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.btnCambiarDatos.setOnClickListener {
            updateUserData()
        }
    }

    private fun updateUserData() {
        val currentUser = mAuth.currentUser
        Log.i("usuarioes ", "Current user: $currentUser")

        if (currentUser != null) {
            val newFullName = binding.etFullName.text.toString().trim()
            val currentPassword = binding.etCurrentPassword.text.toString().trim()
            val newPassword = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (currentPassword.isNotEmpty() && newPassword.isNotEmpty() && newPassword == confirmPassword) {
                val credential = EmailAuthProvider.getCredential(currentUser.email!!, currentPassword)

                currentUser.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Reauthentication successful.")
                            currentUser.updatePassword(newPassword)
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        Log.d(TAG, "User password updated.")
                                        updateFirestoreUserData(newFullName)
                                    } else {
                                        Log.w(TAG, "Error updating password", updateTask.exception)
                                        Toast.makeText(this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Log.w(TAG, "Reauthentication failed", task.exception)
                            Toast.makeText(this, "Error de re-autenticación: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else if (newPassword.isNotEmpty() && newPassword != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {
                updateFirestoreUserData(newFullName)
            }
        } else {
            Log.w(TAG, "No user logged in")
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFirestoreUserData(newFullName: String) {
        val userDocRef = db.collection("usuario").document(idUser)
        val updates = mutableMapOf<String, Any>()

        if (newFullName.isNotEmpty()) {
            updates["nombre"] = newFullName
        }
        Log.i("PedroEsparrago", "${idUser}  ${newFullName}")
        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    if (updates.isNotEmpty()) {
                        userDocRef.update(updates)
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot successfully updated!")
                                Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                                navigateToLogin()
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error updating document", e)
                                Toast.makeText(this, "Error al actualizar datos", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "No se han ingresado datos para actualizar", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (updates.isNotEmpty()) {
                        userDocRef.set(updates, SetOptions.merge())
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot successfully created!")
                                Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                                navigateToLogin()
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error creating document", e)
                                Toast.makeText(this, "Error al crear datos", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "No se han ingresado datos para actualizar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error fetching document", e)
                Toast.makeText(this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
