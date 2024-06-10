package com.example.valpotours

data class Comentario(
    val comentario: String? = null,
    val nombreUsuario: String? = null,
    val idLugar: String? = null,
    val timestamp: com.google.firebase.Timestamp? = null
)
