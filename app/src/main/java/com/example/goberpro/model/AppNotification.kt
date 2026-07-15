package com.example.goberpro.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppNotification(
    val id: Long,
    val title: String,
    val content: String,
    @SerialName("valid_time") val validTime: String? = null
)