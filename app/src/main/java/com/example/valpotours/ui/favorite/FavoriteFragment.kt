package com.example.valpotours.ui.favorite

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.valpotours.Categorias
import com.example.valpotours.LugarTuristicoProvider
import com.example.valpotours.R
import com.example.valpotours.adapter.CategoriasAdapter
import com.example.valpotours.adapter.LugarTuristicoAdapter
import com.example.valpotours.databinding.ActivityMainBinding
import com.example.valpotours.databinding.FragmentFavoriteBinding
import com.example.valpotours.databinding.FragmentHomeBinding
import com.example.valpotours.ui.home.HomeViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

    lateinit var dbref : DatabaseReference
    lateinit var binding: FragmentFavoriteBinding
    lateinit var categoriasArrayList: ArrayList<Categorias>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    private fun getCategoriesData() {
        dbref = FirebaseDatabase.getInstance().getReference("categorias")
        Log.i("PedroEsparrago","${dbref.database.reference.parent}")
        dbref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for(categoriaSnapshot in snapshot.children){
                        val categoria = categoriaSnapshot.getValue(Categorias::class.java)
                        categoriasArrayList.add(categoria!!)
                    }
                    binding.rvCategories.adapter = CategoriasAdapter(categoriasArrayList)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("PedroEsparrago","Error")
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(layoutInflater,container,false)

        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.setHasFixedSize(true)
        categoriasArrayList = arrayListOf<Categorias>()
        getCategoriesData()
        binding.rvLugares.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvLugares.adapter = LugarTuristicoAdapter(LugarTuristicoProvider.lugaresTuristicoList)
        return binding.root
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavoriteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}