package fr.isen.sarhiri.androiderestaurant

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.isen.sarhiri.androiderestaurant.ui.theme.AndroidERestaurantTheme
import fr.isen.sarhiri.androiderestaurant.ui.theme.Orange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class DishDetailActivity : ComponentActivity() {
    private lateinit var dish: Dish
    companion object {
        lateinit var cartDirectory: File
            private set
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cartDirectory= File(getExternalFilesDir(null), "cartDirectory")
        cartDirectory.mkdirs()
        dish = intent.getSerializableExtra("DISH") as Dish
        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DisplayDish(dish, this, this::navigateToActivity2, this::calculatePrice, this::onAddToCartClicked, this::logJsonFileContent, this::clearJsonFile,{ onBackPressedDispatcher.onBackPressed() })
                }
            }
        }
    }
    private fun navigateToActivity(activityClass: Class<*>,param: String) {
        val intent = Intent(this, activityClass)
        intent.putExtra("DISH_TYPE", param)
        startActivity(intent)
    }
    private fun navigateToActivity2(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
    private fun calculatePrice(prix: Float, nb: Int): Float {
        return prix * nb
    }
    private fun saveCartItemToCart(cartItem: CartItem, filePath: String) {
        val gson = Gson()
        val cartItemList: MutableList<CartItem> = if (File(filePath).exists()) {
            val jsonString = File(filePath).readText()
            gson.fromJson(jsonString, object : TypeToken<MutableList<CartItem>>() {}.type)
        } else {
            mutableListOf()
        }
        cartItemList.add(cartItem)
        val json = gson.toJson(cartItemList)
        File(filePath).writeText(json)
    }
    private fun onAddToCartClicked(dishId: Int, dishName: String, quantity: Int, totalPrice: Float, filePath: String) {
        val cartItem = CartItem(dishId, dishName, quantity, totalPrice)
        saveCartItemToCart(cartItem, filePath)
        // Afficher une snackbar ou une AlertDialog pour informer l'utilisateur
    }
    private fun readJsonFile(context: Context, filePath: String): String? {
        return try {
            val file = File(filePath)
            val jsonString = file.readText()
            jsonString
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun logJsonFileContent(context: Context, filePath: String) {
        val jsonString = readJsonFile(context, filePath)
        if (jsonString != null) {
            Log.d("JSON_CONTENT", jsonString)
        } else {
            Log.e("JSON_CONTENT", "Le fichier JSON est vide ou introuvable.")
        }
    }
    private fun clearJsonFile(filePath: String) {
        try {
            val file = File(filePath)
            file.writeText("") // Écrit une chaîne vide dans le fichier
        } catch (e: IOException) {
            e.printStackTrace()
            // Gérer les exceptions, par exemple : afficher un message d'erreur à l'utilisateur
        }
    }

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DisplayDish(dish:Dish,context:Context, navigateToActivity2: (Class<*>) -> Unit, calculatePrice:(Float, Int) -> Float, onAddToCartClicked:(Int, String, Int, Float, String) -> Unit, logJsonFileContent:(Context, String) -> Unit, clearJsonFile:(String) -> Unit, onBackPressed:() -> Unit,modifier: Modifier = Modifier) {
    val iconMinus: ImageVector = Icons.Default.Delete
    val iconPlus: ImageVector = Icons.Default.Add
    var totalPrice by remember { mutableFloatStateOf(0f) }
    var nombre by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val filePath = File(DishDetailActivity.cartDirectory, "cart.json").absolutePath

    val counterCallback = object : CounterCallback {
        override fun onCounterValueChanged(value: Int) {
            totalPrice = calculatePrice(dish.prices[0].price.toFloat(), value)
            nombre = value
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomActionBar(
                onLeftButtonClick = { onBackPressed() },
                onRightButtonClick = { navigateToActivity2(CartActivity::class.java) }
            )
            ImageCarousel(dish.images, context)
            Spacer(modifier.height(20.dp))
            Text(
                text = dish.nameFr,
                modifier = modifier,
                textAlign = TextAlign.Center,
                fontSize = 30.sp
            )
            Spacer(modifier.height(15.dp))
            Text(
                text = "Ingrédients : ",
                fontSize = 10.sp
            )
            Spacer(modifier.height(5.dp))
            Text(
                text = buildString {
                    dish.ingredients.forEach { ingredient ->
                        append(ingredient.nameFr)
                        append(", ")
                    }
                },
                fontSize = 10.sp,
                textAlign = TextAlign.Left
            )

            Spacer(modifier.height(15.dp))
            TwoRoundButtonsWithCounter(
                iconMinus,
                iconPlus,
                callback = counterCallback,
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        //navigateToActivity(CartActivity::class.java)
                        onAddToCartClicked(dish.id.toInt(),dish.nameFr, nombre, totalPrice, filePath)
                        //clearJsonFile(filePath)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Plat ajouté au panier")
                            logJsonFileContent(context, filePath)
                        }
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(75.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange,
                        contentColor = Color.White
                    ),
                ) {
                    Text(
                        text = "TOTAL : $totalPrice €" ?: "--€",
                        //${dish.prices[0].price}€
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

        }
    }

}


@Composable
fun ImageCarousel(imageUrls: List<String>,context:Context) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    var scrollPosition by remember { mutableFloatStateOf(0f) }
    var validImageUrls by remember { mutableStateOf(emptyList<String>()) }

    LaunchedEffect(imageUrls) {
        val validUrls = mutableListOf<String>()
        imageUrls.forEach { url ->
            if (isValidImageUrl(url,context)) {
                validUrls.add(url)
            }
        }
        validImageUrls = validUrls
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    scrollPosition += dragAmount
                }
            }
    ) {
        items(validImageUrls.size) { index ->
            val url = validImageUrls[index]
            val offset = with(LocalDensity.current) { scrollPosition.toDp() }
            Image(
                painter = rememberAsyncImagePainter(url),
                contentDescription = null,
                modifier = Modifier
                    .size(screenWidth, 250.dp)
                    .offset(x = offset),
                contentScale = ContentScale.Crop
            )
        }
    }
}




/// Fonction pour vérifier si une URL est valide
suspend fun isValidImageUrl(url: String, context: Context): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val imageLoader = ImageLoader.Builder(context)
                .build()
            val request = ImageRequest.Builder(context)
                .data(url)
                .build()
            val result = imageLoader.execute(request)
            result.drawable != null // Si le chargement de l'image réussit, alors l'URL est valide
        } catch (e: Exception) {
            false // Si une exception se produit, l'URL n'est pas valide
        }
    }
}

@Composable
fun TwoRoundButtonsWithCounter(
    iconMinus: ImageVector,
    iconPlus: ImageVector,
    callback: CounterCallback? = null,
    modifier: Modifier = Modifier,
) {
    var counter by remember { mutableIntStateOf(0) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                if (counter > 0) {
                    counter--
                    callback?.onCounterValueChanged(counter)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White),
            modifier = Modifier.size(60.dp,40.dp)
        ) {
            Icon(imageVector = iconMinus, contentDescription = null)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = counter.toString(),
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = {
                counter++
                callback?.onCounterValueChanged(counter)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.White),
            modifier = Modifier.size(60.dp,40.dp)
        ) {
            Icon(imageVector = iconPlus, contentDescription = null)
        }
    }
}

interface CounterCallback {
    fun onCounterValueChanged(value: Int)
}

/*@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidERestaurantTheme {
        DisplayDish()
    }
}*/