package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VoiceScreen(onSpeak: (String) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var voiceState by remember { mutableStateOf("IDLE") } // IDLE, LISTENING, PROCESSING, SPEAKING
    var statusText by remember { mutableStateOf("Tap the Orb or say 'Hey Nexus'") }
    var selectedLanguage by remember { mutableStateOf("English / Urdu") }
    var latencyMs by remember { mutableStateOf(85) }

    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (voiceState == "LISTENING") 1.25f else 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (voiceState == "LISTENING") 700 else 1800, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Router & Health Bar
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AltRoute, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Auto (Gemini 1.5 Flash Live)", color = TextPrimary, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
                Text("LATENCY: ${latencyMs}ms", color = NeonEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Animated Voice Orb
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(240.dp)
                .scale(pulseScale)
                .clickable {
                    coroutineScope.launch {
                        if (voiceState == "IDLE") {
                            voiceState = "LISTENING"
                            statusText = "Listening for audio stream (Urdu/English)..."
                            delay(2000)
                            voiceState = "PROCESSING"
                            statusText = "Multi-Model Router analyzing query..."
                            delay(1400)
                            voiceState = "SPEAKING"
                            val reply = "Greetings Operator. Project Nexus V5.0 is active. Model Router selected Gemini 1.5 Flash, 9-agent federation verified, and keystore security confirmed."
                            statusText = reply
                            onSpeak(reply)
                            delay(4500)
                            voiceState = "IDLE"
                            statusText = "Tap the Orb or say 'Hey Nexus'"
                        } else {
                            voiceState = "IDLE"
                            statusText = "Voice session interrupted by user barge-in."
                        }
                    }
                }
        ) {
            val orbColor = when (voiceState) {
                "LISTENING" -> NeonDanger
                "PROCESSING" -> NeonPurple
                "SPEAKING" -> NeonEmerald
                else -> NeonCyan
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(orbColor.copy(alpha = 0.15f), CircleShape)
                    .border(2.dp, orbColor.copy(alpha = 0.6f), CircleShape)
            )

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(orbColor.copy(alpha = 0.35f), CircleShape)
                    .border(3.dp, orbColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when (voiceState) {
                        "LISTENING" -> Icons.Default.Mic
                        "PROCESSING" -> Icons.Default.GraphicEq
                        "SPEAKING" -> Icons.Default.VolumeUp
                        else -> Icons.Default.AutoAwesome
                    },
                    contentDescription = "Voice State",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        // Voice Feedback & Controls
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                voiceState,
                color = when (voiceState) {
                    "LISTENING" -> NeonDanger
                    "PROCESSING" -> NeonPurple
                    "SPEAKING" -> NeonEmerald
                    else -> NeonCyan
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                statusText,
                color = TextPrimary,
                fontSize = 13.5.sp,
                maxLines = 3
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = {
                        selectedLanguage = if (selectedLanguage.contains("Urdu")) "English Only" else "English / Urdu"
                    },
                    label = { Text(selectedLanguage, fontSize = 11.5.sp) },
                    leadingIcon = { Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(14.dp)) }
                )
                AssistChip(
                    onClick = {
                        onSpeak("Salam Operator. Project Nexus V5.0 aapki service ke liye hazir hai. Tamam autonomous modules aur mobile hardware diagnostics active hain.")
                    },
                    label = { Text("Test Urdu Vocal", fontSize = 11.5.sp) }
                )
            }
        }
    }
}
