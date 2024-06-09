package com.example.valpotours.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.valpotours.Categorias
import com.example.valpotours.R

class CategoriasAdapter(
    private val categoriasList: ArrayList<Categorias>,
    private val onItemClick: (Categorias) -> Unit
) : RecyclerView.Adapter<CategoriasViewHolder>() {

    private var selectedCategory: Categorias? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CategoriasViewHolder(layoutInflater.inflate(R.layout.item_categoria, parent, false))
    }

    override fun onBindViewHolder(holder: CategoriasViewHolder, position: Int) {
        val categoria = categoriasList[position]
        holder.render(categoria)

        // Cambiar el color de fondo si esta categoría está seleccionada
        if (categoria == selectedCategory) {
            holder.itemView.setBackgroundColor(Color.parseColor("#50C2C9")) // Color de fondo seleccionado
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF")) // Color de fondo por defecto
        }

        holder.itemView.setOnClickListener {
            val previouslySelectedCategory = selectedCategory
            selectedCategory = if (selectedCategory == categoria) null else categoria

            notifyItemChanged(categoriasList.indexOf(previouslySelectedCategory))
            notifyItemChanged(position)
            onItemClick(categoria)
        }
    }

    override fun getItemCount(): Int = categoriasList.size
}
