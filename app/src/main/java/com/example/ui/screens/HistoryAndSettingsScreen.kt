package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class HistoryItem(val id: String, val type: String, val title: String, val timestamp: String, val detail: String)

@Composable
fun HistoryScreen() {
    val context = LocalContext.current
    val historyLogs = remember {
        mutableStateListOf(
            HistoryItem("1", "VOICE", "Voice command: 'Hardware short circuit test kar ke dikhao'", "10 mins ago", "Routed to Gemini 1.5 Flash. Returned reverse diode multimeter procedure."),
            HistoryItem("2", "AGENT", "9-Agent consensus executed Task #842", "25 mins ago", "Decomposed into 5 sub-milestones. Passed security screening."),
            HistoryItem("3", "REPAIR", "Inspected Dead Phone secondary power short", "1 hour ago", "Identified PM6150 capacitor leak (0.010V diode value)."),
            HistoryItem("4", "VAULT", "API Key vault updated for OpenRouter", "2 hours ago", "Saved secondary token with TLS keystore encryption.")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "ACTIVITY & CONVERSATION HISTORY",
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "Audit log of commands, agent tasks & repair sessions",
                        color = TextSecondary,
                        fontSize = 11.5.sp
                    )
                }

                Button(
                    onClick = {
                        Toast.makeText(context, "History JSON backup exported to downloads", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceCard),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export", color = NeonCyan, fontSize = 11.sp)
                }
            }
        }

        items(historyLogs) { item ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberSurface, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(CyberSurface, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(item.type, color = NeonEmerald, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(item.timestamp, color = TextSecondary, fontSize = 10.5.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(item.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(item.detail, color = TextSecondary, fontSize = 11.5.sp)
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    var speechSpeed by remember { mutableStateOf(1.0f) }
    var selectedLanguage by remember { mutableStateOf("Urdu, Roman Urdu & English") }
    var backendEndpoint by remember { mutableStateOf("http://127.0.0.1:8000") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                "SYSTEM SETTINGS & PREFERENCES",
                color = NeonCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )
            Text(
                "Configure speech vocalization, language auto-detect & networking",
                color = TextSecondary,
                fontSize = 11.5.sp
            )
        }

        // Voice Engine
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier.border(1.dp, CyberSurface, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Speech Synthesis Engine", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Voice Speed: ${"%.1f".format(speechSpeed)}x", color = NeonCyan, fontSize = 11.5.sp)
                    Slider(
                        value = speechSpeed,
                        onValueChange = { speechSpeed = it },
                        valueRange = 0.7f..1.6f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonCyan,
                            inactiveTrackColor = CyberBackground
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Language Processing Mode", color = TextSecondary, fontSize = 11.5.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(selectedLanguage, color = NeonEmerald, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                }
            }
        }

        // Backend Endpoint
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier.border(1.dp, CyberSurface, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Autonomous Python Backend", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = backendEndpoint,
                        onValueChange = { backendEndpoint = it },
                        label = { Text("FastAPI Server Host") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            }
        }

        // Build info
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Nexus AI Assistant V5.0", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Application ID: com.aistudio.nexusai.vzpkqw", color = TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text("Build Target: Android APK • Multi-Model Federated Architecture", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}
