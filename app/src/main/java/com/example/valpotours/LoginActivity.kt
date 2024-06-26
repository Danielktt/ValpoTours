package com.example.valpotours

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.valpotours.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import kotlin.properties.Delegates

class LoginActivity : AppCompatActivity() {

    companion object {
        lateinit var userMail: String
        const val EMAIL_KEY = "EMAIL"
    }

    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    private lateinit var progressBar: ProgressBar

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        mAuth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBar)
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        btnIngresar.setOnClickListener {
            // Llamamos a la función login cuando se hace clic en el botón de iniciar sesión
            progressBar.isVisible = true

            login()
        }
        val textRegistrarse = findViewById<TextView>(R.id.tvRegister2)
        textRegistrarse.setOnClickListener {
            progressBar.isVisible = true
            goRegister()
        }

        val textForgotPass = findViewById<TextView>(R.id.tvForgotPassword)
        textForgotPass.setOnClickListener {
            resetPassword()
        }
    }

    override fun onBackPressed() {

    }

    fun resetPassword() {
        val e = etEmail.text.toString()
        if (!TextUtils.isEmpty(e)) {
            mAuth.sendPasswordResetEmail(e)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                        Toast.makeText(this, R.string.reset_email_sent, Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(this, R.string.invalid_email, Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, R.string.enter_email, Toast.LENGTH_SHORT).show()
        }
    }

    fun login() {
        if(etEmail.text.isEmpty() || etPassword.text.isEmpty()){
            Toast.makeText(this, R.string.enter_email_and_password, Toast.LENGTH_SHORT).show()
            progressBar.isVisible = false
        }else {
            loginUser()
        }
    }
    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if (currentUser != null && currentUser.isEmailVerified) {
            userMail = currentUser.email ?: ""
            goHome(userMail)
        }
    }
    private fun loginUser() {
        email = etEmail.text.toString()
        password = etPassword.text.toString()


        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if(mAuth.currentUser?.isEmailVerified == true) {
                        userMail = email
                        goHome(userMail)
                    }else{
                        progressBar.isVisible = false
                        Toast.makeText(this,R.string.user_not_authenticated,Toast.LENGTH_SHORT).show()
                    }
                } else {
                    progressBar.isVisible = false
                    Toast.makeText(this, R.string.login_failed_Please_verify_your_credentials, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun goHome(userMail:String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(EMAIL_KEY,userMail)
        startActivity(intent)
        finish() // Opcional: Finaliza LoginActivity para que no se pueda volver a ella con el botón atrás
    }

    private fun goRegister() {
        val intent = Intent(this, RegistroActivity::class.java)
        startActivity(intent)
    }
}