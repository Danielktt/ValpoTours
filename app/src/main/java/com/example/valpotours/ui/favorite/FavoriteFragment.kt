package com.example.valpotours.ui.favorite

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.valpotours.Categorias
import com.example.valpotours.LoginActivity.Companion.userMail
import com.example.valpotours.LugaresTuristico
import com.example.valpotours.MainActivity.Companion.listaFav
import com.example.valpotours.adapter.CategoriasAdapter
import com.example.valpotours.adapter.LugarTuristicoAdapter
import com.example.valpotours.databinding.FragmentFavoriteBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class FavoriteFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    lateinit var db : FirebaseFirestore
    lateinit var binding: FragmentFavoriteBinding
    lateinit var categoriasArrayList: ArrayList<Categorias>
    lateinit var lugaresArrayList: ArrayList<LugaresTuristico>
    lateinit var originalLugaresArrayList: ArrayList<LugaresTuristico> // Mantén una copia de la lista original



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)

        // INIT PERFIL
        initPerfil()

        // INIT CATEGORIAS
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.setHasFixedSize(true)
        categoriasArrayList = arrayListOf()
        binding.rvCategories.adapter = CategoriasAdapter(categoriasArrayList) { selectedCategory ->
            filterPlacesByCategory(selectedCategory)
        }
        EventChangeListenerCategories()

        // INIT LUGARES
        binding.rvLugares.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvLugares.setHasFixedSize(true)
        lugaresArrayList = arrayListOf()
        originalLugaresArrayList = arrayListOf() // Inicializa la lista original
        binding.rvLugares.adapter = LugarTuristicoAdapter(lugaresArrayList)
        EvenChangeListenerPlaces()

        return binding.root
    }

    private fun filterPlacesByCategory(category: Categorias) {
        val filteredList = if (category != null) {
            lugaresArrayList.filter { lugar ->
                category.categoria?.let { lugar.categoria?.contains(it, ignoreCase = true) } == true
            }
        } else {
            originalLugaresArrayList // Restaura la lista original si la categoría está vacía
        }
        (binding.rvLugares.adapter as LugarTuristicoAdapter).updateList(ArrayList(filteredList))
        Log.d("FavoriteFragment", "Filtered list size by category: ${filteredList.size}")
    }

    private fun EventChangeListenerCategories() {
        db = FirebaseFirestore.getInstance()
        db.collection("categorias")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.i("Firestore Error", error.message.toString())
                        return
                    }
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            categoriasArrayList.add(dc.document.toObject(Categorias::class.java))
                        }
                    }
                    binding.rvCategories.adapter?.notifyDataSetChanged()
                }
            })
    }

    private fun EvenChangeListenerPlaces() {
        db = FirebaseFirestore.getInstance()
        db.collection("places").whereNotEqualTo("descripcion", null)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {
                        Log.i("Firestore Error", error.message.toString())
                        return
                    }
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.document.id in listaFav) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                val lugar = dc.document.toObject(LugaresTuristico::class.java)
                                lugaresArrayList.add(lugar)
                                originalLugaresArrayList.add(lugar) // También agrega a la lista original
                            }
                        }
                    }
                    (binding.rvLugares.adapter as LugarTuristicoAdapter).updateList(lugaresArrayList)
                }
            })
    }

    private fun initPerfil() {
        db = FirebaseFirestore.getInstance()
        db.collection("usuario").whereEqualTo("email", userMail)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    binding.tvUserName.text = document.data["nombre"].toString()
                    binding.tvUseEmail.text = document.data["email"].toString()
                }
            }
            .addOnFailureListener { exception ->
                Log.i("Error getting documents: ", exception.toString())
            }
    }
}
