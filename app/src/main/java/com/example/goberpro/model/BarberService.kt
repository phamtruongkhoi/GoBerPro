package com.example.goberpro.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BarberService(
    val id: Long,
    val name: String,
    val price: Long,
    val description: String? = null,
    @SerialName("image_url") val imageUrl: String? = null // Bắt buộc phải có dòng này để load ảnh
)