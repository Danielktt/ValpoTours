package com.example.valpotours.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.valpotours.Comentario
import com.example.valpotours.MainActivity
import com.example.valpotours.R
import com.google.firebase.firestore.FirebaseFirestore

class ComentarioAdapter(private val comentarioList: ArrayList<Comentario>) : RecyclerView.Adapter<ComentariosViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentariosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ComentariosViewHolder(layoutInflater.inflate(R.layout.item_comentario, parent, false))
    }

    override fun onBindViewHolder(holder: ComentariosViewHolder, position: Int) {
        val comentario = comentarioList[position]

        // Obtener la valoración correspondiente a este comentario
        db.collection("valoraciones")
            .whereEqualTo("idLugar", comentario.idLugar)
            .whereEqualTo("nombreUsuario", comentario.nombreUsuario)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val rating = documents.first().get("rating") as? Double ?: 0.0
                    holder.render(comentario, rating)
                } else {
                    // Si no hay valoración, mostrar un valor por defecto
                    holder.render(comentario, 0.0)
                }
            }
            .addOnFailureListener { e ->
                Log.e("ComentarioAdapter", "Error al obtener la valoración", e)
                // Si falla la obtención de la valoración, mostrar un valor por defecto
                holder.render(comentario, 0.0)
            }
    }

    override fun getItemCount(): Int = comentarioList.size
}
