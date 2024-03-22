package fr.isen.sarhiri.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.isen.sarhiri.androiderestaurant.ui.theme.AndroidERestaurantTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import fr.isen.sarhiri.androiderestaurant.ui.theme.Orange
import fr.isen.sarhiri.androiderestaurant.ui.theme.Grey

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting(Message("Bienvenue\nchez","DroidRestaurant"),this::showToast, this::navigateToActivity)

                }
            }
        }
    }
    private fun showToast(message: String){
        val duration = Toast.LENGTH_LONG
        val toast = Toast.makeText(this,message,duration)
        toast.show()
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
}

data class Message (val greet: String, val name: String)
@Composable
fun Greeting(msg: Message, showToast: (String)->Unit, navigateToActivity: (Class<*>,String) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier=modifier.padding(top = 30.dp)
    ) {
        Row(
            modifier=modifier.fillMaxWidth(),
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

/*@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidERestaurantTheme {
        Greeting(Message("Bienvenue chez","DroidRestaurant"))
    }
}*/