package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.services.NexusAccessibilityService
import com.example.ui.theme.*

@Composable
fun MobileControlScreen(
    onToggleTorch: () -> Unit,
    onAdjustVolume: (Boolean) -> Unit,
    onLaunchIntent: (String) -> Unit,
    onOpenSettings: () -> Unit
) {
    var screenText by remember { mutableStateOf(NexusAccessibilityService.lastCapturedScreenText) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("ADVANCED ANDROID AGENT & PERMISSION DASHBOARD", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, letterSpacing = 1.sp)
        }

        // Permission Health Dashboard
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier.border(1.dp, NeonEmerald.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Android System Permissions Status", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.5.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    listOf(
                        "Internet Access" to "GRANTED",
                        "Microphone (Voice)" to "GRANTED",
                        "Foreground Service" to "ACTIVE",
                        "Accessibility Bridge" to "READY (User-Configurable)",
                        "Notification Listener" to "READY (User-Configurable)"
                    ).forEach { (name, status) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(name, color = TextSecondary, fontSize = 12.sp)
                            Text(status, color = NeonEmerald, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Device Controls
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier.border(1.dp, NeonCyan.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Hardware Controls & App Navigation", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = onToggleTorch,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberSurface),
                            modifier = Modifier.border(1.dp, NeonEmerald, RoundedCornerShape(10.dp))
                        ) {
                            Icon(Icons.Default.FlashlightOn, contentDescription = null, tint = NeonEmerald)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Torch", color = NeonEmerald)
                        }

                        Button(
                            onClick = { onAdjustVolume(true) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberSurface),
                            modifier = Modifier.border(1.dp, NeonCyan, RoundedCornerShape(10.dp))
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = NeonCyan)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Vol +", color = NeonCyan)
                        }

                        Button(
                            onClick = { onAdjustVolume(false) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberSurface),
                            modifier = Modifier.border(1.dp, NeonCyan, RoundedCornerShape(10.dp))
                        ) {
                            Icon(Icons.Default.VolumeDown, contentDescription = null, tint = NeonCyan)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Vol -", color = NeonCyan)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(onClick = { onLaunchIntent("https://wa.me") }) { Text("WhatsApp") }
                        FilledTonalButton(onClick = { onLaunchIntent("tel:") }) { Text("Dialer") }
                        FilledTonalButton(onClick = onOpenSettings) { Text("Settings") }
                    }
                }
            }
        }

        // Accessibility Reader
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier.border(1.dp, NeonPurple.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Screen Understanding & UI Discovery", fontWeight = FontWeight.Bold, color = TextPrimary)
                        TextButton(onClick = {
                            screenText = NexusAccessibilityService.lastCapturedScreenText.ifBlank {
                                "Accessibility active. Window elements monitored safely."
                            }
                        }) {
                            Text("Inspect UI", color = NeonCyan)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        screenText.ifBlank { "Service status: Ready. Grant Accessibility service in Settings to inspect active foreign app UI elements." },
                        color = TextSecondary,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
