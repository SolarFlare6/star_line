package com.example.starline.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.starline.theme.*
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.starline.data.ApiKeyManager
import com.example.starline.data.SettingsManager

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val apiKeyManager = remember { ApiKeyManager(context) }
    val settingsManager = remember(context) { SettingsManager(context) }

    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(true) }
    var measurementSystem by remember { mutableStateOf(settingsManager.measurementSystem) }

    var nasaKeyInput by remember { mutableStateOf(if (apiKeyManager.isUsingDefaultNasaKey) "" else (apiKeyManager.customNasaKey ?: "")) }
    var geminiKeyInput by remember { mutableStateOf(if (apiKeyManager.isUsingDefaultGeminiKey) "" else (apiKeyManager.customGeminiKey ?: "")) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.statusBarsPadding()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = StarWhite)
            }
            Text("Settings", style = MaterialTheme.typography.titleLarge, color = StarWhite, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(16.dp))

        // Notifications section
        SettingsSectionHeader("Notifications")
        SettingsCard {
            SettingsToggleRow(
                icon = Icons.Default.Notifications,
                title = "Push Notifications",
                subtitle = "Get alerts about space events",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
        }

        Spacer(Modifier.height(16.dp))

        // Measurement section
        SettingsSectionHeader("Units & Measurements")
        SettingsCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NeonSecondary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Straighten, null, tint = NeonSecondary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Text("Measurement System", color = StarWhite, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("Metric", "Imperial").forEach { system ->
                        val isSelected = measurementSystem == system
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                measurementSystem = system
                                settingsManager.updateMeasurementSystem(system)
                            },
                            label = { Text(system) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonSecondary.copy(alpha = 0.2f),
                                selectedLabelColor = NeonSecondary,
                                containerColor = SpaceSurface,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = NeonSecondary.copy(alpha = 0.4f),
                                borderColor = SpaceBorder
                            )
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // API Credentials Section
        SettingsSectionHeader("API Configuration")
        SettingsCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Customize credentials for real-world space telemetry and dynamic fact generation. If empty, Star Line securely uses default pre-baked keys.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = nasaKeyInput,
                    onValueChange = { nasaKeyInput = it },
                    label = { Text("NASA API Key") },
                    placeholder = { Text("Using Secure Default Key") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPrimary,
                        unfocusedBorderColor = SpaceBorder,
                        focusedLabelColor = NeonPrimary,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = StarWhite,
                        unfocusedTextColor = StarWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = geminiKeyInput,
                    onValueChange = { geminiKeyInput = it },
                    label = { Text("Gemini API Key") },
                    placeholder = { Text("Using Secure Default Key") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonSecondary,
                        unfocusedBorderColor = SpaceBorder,
                        focusedLabelColor = NeonSecondary,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = StarWhite,
                        unfocusedTextColor = StarWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (nasaKeyInput.isNotBlank()) {
                                apiKeyManager.customNasaKey = nasaKeyInput
                            } else {
                                apiKeyManager.resetNasaKey()
                            }

                            if (geminiKeyInput.isNotBlank()) {
                                apiKeyManager.customGeminiKey = geminiKeyInput
                            } else {
                                apiKeyManager.resetGeminiKey()
                            }

                            Toast.makeText(context, "API Credentials Saved Successfully!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Save, null, tint = StarWhite, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Save Keys", color = StarWhite, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            apiKeyManager.resetNasaKey()
                            apiKeyManager.resetGeminiKey()
                            nasaKeyInput = ""
                            geminiKeyInput = ""
                            Toast.makeText(context, "Reset to secure default keys", Toast.LENGTH_SHORT).show()
                        },
                        border = ButtonDefaults.outlinedButtonBorder(true).copy(width = 1.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Reset")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // About section
        SettingsSectionHeader("About")
        SettingsCard {
            SettingsInfoRow(icon = Icons.Default.Info, title = "Version", value = "1.0.0")
            SettingsDivider()
            SettingsInfoRow(icon = Icons.Default.Code, title = "Build", value = "Release")
            SettingsDivider()
            SettingsInfoRow(icon = Icons.Default.Language, title = "Region", value = "Global")
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleSmall,
        color = NeonPrimary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SpaceSurface)
            .border(1.dp, SpaceBorder, RoundedCornerShape(16.dp))
    ) {
        content()
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(NeonPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = NeonPrimary, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = StarWhite, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = StarWhite,
                checkedTrackColor = NeonPrimary,
                uncheckedThumbColor = StarWhite,
                uncheckedTrackColor = SpaceBorder.copy(alpha = 0.6f),
                uncheckedBorderColor = SpaceBorder
            )
        )
    }
}

@Composable
private fun SettingsInfoRow(icon: ImageVector, title: String, value: String) {
    Row(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(NeonTertiary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = NeonTertiary, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Text(title, color = StarWhite, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Text(value, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = SpaceBorder
    )
}
