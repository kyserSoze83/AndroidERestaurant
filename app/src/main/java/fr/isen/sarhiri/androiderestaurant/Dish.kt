package fr.isen.sarhiri.androiderestaurant

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Category(
    @SerializedName("name_fr") val nameFr:String,
    val items:List<Dish>
)


data class Dish(
    val id: String,
    @SerializedName("name_fr") val nameFr: String,
    //val nameEn: String,
    //val categoryId: String,
    @SerializedName("categ_name_fr") val categoryFr: String,
    //val categoryEn: String,
    val images: List<String>,
    val ingredients: List<Ingredient>,
    val prices: List<Price>
): Serializable



data class Ingredient(
    //val id: String,
    //val shopId: String,
    @SerializedName("name_fr") val nameFr: String,
    //val nameEn: String,
    //val createDate: String,
    //val updateDate: String,
    val pizzaId: String
): Serializable

data class Price(
    val id: String,
    val pizzaId: String,
    //val sizeId: String,
    val price: String,
    //val createDate: String,
    //val updateDate: String,
    val size: String
): Serializable

data class DishResponse(val data: List<Category>)


/*data class Dish(
    @SerializedName("name_fr") val nameFr: String,
    //val nameEn: String,
    val items: List<DishItem>
)*/