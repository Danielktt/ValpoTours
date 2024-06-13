package com.example.valpotours

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.valpotours.databinding.ActivityChangeDatoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
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
        initComponents()
        initListeners()
    }

    private fun initListeners() {
        btnChangeDatos.setOnClickListener {
            changeUserData()
        }
    }

    private fun initComponents(){
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmailChange)
        etPassword = findViewById(R.id.etPassword)
        etPassword2 = findViewById(R.id.etConfirmPassword)
        btnChangeDatos = findViewById(R.id.btnCambiarDatos)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        user?.let {
            etFullName.setText(it.displayName)
            etEmail.setText(it.email)
        }
    }

    private fun changeUserData() {
        val fullName = etFullName.text.toString()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val password2 = etPassword2.text.toString()

        val user = mAuth.currentUser

        if (fullName.isEmpty() && email.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, R.string.please_complete_all_fields, Toast.LENGTH_SHORT).show()
            return
        }

        user?.let {
            if (fullName.isNotEmpty()) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .build()

                it.updateProfile(profileUpdates).addOnCompleteListener { profileTask ->
                    if (!profileTask.isSuccessful) {
                        Toast.makeText(this, R.string.Something_went_wrong_while_updating_the_profile, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            if (email.isNotEmpty()) {
                it.updateEmail(email).addOnCompleteListener { emailTask ->
                    if (!emailTask.isSuccessful) {
                        Toast.makeText(this, R.string.Something_went_wrong_while_updating_email, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            if (password.isNotEmpty()) {
                if (password == password2) {
                    it.updatePassword(password).addOnCompleteListener { passwordTask ->
                        if (!passwordTask.isSuccessful) {
                            Toast.makeText(this, R.string.Something_went_wrong_while_updating_password, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, R.string.passwords_do_not_match, Toast.LENGTH_SHORT).show()
                }
            }

            val database = Firebase.firestore
            user.email?.let { it1 ->
                database.collection("usuario").document(it1).set(hashMapOf(
                    "nombre" to (fullName.ifEmpty { user.displayName }),
                    "email" to (email.ifEmpty { user.email }),
                    "contraseña" to (if (password.isNotEmpty() && password == password2) password else "no-change")
                ))
            }
            binding.progressBar.isVisible = true
            Toast.makeText(this, R.string.data_updated_successfully, Toast.LENGTH_SHORT).show()
            goHome()
        }
    }

    private fun goHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
