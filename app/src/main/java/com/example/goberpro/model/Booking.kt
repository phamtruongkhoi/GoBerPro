package com.example.goberpro.model

import kotlinx.serialization.Serializable

@Serializable
data class Booking(
    val id: Long? = null,
    val customer_name: String,
    val phone: String? = null,
    val booking_date: String,
    val booking_time: String,
    val total_price: Long = 0,
    val status: String = "Pending"
)

@Serializable
data class BookingItem(
    val id: Long? = null,
    val booking_id: Long,
    val service_id: Long,
    val quantity: Int = 1,
    val price: Long
)