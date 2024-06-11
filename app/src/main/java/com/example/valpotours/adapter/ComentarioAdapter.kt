package com.example.valpotours.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.valpotours.Comentario
import com.example.valpotours.R

class ComentarioAdapter(private val comentarioList:ArrayList<Comentario>) :RecyclerView.Adapter<ComentariosViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentariosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ComentariosViewHolder(layoutInflater.inflate(R.layout.item_comentario,parent,false))
    }

    override fun onBindViewHolder(holder: ComentariosViewHolder, position: Int) {
        val item = comentarioList[position]
        holder.render(item)
    }
    override fun getItemCount(): Int = comentarioList.size
}