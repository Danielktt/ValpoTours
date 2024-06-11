
package com.example.valpotours

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.valpotours.LoginActivity.Companion.userMail
import com.example.valpotours.MainActivity.Companion.idUser
import com.example.valpotours.MainActivity.Companion.listaFav
import com.example.valpotours.adapter.ComentarioAdapter
import com.example.valpotours.adapter.LugarTuristicoViewHolder.Companion.ID_KEY
import com.example.valpotours.databinding.ActivityDetalleLugarBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class DetalleLugar : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleLugarBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var idLugar: String
    private lateinit var comentarioList: ArrayList<Comentario>
    private lateinit var comentarioAdapter: ComentarioAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetalleLugarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.DetalleLugar)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val id_place = intent.extras?.getString(ID_KEY).orEmpty()
        db = FirebaseFirestore.getInstance()
        initDetail(id_place)
        binding.btnBack.setOnClickListener { onBackPressed() }
        binding.btnFavorito.setOnClickListener { editarListaFavoritos(idLugar) }
        binding.btnPublicarComentario.setOnClickListener { publicarComentario() }
    }

    private fun initDetail(id_place: String) {
        db = FirebaseFirestore.getInstance()
        db.collection("places").whereNotEqualTo("descripcion", null)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.data["id"] == id_place) {
                        Log.i("PedroEsparrago", "${document.id} == >${document.data}")
                        Picasso.get().load(document.data["urlimg"].toString())
                            .into(binding.ivDetalleLugar)
                        binding.tvNombre.text = document.data["nombre"].toString()
                        binding.tvCiudad.text = document.data["localidad"].toString()
                        binding.tvDescription.text = document.data["descripcion"].toString()
                        binding.btnComoLLegar.setOnClickListener {
                            // agregar funcionalidades
                        }
                        idLugar = document.id
                        if (document.id in listaFav) {
                            binding.btnFavorito.setImageResource(R.drawable.ic_favorite_true)
                        }
                    }
                }
                actualizarContadorComentarios()

                binding.recyclerViewComentarios.layoutManager = LinearLayoutManager(this)
                binding.recyclerViewComentarios.setHasFixedSize(true)
                comentarioList = arrayListOf()
                binding.recyclerViewComentarios.adapter = ComentarioAdapter(comentarioList)
                listenForCommentChanges()
            }
    }

    private fun editarListaFavoritos(idLugar: String) {
        db = FirebaseFirestore.getInstance()
        val favoritoRef = db.collection("usuario").document(idUser)
        if (idLugar in listaFav) {
            favoritoRef.update("favoritos", FieldValue.arrayRemove(idLugar))
            listaFav.remove(idLugar)
            binding.btnFavorito.setImageResource(R.drawable.ic_favorite_false)
        } else {
            favoritoRef.update("favoritos", FieldValue.arrayUnion(idLugar)).addOnSuccessListener {
                listaFav.add(idLugar)
                binding.btnFavorito.setImageResource(R.drawable.ic_favorite_true)
            }.addOnFailureListener { e ->
                Log.w("PedroEsparrago", "Error al agregar el valor al array", e)
            }
        }
    }

    private fun publicarComentario() {
        val comentario = binding.etComentario.text.toString().trim()
        if (comentario.isEmpty()) {
            // Mostrar un mensaje de error si el comentario está vacío
            return
        }
        // Verificar si el usuario ya ha comentado en este lugar
        db.collection("comentarios")
            .whereEqualTo("idLugar", idLugar)
            .whereEqualTo("nombreUsuario", userMail)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Si el usuario no ha comentado previamente en este lugar, agregar el comentario
                    val comentarioData = hashMapOf(
                        "comentario" to comentario,
                        "nombreUsuario" to userMail,
                        "idLugar" to idLugar,
                        "timestamp" to FieldValue.serverTimestamp()
                    )
                    db.collection("comentarios").add(comentarioData)
                        .addOnSuccessListener {
                            Log.i("PedroEsparrago", "Comentario publicado exitosamente")
                            binding.etComentario.text.clear()
                            // Actualizar el contador de comentarios
                            actualizarContadorComentarios()
                        }
                        .addOnFailureListener { e ->
                            Log.w("PedroEsparrago", "Error al publicar el comentario", e)
                        }
                } else {
                    // Dentro del método initDetail()
                    if (!documents.isEmpty) {
                        // Si el usuario ya ha comentado en este lugar, deshabilitar el botón de comentario
                        binding.btnPublicarComentario.isEnabled = false
                        binding.btnPublicarComentario.text = "Ya has comentado aquí"
                    } else {
                        // Si el usuario no ha comentado, habilitar el botón de comentario
                        binding.btnPublicarComentario.isEnabled = true
                        binding.btnPublicarComentario.text = "Publicar Comentario"
                    }


                    Log.w("PedroEsparrago", "El usuario ya ha comentado en este lugar.")
                }
            }
    }

    private fun actualizarContadorComentarios() {
        // Consultar la cantidad de comentarios para el lugar actual
        db.collection("comentarios")
            .whereEqualTo("idLugar", idLugar)
            .get()
            .addOnSuccessListener { documents ->
                // Obtener el número de comentarios y actualizar el TextView
                val commentCount = documents.size()
                binding.textViewCommentCount.text = "Comentarios: $commentCount"
            }
            .addOnFailureListener { e ->
                Log.w("PedroEsparrago", "Error al obtener la cantidad de comentarios", e)
            }
    }


    private fun listenForCommentChanges() {
        Log.i("PedroEsparrago", "Paso 1")
            db.collection("comentarios").addSnapshotListener { value, error ->
                if (error != null) {
                    Log.i("PedroEsparrago", "Paso 2")
                    Log.i("Firestore Error", error.message.toString())
                    return@addSnapshotListener
                }
                for (dc: DocumentChange in value?.documentChanges!!) {
                    Log.i("PedroEsparrago", "Paso 3")
                    if (dc.type == DocumentChange.Type.ADDED) {
                        Log.i("PedroEsparrago", "Paso 4")
                        val comentario = dc.document.toObject(Comentario::class.java)
                        if(dc.document.data["idLugar"] == idLugar) {
                            comentarioList.add(comentario)
                            Log.i("PedroEsparrago", "Paso 5")

                            Log.i("PedroEsparrago", "Cantidad de Comentario: ${comentarioList}")
                        }
                    }
                }
               binding.recyclerViewComentarios.adapter?.notifyDataSetChanged()
            }
        }
}