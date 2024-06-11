package com.example.valpotours

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.valpotours.databinding.ActivityRegistroBinding
import com.example.valpotours.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegistroActivity : AppCompatActivity() {
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etPassword2: EditText
    private lateinit var btnRegister: Button

    private lateinit var mAuth: FirebaseAuth

    private lateinit var binding: ActivityRegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
        initListeners()
    }

    override fun onBackPressed() {
    }

    private fun initListeners() {
        btnRegister.setOnClickListener {
            registerUser()
        }
        binding.tvSignInLink.setOnClickListener {
            goLogin()
        }
    }

    private fun initComponents(){
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etPassword2 = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegistrar)

        mAuth = FirebaseAuth.getInstance()
        }


    private fun registerUser() {
        val fullName = etFullName.text.toString()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val password2 = etPassword2.text.toString()


        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty()) {
            Toast.makeText(this, R.string.please_complete_all_fields, Toast.LENGTH_SHORT).show()
            return
        }

        if (password != password2) {
            Toast.makeText(this, R.string.passwords_do_not_match, Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    user?.let {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName)
                            .build()

                        it.updateProfile(profileUpdates)
                            .addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    val database = Firebase.firestore
                                    database.collection("usuario").document(email).set(hashMapOf(
                                        "nombre" to fullName,
                                        "email" to email,
                                        "constraseña" to password
                                    )
                                    )
                                    mAuth.currentUser?.sendEmailVerification()
                                    binding.progressBar.isVisible = true
                                    goLogin()
                                } else {
                                    Toast.makeText(
                                        this,
                                        R.string.Something_went_wrong_while_updating_the_profile,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(
                        this,
                        R.string.enter_a_valid_email_and_a_password_of_at_least_6_characters,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun goLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}