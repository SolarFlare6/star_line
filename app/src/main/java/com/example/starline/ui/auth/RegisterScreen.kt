package com.example.starline.ui.auth

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Person
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
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel
) {
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }
    val authState by viewModel.authState.collectAsState()

    BackHandler {
        viewModel.clearState()
        onNavigateToLogin()
    }

    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) onRegisterSuccess()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        StarfieldBackground()

        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-60).dp)
                .align(Alignment.TopStart)
                .blur(80.dp)
                .background(Brush.radialGradient(listOf(NeonTertiary.copy(alpha = 0.25f), Color.Transparent)))
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = 80.dp, y = 40.dp)
                .align(Alignment.BottomEnd)
                .blur(70.dp)
                .background(Brush.radialGradient(listOf(NeonSecondary.copy(alpha = 0.2f), Color.Transparent)))
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
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(NeonTertiary, NeonPrimary))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🚀", fontSize = 30.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Join Star Line", style = MaterialTheme.typography.headlineMedium, color = StarWhite, fontWeight = FontWeight.Bold)
                    Text("Begin your cosmic adventure", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }

            Spacer(Modifier.height(28.dp))

            AnimatedVisibility(visible = visible, enter = fadeIn() + slideInVertically { 80 }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(SpaceSurface.copy(alpha = 0.85f))
                        .border(1.dp, SpaceBorder, RoundedCornerShape(24.dp))
                        .padding(24.dp)
                ) {
                    Column {
                        // Display Name
                        OutlinedTextField(
                            value = displayName,
                            onValueChange = { displayName = it },
                            label = { Text("Display Name", color = TextSecondary) },
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = NeonTertiary) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonTertiary, unfocusedBorderColor = SpaceBorder,
                                focusedTextColor = StarWhite, unfocusedTextColor = TextPrimary, cursorColor = NeonTertiary
                            ),
                            shape = RoundedCornerShape(12.dp), singleLine = true
                        )
                        Spacer(Modifier.height(10.dp))

                        // Email
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email", color = TextSecondary) },
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = NeonTertiary) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonTertiary, unfocusedBorderColor = SpaceBorder,
                                focusedTextColor = StarWhite, unfocusedTextColor = TextPrimary, cursorColor = NeonTertiary
                            ),
                            shape = RoundedCornerShape(12.dp), singleLine = true
                        )
                        Spacer(Modifier.height(10.dp))

                        // Password
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password", color = TextSecondary) },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = NeonTertiary) },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = TextSecondary)
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonTertiary, unfocusedBorderColor = SpaceBorder,
                                focusedTextColor = StarWhite, unfocusedTextColor = TextPrimary, cursorColor = NeonTertiary
                            ),
                            shape = RoundedCornerShape(12.dp), singleLine = true
                        )
                        Spacer(Modifier.height(10.dp))

                        // Confirm Password
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password", color = TextSecondary) },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = NeonTertiary) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonTertiary, unfocusedBorderColor = SpaceBorder,
                                focusedTextColor = StarWhite, unfocusedTextColor = TextPrimary, cursorColor = NeonTertiary
                            ),
                            shape = RoundedCornerShape(12.dp), singleLine = true
                        )

                        val errorMsg = localError ?: (authState as? AuthState.Error)?.message
                        if (errorMsg != null) {
                            Spacer(Modifier.height(8.dp))
                            Text(errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        }

                        Spacer(Modifier.height(20.dp))

                        Button(
                            onClick = {
                                localError = null
                                if (password != confirmPassword) {
                                    localError = "Passwords do not match."
                                } else {
                                    viewModel.register(email, password, displayName)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            enabled = authState !is AuthState.Loading && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonTertiary, disabledContainerColor = NeonTertiary.copy(alpha = 0.4f))
                        ) {
                            if (authState is AuthState.Loading) {
                                CircularProgressIndicator(color = StarWhite, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Create Account", fontWeight = FontWeight.Bold, color = StarWhite, fontSize = 16.sp)
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            Text("Already have an account?", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                            TextButton(onClick = { viewModel.clearState(); onNavigateToLogin() }) {
                                Text("Sign In", color = NeonTertiary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}
