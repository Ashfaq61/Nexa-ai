package com.example

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.util.*

enum class NexusScreen(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val category: String) {
    HOME("Dashboard", Icons.Default.Dashboard, "Intelligence"),
    CHAT("AI Chat", Icons.Default.Chat, "Intelligence"),
    VOICE("Live Voice", Icons.Default.Mic, "Intelligence"),
    AGENTS("9 Agents", Icons.Default.Hub, "Intelligence"),
    MOBILE_REPAIR("Mobile Repair", Icons.Default.Build, "Hardware"),
    MOBILE_CONTROL("Mobile Control", Icons.Default.Smartphone, "Tools"),
    RESEARCH("AI Research", Icons.Default.TravelExplore, "Tools"),
    DEV_STUDIO("Dev Studio", Icons.Default.Code, "Tools"),
    API_SETTINGS("API Keys", Icons.Default.Key, "System"),
    SECURITY_CENTER("Security", Icons.Default.Security, "System"),
    HISTORY("History", Icons.Default.History, "System"),
    SETTINGS("Settings", Icons.Default.Settings, "System")
}

data class ChatMessageItem(val id: String, val text: String, val isUser: Boolean, val providerModel: String = "Gemini 1.5 Pro", val timestamp: Long = System.currentTimeMillis())
data class DiscoveredTool(val name: String, val source: String, val description: String, val metric: String, val url: String, val status: String = "ACTIVE")
data class ProposalItem(val id: String, val title: String, val category: String, val summary: String, val diff: String, var approved: Boolean = false)
data class TaskGoalItem(val title: String, var status: String, val retries: Int = 0)

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTorchOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        tts = TextToSpeech(this, this)

        setContent {
            MyApplicationTheme {
                NexusMainAppV5(
                    onSpeak = { text -> speakOut(text) },
                    onToggleTorch = { toggleTorch() },
                    onAdjustVolume = { up -> adjustVolume(up) },
                    onLaunchIntent = { uriStr -> launchExternalIntent(uriStr) },
                    onOpenSettings = { openSystemSettings() }
                )
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
        }
    }

    private fun speakOut(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "nexus_tts")
    }

    private fun toggleTorch() {
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as? CameraManager ?: return
        try {
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return
            isTorchOn = !isTorchOn
            cameraManager.setTorchMode(cameraId, isTorchOn)
            Toast.makeText(this, if (isTorchOn) "Torch Activated" else "Torch Deactivated", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Torch Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun adjustVolume(up: Boolean) {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
        val direction = if (up) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, AudioManager.FLAG_SHOW_UI)
    }

    private fun launchExternalIntent(uriStr: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriStr))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not open: $uriStr", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openSystemSettings() {
        startActivity(Intent(Settings.ACTION_SETTINGS))
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NexusMainAppV5(
    onSpeak: (String) -> Unit,
    onToggleTorch: () -> Unit,
    onAdjustVolume: (Boolean) -> Unit,
    onLaunchIntent: (String) -> Unit,
    onOpenSettings: () -> Unit
) {
    var currentScreen by remember { mutableStateOf(NexusScreen.HOME) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Handle back button to return to HOME when on sub-screens
    BackHandler(enabled = currentScreen != NexusScreen.HOME) {
        currentScreen = NexusScreen.HOME
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = CyberSurface,
                modifier = Modifier.width(300.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(12.dp).background(NeonEmerald, CircleShape))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("PROJECT NEXUS V5.0", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 15.sp, letterSpacing = 1.sp)
                        Text("Autonomous Android Intelligence", color = TextSecondary, fontSize = 11.sp)
                    }
                }
                HorizontalDivider(color = CyberBackground, modifier = Modifier.padding(vertical = 12.dp))

                listOf("Intelligence", "Hardware", "Tools", "System").forEach { category ->
                    Text(
                        category.uppercase(),
                        color = NeonCyan.copy(alpha = 0.7f),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )

                    NexusScreen.values().filter { it.category == category }.forEach { screen ->
                        NavigationDrawerItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title, fontSize = 13.sp) },
                            selected = currentScreen == screen,
                            onClick = {
                                currentScreen = screen
                                coroutineScope.launch { drawerState.close() }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = CyberSurfaceCard,
                                selectedIconColor = NeonCyan,
                                selectedTextColor = NeonCyan,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextPrimary
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .background(NeonEmerald, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "NEXUS V5.0 • ${currentScreen.title.uppercase()}",
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan,
                                fontSize = 15.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = NeonCyan)
                        }
                    },
                    actions = {
                        AssistChip(
                            onClick = { currentScreen = NexusScreen.API_SETTINGS },
                            label = { Text("Vault OK", fontSize = 10.5.sp, color = NeonEmerald) },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(12.dp)) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = CyberSurfaceCard)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberBackground)
                )
            },
            bottomBar = {
                val primaryTabs = listOf(
                    NexusScreen.HOME,
                    NexusScreen.CHAT,
                    NexusScreen.VOICE,
                    NexusScreen.MOBILE_REPAIR,
                    NexusScreen.API_SETTINGS
                )
                NavigationBar(containerColor = CyberSurface, tonalElevation = 8.dp) {
                    primaryTabs.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title, fontSize = 10.sp, maxLines = 1) },
                            selected = currentScreen == screen,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NeonCyan,
                                selectedTextColor = NeonCyan,
                                indicatorColor = CyberSurfaceCard,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            ),
                            onClick = { currentScreen = screen }
                        )
                    }
                }
            },
            containerColor = CyberBackground
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    NexusScreen.HOME -> HomeScreen(onNavigate = { currentScreen = it }, onSpeak = onSpeak)
                    NexusScreen.CHAT -> ChatScreen(onSpeak = onSpeak)
                    NexusScreen.VOICE -> VoiceScreen(onSpeak = onSpeak)
                    NexusScreen.AGENTS -> AgentsScreen()
                    NexusScreen.MOBILE_REPAIR -> MobileRepairScreen(onSpeak = onSpeak)
                    NexusScreen.MOBILE_CONTROL -> MobileControlScreen(
                        onToggleTorch = onToggleTorch,
                        onAdjustVolume = onAdjustVolume,
                        onLaunchIntent = onLaunchIntent,
                        onOpenSettings = onOpenSettings
                    )
                    NexusScreen.RESEARCH -> ResearchScreen(onLaunchIntent = onLaunchIntent)
                    NexusScreen.DEV_STUDIO -> DevStudioScreen()
                    NexusScreen.API_SETTINGS -> ApiKeyManagerScreen(onLaunchUrl = onLaunchIntent)
                    NexusScreen.SECURITY_CENTER -> SecurityCenterScreen()
                    NexusScreen.HISTORY -> HistoryScreen()
                    NexusScreen.SETTINGS -> SettingsScreen()
                }
            }
        }
    }
}
