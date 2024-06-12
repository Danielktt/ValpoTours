package com.example.valpotours.adapter

import android.content.Intent
import android.media.Rating
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.example.valpotours.DetalleLugar
import com.example.valpotours.LugaresTuristico
import com.example.valpotours.R
import com.example.valpotours.databinding.ItemLugarturisticoBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LugarTuristicoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    companion object {
        const val ID_KEY = "ID"
    }

    private lateinit var db: FirebaseFirestore
    val binding = ItemLugarturisticoBinding.bind(view)


    fun render(lugarModel: LugaresTuristico) {
        CoroutineScope(Dispatchers.Main).launch {
            Log.i("PedroEsparrago", "1")
            val idLugar = obtenerIdLugar(lugarModel.id.toString())
            Log.i("PedroEsparrago", "2")
            val averageRating = actualizarPromedioRating(idLugar)
            binding.tvValoration.text = averageRating.toString()
        }
        Log.i("PedroEsparrago", "10")
        binding.tvLugarName.text = lugarModel.nombre

        Glide.with(binding.ivLugar.context).load(lugarModel.urlimg).into(binding.ivLugar)
        binding.ivLugar.setOnClickListener {
            val context = it.context
            val intent = Intent(context, DetalleLugar::class.java)
            intent.putExtra(ID_KEY, lugarModel.id)
            context.startActivity(intent)
        }
    }

    private suspend fun obtenerIdLugar(id: String): String {
        db = FirebaseFirestore.getInstance()
        var idLugar: String = "0"

        try {
            val documents = db.collection("places").whereEqualTo("id", "${id}").get()
                .await()
            for (dc in documents) {
                idLugar = dc.id
            }
        } catch (e: Exception) {
            Log.i("PedroEsparrago", "${e}")
        }
        return idLugar
    }

    private suspend fun actualizarPromedioRating(id: String): Double {
        Log.i("PedroEsparrago", "3")
        var rating = 0.3
        Log.i("PedroEsparrago", "4")
        try {
            val documents = db.collection("valoraciones")
                .whereEqualTo("idLugar", id)
                .get()
                .await()
            Log.i("PedroEsparrago", "5")
            Log.i("PedroEsparrago", "")
            val totalRating = documents.sumOf { it.getDouble("rating") ?: 0.0 }
            Log.i("PedroEsparrago", "6")
            Log.i("PedroEsparrago", "totalRating ${totalRating}")
            rating = totalRating / documents.size()
            Log.i("PedroEsparrago", "7")
            Log.i("PedroEsparrago", "rating ${rating}")

        } catch (e: Exception) {
            Log.w("PedroEsparrago", "Error al calcular la valoración promedio", e)
        }
        return rating
    }
}

//XFQ3+W2 Viña del Mar