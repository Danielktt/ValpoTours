package com.example.valpotours.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.valpotours.Categorias
import com.example.valpotours.LugaresTuristico
import com.example.valpotours.adapter.CategoriasAdapter
import com.example.valpotours.adapter.LugarTuristicoAdapter
import com.example.valpotours.databinding.FragmentHomeBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var categoriasArrayList: ArrayList<Categorias>
    private lateinit var lugaresArrayList: ArrayList<LugaresTuristico>
    private lateinit var lugarTuristicoAdapter: LugarTuristicoAdapter
    private lateinit var originalLugaresArrayList: ArrayList<LugaresTuristico> // Mantén una copia de la lista original
    private var selectedCategory: Categorias? = null // Variable para mantener la categoría seleccionada

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        db = FirebaseFirestore.getInstance()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("HomeFragment", "Query submitted: $query")
                filterPlaces(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("HomeFragment", "Query text changed: $newText")
                filterPlaces(newText)
                return false
            }
        })

        initRecycleView()
        return root
    }

    private fun filterPlaces(query: String?) {
        Log.d("HomeFragment", "Filtering places with query: $query")
        val filteredList = if (!query.isNullOrEmpty()) {
            lugaresArrayList.filter { lugar ->
                val matchesNombre = lugar.nombre?.contains(query, ignoreCase = true) == true
                val matchesCategoria = lugar.categoria?.contains(query, ignoreCase = true) == true
                Log.d("HomeFragment", "Filtering ${lugar.nombre}, nombre matches: $matchesNombre, categoria matches: $matchesCategoria")
                matchesNombre || matchesCategoria
            }
        } else {
            originalLugaresArrayList // Restaura la lista original si la consulta está vacía
        }
        lugarTuristicoAdapter.updateList(ArrayList(filteredList))
        Log.d("HomeFragment", "Filtered list size: ${filteredList.size}")
    }

    private fun initRecycleView() {
        // INIT CATEGORIA
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.setHasFixedSize(true)
        categoriasArrayList = arrayListOf()
        binding.rvCategories.adapter = CategoriasAdapter(categoriasArrayList) { selectedCategory ->
            filterPlacesByCategory(selectedCategory)
        }
        listenForCategoryChanges()

        // INIT PLACES
        binding.recycleLugares.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recycleLugares.setHasFixedSize(true)
        lugaresArrayList = arrayListOf()
        originalLugaresArrayList = arrayListOf() // Inicializa la lista original
        lugarTuristicoAdapter = LugarTuristicoAdapter(lugaresArrayList)
        binding.recycleLugares.adapter = lugarTuristicoAdapter
        listenForPlaceChanges()
    }

    private fun filterPlacesByCategory(category: Categorias) {
        val filteredList: List<LugaresTuristico>
        if (category == selectedCategory) {
            // Si la categoría seleccionada es la misma, restaura la lista original
            filteredList = originalLugaresArrayList
            selectedCategory = null // Restablece la categoría seleccionada
        } else {
            // Si se selecciona una nueva categoría, filtra la lista
            filteredList = lugaresArrayList.filter { lugar ->
                category.categoria?.let { lugar.categoria?.contains(it, ignoreCase = true) } == true
            }
            selectedCategory = category // Actualiza la categoría seleccionada
        }
        lugarTuristicoAdapter.updateList(ArrayList(filteredList))
        Log.d("FavoriteFragment", "Filtered list size by category: ${filteredList.size}")
    }

    private fun listenForCategoryChanges() {
        db.collection("categorias").addSnapshotListener { value, error ->
            if (error != null) {
                Log.i("Firestore Error", error.message.toString())
                return@addSnapshotListener
            }
            for (dc: DocumentChange in value?.documentChanges!!) {
                if (dc.type == DocumentChange.Type.ADDED) {
                    categoriasArrayList.add(dc.document.toObject(Categorias::class.java))
                }
            }
            binding.rvCategories.adapter?.notifyDataSetChanged()
        }
    }

    private fun listenForPlaceChanges() {
        db.collection("places").addSnapshotListener { value, error ->
            if (error != null) {
                Log.i("Firestore Error", error.message.toString())
                return@addSnapshotListener
            }
            for (dc: DocumentChange in value?.documentChanges!!) {
                if (dc.type == DocumentChange.Type.ADDED) {
                    val lugar = dc.document.toObject(LugaresTuristico::class.java)
                    lugaresArrayList.add(lugar)
                    originalLugaresArrayList.add(lugar) // También agrega a la lista original
                }
            }
            lugarTuristicoAdapter.updateList(lugaresArrayList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
