package com.example.valpotours.ui.home

import android.location.Location
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
import com.example.valpotours.LocationService
import com.example.valpotours.adapter.CategoriasAdapter
import com.example.valpotours.adapter.LugarTuristicoAdapter
import com.example.valpotours.databinding.FragmentHomeBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var categoriasArrayList: ArrayList<Categorias>
    private lateinit var lugaresArrayList: ArrayList<LugaresTuristico>
    private lateinit var lugarTuristicoAdapter: LugarTuristicoAdapter
    private lateinit var originalLugaresArrayList: ArrayList<LugaresTuristico>
    private var selectedCategory: Categorias? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ViewModelProvider(this).get(HomeViewModel::class.java)

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
            originalLugaresArrayList
        }
        lugarTuristicoAdapter.updateList(ArrayList(filteredList))
        Log.d("HomeFragment", "Filtered list size: ${filteredList.size}")
    }

    private fun initRecycleView() {
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.setHasFixedSize(true)
        categoriasArrayList = arrayListOf()
        binding.rvCategories.adapter = CategoriasAdapter(categoriasArrayList) { selectedCategory ->
            filterPlacesByCategory(selectedCategory)
        }
        listenForCategoryChanges()

        binding.recycleLugares.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recycleLugares.setHasFixedSize(true)
        lugaresArrayList = arrayListOf()
        originalLugaresArrayList = arrayListOf()
        lugarTuristicoAdapter = LugarTuristicoAdapter(lugaresArrayList)
        binding.recycleLugares.adapter = lugarTuristicoAdapter
        listenForPlaceChanges()
    }

    private fun filterPlacesByCategory(category: Categorias) {
        val filteredList: List<LugaresTuristico>
        if (category == selectedCategory) {
            filteredList = originalLugaresArrayList
            selectedCategory = null
        } else {
            filteredList = lugaresArrayList.filter { lugar ->
                category.categoria?.let { lugar.categoria?.contains(it, ignoreCase = true) } == true
            }
            selectedCategory = category
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
        db.collection("places").whereNotEqualTo("descripcion",null).addSnapshotListener { value, error ->
            if (error != null) {
                Log.i("Firestore Error", error.message.toString())
                return@addSnapshotListener
            }
            for (dc: DocumentChange in value?.documentChanges!!) {
                if (dc.type == DocumentChange.Type.ADDED) {
                    val lugar = dc.document.toObject(LugaresTuristico::class.java)
                    lugaresArrayList.add(lugar)
                    originalLugaresArrayList.add(lugar)
                }
            }
            lugarTuristicoAdapter.updateList(lugaresArrayList)
            obtenerYOrdenarLugaresPorCercania() // Llamada aquí para actualizar la lista según la ubicación del usuario
        }
    }

    private fun obtenerYOrdenarLugaresPorCercania() {
        CoroutineScope(Dispatchers.Main).launch {
            val locationService = LocationService()
            val userLocation = withContext(Dispatchers.IO) {
                locationService.getUserLocation(requireContext())
            }

            if (userLocation != null) {
                Log.i("HomeFragment", "User location obtained: ${userLocation.latitude}, ${userLocation.longitude}")
                lugaresArrayList.sortBy { lugar ->
                    val lugarLocation = Location("").apply {
                        latitude = lugar.latitud?.toDoubleOrNull() ?: 0.0
                        longitude = lugar.longitud?.toDoubleOrNull() ?: 0.0
                    }
                    userLocation.distanceTo(lugarLocation)
                }
                lugarTuristicoAdapter.updateList(lugaresArrayList)
                Log.i("HomeFragment", "Lugares ordenados por cercanía: $lugaresArrayList")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
