package com.example.starline.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.starline.theme.*
import com.example.starline.ui.components.StarfieldBackground
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) onLoginSuccess()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        StarfieldBackground()

        // Purple nebula glow top-right
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = 100.dp, y = (-80).dp)
                .align(Alignment.TopEnd)
                .blur(80.dp)
                .background(
                    Brush.radialGradient(listOf(NeonPrimary.copy(alpha = 0.3f), Color.Transparent))
                )
        )
        // Blue glow bottom-left
        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = (-60).dp, y = 60.dp)
                .align(Alignment.BottomStart)
                .blur(70.dp)
                .background(
                    Brush.radialGradient(listOf(NeonSecondary.copy(alpha = 0.25f), Color.Transparent))
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(visible = visible, enter = fadeIn() + slideInVertically { -60 }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Logo orb
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(NeonPrimary, NeonSecondary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✦", fontSize = 34.sp, color = StarWhite)
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Star Line",
                        style = MaterialTheme.typography.headlineLarge,
                        color = StarWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Explore the cosmos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            AnimatedVisibility(visible = visible, enter = fadeIn() + slideInVertically { 80 }) {
                // Glassmorphic card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(SpaceSurface.copy(alpha = 0.85f))
                        .border(1.dp, SpaceBorder, RoundedCornerShape(24.dp))
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            "Welcome Back",
                            style = MaterialTheme.typography.titleLarge,
                            color = StarWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Sign in to continue your journey",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(Modifier.height(24.dp))

                        // Email field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email", color = TextSecondary) },
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = NeonPrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonPrimary,
                                unfocusedBorderColor = SpaceBorder,
                                focusedTextColor = StarWhite,
                                unfocusedTextColor = TextPrimary,
                                cursorColor = NeonPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(Modifier.height(12.dp))

                        // Password field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password", color = TextSecondary) },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = NeonPrimary) },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        null, tint = TextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonPrimary,
                                unfocusedBorderColor = SpaceBorder,
                                focusedTextColor = StarWhite,
                                unfocusedTextColor = TextPrimary,
                                cursorColor = NeonPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Error message
                        if (authState is AuthState.Error) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                (authState as AuthState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(Modifier.height(24.dp))

                        // Login Button
                        Button(
                            onClick = { viewModel.login(email, password) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            enabled = authState !is AuthState.Loading && email.isNotBlank() && password.isNotBlank(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonPrimary,
                                disabledContainerColor = NeonPrimary.copy(alpha = 0.4f)
                            )
                        ) {
                            if (authState is AuthState.Loading) {
                                CircularProgressIndicator(color = StarWhite, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Launch Into Space", fontWeight = FontWeight.Bold, color = StarWhite, fontSize = 16.sp)
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Register link
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("New to Star Line?", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                            TextButton(onClick = {
                                viewModel.clearState()
                                onNavigateToRegister()
                            }) {
                                Text("Create Account", color = NeonPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}
