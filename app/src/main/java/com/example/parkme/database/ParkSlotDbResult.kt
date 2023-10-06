package com.example.parkme.database

import com.example.parkme.models.ParkSlot

data class ParkSlotDbResult(
    val count: String,
    val next: String,
    val previous: String,
    val results: List<ParkSlot>
)