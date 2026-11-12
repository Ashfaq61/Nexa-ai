package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun SocialMediaScreen(onLaunchUrl: (String) -> Unit) {
    val context = LocalContext.current
    var selectedPlatform by remember { mutableStateOf("YouTube") }
    var topicInput by remember { mutableStateOf("Mobile phone dead short circuit repair tutorial") }
    var generatedCaption by remember { mutableStateOf<String?>(null) }
    var generatedHashtags by remember { mutableStateOf<String?>(null) }

    val platforms = listOf("YouTube", "TikTok", "Instagram", "Facebook", "Telegram")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                "AUTONOMOUS SOCIAL MEDIA ASSISTANT",
                color = NeonCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )
            Text(
                "Generate viral captions, tags & descriptions with user confirmation gate",
                color = TextSecondary,
                fontSize = 11.5.sp
            )
        }

        // Platform Selector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                platforms.forEach { p ->
                    FilterChip(
                        selected = selectedPlatform == p,
                        onClick = { selectedPlatform = p },
                        label = { Text(p, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberSurfaceCard,
                            selectedLabelColor = NeonCyan
                        )
                    )
                }
            }
        }

        // Generator Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier.border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Draft Content for $selectedPlatform", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = topicInput,
                        onValueChange = { topicInput = it },
                        label = { Text("Video Topic or Post Idea") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            when (selectedPlatform) {
                                "YouTube" -> {
                                    generatedCaption = "⚡ How to Diagnose & Fix Any Dead Smartphone in 5 Minutes! [Diode Mode Multimeter Guide]\n\nIn this complete step-by-step tutorial, we show you how to detect short circuits on VPH_PWR and VBAT rails using reverse diode-mode testing.\n\n📌 Timestamps:\n0:00 - Introduction to Dead Phone Troubleshooting\n1:15 - Multimeter Diode Mode Ground Rule\n2:40 - Isolating Shorted Capacitors around PM6150\n4:10 - Verification & Boot Test\n\n🔔 Subscribe to Nova Tech for daily hardware repairs!"
                                    generatedHashtags = "#MobileRepair #PhoneFix #HardwareDiagnostic #DeadPhone #TechTutorial #Multimeter"
                                }
                                "TikTok", "Instagram" -> {
                                    generatedCaption = "Stop replacing random ICs before checking diode mode! 🛑 Here is the secret 0.350V test every repair technician needs to know. Drop a comment if this saved your board! 📱✨"
                                    generatedHashtags = "#SmartphoneRepair #MicroSoldering #PhoneHacks #TechFix #Electronics #ViralTech"
                                }
                                else -> {
                                    generatedCaption = "📢 Official NOVA AI Tech Briefing: Advanced component-level diagnosis for Qualcomm and MediaTek mobile boards. Full repair guide published!"
                                    generatedHashtags = "#NovaAI #HardwareRepair #TechnologyUpdate"
                                }
                            }
                            Toast.makeText(context, "Content generated for $selectedPlatform", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Generate Captions & Tags", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Generated Output
        if (generatedCaption != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberBackground),
                    modifier = Modifier.border(1.dp, NeonEmerald.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Generated Draft ($selectedPlatform)", color = NeonEmerald, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                            IconButton(
                                onClick = {
                                    val full = "$generatedCaption\n\n$generatedHashtags"
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                    clipboard?.setPrimaryClip(ClipData.newPlainText("Social Post", full))
                                    Toast.makeText(context, "Copied post to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = NeonEmerald, modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(generatedCaption!!, color = TextPrimary, fontSize = 12.sp, lineHeight = 17.sp)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(generatedHashtags!!, color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = CyberSurface)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Safety Rule: Requires Operator confirmation before publishing.", color = TextSecondary, fontSize = 9.5.sp)
                            Button(
                                onClick = {
                                    val url = when (selectedPlatform) {
                                        "YouTube" -> "https://studio.youtube.com"
                                        "TikTok" -> "https://www.tiktok.com/upload"
                                        "Instagram" -> "https://instagram.com"
                                        "Facebook" -> "https://facebook.com"
                                        else -> "https://t.me"
                                    }
                                    onLaunchUrl(url)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Open $selectedPlatform", color = Color.Black, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
