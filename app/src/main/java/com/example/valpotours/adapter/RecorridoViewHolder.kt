package com.example.valpotours.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.valpotours.LugaresTuristico
import com.example.valpotours.MainActivity.Companion.idUser
import com.example.valpotours.MainActivity.Companion.listaRecorrido
import com.example.valpotours.RecorridoActivity
import com.example.valpotours.databinding.ItemRecorridoBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class RecorridoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val binding = ItemRecorridoBinding.bind(view)
    private lateinit var db: FirebaseFirestore

    fun render(lugarModel: LugaresTuristico) {
        binding.tvCity.text = lugarModel.localidad
        binding.tvPLace.text = lugarModel.nombre
        Glide.with(binding.ivPlace.context).load(lugarModel.urlimg).into(binding.ivPlace)
        binding.btnDelete.setOnClickListener {
            deletePlace(lugarModel.id.toString())
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun deletePlace(idLugar: String) {
        db = FirebaseFirestore.getInstance()
        val lugarRef = db.collection("usuario").document(idUser)
            lugarRef.update("recorrido", FieldValue.arrayRemove(idLugar))
            listaRecorrido.remove(idLugar)
    }
}