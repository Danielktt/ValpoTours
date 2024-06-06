package com.example.valpotours.adapter

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.example.valpotours.DetalleLugar
import com.example.valpotours.LugaresTuristico
import com.example.valpotours.R
import com.example.valpotours.databinding.ItemLugarturisticoBinding

class LugarTuristicoViewHolder(view: View): RecyclerView.ViewHolder(view){

    val  binding = ItemLugarturisticoBinding.bind(view)
    val lugarTuristico = view.findViewById<TextView>(R.id.tvLugarName)
    val valoracion = view.findViewById<TextView>(R.id.tvValoration)
    val photo = view.findViewById<ImageView>(R.id.ivLugar)

    fun render(lugarModel : LugaresTuristico){
        lugarTuristico.text = lugarModel.nombre
        valoracion.text = lugarModel.id
        Glide.with(binding.ivLugar.context).load(lugarModel.urlimg).into(binding.ivLugar)
        binding.ivLugar.setOnClickListener{
            val context = it.context
            val intent = Intent(context,DetalleLugar::class.java)
            context.startActivity(intent)
        }
    }
}