package com.example.ui.screens

import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.NexusScreen
import com.example.data.ApiKeyRepository
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    onNavigate: (NexusScreen) -> Unit,
    onSpeak: (String) -> Unit
) {
    val context = LocalContext.current
    val repository = remember { ApiKeyRepository(context) }
    var currentTheme by remember { mutableStateOf(repository.getThemeMode()) }

    // Animated breathing avatar halo
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_breath")
    val avatarScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar_scale"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Avatar & Studio Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(NeonCyan, NeonPurple)),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Female AI Avatar Representation
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .scale(avatarScale)
                                    .background(
                                        Brush.sweepGradient(listOf(NeonCyan, NeonPurple, NeonPink, NeonCyan)),
                                        CircleShape
                                    )
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Face,
                                    contentDescription = "Nova AI Female Avatar",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "NOVA AI V7.0",
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    letterSpacing = 1.2.sp
                                )
                                Text("Autonomous AI Assistant Studio", color = TextSecondary, fontSize = 11.5.sp)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(NeonEmerald.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("LEVEL 1–8 VERIFIED", color = NeonEmerald, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Voice-first autonomous Android AI assistant powered by Universal Multi-AI Brain (Gemini, Groq, OpenRouter, DeepSeek, Anthropic, Ollama Local), 10 specialized agents, full phone control, AI vision, and mobile repair hardware expert.",
                        color = TextSecondary,
                        fontSize = 11.5.sp,
                        lineHeight = 16.5.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Theme selector (AMOLED, Dark, Light)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("AMOLED", "DARK", "LIGHT").forEach { theme ->
                                FilterChip(
                                    selected = currentTheme == theme,
                                    onClick = {
                                        currentTheme = theme
                                        repository.setThemeMode(theme)
                                    },
                                    label = { Text(theme, fontSize = 9.5.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberSurface,
                                        selectedLabelColor = NeonCyan
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                onSpeak("Salam Operator! NOVA AI Version 7.0 is online with 10 autonomous agents, universal multi-AI brain, and real-time voice ready.")
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Vocal Greeting", tint = NeonCyan)
                        }
                    }
                }
            }
        }

        // Live Telemetry Matrix
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(title = "Multi-AI Brain", value = "9 Providers", sub = "Gemini / Groq / DeepSeek", modifier = Modifier.weight(1f))
                StatCard(title = "Agent Federation", value = "10/10 Online", sub = "Self-Healing active", modifier = Modifier.weight(1f))
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(title = "Voice Assistant", value = "8 Voices", sub = "Urdu / Hindi / English VAD", modifier = Modifier.weight(1f))
                StatCard(title = "Phone Control", value = "Accessibility OK", sub = "Screen read & tap/swipe", modifier = Modifier.weight(1f))
            }
        }

        // Mission Control Hubs Grid
        item {
            Text("MISSION CONTROL HUBS", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HubCard(
                    title = "Live Voice Orb",
                    subtitle = "8 voices • Blue/Purple orb",
                    icon = Icons.Default.Mic,
                    tint = NeonCyan,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NexusScreen.VOICE) }
                )
                HubCard(
                    title = "AI Chat Core",
                    subtitle = "Urdu RTL & English streaming",
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
                    title = "AI Vision & OCR",
                    subtitle = "Screenshot VLM analysis",
                    icon = Icons.Default.Visibility,
                    tint = NeonPurple,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NexusScreen.VISION) }
                )
                HubCard(
                    title = "Phone Control",
                    subtitle = "Read screen & app actions",
                    icon = Icons.Default.Smartphone,
                    tint = NeonCyan,
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
                    title = "10 Specialized Agents",
                    subtitle = "Federated orchestrator",
                    icon = Icons.Default.Hub,
                    tint = NeonEmerald,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NexusScreen.AGENTS) }
                )
                HubCard(
                    title = "Automation Scheduler",
                    subtitle = "WorkManager routines",
                    icon = Icons.Default.Schedule,
                    tint = NeonPurple,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NexusScreen.AUTOMATION) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HubCard(
                    title = "Memory Store",
                    subtitle = "Local SQLite persistent facts",
                    icon = Icons.Default.Psychology,
                    tint = NeonCyan,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NexusScreen.MEMORY) }
                )
                HubCard(
                    title = "Mobile Repair AI",
                    subtitle = "Diode multimeter values",
                    icon = Icons.Default.Build,
                    tint = NeonEmerald,
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
                    title = "Social Media Assistant",
                    subtitle = "YouTube, TikTok & Insta tags",
                    icon = Icons.Default.Share,
                    tint = NeonPurple,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NexusScreen.SOCIAL_MEDIA) }
                )
                HubCard(
                    title = "Multi-AI Brain",
                    subtitle = "Groq, Gemini, DeepSeek, Local",
                    icon = Icons.Default.Key,
                    tint = NeonCyan,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NexusScreen.API_SETTINGS) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HubCard(
                    title = "Dev Studio & Self-Repair",
                    subtitle = "Diffs & automated tests",
                    icon = Icons.Default.Code,
                    tint = NeonEmerald,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(NexusScreen.DEV_STUDIO) }
                )
                HubCard(
                    title = "Security & Killswitch",
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

