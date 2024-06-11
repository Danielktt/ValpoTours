package com.example.valpotours.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.valpotours.Comentario
import com.example.valpotours.R
import com.example.valpotours.databinding.ItemCategoriaBinding
import com.example.valpotours.databinding.ItemComentarioBinding
import java.text.SimpleDateFormat
import java.util.*

class ComentariosViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val binding = ItemComentarioBinding.bind(view)
    fun render(comentarioModel: Comentario, rating: Double) {
        binding.tvComentario.text = comentarioModel.comentario
        binding.tvNombreUsuario.text = comentarioModel.nombreUsuario
        binding.txtValoracion.text = if (rating == 0.0) "Sin valorar" else rating.toString()

        val timestampText = comentarioModel.timestamp?.let { timestamp ->
            // Convertir el timestamp a una fecha legible
            val date = Date(timestamp.seconds * 1000)
            val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault())
            dateFormat.format(date)
        } ?: "No disponible"
        binding.tvTimestamp.text = timestampText
    }



}
