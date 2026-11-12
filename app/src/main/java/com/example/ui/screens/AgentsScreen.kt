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

data class AgentItem(
    val id: String,
    val name: String,
    val role: String,
    val status: String,
    val capabilities: List<String>,
    val successRate: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun AgentsScreen() {
    val context = LocalContext.current
    var recoveryLog by remember { mutableStateOf<String?>(null) }

    val agents = remember {
        listOf(
            AgentItem("master", "Master Agent", "Autonomous orchestration and consensus coordination", "ONLINE", listOf("Task routing", "Agent consensus", "Goal evaluation"), "99.2%", Icons.Default.Hub),
            AgentItem("research", "Research Agent", "GitHub, Hugging Face and real-time knowledge scouting", "ACTIVE", listOf("GitHub MCP", "HuggingFace API", "License vetting"), "97.5%", Icons.Default.TravelExplore),
            AgentItem("planning", "Planning Agent", "Task decomposition, dependency graphs and milestone tracking", "ACTIVE", listOf("Decomposition", "DAG resolution", "Dynamic replanning"), "98.1%", Icons.Default.AccountTree),
            AgentItem("execution", "Execution Agent", "Deterministic sandbox execution and mobile control triggers", "ACTIVE", listOf("Android Actions", "Accessibility triggers", "Intent dispatch"), "96.8%", Icons.Default.PlayCircle),
            AgentItem("coding", "Coding Agent", "Source code inspection, diff generation and refactoring", "ACTIVE", listOf("Syntax analysis", "Unified diff generator", "Refactoring"), "95.4%", Icons.Default.Code),
            AgentItem("testing", "Testing Agent", "Test runner, regression validator and static analysis", "ACTIVE", listOf("Pytest execution", "Flutter analysis", "Gradle check"), "98.9%", Icons.Default.FactCheck),
            AgentItem("security", "Security Agent", "Cryptographic guardrails, PII redactor and keystore protector", "ACTIVE", listOf("PII redaction", "API key masking", "Permission audit"), "100.0%", Icons.Default.Security),
            AgentItem("memory", "Memory Agent", "Episodic, working and long-term Hindsight memory manager", "ACTIVE", listOf("Semantic search", "Vector indexing", "Context compression"), "99.0%", Icons.Default.Psychology),
            AgentItem("recovery", "Recovery Agent", "Self-healing, automated fallback routing and crash prevention", "ACTIVE", listOf("Circuit breaker", "Provider fallback", "State rollback"), "97.8%", Icons.Default.Healing)
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
                        "9-AGENT AUTONOMOUS SYSTEM",
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "Specialized agent federation with consensus validation",
                        color = TextSecondary,
                        fontSize = 11.5.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .background(NeonEmerald.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("9/9 HEALTHY", color = NeonEmerald, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Recovery / Self-Healing Trigger Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier.border(1.dp, NeonPurple.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Healing, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Autonomous Self-Healing Agent", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                        }

                        Button(
                            onClick = {
                                recoveryLog = "Self-Healing Cycle completed. Cache flushed, model router failover tested, memory indexes optimized."
                                Toast.makeText(context, "Recovery Agent: All 9 agents synchronized", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Trigger Self-Heal", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (recoveryLog != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            recoveryLog!!,
                            color = NeonEmerald,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Autonomous 9-Step Improvement Cycle Visualizer
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                modifier = Modifier.border(1.dp, NeonCyan.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("9-STEP AUTONOMOUS CYCLE:", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "DISCOVER → ANALYZE → PLAN → PROPOSE → APPROVE → IMPLEMENT → TEST → VERIFY → REPORT",
                        color = TextPrimary,
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Agents List
        items(agents) { agent ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonCyan.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(CyberSurface, CircleShape)
                                    .border(1.dp, NeonCyan.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(agent.icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(agent.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                Text(agent.role, color = TextSecondary, fontSize = 11.sp)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(NeonEmerald.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(agent.status, color = NeonEmerald, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            agent.capabilities.forEach { cap ->
                                Box(
                                    modifier = Modifier
                                        .background(CyberBackground, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(cap, color = TextSecondary, fontSize = 9.sp)
                                }
                            }
                        }
                        Text("Accuracy: ${agent.successRate}", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
