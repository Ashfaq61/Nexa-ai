package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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

enum class NovaVoiceState(val label: String, val primaryColor: Color, val secondaryColor: Color) {
    IDLE("IDLE / READY", NeonCyan, NeonPurple),
    CONNECTING("CONNECTING TO LIVE STREAM...", NeonPurple, NeonCyan),
    LISTENING("LISTENING (VOICE ACTIVE)...", NeonDanger, NeonPurple),
    SPEAKING("NOVA IS SPEAKING...", NeonEmerald, NeonCyan)
}

data class VoiceProfile(val id: String, val name: String, val gender: String, val language: String)

@Composable
fun VoiceScreen(
    onSpeak: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var voiceState by remember { mutableStateOf(NovaVoiceState.IDLE) }
    var statusText by remember { mutableStateOf("Say 'Hey Nova' or tap the glowing orb to begin") }
    var selectedLanguage by remember { mutableStateOf("Auto (Urdu / Hindi / English)") }
    var isWakeWordActive by remember { mutableStateOf(true) }
    var executedActionLog by remember { mutableStateOf<String?>(null) }

    // 8 Selectable Voices
    val voices = remember {
        listOf(
            VoiceProfile("nova", "Nova", "Female", "English/Urdu/Hindi"),
            VoiceProfile("aoede", "Aoede", "Female", "Energetic English"),
            VoiceProfile("kore", "Kore", "Female", "Calm & Warm"),
            VoiceProfile("zoya", "Zoya", "Female", "Urdu/Hindi Melodic"),
            VoiceProfile("fenrir", "Fenrir", "Male", "Deep & Authoritative"),
            VoiceProfile("puck", "Puck", "Male", "Playful & Fast"),
            VoiceProfile("charon", "Charon", "Male", "Balanced Technical"),
            VoiceProfile("kabir", "Kabir", "Male", "Hindi/Urdu Baritone")
        )
    }
    var selectedVoice by remember { mutableStateOf(voices.first()) }

    // Pulsing animations for glowing core
    val infiniteTransition = rememberInfiniteTransition(label = "nova_live_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = when (voiceState) {
            NovaVoiceState.LISTENING -> 1.26f
            NovaVoiceState.SPEAKING -> 1.15f
            NovaVoiceState.CONNECTING -> 1.10f
            NovaVoiceState.IDLE -> 1.05f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (voiceState) {
                    NovaVoiceState.LISTENING -> 650
                    NovaVoiceState.SPEAKING -> 900
                    NovaVoiceState.CONNECTING -> 1100
                    NovaVoiceState.IDLE -> 2000
                },
                easing = EaseInOutQuad
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "core_scale"
    )

    // Waveform heights animation during speaking
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = if (voiceState == NovaVoiceState.SPEAKING) 46f else 12f,
        animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse),
        label = "wave_1"
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 16f,
        targetValue = if (voiceState == NovaVoiceState.SPEAKING) 58f else 16f,
        animationSpec = infiniteRepeatable(tween(420, easing = LinearEasing), RepeatMode.Reverse),
        label = "wave_2"
    )
    val wave3 by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = if (voiceState == NovaVoiceState.SPEAKING) 50f else 10f,
        animationSpec = infiniteRepeatable(tween(280, easing = LinearEasing), RepeatMode.Reverse),
        label = "wave_3"
    )

    // Avatar halo glow animation
    val haloRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing)),
        label = "halo_rotate"
    )

    fun executeQuickTool(url: String, actionName: String) {
        executedActionLog = "Executed tool: openWebsite('$url')"
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
            Toast.makeText(context, "Opened $actionName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Could not launch $url", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header: Animated Avatar & Live Telemetry
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NeonPurple.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mini Female AI Avatar Icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                Brush.linearGradient(listOf(NeonCyan, NeonPurple)),
                                CircleShape
                            )
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Face,
                            contentDescription = "Nova Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("NOVA V7.0 LIVE", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Voice: ${selectedVoice.name} (${selectedVoice.gender})", color = TextSecondary, fontSize = 10.5.sp)
                    }
                }

                // VAD / Wake word chip
                AssistChip(
                    onClick = {
                        isWakeWordActive = !isWakeWordActive
                        Toast.makeText(context, if (isWakeWordActive) "Wake-Word 'Hey Nova' Active" else "Wake-Word Inactive", Toast.LENGTH_SHORT).show()
                    },
                    label = { Text(if (isWakeWordActive) "'Hey Nova' ON" else "VAD Manual", fontSize = 10.5.sp, color = NeonEmerald) },
                    leadingIcon = { Icon(Icons.Default.Hearing, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(13.dp)) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = CyberSurface)
                )
            }
        }

        // Central Glowing Blue & Purple Voice Orb
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(250.dp)
                .scale(pulseScale)
                .clickable {
                    coroutineScope.launch {
                        when (voiceState) {
                            NovaVoiceState.IDLE -> {
                                voiceState = NovaVoiceState.CONNECTING
                                statusText = "Connecting real-time bidirectional audio stream..."
                                delay(900)

                                voiceState = NovaVoiceState.LISTENING
                                statusText = "Listening for your voice in English, Urdu, or Hindi..."
                                delay(2200)

                                voiceState = NovaVoiceState.SPEAKING
                                val reply = "Salam Operator! NOVA AI Version 7.0 is active. Voice streaming nominal, 10-agent orchestrator connected, and mobile phone control online."
                                statusText = "\"$reply\""
                                onSpeak(reply)
                                delay(4400)

                                voiceState = NovaVoiceState.LISTENING
                                statusText = "Listening... say 'Open YouTube', ask a hardware question, or command phone controls!"
                            }
                            NovaVoiceState.SPEAKING -> {
                                // Instant barge-in / interrupt
                                voiceState = NovaVoiceState.LISTENING
                                statusText = "Barge-in detected: NOVA interrupted to listen to you..."
                                Toast.makeText(context, "Voice interrupted by user", Toast.LENGTH_SHORT).show()
                            }
                            NovaVoiceState.LISTENING -> {
                                voiceState = NovaVoiceState.SPEAKING
                                val quickReply = "Ji Janab! Main aapki aawaz sun rahi hoon. Chaliye batayein, aaj kaunsa task execute karna hai? ✨"
                                statusText = "\"$quickReply\""
                                onSpeak(quickReply)
                                delay(3600)
                                voiceState = NovaVoiceState.LISTENING
                                statusText = "Listening for next voice command..."
                            }
                            NovaVoiceState.CONNECTING -> {
                                voiceState = NovaVoiceState.IDLE
                                statusText = "Cancelled. Tap to reconnect."
                            }
                        }
                    }
                }
        ) {
            // Glowing Blue & Purple Radial Ambient Halo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(
                                NeonPurple.copy(alpha = 0.40f),
                                NeonCyan.copy(alpha = 0.20f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
                    .border(2.dp, NeonCyan.copy(alpha = 0.5f), CircleShape)
            )

            // Inner Core with Waveform or Mic
            Box(
                modifier = Modifier
                    .size(175.dp)
                    .background(CyberSurfaceCard, CircleShape)
                    .border(
                        3.dp,
                        Brush.sweepGradient(listOf(NeonCyan, NeonPurple, NeonEmerald, NeonCyan)),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (voiceState == NovaVoiceState.SPEAKING) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.width(4.dp).height(wave1.dp).background(NeonCyan, RoundedCornerShape(2.dp)))
                        Box(modifier = Modifier.width(4.dp).height(wave2.dp).background(NeonEmerald, RoundedCornerShape(2.dp)))
                        Box(modifier = Modifier.width(4.dp).height(wave3.dp).background(NeonPurple, RoundedCornerShape(2.dp)))
                        Box(modifier = Modifier.width(4.dp).height(wave1.dp).background(NeonCyan, RoundedCornerShape(2.dp)))
                        Box(modifier = Modifier.width(4.dp).height(wave2.dp).background(NeonEmerald, RoundedCornerShape(2.dp)))
                    }
                } else {
                    Icon(
                        when (voiceState) {
                            NovaVoiceState.IDLE -> Icons.Default.Mic
                            NovaVoiceState.CONNECTING -> Icons.Default.Sync
                            NovaVoiceState.LISTENING -> Icons.Default.GraphicEq
                            NovaVoiceState.SPEAKING -> Icons.Default.VolumeUp
                        },
                        contentDescription = "Voice State",
                        tint = if (voiceState == NovaVoiceState.IDLE) NeonCyan else Color.White,
                        modifier = Modifier.size(68.dp)
                    )
                }
            }
        }

        // Voice Selector Strip (8 Male & Female Voices)
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("SELECT ASSISTANT VOICE (8 PROFILES)", color = TextSecondary, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                voices.forEach { v ->
                    FilterChip(
                        selected = selectedVoice == v,
                        onClick = {
                            selectedVoice = v
                            Toast.makeText(context, "Voice changed to ${v.name} (${v.gender})", Toast.LENGTH_SHORT).show()
                        },
                        label = { Text("${v.name} (${v.gender[0]})", fontSize = 10.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberSurfaceCard,
                            selectedLabelColor = NeonCyan
                        )
                    )
                }
            }
        }

        // Dialogue Transcript & Actions
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberSurface, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        statusText,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    if (executedActionLog != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            executedActionLog!!,
                            color = NeonEmerald,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick function actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = { executeQuickTool("https://youtube.com", "YouTube") },
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = CyberSurfaceCard),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = NeonDanger, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("YouTube", fontSize = 11.sp)
                }

                FilledTonalButton(
                    onClick = {
                        val urduDemo = "Salam Operator! Main NOVA AI hoon. Main Urdu, Hindi aur English teeno zabaano mein baat kar sakti hoon."
                        statusText = "\"$urduDemo\""
                        onSpeak(urduDemo)
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = CyberSurfaceCard),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Translate, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Urdu Vocal", fontSize = 11.sp)
                }

                FilledTonalButton(
                    onClick = {
                        val hardwareTip = "Dead phone diagnostic rule: Set multimeter to diode mode. Connect Red probe to Ground and Black probe to VBAT. Normal reading is 0.350 to 0.450 Volts."
                        statusText = "\"$hardwareTip\""
                        onSpeak(hardwareTip)
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = CyberSurfaceCard),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Build, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hardware Tip", fontSize = 11.sp)
                }
            }
        }
    }
}
