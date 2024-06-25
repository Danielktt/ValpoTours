package com.example.valpotours.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.valpotours.LugaresTuristico
import com.example.valpotours.R

class RecorridoAdapter(private var recorridoList:ArrayList<LugaresTuristico>) : RecyclerView.Adapter<RecorridoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecorridoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return RecorridoViewHolder(layoutInflater.inflate(R.layout.item_recorrido,parent,false))
    }

    override fun getItemCount(): Int = recorridoList.size

    override fun onBindViewHolder(holder: RecorridoViewHolder, position: Int) {
        holder.render(recorridoList[position])
    }

    fun updateList(newList: ArrayList<LugaresTuristico>) {
        this.recorridoList = newList
        notifyDataSetChanged()
    }
}