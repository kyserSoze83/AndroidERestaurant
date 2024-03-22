package fr.isen.sarhiri.androiderestaurant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fr.isen.sarhiri.androiderestaurant.ui.theme.AndroidERestaurantTheme
import fr.isen.sarhiri.androiderestaurant.ui.theme.Grey
import fr.isen.sarhiri.androiderestaurant.ui.theme.Orange
import com.google.gson.Gson
import org.json.JSONObject
import com.android.volley.Request




class DishActivity : ComponentActivity() {
    private val dishResponseState = mutableStateOf<DishResponse?>(null)
    private lateinit var category: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = intent.getStringExtra("DISH_TYPE") ?: "Pas de type provisionné"
        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    /*val dishesToDisplay= remember{
                        mutableListOf<Dish>()
                    }*/
                    fetchMenuItems(this,
                        "1",
                        onResult={ dishResponse ->
                            dishResponseState.value=dishResponse
                        })
                    DisplayDishes(this,this::navigateToActivity,this::navigateToActivity2, dishResponseState, category)
                }
            }
        }
    }
    private fun navigateToActivity(activityClass: Class<*>, dish: Dish) {
        val intent = Intent(this, activityClass)
        intent.putExtra("DISH", dish)
        startActivity(intent)
    }
    private fun navigateToActivity2(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
    private fun fetchMenuItems(context: Context, idShop: String, onResult:(DishResponse?)->Unit) {
        val url = "http://test.api.catering.bluecodegames.com/menu"

        val params = JSONObject().apply {
            put("id_shop", idShop)
        }

        val requestQueue = Volley.newRequestQueue(context)
        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            params,
            { response ->
                Log.d("DishActivity","result : $response")
                //dishes = parseJsonToDishes(response.toString())
                val gson = Gson()
                val dishResponse = gson.fromJson(response.toString(), DishResponse::class.java)
                onResult(dishResponse)
                //dishes = dishResponse.data.find { it.nameFr == category }?.items ?: emptyList()
                //Log.d("DishesList","Dishes : $cate")

            },
            { error ->
                Log.e("DishActivity","result : $error.message")
            }
        )
        requestQueue.add(request)
    }
}

@Composable
fun DisplayDishes(context: Context, navigateToActivity: (Class<*>,Dish) -> Unit,navigateToActivity2: (Class<*>) -> Unit, dishResponse: State<DishResponse?>,categ:String, modifier:Modifier=Modifier) {
    val category = dishResponse.value?.data?.find { it.nameFr == categ }
    Column {
        ClickableButton("Retour") {
            //showToast("Entrée")
            navigateToActivity2(HomeActivity::class.java)
        }
        Text(
            text = categ,
            modifier = modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(top = 25.dp),
            fontSize = 30.sp,
            color = Orange
        )
        Spacer(modifier.height(40.dp))
        LazyColumn(
            modifier = modifier
            .fillMaxWidth()
            .padding(40.dp),
            ) {
            category?.items?.let { items ->
                items(items = items) { dish ->
                    Button(
                        onClick = { navigateToActivity(DishDetailActivity::class.java, dish) },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Orange),
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.padding(start = 20.dp)
                            ) {
                                Text(
                                    text = buildString {
                                        append(dish.nameFr ?: "--")
                                        append(" - ")
                                        append("${dish.prices.getOrNull(0)?.price ?: "--"}€")
                                    },
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Left
                                )

                            }
                            Spacer(modifier.height(5.dp))
                            Box(
                                modifier = modifier
                                    .height(1.dp)
                                    .fillMaxWidth()
                                    .border(width = 1.dp, color = Grey)
                            )
                            Spacer(modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }
}
