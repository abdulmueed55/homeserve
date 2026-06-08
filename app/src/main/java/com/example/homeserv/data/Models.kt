package com.example.homeserv.data

data class User(val id: Int, val name: String, val phone: String, val role: String)

data class Provider(
    val id: Int,
    val name: String,
    val phone: String,
    val serviceType: String,
    val userId: Int?
) { override fun toString(): String = "$name - $serviceType" }

data class Offer(
    val id: Int,
    val providerId: Int,
    val providerName: String,
    val serviceType: String,
    val title: String,
    val description: String,
    val price: Double,
    val duration: String
)

data class BookingItem(
    val id: Int,
    val offerId: Int,
    val serviceTitle: String,
    val customerName: String,
    val providerName: String,
    val price: Double,
    val dateTime: String,
    val status: String,
    val notes: String
)

data class DashboardCounts(val totalOffers: Int, val totalProviders: Int, val totalBookings: Int)

object Roles {
    const val CUSTOMER = "Customer"
    const val PROVIDER = "Service Provider"
    const val ADMIN = "Admin"
}

object BookingStatus {
    const val ACTIVE = "Active"
    const val COMPLETED = "Completed"
}
