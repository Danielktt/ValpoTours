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
import com.example.valpotours.adapter.CategoriasAdapter
import com.example.valpotours.adapter.LugarTuristicoAdapter
import com.example.valpotours.databinding.FragmentFavoriteBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import retrofit2.http.Path

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoriteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var db : FirebaseFirestore
    lateinit var binding: FragmentFavoriteBinding
    lateinit var categoriasArrayList: ArrayList<Categorias>
    lateinit var lugaresArrayList: ArrayList<LugaresTuristico>
    lateinit var listaFav:ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(layoutInflater,container,false)

        //INIT PERFIL
        Log.i("PedroEsparrago","Iniciando Perfil")
        initPerfil()
        //INIT CATEGORIES
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.setHasFixedSize(true)
        categoriasArrayList = arrayListOf()
        binding.rvCategories.adapter = CategoriasAdapter(categoriasArrayList)
        EventChangeListenerCategories()
        //INIT PLACES
        binding.rvLugares.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvLugares.setHasFixedSize(true)
        lugaresArrayList = arrayListOf()
        listaFav = arrayListOf()
        binding.rvLugares.adapter = LugarTuristicoAdapter(lugaresArrayList)
        EvenChangeListenerPlaces()
        return binding.root
    }

    private fun EventChangeListenerCategories() {
        db = FirebaseFirestore.getInstance()
        db.collection("categorias")
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
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
        Log.i("PedroEsparrago","Hola")

        db.collection("usuario").whereEqualTo("email", userMail)
            .get()
            .addOnSuccessListener {
                    documents ->
                for (document in documents) {
                    Log.i("PedroEsparrago","${document.id} == >${document.get("favoritos")}")
                    db.collection("places").addSnapshotListener(object : EventListener<QuerySnapshot>{
                        override fun onEvent(
                            value: QuerySnapshot?,
                            error: FirebaseFirestoreException?
                        ) {
                            if(error != null){
                                Log.i("Firestore Error", error.message.toString())
                                return
                            }
                            for(dc : DocumentChange in value?.documentChanges!!){
                                Log.i("PedroEsparrago","${dc.document.id}")
                                    if (dc.type == DocumentChange.Type.ADDED) {
                                        lugaresArrayList.add(dc.document.toObject(LugaresTuristico::class.java))
                                    }
                            }
                            binding.rvLugares.adapter?.notifyDataSetChanged()
                        }
                    })
                }
            }
            .addOnFailureListener { exception ->
                Log.i("Error getting documents: ", exception.toString())
            }
    }

    private fun initPerfil() {
        db = FirebaseFirestore.getInstance()
        db.collection("usuario").whereEqualTo("email", userMail)
            .get()
            .addOnSuccessListener {
                documents ->
                for (document in documents) {
                    //Log.i("PedroEsparrago","${document.id} == >${document.data}")
                    binding.tvUserName.text = document.data.get("nombre").toString()
                    binding.tvUseEmail.text = document.data.get("email").toString()
                }
            }
            .addOnFailureListener { exception ->
                Log.i("Error getting documents: ", exception.toString())
            }

    }
}






