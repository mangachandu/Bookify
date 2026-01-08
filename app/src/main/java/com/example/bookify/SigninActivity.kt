package com.example.bookify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookify.ui.theme.BookifyTheme
import com.google.firebase.auth.FirebaseAuth

private const val GDPR_PREFS = "gdpr_prefs"
private const val GDPR_ACCEPTED = "gdpr_accepted"

class SigninActivity : ComponentActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // If already logged in → go Home directly
        if (auth.currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        setContent {
            BookifyTheme {
                SignInScreen(
                    onSignIn = { email, password ->
                        signInUser(email, password)
                    },
                    onCreateAccountClick = {
                        startActivity(Intent(this, SignupActivity::class.java))
                    },
                    onForgotPassword = { email ->
                        resetPassword(email)
                    }
                )
            }
        }
    }

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message ?: "Login failed", Toast.LENGTH_LONG).show()
            }
    }

    private fun resetPassword(email: String) {
        if (email.trim().isEmpty()) {
            Toast.makeText(this, "Enter your email first", Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(email.trim())
            .addOnSuccessListener {
                Toast.makeText(this, "Reset link sent to $email", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message ?: "Failed to send reset email", Toast.LENGTH_LONG).show()
            }
    }
}

@Composable
fun SignInScreen(
    onSignIn: (String, String) -> Unit,
    onCreateAccountClick: () -> Unit,
    onForgotPassword: (String) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    // ✅ GDPR state (shows only if not accepted before)
    val prefs = remember {
        context.getSharedPreferences(GDPR_PREFS, Context.MODE_PRIVATE)
    }
    var showGdpr by remember { mutableStateOf(!prefs.getBoolean(GDPR_ACCEPTED, false)) }

    if (showGdpr) {
        AlertDialog(
            onDismissRequest = { /* force user choice */ },
            title = { Text("GDPR Consent") },
            text = {
                Text(
                    "Bookify collects and stores your login email and booking activity to provide " +
                            "appointment features. We use Firebase for authentication and database storage.\n\n" +
                            "By tapping Accept, you agree to this data processing for app functionality."
                )
            },
            confirmButton = {
                Button(onClick = {
                    prefs.edit().putBoolean(GDPR_ACCEPTED, true).apply()
                    showGdpr = false
                }) {
                    Text("Accept")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    Toast.makeText(context, "You declined consent. Closing app.", Toast.LENGTH_LONG).show()
                    activity?.finishAffinity()
                }) {
                    Text("Decline")
                }
            }
        )
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    fun isEmailValid(e: String) =
        android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches()

    fun validate(): Boolean {
        errorText = when {
            !isEmailValid(email) -> "Enter a valid email address."
            password.isBlank() -> "Password cannot be empty."
            else -> null
        }
        return errorText == null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF005C97),
                        Color(0xFF363795),
                        Color(0xFF5EB1FF)
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))

            Text(
                text = "Bookify",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))
            Text(
                "Sign in to continue",
                fontSize = 16.sp,
                color = Color(0xFFEAF2FF).copy(alpha = 0.9f)
            )

            Spacer(Modifier.height(24.dp))

            Surface(
                color = Color.White,
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPass = !showPass }) {
                                Icon(
                                    imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (showPass) "Hide password" else "Show password"
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (!prefs.getBoolean(GDPR_ACCEPTED, false)) {
                                    Toast.makeText(context, "Please accept GDPR consent first.", Toast.LENGTH_SHORT).show()
                                    showGdpr = true
                                    return@KeyboardActions
                                }

                                if (validate()) onSignIn(email, password)
                                else Toast.makeText(context, "Fix the errors", Toast.LENGTH_SHORT).show()
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorText != null) {
                        Text(
                            text = errorText!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    Button(
                        onClick = {
                            if (!prefs.getBoolean(GDPR_ACCEPTED, false)) {
                                Toast.makeText(context, "Please accept GDPR consent first.", Toast.LENGTH_SHORT).show()
                                showGdpr = true
                                return@Button
                            }

                            if (validate()) onSignIn(email, password)
                            else Toast.makeText(context, "Fix the errors", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }

                    TextButton(
                        onClick = { onForgotPassword(email) },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Forgot password?")
                    }

                    Divider()

                    TextButton(
                        onClick = onCreateAccountClick,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("New to Bookify? Create an account")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun SignInPreview() {
    BookifyTheme {
        SignInScreen(
            onSignIn = { _, _ -> },
            onCreateAccountClick = {},
            onForgotPassword = {}
        )
    }
}
