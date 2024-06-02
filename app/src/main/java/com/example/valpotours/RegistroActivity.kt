package com.example.valpotours

import android.content.Intent
import android.os.Bundle
import android.view.textclassifier.TextLinks.TextLink
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistroActivity : AppCompatActivity() {

    private lateinit var btnBack:Button
    private lateinit var tvWelcome:TextView
    private lateinit var cvFullName:CardView
    private lateinit var etFullName:EditText
    private lateinit var cvEmail:CardView
    private lateinit var etEmail:EditText
    private lateinit var cvPassword:CardView
    private lateinit var etPassword:EditText
    private lateinit var cvConfirmPassword:CardView
    private lateinit var etConfirmPassword:EditText
    private lateinit var btnRegistrar:Button
    private lateinit var tvSignIn:TextView
    private lateinit var tvSignInLink:TextLink

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initComponents()
        initListeners()
    }

    private fun initListeners() {
        btnBack.setOnClickListener {
            var intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initComponents() {
        btnBack = findViewById(R.id.btnBack)
        tvWelcome = findViewById(R.id.tvWelcome)
        cvFullName = findViewById(R.id.cvFullName)
        etFullName = findViewById(R.id.etFullName)
        cvEmail = findViewById(R.id.cvEmail)
        etEmail = findViewById(R.id.etEmail)
        cvPassword = findViewById(R.id.cvPassword)
        etPassword = findViewById(R.id.etPassword)
        cvConfirmPassword = findViewById(R.id.cvConfirmPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        tvSignIn = findViewById(R.id.tvSignIn)
        tvSignInLink = findViewById<TextLink>(R.id.tvSignInLink)
    }
}