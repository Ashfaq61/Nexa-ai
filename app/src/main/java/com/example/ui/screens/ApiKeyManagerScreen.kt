package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ApiKeyRepository
import com.example.data.ApiKeySlot
import com.example.data.ApiProvider
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeyManagerScreen(
    onLaunchUrl: (String) -> Unit
) {
    val context = LocalContext.current
    val repository = remember { ApiKeyRepository(context) }
    var activeProvider by remember { mutableStateOf(repository.getActiveProvider()) }
    var autoFallback by remember { mutableStateOf(repository.isAutoFallbackEnabled()) }
    var selectedProviderTab by remember { mutableStateOf(activeProvider) }
    var slots by remember { mutableStateOf(repository.getKeysForProvider(selectedProviderTab)) }

    // Dialog state for adding/editing keys
    var showAddDialog by remember { mutableStateOf(false) }
    var slotNameInput by remember { mutableStateOf("Primary Key") }
    var apiKeyInput by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isTestingConnection by remember { mutableStateOf(false) }
    var testResultMsg by remember { mutableStateOf<String?>(null) }
    var testResultSuccess by remember { mutableStateOf<Boolean?>(null) }

    fun refreshSlots() {
        slots = repository.getKeysForProvider(selectedProviderTab)
        activeProvider = repository.getActiveProvider()
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
                        "API KEY VAULT & MULTI-MODEL ROUTER",
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "Encrypted Keystore-backed multi-slot storage",
                        color = TextSecondary,
                        fontSize = 11.5.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .background(NeonEmerald.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("VAULT SECURED", color = NeonEmerald, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Active Provider & Failover Status Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier.border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Active Inference Provider", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(activeProvider.displayName, color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        AssistChip(
                            onClick = { },
                            label = { Text(activeProvider.defaultModel, fontSize = 11.sp, color = NeonPurple) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = CyberSurface)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = CyberBackground)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Auto-Fallback Circuit Breaker", color = TextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                            Text("Failover to secondary provider on rate-limit", color = TextSecondary, fontSize = 10.5.sp)
                        }
                        Switch(
                            checked = autoFallback,
                            onCheckedChange = {
                                autoFallback = it
                                repository.setAutoFallbackEnabled(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonCyan,
                                checkedTrackColor = CyberSurfaceCard,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = CyberBackground
                            )
                        )
                    }
                }
            }
        }

        // Provider Selector Tabs
        item {
            Text("Select Provider to Manage", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ApiProvider.values().forEach { provider ->
                    val isSelected = selectedProviderTab == provider
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedProviderTab = provider
                            refreshSlots()
                        },
                        label = {
                            Text(
                                when (provider) {
                                    ApiProvider.GEMINI -> "Gemini"
                                    ApiProvider.GROQ -> "Groq"
                                    ApiProvider.OPENROUTER -> "OpenRouter"
                                    ApiProvider.DEEPSEEK -> "DeepSeek"
                                    ApiProvider.ANTHROPIC -> "Anthropic"
                                    ApiProvider.HUGGINGFACE -> "HuggingFace"
                                    ApiProvider.OPENAI -> "OpenAI"
                                    ApiProvider.OLLAMA_LOCAL -> "Ollama Local (No Key)"
                                    ApiProvider.CUSTOM_API -> "Custom API"
                                },
                                fontSize = 11.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberSurfaceCard,
                            selectedLabelColor = NeonCyan
                        )
                    )
                }
            }

            if (selectedProviderTab.isLocal) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberSurfaceCard, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        "Hardware requirement: ${selectedProviderTab.hardwareReqs}. Does not require cloud API keys.",
                        color = NeonPurple,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Action Row for Current Provider
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${selectedProviderTab.displayName} Keys",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                    if (selectedProviderTab == activeProvider) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("(ACTIVE)", color = NeonEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (selectedProviderTab != activeProvider) {
                        OutlinedButton(
                            onClick = {
                                repository.setActiveProvider(selectedProviderTab)
                                activeProvider = selectedProviderTab
                                Toast.makeText(context, "${selectedProviderTab.displayName} set as Active Provider", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Set Active", fontSize = 11.sp, color = NeonCyan)
                        }
                    }

                    Button(
                        onClick = {
                            slotNameInput = "Key Slot ${slots.size + 1}"
                            apiKeyInput = ""
                            testResultMsg = null
                            testResultSuccess = null
                            showAddDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Key", fontSize = 11.5.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Instructions Link
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLaunchUrl(selectedProviderTab.instructionsUrl) }
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Get an API Key from ${selectedProviderTab.instructionsUrl}",
                            color = NeonCyan,
                            fontSize = 11.5.sp,
                            maxLines = 1
                        )
                    }
                    Icon(Icons.Default.ArrowOutward, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                }
            }
        }

        // Keys List
        if (slots.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.KeyOff, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No keys saved for this provider", color = TextSecondary, fontSize = 13.sp)
                        Text("Tap 'Add Key' above to store your key in the secure vault.", color = TextSecondary.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }
            }
        } else {
            items(slots) { slot ->
                KeySlotCard(
                    slot = slot,
                    onDelete = {
                        repository.deleteKeySlot(slot.provider, slot.id)
                        refreshSlots()
                        Toast.makeText(context, "Key removed from vault", Toast.LENGTH_SHORT).show()
                    },
                    onTest = {
                        isTestingConnection = true
                        val (success, msg) = repository.testKeyConnection(slot.provider, slot.apiKey)
                        isTestingConnection = false
                        val updatedSlot = slot.copy(lastTestedSuccess = success, lastTestedMessage = msg)
                        repository.saveKeySlot(updatedSlot)
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        refreshSlots()
                    }
                )
            }
        }
    }

    // Add Key Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add ${selectedProviderTab.displayName} Key", color = TextPrimary, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = slotNameInput,
                        onValueChange = { slotNameInput = it },
                        label = { Text("Slot Name (e.g. Primary, Work, Backup)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        label = { Text("API Key Token") },
                        placeholder = {
                            Text(
                                when (selectedProviderTab) {
                                    ApiProvider.GEMINI -> "AIzaSy..."
                                    ApiProvider.GROQ -> "gsk_..."
                                    ApiProvider.OPENROUTER -> "sk-or-v1-..."
                                    ApiProvider.DEEPSEEK -> "sk-..."
                                    ApiProvider.ANTHROPIC -> "sk-ant-..."
                                    ApiProvider.HUGGINGFACE -> "hf_..."
                                    ApiProvider.OPENAI -> "sk-proj-..."
                                    ApiProvider.OLLAMA_LOCAL -> "http://localhost:11434"
                                    ApiProvider.CUSTOM_API -> "https://api.yourdomain.com/v1"
                                },
                                fontSize = 11.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    if (testResultMsg != null) {
                        Text(
                            testResultMsg!!,
                            color = if (testResultSuccess == true) NeonEmerald else NeonDanger,
                            fontSize = 11.5.sp
                        )
                    }

                    Text(
                        "Stored in encrypted Android SharedPreferences with private keystore isolation. Never transmitted without TLS.",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (apiKeyInput.isNotBlank()) {
                            val newSlot = ApiKeySlot(
                                id = "${selectedProviderTab.name}_${System.currentTimeMillis()}",
                                provider = selectedProviderTab,
                                slotName = slotNameInput.ifBlank { "Key Slot" },
                                apiKey = apiKeyInput.trim(),
                                isPrimary = slots.isEmpty()
                            )
                            repository.saveKeySlot(newSlot)
                            showAddDialog = false
                            refreshSlots()
                            Toast.makeText(context, "Key securely saved in vault", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Save to Vault", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CyberSurfaceCard
        )
    }
}

@Composable
fun KeySlotCard(
    slot: ApiKeySlot,
    onDelete: () -> Unit,
    onTest: () -> Unit
) {
    var isRevealed by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (slot.isPrimary) NeonCyan.copy(alpha = 0.4f) else CyberSurface, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Key,
                        contentDescription = null,
                        tint = if (slot.isPrimary) NeonCyan else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(slot.slotName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                    if (slot.isPrimary) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(NeonCyan.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("PRIMARY", color = NeonCyan, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Row {
                    IconButton(onClick = onTest, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Test Key", tint = NeonEmerald, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Key", tint = NeonDanger, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Masked / Revealed Key View
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberBackground, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isRevealed) slot.apiKey else maskKey(slot.apiKey),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    maxLines = 1
                )
                IconButton(onClick = { isRevealed = !isRevealed }, modifier = Modifier.size(24.dp)) {
                    Icon(
                        if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Visibility",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (slot.lastTestedSuccess != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (slot.lastTestedSuccess == true) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (slot.lastTestedSuccess == true) NeonEmerald else NeonDanger,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        slot.lastTestedMessage ?: (if (slot.lastTestedSuccess == true) "Connection verified" else "Connection failed"),
                        color = if (slot.lastTestedSuccess == true) NeonEmerald else NeonDanger,
                        fontSize = 10.5.sp
                    )
                }
            }
        }
    }
}

private fun maskKey(key: String): String {
    if (key.length <= 8) return "••••••••"
    val prefix = key.take(4)
    val suffix = key.takeLast(4)
    return "$prefix••••••••$suffix"
}
