package com.example.valpotours.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.valpotours.LugarTuristicoProvider
import com.example.valpotours.adapter.LugarTuristicoAdapter
import com.example.valpotours.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        binding.recycleLugares.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recycleLugares.adapter = LugarTuristicoAdapter(LugarTuristicoProvider.lugaresTuristicoList)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}