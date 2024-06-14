package com.example.valpotours.api

import androidx.fragment.app.Fragment
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

fun Fragment.recreate(){
    parentFragmentManager.beginTransaction()
        .detach(this)
        .attach(this)
        .commit()
}