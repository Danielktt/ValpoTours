package com.example.valpotours

import android.Manifest
import android.net.Uri
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.valpotours.LoginActivity.Companion.userMail
import com.example.valpotours.MainActivity.Companion.idUser
import com.example.valpotours.MainActivity.Companion.listaFav
import com.example.valpotours.MainActivity.Companion.listaRecorrido
import com.example.valpotours.adapter.ComentarioAdapter
import com.example.valpotours.adapter.LugarTuristicoViewHolder.Companion.ID_KEY
import com.example.valpotours.databinding.ActivityDetalleLugarBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


class DetalleLugar : AppCompatActivity() {
    private var urlMapa: String = ""
    private lateinit var binding: ActivityDetalleLugarBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var idLugar: String
    private lateinit var comentarioList: ArrayList<Comentario>
    private lateinit var comentarioAdapter: ComentarioAdapter
    private var userHasRated = false



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
        binding.btnBack.setOnClickListener { val intent= Intent(this,MainActivity::class.java)
        startActivity(intent)}
        binding.btnFavorito.setOnClickListener { editarListaFavoritos(idLugar) }
        binding.btnAddToTour.setOnClickListener { añadirARecorrido(id_place) }
        binding.btnPublicarComentario.setOnClickListener { publicarComentario() }
        binding.btnValorar.setOnClickListener { valorarLugar() }
        binding.btnEliminarValoracion.setOnClickListener { eliminarValoracion() } // Listener agregado aquí
        binding.btnComoLLegar.setOnClickListener {
            if (urlMapa.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlMapa))
                startActivity(intent)
            } else {
                Toast.makeText(this, "URL del mapa no disponible", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun añadirARecorrido(idLugar: String) {
        db = FirebaseFirestore.getInstance()
        val lugarRef = db.collection("usuario").document(idUser)
        if (idLugar in listaRecorrido) {
            Toast.makeText(this,R.string.place_in_list, Toast.LENGTH_SHORT).show()
        } else {
            lugarRef.update("recorrido", FieldValue.arrayUnion(idLugar)).addOnSuccessListener {
                listaRecorrido.add(idLugar)
            }.addOnFailureListener { e ->
                Log.i("PedroEsparrago", "Error al agregar el valor al array", e)
            }
        }
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
                        urlMapa = document.data["urlmaps"].toString()
                        idLugar = document.id
                        if (document.id in listaFav) {
                            binding.btnFavorito.setImageResource(R.drawable.ic_favorite_true)
                        }
                    }
                }

                actualizarPromedioRating() // Movimos la llamada aquí
                actualizarContadorComentarios()
                verificarValoracionUsuario()

                binding.recyclerViewComentarios.layoutManager = LinearLayoutManager(this)
                binding.recyclerViewComentarios.setHasFixedSize(true)
                comentarioList = arrayListOf()
                binding.recyclerViewComentarios.adapter = ComentarioAdapter(comentarioList)
                listenForCommentChanges()
            }
            .addOnFailureListener { e ->
                Log.w("PedroEsparrago", "Error al cargar los detalles del lugar", e)
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
                            listenForCommentChanges()
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
        db.collection("comentarios")
            .whereEqualTo("idLugar", idLugar)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.i("PedroEsparrago", "Error: ${error.message}")
                    return@addSnapshotListener
                }

                comentarioList.clear()
                var userComment: Comentario? = null

                for (dc in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val comentario = dc.document.toObject(Comentario::class.java)
                        if (dc.document.data["idLugar"] == idLugar) {
                            if (comentario.nombreUsuario == userMail) {
                                userComment = comentario
                            } else {
                                comentarioList.add(comentario)
                            }
                        }
                    }
                }

                userComment?.let {
                    comentarioList.add(
                        0,
                        it
                    ) // Agregar el comentario del usuario al inicio de la lista
                }

                binding.recyclerViewComentarios.adapter?.notifyDataSetChanged()
            }
    }


    private fun verificarValoracionUsuario() {
        // Verificar si el usuario ya ha valorado este lugar
        db.collection("valoraciones")
            .whereEqualTo("idLugar", idLugar)
            .whereEqualTo("nombreUsuario", userMail)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    userHasRated = false
                    actualizarPromedioRating()
                    binding.rbValoracion.visibility = View.VISIBLE
                    binding.btnValorar.visibility = View.VISIBLE
                    binding.btnEliminarValoracion.visibility = View.GONE
                } else {
                    userHasRated = true
                    val rating = documents.first().get("rating") as? Double ?: 0.0
                    binding.rbValoracion.rating = rating.toFloat()
                    binding.rbValoracion.visibility = View.GONE
                    binding.btnValorar.visibility = View.GONE
                    binding.btnEliminarValoracion.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { e ->
                Log.w("PedroEsparrago", "Error al verificar la valoración del usuario", e)
            }
    }


    private fun valorarLugar() {
        if (!userHasRated) {
            val rating = binding.rbValoracion.rating
            val ratingData = hashMapOf(
                "rating" to rating,
                "nombreUsuario" to userMail,
                "idLugar" to idLugar,
                "timestamp" to FieldValue.serverTimestamp()
            )
            db.collection("valoraciones").add(ratingData)
                .addOnSuccessListener {
                    Log.i("PedroEsparrago", "Valoración guardada exitosamente")
                    userHasRated = true
                    binding.btnValorar.visibility = View.GONE
                    binding.btnEliminarValoracion.visibility = View.VISIBLE
                    binding.rbValoracion.visibility = View.GONE
                    actualizarPromedioRating()
                }
                .addOnFailureListener { e ->
                    Log.w("PedroEsparrago", "Error al guardar la valoración", e)
                }
        } else {
            binding.rbValoracion.visibility = View.GONE
            binding.tvValoracionPromedio.visibility = View.VISIBLE
        }
        actualizarPromedioRating()
    }

    private fun eliminarValoracion() {
        db.collection("valoraciones")
            .whereEqualTo("idLugar", idLugar)
            .whereEqualTo("nombreUsuario", userMail)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            Log.i("PedroEsparrago", "Valoración eliminada exitosamente")
                            userHasRated = false
                            binding.btnValorar.visibility = View.VISIBLE
                            binding.btnEliminarValoracion.visibility = View.GONE
                            binding.rbValoracion.visibility = View.VISIBLE
                            actualizarPromedioRating()
                        }
                        .addOnFailureListener { e ->
                            Log.w("PedroEsparrago", "Error al eliminar la valoración", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("PedroEsparrago", "Error al obtener las valoraciones para eliminar", e)
            }
    }

    private fun actualizarPromedioRating() {
        if (userHasRated) {
            binding.btnValorar.text = "Valorado"
        }

        db.collection("valoraciones")
            .whereEqualTo("idLugar", idLugar)
            .get()
            .addOnSuccessListener { documents ->
                val totalRating = documents.sumOf { it.getDouble("rating") ?: 0.0 }
                val averageRating = totalRating / documents.size()
                binding.tvValoracionPromedio.text = "${String.format("%.1f", averageRating)}"
            }
            .addOnFailureListener { e ->
                Log.w("PedroEsparrago", "Error al calcular la valoración promedio", e)
            }


    }

}
