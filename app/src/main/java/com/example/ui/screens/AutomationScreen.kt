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

data class RoutineItem(
    val id: String,
    val title: String,
    val trigger: String,
    val action: String,
    var isEnabled: Boolean = true
)

@Composable
fun AutomationScreen(onSpeak: (String) -> Unit) {
    val context = LocalContext.current

    val routines = remember {
        mutableStateListOf(
            RoutineItem("1", "Morning AI Intelligence Briefing", "Daily at 08:00 AM", "Fetch trending GitHub agents & vocalize 60-sec summary"),
            RoutineItem("2", "Battery Health & Low-Power Guardian", "When battery drops below 20%", "Activate battery saver & vocalize recharge alert"),
            RoutineItem("3", "Unread Notification Digest", "Every 2 hours while screen off", "Summarize WhatsApp & SMS messages into short audio briefing"),
            RoutineItem("4", "Nightly Security & Cache Cleaner", "Daily at 02:00 AM", "Clear transient memory buffers & audit Keystore encryption status")
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
                        "AUTONOMOUS SCHEDULER & ROUTINES",
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "WorkManager background tasks, battery triggers & digests",
                        color = TextSecondary,
                        fontSize = 11.5.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .background(NeonEmerald.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("WORKMANAGER ACTIVE", color = NeonEmerald, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Daily briefing trigger now card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier.border(1.dp, NeonEmerald.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Trigger On-Demand Daily Briefing", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Simulate morning voice digest right now", color = TextSecondary, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                val briefingText = "Good morning Operator! All systems nominal. Battery at 85%, three unread WhatsApp messages queued, and two trending agent frameworks discovered on GitHub."
                                onSpeak(briefingText)
                                Toast.makeText(context, "Vocalizing Daily Briefing", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Play Briefing", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Text("CONFIGURED RECURRING ROUTINES", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        items(routines) { item ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (item.isEnabled) NeonCyan.copy(alpha = 0.25f) else CyberSurface, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                        Switch(
                            checked = item.isEnabled,
                            onCheckedChange = {
                                val idx = routines.indexOf(item)
                                if (idx != -1) {
                                    routines[idx] = item.copy(isEnabled = it)
                                }
                                Toast.makeText(context, "${item.title}: ${if (it) "Enabled" else "Disabled"}", Toast.LENGTH_SHORT).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonCyan,
                                checkedTrackColor = CyberSurfaceCard,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = CyberBackground
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Trigger: ${item.trigger}", color = NeonPurple, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Action: ${item.action}", color = TextSecondary, fontSize = 11.5.sp)
                }
            }
        }
    }
}
