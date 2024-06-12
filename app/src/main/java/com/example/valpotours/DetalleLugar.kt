package com.example.valpotours


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

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
        binding.btnBack.setOnClickListener { onBackPressed() }
        binding.btnFavorito.setOnClickListener { editarListaFavoritos(idLugar) }
        binding.btnPublicarComentario.setOnClickListener { publicarComentario() }
        binding.btnValorar.setOnClickListener { valorarLugar() }
        binding.btnEliminarValoracion.setOnClickListener { eliminarValoracion() } // Listener agregado aquí
        binding.btnComoLLegar.setOnClickListener{
            if(urlMapa.isNotEmpty()){
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlMapa))
                startActivity(intent)

            }else{
                Toast.makeText(this, "URL del mapa no disponible", Toast.LENGTH_SHORT).show()

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
                    }
                }

                actualizarPromedioRating() // Movimos la llamada aquí
                actualizarContadorComentarios()
                verificarValoracionUsuario()

                binding.recyclerViewComentarios.layoutManager = LinearLayoutManager(this)
                binding.recyclerViewComentarios.setHasFixedSize(true)
                comentarioList = arrayListOf()
                comentarioAdapter = ComentarioAdapter(comentarioList)
                binding.recyclerViewComentarios.adapter = comentarioAdapter


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
    private fun navigateToMap(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
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
        val currentUser = FirebaseAuth.getInstance().currentUser?.email
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
                        // Verifica si el comentario es del usuario autenticado
                        if (comentario.nombreUsuario == currentUser) {
                            // Agrega el comentario al principio de la lista
                            comentarioList.add(0, comentario)
                        } else {
                            comentarioList.add(comentario)
                        }
                        Log.i("PedroEsparrago", " $currentUser: ${comentarioList}")
                    }
                }
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
                    userHasRated = true
                    binding.btnValorar.visibility = View.GONE
                    binding.btnEliminarValoracion.visibility = View.VISIBLE
                    binding.rbValoracion.visibility = View.GONE
                    actualizarPromedioRating()
                }
                .addOnFailureListener { e ->
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
                            userHasRated = false
                            binding.btnValorar.visibility = View.VISIBLE
                            binding.btnEliminarValoracion.visibility = View.GONE
                            binding.rbValoracion.visibility = View.VISIBLE
                            actualizarPromedioRating()
                        }
                        .addOnFailureListener { e ->
                        }
                }
            }
            .addOnFailureListener { e ->
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

    private fun navigateToMap() {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }
}
