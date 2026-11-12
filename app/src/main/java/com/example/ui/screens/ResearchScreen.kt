package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.DiscoveredTool
import com.example.ui.theme.*

@Composable
fun ResearchScreen(onLaunchIntent: (String) -> Unit) {
    val tools = remember {
        listOf(
            DiscoveredTool("Browser Use / Playwright MCP", "Automation", "Autonomous website navigation, element extraction and form execution.", "Ready", "https://github.com/browser-use/browser-use"),
            DiscoveredTool("Langfuse & OpenTelemetry", "Observability", "Production tracing, latency profiling, and Promptfoo evaluation metrics.", "Active", "https://github.com/langfuse/langfuse"),
            DiscoveredTool("Hindsight Memory Engine", "Memory", "Semantic long-term memory, privacy scrubbing and duplicate elimination.", "Active", "https://github.com/vector-memory/hindsight"),
            DiscoveredTool("OpenSandbox", "Security", "Isolated container sandbox preventing arbitrary shell execution.", "Active", "https://github.com/sandbox/opensandbox"),
            DiscoveredTool("CrewAI", "GitHub", "Role-playing multi-agent team orchestration.", "24,500 ⭐", "https://github.com/crewAIInc/crewAI"),
            DiscoveredTool("SmolAgents", "GitHub", "Lightweight code-action agents by Hugging Face.", "16,800 ⭐", "https://github.com/huggingface/smolagents"),
            DiscoveredTool("LangGraph", "GitHub", "Stateful cyclical agent workflows.", "19,400 ⭐", "https://github.com/langchain-ai/langgraph")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("V5.0 RESEARCH & INTEGRATION REGISTRY", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, letterSpacing = 1.sp)
        }

        items(tools) { item ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier
                    .border(1.dp, NeonCyan.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .clickable { onLaunchIntent(item.url) }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                        Text(item.metric, color = NeonEmerald, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(item.description, color = TextSecondary, fontSize = 12.5.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("${item.source.uppercase()} • ${item.url}", color = NeonCyan.copy(alpha = 0.7f), fontSize = 10.5.sp)
                }
            }
        }
    }
}
