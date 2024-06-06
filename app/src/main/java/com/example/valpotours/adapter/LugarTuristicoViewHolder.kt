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

    companion object{
        const val ID_KEY = "ID"
    }

    val  binding = ItemLugarturisticoBinding.bind(view)

    fun render(lugarModel : LugaresTuristico){
        binding.tvLugarName.text = lugarModel.nombre
        binding.tvValoration.text = lugarModel.id
        Glide.with(binding.ivLugar.context).load(lugarModel.urlimg).into(binding.ivLugar)
        binding.ivLugar.setOnClickListener{
            val context = it.context
            val intent = Intent(context,DetalleLugar::class.java)
            intent.putExtra(ID_KEY, lugarModel.id)
            context.startActivity(intent)
        }
    }
}