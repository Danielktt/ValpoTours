package com.example.valpotours.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.valpotours.Comentario
import com.example.valpotours.databinding.ItemComentarioBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ComentariosViewHolder(view: View, private val adapter: ComentarioAdapter) : RecyclerView.ViewHolder(view) {

    val binding = ItemComentarioBinding.bind(view)

    fun render(comentarioModel: Comentario, rating: Double) {
        binding.tvComentario.text = comentarioModel.comentario
        binding.tvNombreUsuario.text = comentarioModel.nombreUsuario
        binding.txtValoracion.text = if (rating == 0.0) "Sin valorar" else rating.toString()

        binding.btnEliminarComentario.setOnClickListener {
            adapter.eliminarComentario(layoutPosition)
        }

        val timestampText = comentarioModel.timestamp?.let { timestamp ->
            // Convertir el timestamp a una fecha legible
            val date = Date(timestamp.seconds * 1000)
            val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault())
            dateFormat.format(date)
        } ?: "No disponible"
        binding.tvTimestamp.text = timestampText
    }
}

