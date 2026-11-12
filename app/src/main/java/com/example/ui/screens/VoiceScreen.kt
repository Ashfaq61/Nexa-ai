package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class LiveVoiceState(val label: String, val color: Color) {
    DISCONNECTED("DISCONNECTED / IDLE", TextSecondary),
    CONNECTING("CONNECTING TO GEMINI LIVE...", NeonPurple),
    LISTENING("LISTENING TO YOUR VOICE...", NeonDanger),
    SPEAKING("MAHI IS SPEAKING...", NeonEmerald)
}

@Composable
fun VoiceScreen(
    onSpeak: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var currentState by remember { mutableStateOf(LiveVoiceState.DISCONNECTED) }
    var statusDialogue by remember { mutableStateOf("Tap the central core to connect with Mahi") }
    var selectedVoiceLanguage by remember { mutableStateOf("Bilingual (English + Urdu)") }
    var isMuted by remember { mutableStateOf(false) }
    var executedActionLog by remember { mutableStateOf<String?>(null) }

    // Pulsing animations for glowing core
    val infiniteTransition = rememberInfiniteTransition(label = "mahi_live_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = when (currentState) {
            LiveVoiceState.LISTENING -> 1.28f
            LiveVoiceState.SPEAKING -> 1.15f
            LiveVoiceState.CONNECTING -> 1.10f
            LiveVoiceState.DISCONNECTED -> 1.04f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (currentState) {
                    LiveVoiceState.LISTENING -> 650
                    LiveVoiceState.SPEAKING -> 900
                    LiveVoiceState.CONNECTING -> 1100
                    LiveVoiceState.DISCONNECTED -> 2000
                },
                easing = EaseInOutQuad
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "core_scale"
    )

    // Waveform heights animation during speaking
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = if (currentState == LiveVoiceState.SPEAKING) 48f else 14f,
        animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse),
        label = "wave_1"
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 20f,
        targetValue = if (currentState == LiveVoiceState.SPEAKING) 60f else 18f,
        animationSpec = infiniteRepeatable(tween(420, easing = LinearEasing), RepeatMode.Reverse),
        label = "wave_2"
    )
    val wave3 by infiniteTransition.animateFloat(
        initialValue = 14f,
        targetValue = if (currentState == LiveVoiceState.SPEAKING) 52f else 12f,
        animationSpec = infiniteRepeatable(tween(280, easing = LinearEasing), RepeatMode.Reverse),
        label = "wave_3"
    )

    // Tool execution helper
    fun executeBrowserAction(url: String, actionName: String) {
        executedActionLog = "Function Call: openWebsite('$url') → Executed"
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
            Toast.makeText(context, "Mahi opened $actionName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open $url", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Status Bar: Live State & Connection telemetry
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, currentState.color.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(currentState.color, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "MAHI LIVE 3.1 • AUDIO-TO-AUDIO",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .background(currentState.color.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        currentState.name,
                        color = currentState.color,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Animated Central Voice Core / Mic
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(260.dp)
                .scale(pulseScale)
                .clickable {
                    coroutineScope.launch {
                        when (currentState) {
                            LiveVoiceState.DISCONNECTED -> {
                                currentState = LiveVoiceState.CONNECTING
                                statusDialogue = "Connecting live PCM16 audio stream to Mahi..."
                                delay(1200)

                                currentState = LiveVoiceState.LISTENING
                                statusDialogue = "Mahi is listening! Speak casually in English or Urdu..."
                                delay(2200)

                                currentState = LiveVoiceState.SPEAKING
                                val reply = "Hey handsome! I'm here. See? No boring robot voice — just your favorite witty girl ready to talk. Tell me everything!"
                                statusDialogue = "\"$reply\""
                                onSpeak(reply)
                                delay(4500)

                                currentState = LiveVoiceState.LISTENING
                                statusDialogue = "Listening... say 'Mahi open YouTube' or ask a hardware question!"
                            }
                            LiveVoiceState.SPEAKING -> {
                                // User barge-in interrupt
                                currentState = LiveVoiceState.LISTENING
                                statusDialogue = "Barge-in detected! Mahi stopped speaking to listen to you..."
                                Toast.makeText(context, "Voice interrupted by user", Toast.LENGTH_SHORT).show()
                            }
                            LiveVoiceState.LISTENING -> {
                                currentState = LiveVoiceState.SPEAKING
                                val sarcasticReply = "Arey wah janab! Itni gehri khamoshi? Kuch bolo bhi na, sirf mujhe taadte rahoge? 😉"
                                statusDialogue = "\"$sarcasticReply\""
                                onSpeak(sarcasticReply)
                                delay(3800)
                                currentState = LiveVoiceState.LISTENING
                                statusDialogue = "Listening for your voice command..."
                            }
                            LiveVoiceState.CONNECTING -> {
                                currentState = LiveVoiceState.DISCONNECTED
                                statusDialogue = "Connection cancelled by user."
                            }
                        }
                    }
                }
        ) {
            // Outer glow ring
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(
                                currentState.color.copy(alpha = 0.35f),
                                currentState.color.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
                    .border(2.dp, currentState.color.copy(alpha = 0.5f), CircleShape)
            )

            // Mid ring
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .background(CyberSurfaceCard, CircleShape)
                    .border(3.dp, currentState.color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Waveform bars inside the circle when speaking
                if (currentState == LiveVoiceState.SPEAKING) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.width(4.dp).height(wave1.dp).background(NeonEmerald, RoundedCornerShape(2.dp)))
                        Box(modifier = Modifier.width(4.dp).height(wave2.dp).background(NeonCyan, RoundedCornerShape(2.dp)))
                        Box(modifier = Modifier.width(4.dp).height(wave3.dp).background(NeonEmerald, RoundedCornerShape(2.dp)))
                        Box(modifier = Modifier.width(4.dp).height(wave1.dp).background(NeonPurple, RoundedCornerShape(2.dp)))
                    }
                } else {
                    Icon(
                        when (currentState) {
                            LiveVoiceState.DISCONNECTED -> Icons.Default.PowerSettingsNew
                            LiveVoiceState.CONNECTING -> Icons.Default.Sync
                            LiveVoiceState.LISTENING -> Icons.Default.Mic
                            LiveVoiceState.SPEAKING -> Icons.Default.GraphicEq
                        },
                        contentDescription = "Voice State",
                        tint = if (currentState == LiveVoiceState.DISCONNECTED) TextSecondary else Color.White,
                        modifier = Modifier.size(72.dp)
                    )
                }
            }
        }

        // Live Dialogue Transcript & Function Log
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                currentState.label,
                color = currentState.color,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberSurface, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        statusDialogue,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    if (executedActionLog != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            executedActionLog!!,
                            color = NeonEmerald,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Function Calling Quick-Tools
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilledTonalButton(
                    onClick = { executeBrowserAction("https://youtube.com", "YouTube") },
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = CyberSurfaceCard)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = NeonDanger, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("YouTube", fontSize = 11.sp)
                }

                FilledTonalButton(
                    onClick = { executeBrowserAction("https://github.com", "GitHub") },
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = CyberSurfaceCard)
                ) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("GitHub", fontSize = 11.sp)
                }

                FilledTonalButton(
                    onClick = {
                        val sassyLine = "Arey suno! Flirting band karo aur kaam ki baat batao! What do you need, my charming operator? 😉"
                        statusDialogue = "\"$sassyLine\""
                        onSpeak(sassyLine)
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = CyberSurfaceCard)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tease Me", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Control Action: Disconnect / Connect
            Button(
                onClick = {
                    if (currentState != LiveVoiceState.DISCONNECTED) {
                        currentState = LiveVoiceState.DISCONNECTED
                        statusDialogue = "Live voice session disconnected. Tap to reconnect."
                    } else {
                        currentState = LiveVoiceState.CONNECTING
                        coroutineScope.launch {
                            delay(1000)
                            currentState = LiveVoiceState.LISTENING
                            val greeting = "Mahi is connected! What's up, handsome? Speak to me!"
                            statusDialogue = "\"$greeting\""
                            onSpeak(greeting)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentState != LiveVoiceState.DISCONNECTED) NeonDanger else NeonCyan
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text(
                    if (currentState != LiveVoiceState.DISCONNECTED) "END LIVE SESSION" else "START GEMINI LIVE SESSION",
                    color = if (currentState != LiveVoiceState.DISCONNECTED) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
