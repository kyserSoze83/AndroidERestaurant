package fr.isen.sarhiri.androiderestaurant

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.isen.sarhiri.androiderestaurant.ui.theme.AndroidERestaurantTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.isen.sarhiri.androiderestaurant.ui.theme.Orange
import fr.isen.sarhiri.androiderestaurant.ui.theme.Grey
import java.io.File
import java.io.IOException

class HomeActivity : ComponentActivity() {
    companion object {
        lateinit var cartDirectory: File
            private set
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cartDirectory= File(getExternalFilesDir(null), "cartDirectory")
        cartDirectory.mkdirs()

    }
    override fun onResume(){
        super.onResume()
        val filePath = File(HomeActivity.cartDirectory, "cart.json").absolutePath
        var count=loadCartItems(filePath).size
        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    DisplayHome(Message("Welcome\nto","The Pepe's Restaurant"),this::showToast, this::navigateToActivity, this::navigateToActivity2,count)
                }
            }
        }
    }

    private fun showToast(message: String){
        val duration = Toast.LENGTH_LONG
        val toast = Toast.makeText(this,message,duration)
        toast.show()
    }
    private fun navigateToActivity2(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
    private fun navigateToActivity(activityClass: Class<*>,param: String) {
        val intent = Intent(this, activityClass)
        intent.putExtra("DISH_TYPE", param)
        startActivity(intent)
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("HomeActivity", "L'activité HomeActivity est en train d'être détruite.")
    }
    private fun loadCartItems(filePath: String): List<CartItem> {
        return try {
            val gson = Gson()
            val jsonString = File(filePath).readText()
            gson.fromJson(jsonString, object : TypeToken<List<CartItem>>() {}.type)
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }
}

data class Message (val greet: String, val name: String)
@Composable
fun DisplayHome(msg: Message, showToast: (String)->Unit, navigateToActivity: (Class<*>,String) -> Unit, navigateToActivity2:(Class<*>) -> Unit, count: Int,modifier: Modifier = Modifier) {

    Column(
    ) {
        HomeCustomActionBar(
            onRightButtonClick = { navigateToActivity2(CartActivity::class.java) },
            count
        )
        Row(
            modifier=modifier.fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 25.dp),
            horizontalArrangement = Arrangement.Center
        ){
            Column(
                modifier=modifier,
                horizontalAlignment = Alignment.End
            ){
                Text(
                    text = msg.greet,
                    modifier = modifier,
                    fontSize = 25.sp,
                    color = Orange,
                    textAlign= TextAlign.End
                )
                Spacer(modifier = modifier.height(20.dp))
                Text(
                    text = msg.name,
                    modifier = modifier,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.magic_spark)),
                    textAlign= TextAlign.End
                )
            }
            Spacer(modifier = modifier.width(50.dp))
            Image(
                painter = painterResource(R.drawable.pepethefrogcooker_removebg_preview),
                contentDescription = "Contact profile picture",
                modifier=modifier.size(154.dp,146.dp)
            )
        }
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(100.dp),
            contentAlignment = Alignment.Center
        ){
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment =  Alignment.CenterHorizontally
            ) {
                ClickableButton("Entrées"){
                    //showToast("Entrée")
                    navigateToActivity(DishActivity::class.java,"Entrées")
                }
                Spacer(modifier.height(5.dp))
                Box(
                    modifier = modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .border(width = 1.dp, color = Grey)
                )
                Spacer(modifier.height(20.dp))
                ClickableButton("Plats") {
                    //showToast("Plats")
                    navigateToActivity(DishActivity::class.java, "Plats")
                }
                Spacer(modifier.height(5.dp))
                Box(
                    modifier = modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .border(width = 1.dp, color = Grey)
                )
                Spacer(modifier.height(20.dp))
                ClickableButton("Desserts") {
                    //showToast("Desserts")
                    navigateToActivity(DishActivity::class.java, "Desserts")
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


@Composable
fun ClickableButton(text: String, onClick: () -> Unit) {
    Button(
        onClick =onClick,
        modifier=Modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Orange),
    ){
        Text(
            text = text,
            modifier=Modifier,
            fontSize = 30.sp
        )
    }
}

@Composable
fun HomeCustomActionBar(
    onRightButtonClick: () -> Unit,
    count : Int,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    val iconCart: ImageVector = Icons.Default.ShoppingCart

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(color = Orange),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,

    ) {
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

/*@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidERestaurantTheme {
        Greeting(Message("Bienvenue chez","DroidRestaurant"))
    }
}*/