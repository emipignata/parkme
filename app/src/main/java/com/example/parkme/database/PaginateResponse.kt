package com.example.parkme.database

data class PaginateResponse<T>(
    val count: String,
    val next: String,
    val previous: String,
    val results: List<T>
)