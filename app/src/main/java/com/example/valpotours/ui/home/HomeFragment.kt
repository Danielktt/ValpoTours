package com.example.valpotours.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.valpotours.Categorias
import com.example.valpotours.LugaresTuristico
import com.example.valpotours.adapter.CategoriasAdapter
import com.example.valpotours.adapter.LugarTuristicoAdapter
import com.example.valpotours.databinding.FragmentHomeBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var db : FirebaseFirestore
    lateinit var categoriasArrayList: ArrayList<Categorias>
    lateinit var lugaresArrayList: ArrayList<LugaresTuristico>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initRecycleView()

        return root
    }

    private fun initRecycleView() {
        //INIT CATEGORIA
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.setHasFixedSize(true)
        categoriasArrayList = arrayListOf()
        binding.rvCategories.adapter = CategoriasAdapter(categoriasArrayList)
        EventChangeListener()
        //INIT PLACES
        binding.recycleLugares.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recycleLugares.setHasFixedSize(true)
        lugaresArrayList = arrayListOf()
        binding.recycleLugares.adapter = LugarTuristicoAdapter(lugaresArrayList)
        EvenChangeListenerPlaces()
    }

    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("categorias")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                    if(error != null){
                        Log.i("Firestore Error", error.message.toString())
                        return
                    }

                    for(dc : DocumentChange in value?.documentChanges!!){
                        if (dc.type == DocumentChange.Type.ADDED){
                            categoriasArrayList.add(dc.document.toObject(Categorias::class.java))
                        }
                    }

                    binding.rvCategories.adapter?.notifyDataSetChanged()

                }
            })
    }

    private fun EvenChangeListenerPlaces(){
        db = FirebaseFirestore.getInstance()
        db.collection("places")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                    if(error != null){
                        Log.i("Firestore Error", error.message.toString())
                        return
                    }

                    for(dc : DocumentChange in value?.documentChanges!!){
                        if (dc.type == DocumentChange.Type.ADDED){
                            lugaresArrayList.add(dc.document.toObject(LugaresTuristico::class.java))
                        }
                    }

                    binding.recycleLugares.adapter?.notifyDataSetChanged()

                }
            })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}