package com.example.valpotours

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.valpotours.LoginActivity.Companion.userMail
import com.example.valpotours.MainActivity.Companion.listaFav
import com.example.valpotours.adapter.LugarTuristicoViewHolder.Companion.ID_KEY
import com.example.valpotours.databinding.ActivityDetalleLugarBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class DetalleLugar : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleLugarBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var idLugar: String

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
        val id_place = intent.extras?.getString(ID_KEY).orEmpty()
        Log.i("PedroEsparrago","${id_place}")
        initDetail(id_place)
        binding.btnBack.setOnClickListener{goHome()}
        binding.btnFavorito.setOnClickListener { editarListaFavoritos(idLugar) }
    }
    private fun goHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun initDetail(id_place: String) {
        db = FirebaseFirestore.getInstance()
        db.collection("places").whereNotEqualTo("descripcion",null)
            .get()
            .addOnSuccessListener {
                    documents ->
                for (document in documents) {
                    if(document.data.get("id")==id_place) {
                        Log.i("PedroEsparrago", "${document.id} == >${document.data}")
                        Picasso.get().load(document.data.get("urlimg").toString())
                            .into(binding.ivDetalleLugar);
                        //Glide.with(binding.ivDetalleLugar.context).load(document.data.get("urlimg")).into(binding.ivDetalleLugar)
                        binding.tvNombre.text = document.data.get("nombre").toString()
                        binding.tvCiudad.text = document.data.get("localidad").toString()
                        binding.tvDescription.text = document.data.get("descripcion").toString()
                        binding.btnComoLLegar.setOnClickListener {
                            //agregar funcionalidades
                        }
                        idLugar = document.id
                        if (document.id in listaFav) {
                            binding.btnFavorito.setImageResource(R.drawable.ic_favorite_true)
                        }
                    }
                }
            }
    }
    private fun editarListaFavoritos(idLugar: String) {
        val favoritoRef = db.collection("usuarios").document()
        Log.i("PedroEsparrago","${idLugar}")
        if(idLugar in listaFav){
            favoritoRef.update("favoritos",FieldValue.arrayRemove(idLugar))
            listaFav.remove(idLugar)
            binding.btnFavorito.setImageResource(R.drawable.ic_favorite_false)
        }else{
            favoritoRef.update("favoritos",FieldValue.arrayUnion(idLugar))
            listaFav.add(idLugar)
            binding.btnFavorito.setImageResource(R.drawable.ic_favorite_true)
        }
    }
}