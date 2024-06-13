package com.example.valpotours.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.valpotours.ChangeDatoActivity
import com.example.valpotours.LoginActivity
import com.example.valpotours.MainActivity
import com.example.valpotours.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater,container,false)
        // Inflate the layout for this fragment
        binding.btnLogOut.setOnClickListener {
            binding.progressBar.isVisible = true
            logOut()
        }
        binding.btnVersion.setOnClickListener{
            val intent = Intent(context, ChangeDatoActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

  fun logOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context,LoginActivity::class.java)
        startActivity(intent)
    }
}