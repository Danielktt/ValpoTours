package com.example.valpotours

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.valpotours.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import kotlin.properties.Delegates

class LoginActivity : AppCompatActivity() {

    companion object {
        lateinit var userMail: String
    }

    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

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
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        btnIngresar.setOnClickListener {
            // Llamamos a la función login cuando se hace clic en el botón de iniciar sesión
            login()
        }
        val textRegistrarse = findViewById<TextView>(R.id.tvRegister2)
        textRegistrarse.setOnClickListener{
            goRegister()
        }

        val textForgotPass = findViewById<TextView>(R.id.tvForgotPassword)
        textForgotPass.setOnClickListener{
            resetPassword()
        }
    }


    fun resetPassword(){
        var e = etEmail.text.toString()
        if (!TextUtils.isEmpty(e)){
            mAuth.sendPasswordResetEmail(e)
                .addOnCompleteListener{task ->
                    if (task.isSuccessful)  Toast.makeText(this, "mail enviado.", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(this, "Correo invalido", Toast.LENGTH_SHORT).show()
                }
        }else{
            Toast.makeText(this, "Ingrese correo", Toast.LENGTH_SHORT).show()
        }
    }
    fun login() {
        loginUser()
    }

    private fun loginUser() {
        email = etEmail.text.toString()
        password = etPassword.text.toString()

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) goHome(email)
                else {
                    Toast.makeText(this, "Error al iniciar sesión. Por favor, verifica tus credenciales.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun goHome(email:String) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    private fun goRegister(){
        val intent = Intent(this, RegistroActivity::class.java)
        startActivity(intent)
    }
}
