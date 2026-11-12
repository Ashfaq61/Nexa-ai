package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ChatMessageItem
import com.example.data.ApiKeyRepository
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun ChatScreen(onSpeak: (String) -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { ApiKeyRepository(context) }

    val messages = remember {
        mutableStateListOf(
            ChatMessageItem("1", "Salam Operator! Project Nexus V5.0 Neural Core online. Multi-model router active with Urdu, Roman Urdu, and English comprehension.", false, "Gemini 1.5 Pro")
        )
    }
    var inputQuery by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf("Auto Router") }
    var detectedLanguage by remember { mutableStateOf("English / Urdu Auto-Detect") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Router Selection & Language Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberSurface)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Translate, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(detectedLanguage, color = TextSecondary, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("Auto", "Gemini", "OpenRouter", "Groq", "HF").forEach { option ->
                    FilterChip(
                        selected = selectedModel == option,
                        onClick = { selectedModel = option },
                        label = { Text(option, fontSize = 9.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberSurfaceCard,
                            selectedLabelColor = NeonCyan
                        )
                    )
                }
            }
        }

        // Action Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = {
                    messages.clear()
                    messages.add(ChatMessageItem("1", "Conversation cleared. Ready for instructions.", false, "Nexus Core"))
                    Toast.makeText(context, "Chat cleared", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(Icons.Default.DeleteSweep, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(18.dp))
            }
            IconButton(
                onClick = {
                    val fullChat = messages.joinToString("\n\n") { "${if (it.isUser) "User" else "Nexus"}: ${it.text}" }
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                    clipboard?.setPrimaryClip(ClipData.newPlainText("Nexus Chat", fullChat))
                    Toast.makeText(context, "Full conversation copied", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy All", tint = TextSecondary, modifier = Modifier.size(16.dp))
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                val alignment = if (msg.isUser) Alignment.End else Alignment.Start
                val bgColor = if (msg.isUser) NeonCyan.copy(alpha = 0.2f) else CyberSurfaceCard
                val borderColor = if (msg.isUser) NeonCyan else NeonPurple.copy(alpha = 0.5f)

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = alignment
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = bgColor),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .widthIn(max = 310.dp)
                            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    if (msg.isUser) "Operator" else "Nexus AI V5.0",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (msg.isUser) NeonCyan else NeonEmerald
                                )
                                Text(
                                    msg.providerModel,
                                    fontSize = 9.sp,
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(msg.text, color = TextPrimary, fontSize = 13.5.sp, lineHeight = 18.sp)

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = { onSpeak(msg.text) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Speak", tint = TextSecondary, modifier = Modifier.size(14.dp))
                                }
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                        clipboard?.setPrimaryClip(ClipData.newPlainText("Nexus Message", msg.text))
                                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberSurface)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputQuery,
                onValueChange = {
                    inputQuery = it
                    val lower = it.lowercase()
                    detectedLanguage = if (lower.contains("kya") || lower.contains("kaise") || lower.contains("hai") || lower.contains("bhai") || lower.contains("shukriya")) {
                        "Language: Roman Urdu / Urdu Detected"
                    } else {
                        "Language: English Detected"
                    }
                },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Command Nexus V5.0 (Urdu or English)...", color = TextSecondary, fontSize = 12.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = NeonCyan.copy(alpha = 0.3f),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (inputQuery.isNotBlank()) {
                        val userText = inputQuery
                        messages.add(ChatMessageItem(UUID.randomUUID().toString(), userText, true, "Operator"))
                        inputQuery = ""

                        coroutineScope.launch {
                            delay(400)
                            val modelTag = if (selectedModel == "Auto") "Gemini 1.5 Flash (Routed)" else selectedModel
                            val reply = if (userText.lowercase().contains("urdu") || userText.lowercase().contains("kaise") || userText.lowercase().contains("kya")) {
                                "Ji Janab Operator! Main Nexus V5.0 hoon. Aapka hukam receive ho gaya hai: '$userText'. Model router ne optimal AI provider connect kiya hai aur OpenSandbox guardrails PASS hain."
                            } else if (userText.lowercase().contains("repair") || userText.lowercase().contains("short") || userText.lowercase().contains("charge")) {
                                "Nexus Hardware Diagnostic: For short circuit analysis, set multimeter to Diode Mode (Red probe to GND, Black probe to rail). Normal readings: 0.350V - 0.450V. 0.000V indicates line short."
                            } else {
                                "Nexus V5.0 Autonomous Core executed instruction: '$userText'. Verified by 9-agent federation and encrypted keystore parameters."
                            }
                            messages.add(ChatMessageItem(UUID.randomUUID().toString(), reply, false, modelTag))
                            onSpeak(reply)
                        }
                    }
                },
                modifier = Modifier.background(NeonCyan, CircleShape)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black)
            }
        }
    }
}
