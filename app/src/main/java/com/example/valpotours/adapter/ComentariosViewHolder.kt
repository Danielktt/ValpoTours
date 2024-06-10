package com.example.valpotours.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.valpotours.Comentario
import com.example.valpotours.R
import com.example.valpotours.databinding.ItemComentarioBinding

class ComentariosViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val comentarioTextView: TextView = view.findViewById(R.id.etComentario)
    private val nombreTextView: TextView = view.findViewById(R.id.tvNombreUsuario)
    private val timestampTextView: TextView = view.findViewById(R.id.tvTimestamp)

    fun render(comentarioModel: Comentario?) {
        comentarioModel?.let { comentario ->
            comentarioTextView.text = comentario.comentario ?: ""
            nombreTextView.text = comentario.nombreUsuario ?: ""
            timestampTextView.text = comentario.timestamp?.toString() ?: ""
        }
    }
}
