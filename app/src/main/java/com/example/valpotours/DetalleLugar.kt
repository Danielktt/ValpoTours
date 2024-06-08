package com.example.valpotours

import android.R.color
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.valpotours.MainActivity.Companion.listaFav
import com.example.valpotours.adapter.LugarTuristicoViewHolder.Companion.ID_KEY
import com.example.valpotours.databinding.ActivityDetalleLugarBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class DetalleLugar : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleLugarBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetalleLugarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.DetalleLugar)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val id_place = intent.extras?.getString(ID_KEY) ?: "null"
        binding.btnBack.setOnClickListener{goHome()}
        binding.btnFavorito.setOnClickListener { editarListaFavoritos() }
        initDetail(id_place as String)
    }
    private fun goHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun initDetail(id_place: String) {
        db = FirebaseFirestore.getInstance()
        db.collection("places").whereEqualTo("id", id_place)
            .get()
            .addOnSuccessListener {
                    documents ->
                for (document in documents) {
                    Log.i("PedroEsparrago","${document.id} == >${document.data}")
                    Picasso.get().load(document.data.get("urlimg").toString()).into(binding.ivDetalleLugar);
                    //Glide.with(binding.ivDetalleLugar.context).load(document.data.get("urlimg")).into(binding.ivDetalleLugar)
                    binding.tvNombre.text = document.data.get("nombre").toString()
                    binding.tvCiudad.text = document.data.get("localidad").toString()
                    binding.tvDescription.text = document.data.get("descripcion").toString()
                    binding.btnComoLLegar.setOnClickListener {
                    //agregar funcionalidades
                    }
                    if(document.id in listaFav){
                        binding.btnFavorito.setImageResource(R.drawable.ic_favorite_true)
                    }
                }
            }
    }
    private fun editarListaFavoritos() {
        TODO("Not yet implemented")
    }

}