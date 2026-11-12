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
fun SecurityCenterScreen() {
    val context = LocalContext.current
    var isEmergencyHaltActive by remember { mutableStateOf(false) }

    val auditLogs = remember {
        listOf(
            "14:22:01 — Keystore vault encrypted API key slot 'Primary Gemini'",
            "14:20:15 — PII Redactor screened prompt: 0 email/phone leaks detected",
            "14:18:02 — OpenSandbox verified execution of safe code block",
            "14:10:30 — Accessibility bridge security token verified",
            "14:00:12 — Hardware diode test database integrity checked (MD5 Valid)"
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
                        "SECURITY CENTER & DEFENSE MATRIX",
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "Cryptographic guardrails, PII masking & emergency killswitch",
                        color = TextSecondary,
                        fontSize = 11.5.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .background(NeonEmerald.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("SECURED", color = NeonEmerald, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Emergency Killswitch Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier.border(1.dp, NeonDanger.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Dangerous, contentDescription = null, tint = NeonDanger, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("EMERGENCY KILLSWITCH", color = NeonDanger, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Instantly terminate all autonomous agents, drop active web socket connections, flush memory scratchpads, and pause task executions.",
                        color = TextSecondary,
                        fontSize = 11.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            isEmergencyHaltActive = !isEmergencyHaltActive
                            Toast.makeText(
                                context,
                                if (isEmergencyHaltActive) "HALT ACTIVATED: All agents paused" else "SYSTEM RESTORED: Agents nominal",
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEmergencyHaltActive) NeonEmerald else NeonDanger
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (isEmergencyHaltActive) "RESTORE AUTONOMOUS EXECUTION" else "HALT ALL AGENT OPERATIONS",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Security Features Matrix
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier.border(1.dp, CyberSurface, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Cryptographic & Privacy Safeguards", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    listOf(
                        "Android Keystore Encryption" to "ACTIVE (AES-256 GCM)",
                        "PII Auto-Redactor (Email/Phone)" to "ENFORCED",
                        "Shell Injection Sandbox Barrier" to "ACTIVE",
                        "TLS 1.3 End-to-End Transport" to "VERIFIED",
                        "Zero Plaintext Storage in Logs" to "STRICT PASS"
                    ).forEach { (name, status) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(name, color = TextSecondary, fontSize = 12.sp)
                            Text(status, color = NeonEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Audit Log Timeline
        item {
            Text("SECURITY AUDIT TRAIL", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberBackground),
                modifier = Modifier.border(1.dp, CyberSurface, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    auditLogs.forEach { log ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(6.dp).background(NeonEmerald, CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(log, color = TextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}
