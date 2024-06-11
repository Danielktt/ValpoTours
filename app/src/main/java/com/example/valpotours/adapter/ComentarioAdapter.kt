package com.example.valpotours.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
        val view = layoutInflater.inflate(R.layout.item_comentario, parent, false)
        return ComentariosViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: ComentariosViewHolder, position: Int) {
        val comentario = comentarioList[position]
        val currentUser = MainActivity.idUser // Supongamos que esta es la variable que contiene el nombre de usuario actual
        val comentarioUser = comentario.nombreUsuario
        val isCurrentUserComment = currentUser == comentarioUser
        if (isCurrentUserComment) {
            holder.binding.btnEliminarComentario.visibility = View.VISIBLE
            holder.binding.btnEliminarComentario.setOnClickListener {
                // Aquí puedes implementar la lógica para eliminar el comentario
            }
        } else {
            holder.binding.btnEliminarComentario.visibility = View.GONE
        }
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
    fun eliminarComentario(position: Int) {
        val comentario = comentarioList[position]
        val db = FirebaseFirestore.getInstance()
        val nombreUsuario = comentario.nombreUsuario
        val idLugar = comentario.idLugar

        if (nombreUsuario != null && idLugar != null) {
            db.collection("comentarios")
                .whereEqualTo("nombreUsuario", nombreUsuario)
                .whereEqualTo("idLugar", idLugar)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                            .addOnSuccessListener {
                                comentarioList.removeAt(position)
                                notifyDataSetChanged()
                            }
                            .addOnFailureListener { e ->
                                Log.e("ComentarioAdapter", "Error al eliminar comentario", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ComentarioAdapter", "Error al obtener comentarios", e)
                }
        } else {
            Log.e("ComentarioAdapter", "Nombre de usuario o ID de lugar nulo")
        }
    }



    interface OnCommentDeleteListener {
        fun onDeleteComment(position: Int)
    }
    private var    onDeleteListener: OnCommentDeleteListener? = null
    fun setOnCommentDeleteListener(listener: OnCommentDeleteListener) {
        onDeleteListener = listener
    }

    override fun getItemCount(): Int = comentarioList.size
}

