package com.example.bookify

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookify.ui.theme.BookifyTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookifyTheme {
                SplashScreen()
            }
        }
    }
}

@Composable
fun SplashScreen() {
    var visible by remember { mutableStateOf(false) }
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        visible = true
        delay(2500) // Duration before moving to next screen
        // Navigate to SigninActivity after splash
        val intent = Intent(context, SigninActivity::class.java)
        context.startActivity(intent)
        (context as? Activity)?.finish() // close splash so back button doesn't return here
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Background image (movie theme)
        Image(
            painter = painterResource(id = R.drawable.movie_bg1), // your image here
            contentDescription = "Movie Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

    }
}



        @Preview(showBackground = true)
        @Composable
        fun SplashPreview() {
            BookifyTheme {
                SplashScreen()
            }
        }


