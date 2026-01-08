package com.example.bookify

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bookify.ui.theme.BookifyTheme

class ProfessionalProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val name = intent.getStringExtra("name") ?: ""
        val category = intent.getStringExtra("category") ?: ""
        val rating = intent.getDoubleExtra("rating", 0.0)
        val price = intent.getStringExtra("price") ?: ""
        val location = intent.getStringExtra("location") ?: ""
        val about = intent.getStringExtra("about") ?: ""

        setContent {
            BookifyTheme {
                ProfileScreen(
                    name = name,
                    category = category,
                    rating = rating,
                    price = price,
                    location = location,
                    about = about,
                    onBookAppointment = {
                        // ✅ Show booked message
                        Toast.makeText(this, "Appointment booked!", Toast.LENGTH_LONG).show()

                        // ✅ Go to Home screen
                        val i = Intent(this, HomeActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(i)
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    name: String,
    category: String,
    rating: Double,
    price: String,
    location: String,
    about: String,
    onBookAppointment: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = { TopAppBar(title = { Text("Profile") }) }
    ) { pv ->
        Column(
            modifier = Modifier
                .padding(pv)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("$category • $location")
            Spacer(Modifier.height(10.dp))
            Text("Rating: $rating")
            Text("Price: $price")

            Spacer(Modifier.height(16.dp))
            Text("About", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text(if (about.isBlank()) "No description available." else about)

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onBookAppointment,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Book Appointment")
            }
        }
    }
}
