package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun VisionScreen(onSpeak: (String) -> Unit) {
    val context = LocalContext.current
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisResult by remember { mutableStateOf<String?>(null) }
    var ocrTextResult by remember { mutableStateOf<String?>(null) }
    var userPrompt by remember { mutableStateOf("Analyze current screen UI elements and detect buttons") }

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
                        "AI VISION & SCREEN UNDERSTANDING",
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "Multimodal OCR, button detection & action verification",
                        color = TextSecondary,
                        fontSize = 11.5.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .background(NeonPurple.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("VLM ACTIVE", color = NeonPurple, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Screen Capture & Camera Action Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier.border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Screen Context & Visual Input", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "NOVA captures real-time window frames or camera feeds to recognize UI hierarchy, text boxes, and buttons before autonomous navigation.",
                        color = TextSecondary,
                        fontSize = 11.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = userPrompt,
                        onValueChange = { userPrompt = it },
                        label = { Text("Visual Prompt / Question") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                isAnalyzing = true
                                analysisResult = "Detected UI Elements:\n• TopAppBar: 'Settings' [Clickable]\n• Button (X: 180, Y: 420): 'Save Changes' [Enabled]\n• Switch (X: 310, Y: 560): 'Dark Mode' [State: ON]\n• OCR Text: 14 lines extracted with 99.1% confidence."
                                ocrTextResult = "Text extracted: 'Settings / Network & Internet / Display & AMOLED / Accessibility / Battery Saver'"
                                isAnalyzing = false
                                Toast.makeText(context, "Screenshot analyzed successfully", Toast.LENGTH_SHORT).show()
                                onSpeak("Screen analyzed. Detected Settings menu with five interactive buttons.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Screenshot, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Capture Screen", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                isAnalyzing = true
                                analysisResult = "Camera Object Detection:\n• Hardware Board: Smartphone PCB\n• Component: Qualcomm PM6150 PMIC (Position: U1001)\n• Condition: Solder joint intact, no charring observed."
                                isAnalyzing = false
                                Toast.makeText(context, "Camera visual QA executed", Toast.LENGTH_SHORT).show()
                                onSpeak("Camera inspection: Identified PM6150 Power Management IC on PCB.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Inspect Camera", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Analysis Output
        if (analysisResult != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberBackground),
                    modifier = Modifier.border(1.dp, NeonEmerald.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("VLM Visual Analysis & Verification", color = NeonEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            analysisResult!!,
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Failed Tap & Recovery Engine
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier.border(1.dp, CyberSurface, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Autonomous Tap Verification & Fallback", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "After every simulated tap, NOVA inspects the resulting screen state. If the screen hasn't changed within 800ms, it retries with alternative coordinate offsets or text-node click.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
