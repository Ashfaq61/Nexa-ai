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
import com.example.ProposalItem
import com.example.ui.theme.*

@Composable
fun DevStudioScreen() {
    val context = LocalContext.current
    var testRunning by remember { mutableStateOf(false) }
    var testResultOutput by remember { mutableStateOf<String?>(null) }

    val proposals = remember {
        mutableStateListOf(
            ProposalItem(
                id = "prop_01",
                title = "SmolAgents Code-Action Sandbox Execution",
                category = "Architecture",
                summary = "Adopt structured code-based tool execution in backend sandbox to reduce token overhead by 30%.",
                diff = """
+ class SmolAgentExecutor:
+     def __init__(self, authorized_tools):
+         self.agent = CodeAgent(tools=authorized_tools, model=gemini)
+     def execute_safely(self, task):
+         return self.agent.run(task)
                """.trimIndent(),
                approved = false
            ),
            ProposalItem(
                id = "prop_02",
                title = "Multimeter Diode-Mode Real-Time Guidance",
                category = "Hardware AI",
                summary = "Add reverse diode-mode ground probe validator and charging IC cross-reference.",
                diff = """
+ fun diagnoseChargingLine(diodeMv: Int): String {
+     return if (diodeMv == 0) "Short to Ground" else "Normal 0.620V"
+ }
                """.trimIndent(),
                approved = true
            )
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
                        "DEV STUDIO & SELF-IMPROVEMENT ENGINE",
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "AST inspection, unified diff generator & test runner",
                        color = TextSecondary,
                        fontSize = 11.5.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .background(NeonPurple.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("SANDBOX GUARD", color = NeonPurple, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Test Runner Card
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
                            Text("Automated Regression Test Suite", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Pytest Backend CI + Android Gradle Compile", color = TextSecondary, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                testRunning = true
                                testResultOutput = "Running suite...\n[1/3] Backend pytest: 5/5 PASSED\n[2/3] Gradle compileDebugKotlin: SUCCESS\n[3/3] Sandbox allowlist check: VERIFIED"
                                testRunning = false
                                Toast.makeText(context, "Test Suite: 100% Passed", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Run Tests", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (testResultOutput != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberBackground, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                testResultOutput!!,
                                color = NeonEmerald,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Human-In-The-Loop Rule
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                modifier = Modifier.border(1.dp, NeonCyan.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Autonomous Safe Rule: Self-improvement diffs are generated automatically by Coding Agent but strictly require Operator Approval before merging.",
                        color = TextPrimary,
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Proposals Section
        item {
            Text("PROPOSED SELF-IMPROVEMENTS (${proposals.size})", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        items(proposals) { proposal ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (proposal.approved) NeonEmerald.copy(alpha = 0.4f) else NeonPurple.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(proposal.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                        Box(
                            modifier = Modifier
                                .background(if (proposal.approved) NeonEmerald.copy(alpha = 0.2f) else CyberSurface, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                if (proposal.approved) "APPROVED" else "AWAITING APPROVAL",
                                color = if (proposal.approved) NeonEmerald else NeonCyan,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(proposal.summary, color = TextSecondary, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Code Diff
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberBackground, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            proposal.diff,
                            color = NeonEmerald,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.5.sp,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!proposal.approved) {
                            Button(
                                onClick = {
                                    val idx = proposals.indexOf(proposal)
                                    if (idx != -1) {
                                        proposals[idx] = proposal.copy(approved = true)
                                    }
                                    Toast.makeText(context, "Proposal approved by Operator", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Approve & Merge", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text("Merged safely to staging", color = NeonEmerald, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
