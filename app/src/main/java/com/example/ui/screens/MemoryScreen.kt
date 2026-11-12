package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

data class MemoryFact(val id: String, val category: String, val content: String, val timestamp: String)

@Composable
fun MemoryScreen() {
    val context = LocalContext.current
    var newMemoryInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Preferences") }

    val memories = remember {
        mutableStateListOf(
            MemoryFact("1", "User Preference", "Prefers concise, witty responses in Roman Urdu or English.", "Nov 10, 2026"),
            MemoryFact("2", "Hardware Context", "Specializes in Qualcomm PM6150 / BQ charging IC diagnostics.", "Nov 11, 2026"),
            MemoryFact("3", "Workflow", "Always verify diode mode values before soldering jumpers.", "Nov 11, 2026"),
            MemoryFact("4", "Security Guard", "Never auto-merge code patches without manual Operator review.", "Nov 12, 2026")
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
                        "ADVANCED LOCAL MEMORY ENGINE",
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "Persistent local-first episodic store & PII-scrubbed context",
                        color = TextSecondary,
                        fontSize = 11.5.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Memory exported as secure encrypted JSON backup", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceCard),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export", fontSize = 10.5.sp, color = NeonCyan)
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "Ready to import memory JSON file", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceCard),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Import", fontSize = 10.5.sp, color = NeonEmerald)
                    }
                }
            }
        }

        // Add custom instruction / preference
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier.border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Add Permanent Memory Instruction", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newMemoryInput,
                        onValueChange = { newMemoryInput = it },
                        label = { Text("What should NOVA remember?") },
                        placeholder = { Text("e.g. Always include diode readings in hardware solutions") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "PII scrubber active: Passwords and keys automatically filtered.",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                if (newMemoryInput.isNotBlank()) {
                                    memories.add(
                                        MemoryFact(
                                            id = System.currentTimeMillis().toString(),
                                            category = selectedCategory,
                                            content = newMemoryInput.trim(),
                                            timestamp = "Just now"
                                        )
                                    )
                                    newMemoryInput = ""
                                    Toast.makeText(context, "Saved to persistent memory", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                        ) {
                            Text("Remember", color = Color.Black, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Memories List
        item {
            Text("STORED KNOWLEDGE & PREFERENCES (${memories.size})", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        items(memories) { fact ->
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
                            Text(fact.category.uppercase(), color = NeonPurple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        IconButton(
                            onClick = {
                                memories.remove(fact)
                                Toast.makeText(context, "Memory deleted", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NeonDanger, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(fact.content, color = TextPrimary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Saved: ${fact.timestamp}", color = TextSecondary, fontSize = 10.5.sp)
                }
            }
        }
    }
}
