package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.NexusScreen
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    onNavigate: (NexusScreen) -> Unit,
    onSpeak: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // System Hero Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonCyan.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(NeonEmerald, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "NEXUS AI ASSISTANT V5.0",
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 1.2.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(NeonEmerald.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("AUTONOMOUS", color = NeonEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Voice-first autonomous Android agent powered by multi-model routing (Gemini, OpenRouter, Groq, HF), 9 specialized agents, native mobile control, and hardware repair diagnosis.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .background(CyberSurface, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("9 Agents Online", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .background(CyberSurface, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("Urdu + English", color = NeonPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        IconButton(
                            onClick = {
                                onSpeak("Salam Operator. Project Nexus Version 5.0 online hai. Tamam 9 autonomous agents aur multi-model routing active hain.")
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Status Audio", tint = NeonCyan)
                        }
                    }
                }
            }
        }

        // Live Telemetry Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(title = "Multi-Model Router", value = "Gemini / Groq / OR", sub = "Auto-fallback active", modifier = Modifier.weight(1f))
                StatCard(title = "Hardware Repair", value = "5 Categories", sub = "Diode mode values", modifier = Modifier.weight(1f))
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(title = "Agent Federation", value = "9/9 Active", sub = "Recovery healthy", modifier = Modifier.weight(1f))
                StatCard(title = "Keystore Vault", value = "Encrypted", sub = "Zero plaintext exposure", modifier = Modifier.weight(1f))
            }
        }

        // Quick Navigation Grid
        item {
            Text("MISSION CONTROL HUBS", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, letterSpacing = 1.sp)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HubCard(
                    title = "Live Voice Orb",
                    subtitle = "Barge-in voice assistant",
                    icon = Icons.Default.Mic,
                    tint = NeonCyan,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NexusScreen.VOICE) }
                )
                HubCard(
                    title = "AI Chat Core",
                    subtitle = "Urdu, Roman & English",
                    icon = Icons.Default.Chat,
                    tint = NeonEmerald,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NexusScreen.CHAT) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HubCard(
                    title = "API Key Vault",
                    subtitle = "Multi-slot credentials",
                    icon = Icons.Default.Key,
                    tint = NeonPurple,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NexusScreen.API_SETTINGS) }
                )
                HubCard(
                    title = "Mobile Repair AI",
                    subtitle = "Diode & IC diagnostics",
                    icon = Icons.Default.Build,
                    tint = NeonCyan,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NexusScreen.MOBILE_REPAIR) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HubCard(
                    title = "9 Specialized Agents",
                    subtitle = "Federated consensus",
                    icon = Icons.Default.Hub,
                    tint = NeonEmerald,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NexusScreen.AGENTS) }
                )
                HubCard(
                    title = "Android Control",
                    subtitle = "Torch, volume, intents",
                    icon = Icons.Default.Smartphone,
                    tint = NeonPurple,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NexusScreen.MOBILE_CONTROL) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HubCard(
                    title = "Dev Studio",
                    subtitle = "Code diffs & test runner",
                    icon = Icons.Default.Code,
                    tint = NeonCyan,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NexusScreen.DEV_STUDIO) }
                )
                HubCard(
                    title = "Security Center",
                    subtitle = "Emergency stop & audit",
                    icon = Icons.Default.Security,
                    tint = NeonDanger,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NexusScreen.SECURITY_CENTER) }
                )
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, sub: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
        modifier = modifier.border(1.dp, CyberSurface, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = TextSecondary, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = TextPrimary, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(sub, color = NeonEmerald, fontSize = 10.sp)
        }
    }
}

@Composable
fun HubCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
        modifier = modifier
            .border(1.dp, tint.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(CyberSurface, CircleShape)
                    .border(1.dp, tint.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, color = TextSecondary, fontSize = 11.sp, maxLines = 1)
        }
    }
}
