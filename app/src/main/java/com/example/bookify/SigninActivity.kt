package com.example.bookify

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

class SigninActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookifyTheme {
                SignInScreen(
                    onSignIn = { email, _ ->
                        // TODO: replace with real auth
                        Toast.makeText(this, "Welcome $email!", Toast.LENGTH_SHORT).show()
                    },
                    onCreateAccountClick = {
                        startActivity(Intent(this, SignupActivity::class.java))
                    },
                    onForgotPassword = {
                        Toast.makeText(this, "Forgot password tapped", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun SignInScreen(
    onSignIn: (String, String) -> Unit,
    onCreateAccountClick: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val context = LocalContext.current

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
                        Color(0xFF005C97), // deep blue
                        Color(0xFF363795), // indigo/blue
                        Color(0xFF5EB1FF)  // soft sky blue
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

            // App title
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
                                if (validate()) onSignIn(email, password)
                                else Toast.makeText(context, "Fix the errors", Toast.LENGTH_SHORT).show()
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Error text
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
                        onClick = onForgotPassword,
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
