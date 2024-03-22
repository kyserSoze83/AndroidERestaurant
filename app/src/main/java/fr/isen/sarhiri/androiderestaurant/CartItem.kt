package fr.isen.sarhiri.androiderestaurant

data class CartItem (
    val dishId: Int,
    val dishName: String,
    val quantity: Int,
    val totalPrice: Float
)