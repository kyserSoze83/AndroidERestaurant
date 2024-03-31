package fr.isen.sarhiri.androiderestaurant

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.isen.sarhiri.androiderestaurant.CartActivity.Companion.cartDirectory
import fr.isen.sarhiri.androiderestaurant.ui.theme.AndroidERestaurantTheme
import fr.isen.sarhiri.androiderestaurant.ui.theme.Orange
import java.io.File
import java.io.IOException




class CartActivity : ComponentActivity() {
    companion object {
        lateinit var cartDirectory: File
            private set
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cartDirectory = File(getExternalFilesDir(null), "cartDirectory")
        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    DisplayCart(this,/*this::loadCartItems,*/ this::removeCartItemById, this::navigateToActivity, { onBackPressedDispatcher.onBackPressed() })
                }
            }
        }
    }
    private fun navigateToActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }

    private fun removeCartItemById(filePath: String, itemId: Int) {
        val cartItems = loadCartItems(filePath).toMutableList()
        val itemToRemove = cartItems.find { it.dishId == itemId }
        if (itemToRemove != null) {
            cartItems.remove(itemToRemove)
            saveCartItems(filePath, cartItems)
            recreate()
        } else {
            println("Aucun plat trouvé avec l'ID spécifié")
        }
    }
    private fun saveCartItems(filePath: String, cartItems: List<CartItem>) {
        try {
            val gson = Gson()
            val json = gson.toJson(cartItems)
            File(filePath).writeText(json)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

@Composable
fun DisplayCart(context: Context, /*loadCartItems:( String)->List<CartItem>,*/ removeCartItemById:(String, Int) -> Unit, navigateToActivity:(Class<*>) -> Unit, onBackPressed:() -> Unit, modifier: Modifier = Modifier) {

    val filePath = File(cartDirectory, "cart.json").absolutePath
    val items = loadCartItems(filePath)
    val iconMinus: ImageVector = Icons.Default.Delete
    var total by remember { mutableFloatStateOf(0f) }
    Column(){
        CustomActionBar(
            onLeftButtonClick = { onBackPressed() },
            onRightButtonClick = { navigateToActivity(CartActivity::class.java) },
            false,
            items.size,
        )
        Spacer(modifier = modifier.height(20.dp))
        items.forEach { item ->
            Column(modifier.padding(start = 10.dp, end = 10.dp)) {
                Text(
                    text = item.dishName,
                    modifier = modifier
                )
                val qty=item.quantity.toString()
                Text(
                    text = "Quantité : $qty",
                    modifier = modifier
                )
                val prix=item.totalPrice.toString();
                total=addition(total, item.totalPrice)
                Text(
                    text = "Sous-total : $prix€",
                    modifier = modifier
                )
                Button(onClick = {
                    removeCartItemById(filePath, item.dishId)},
                    colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White),
                    modifier = Modifier.size(60.dp,40.dp)
                ) {
                    Icon(imageVector = iconMinus, contentDescription = null)
                }
            }
            Spacer(modifier = modifier.height(20.dp))
        }
        Spacer(modifier = modifier.height(25.dp))
        Button(onClick = { /*TODO*/ },
            modifier
                //.background(Orange)
                .width(250.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White),
            ){
            Text(text = "Passez au paiment : $total€",
                Modifier.background(Color.Transparent))
        }
    }


}
@Composable
fun CustomActionBar(
    onLeftButtonClick: () -> Unit,
    onRightButtonClick: () -> Unit,
    showCartIcon : Boolean =false,
    count:Int,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    val iconCart: ImageVector = Icons.Default.ShoppingCart

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(color = Orange),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { onLeftButtonClick() }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
        Text(
            text = "The Pepe's Restaurant",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        if(showCartIcon){
            Box(modifier = modifier){
                if(count>0){
                    Text(
                    text = count.toString(),
                        modifier
                            .background(Color.Red, CircleShape)
                            .width(20.dp),
                    fontSize = 10.sp,
                    color = Color.White,
                    )
                }
                IconButton(
                    onClick = { onRightButtonClick() }
                ) {
                    Icon(imageVector = iconCart,
                        contentDescription = "Cart",
                        tint = Color.White
                    )
                }
            }
        }
    }
}


fun addition(total: Float, sousTotal: Float): Float {
    return total + sousTotal
}


fun loadCartItems(filePath: String): List<CartItem> {
    return try {
        val gson = Gson()
        val jsonString = File(filePath).readText()
        gson.fromJson(jsonString, object : TypeToken<List<CartItem>>() {}.type)
    } catch (e: IOException) {
        e.printStackTrace()
        emptyList()
    }
}
/*@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidERestaurantTheme {
        DisplayCart("Android")
    }
}*/