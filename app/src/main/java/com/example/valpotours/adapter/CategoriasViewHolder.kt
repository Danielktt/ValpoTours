package com.example.valpotours.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.valpotours.Categorias
import com.example.valpotours.databinding.ItemCategoriaBinding

class CategoriasViewHolder (view: View):RecyclerView.ViewHolder(view){

    val binding = ItemCategoriaBinding.bind(view)

    fun render(CategoriaModel: Categorias){
        binding.tvCategoryName.text = CategoriaModel.nombre
    }

}