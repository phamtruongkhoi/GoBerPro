package com.example.goberpro.model

import kotlinx.serialization.Serializable

@Serializable
data class BarberService(
    val id: Long,
    val name: String,
    val price: Long,
    val description: String? = null
)