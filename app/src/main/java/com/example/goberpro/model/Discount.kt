package com.example.goberpro.model

import kotlinx.serialization.Serializable

@Serializable
data class Discount(
    val code: String,
    val discount_percent: Int,
    val is_active: Boolean = true
)
