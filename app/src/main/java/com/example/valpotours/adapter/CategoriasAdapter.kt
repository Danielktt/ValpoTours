package com.example.valpotours.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.valpotours.Categorias
import com.example.valpotours.R

class CategoriasAdapter(val categoriasList:ArrayList<Categorias>) : RecyclerView.Adapter<CategoriasViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CategoriasViewHolder(layoutInflater.inflate(R.layout.item_categoria, parent, false))
    }

    override fun onBindViewHolder(holder: CategoriasViewHolder, position: Int) {
        holder.render(categoriasList[position])
    }

    override fun getItemCount(): Int = categoriasList.size
}