package com.example.valpotours

data class LugaresTuristico(
    val id: String? = null,
    val categoria: String?= null,
    val direccion: String?= null,
    val urlmaps:String?=null,
    val latitud: String?= null,
    val localidad: String?= null,
    val longitud: String?= null,
    val nombre: String?= null,
    val provincia: String?= null,
    val urlimg: String = null.toString()
)