package com.example.parkme.models

data class PaginatedResponse<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)

//este es el contrato de la lista ponerse de acuerdo con el team back
//esto trae la lista de cochera