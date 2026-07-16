package com.example.goberpro.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppProduct(
    val id: Long,
    val name: String,
    val price: Long,
    @SerialName("image_url") val imageUrl: String? = null
)

@Serializable
data class AppCombo(
    val id: Long,
    val name: String,
    @SerialName("image_url") val imageUrl: String? = null
)

@Serializable
data class AppBarber(
    val id: Long,
    val name: String,
    @SerialName("image_url") val imageUrl: String? = null
)
@Serializable
data class InsertService(
    val name: String,
    val price: Long,
    @SerialName("image_url") val imageUrl: String
)

@Serializable
data class InsertProduct(
    val name: String,
    val price: Long,
    @SerialName("image_url") val imageUrl: String
)