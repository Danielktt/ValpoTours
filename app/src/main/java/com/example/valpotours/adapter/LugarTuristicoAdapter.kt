package com.example.valpotours.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.valpotours.LugaresTuristico
import com.example.valpotours.R


class LugarTuristicoAdapter(private var lugarturisticoList:ArrayList<LugaresTuristico>) : RecyclerView.Adapter<LugarTuristicoViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LugarTuristicoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LugarTuristicoViewHolder(layoutInflater.inflate(R.layout.item_lugarturistico,parent,false))
    }

    override fun getItemCount(): Int = lugarturisticoList.size

    override fun onBindViewHolder(holder: LugarTuristicoViewHolder, position: Int) {
        holder.render(lugarturisticoList[position])

    }
    fun updateList(newList: ArrayList<LugaresTuristico>) {
        this.lugarturisticoList = newList
        notifyDataSetChanged()
    }

}
