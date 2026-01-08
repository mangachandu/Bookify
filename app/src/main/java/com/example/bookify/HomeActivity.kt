package com.example.bookify

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookify.ui.theme.BookifyTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

data class Professional(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val rating: Double = 0.0,
    val price: String = "",
    val location: String = "",
    val about: String = "",
    val imageUrl: String = ""
)

class HomeActivity : ComponentActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private var listener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // protect screen
        if (auth.currentUser == null) {
            startActivity(Intent(this, SigninActivity::class.java))
            finish()
            return
        }

        setContent {
            BookifyTheme {
                HomeScreen(
                    db = db,
                    onLogout = {
                        auth.signOut()
                        startActivity(Intent(this, SigninActivity::class.java))
                        finish()
                    },
                    onOpenProfile = { prof ->
                        val i = Intent(this, ProfessionalProfileActivity::class.java).apply {
                            putExtra("id", prof.id)
                            putExtra("name", prof.name)
                            putExtra("category", prof.category)
                            putExtra("rating", prof.rating)
                            putExtra("price", prof.price)
                            putExtra("location", prof.location)
                            putExtra("about", prof.about)
                        }
                        startActivity(i)
                    },
                    onAttachListener = { reg -> listener = reg },
                    showToast = { msg -> Toast.makeText(this, msg, Toast.LENGTH_LONG).show() }
                )
            }
        }
    }

    override fun onDestroy() {
        listener?.remove()
        super.onDestroy()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    db: FirebaseFirestore,
    onLogout: () -> Unit,
    onOpenProfile: (Professional) -> Unit,
    onAttachListener: (ListenerRegistration) -> Unit,
    showToast: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var professionals by remember { mutableStateOf(listOf<Professional>()) }

    val categories = listOf("All", "Doctors", "Clinics", "Beauty & Salon", "Wellness", "Fitness")



    // ✅ Real-time Firestore listener
    DisposableEffect(Unit) {
        val reg = db.collection("professionals")
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    error = e.message
                    loading = false
                    return@addSnapshotListener
                }

                val list = snap?.documents?.map { doc ->
                    Professional(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        category = doc.getString("category") ?: "",
                        rating = doc.getDouble("rating") ?: 0.0,
                        price = doc.getString("price") ?: "",
                        location = doc.getString("location") ?: "",
                        about = doc.getString("about") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: ""
                    )
                } ?: emptyList()

                professionals = list
                loading = false
            }

        onAttachListener(reg)
        onDispose { reg.remove() }
    }

    // ✅ UI filtering
    val filtered = remember(searchQuery, selectedCategory, professionals) {
        professionals.filter { p ->
            val matchesCategory = (selectedCategory == "All") || (p.category == selectedCategory)
            val q = searchQuery.trim().lowercase()
            val matchesSearch =
                q.isEmpty() ||
                        p.name.lowercase().contains(q) ||
                        p.category.lowercase().contains(q) ||
                        p.location.lowercase().contains(q)
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bookify", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // 🔍 Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                placeholder = { Text("Search professionals or services") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            // ✅ Category chips (same UI as your screenshot)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.take(3).forEach { cat ->
                    FilterChipItem(
                        text = cat,
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.drop(3).forEach { cat ->
                    FilterChipItem(
                        text = cat,
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = if (selectedCategory == "All") "All Professionals" else selectedCategory,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(10.dp))

            when {
                loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Text(
                        text = "Error: ${error!!}",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                filtered.isEmpty() -> {
                    Text("No results found.")
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(filtered) { prof ->
                            ProfessionalCard(professional = prof, onClick = { onOpenProfile(prof) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChipItem(text: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text, maxLines = 1, overflow = TextOverflow.Ellipsis) }
    )
}

@Composable
fun ProfessionalCard(professional: Professional, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(professional.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(6.dp))

            Text("${professional.category} • ${professional.location}")

            Spacer(Modifier.height(6.dp))

            Text("Rating: ${professional.rating}  •  ${professional.price}")
        }
    }
}



