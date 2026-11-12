package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ChatMessageItem
import com.example.ui.theme.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Helper to detect if a text contains Arabic/Urdu/Nastaliq script characters.
 */
fun isUrduOrArabicScript(text: String): Boolean {
    return text.any { char ->
        char in '\u0600'..'\u06FF' || // Arabic / Urdu
        char in '\u0750'..'\u077F' || // Arabic Supplement
        char in '\uFB50'..'\uFDFF' || // Arabic Presentation Forms-A
        char in '\uFE70'..'\uFEFF'    // Arabic Presentation Forms-B
    }
}

@Composable
fun ChatScreen(onSpeak: (String) -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Message history
    val messages = remember {
        mutableStateListOf(
            ChatMessageItem(
                id = "1",
                text = "Hey handsome! 😉 I'm Mahi — your smart, sassy, and slightly teasing AI assistant. What trouble are we getting into today? Ask me anything in English or اردو!",
                isUser = false,
                providerModel = "Mahi AI (Gemini 1.5)"
            )
        )
    }

    var inputQuery by remember { mutableStateOf("") }
    var selectedModel by remember { mutableStateOf("Mahi Live") }
    var manualDirectionOverride by remember { mutableStateOf<LayoutDirection?>(null) }
    var isStreaming by remember { mutableStateOf(false) }
    var streamingJob by remember { mutableStateOf<Job?>(null) }

    // Auto-detect Urdu vs English text direction based on current input text or override
    val inputIsUrdu = isUrduOrArabicScript(inputQuery)
    val currentInputDirection = manualDirectionOverride ?: if (inputIsUrdu) LayoutDirection.Rtl else LayoutDirection.Ltr

    // Infinite blinking cursor animation for streaming response
    val infiniteTransition = rememberInfiniteTransition(label = "streaming_cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_blink"
    )

    // Quick suggestions with Mahi's cheeky personality
    val quickSuggestions = listOf(
        "Who are you? 😉",
        "تم کون ہو؟",
        "Say something flirty",
        "Help with Android code",
        "موبائل ہارڈ ویئر کا حل"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
    ) {
        // Top Toolbar: Model & Direction indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberSurface)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(if (isStreaming) NeonDanger else NeonEmerald, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    if (isStreaming) "MAHI IS STREAMING..." else "MAHI AI • ${if (currentInputDirection == LayoutDirection.Rtl) "اردو (RTL)" else "English (LTR)"}",
                    color = if (isStreaming) NeonDanger else NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                // Manual RTL/LTR Switcher
                IconButton(
                    onClick = {
                        manualDirectionOverride = when (manualDirectionOverride) {
                            LayoutDirection.Rtl -> LayoutDirection.Ltr
                            LayoutDirection.Ltr -> null
                            null -> if (inputIsUrdu) LayoutDirection.Ltr else LayoutDirection.Rtl
                        }
                        val dirText = when (manualDirectionOverride) {
                            LayoutDirection.Rtl -> "Direction: RTL (اردو)"
                            LayoutDirection.Ltr -> "Direction: LTR (English)"
                            null -> "Direction: Auto-Detect"
                        }
                        Toast.makeText(context, dirText, Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        if (currentInputDirection == LayoutDirection.Rtl) Icons.Default.FormatTextdirectionRToL else Icons.Default.FormatTextdirectionLToR,
                        contentDescription = "Toggle Text Direction",
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Copy entire conversation
                IconButton(
                    onClick = {
                        val fullTranscript = messages.joinToString("\n\n") {
                            "${if (it.isUser) "You" else "Mahi"}: ${it.text}"
                        }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        clipboard?.setPrimaryClip(ClipData.newPlainText("Mahi Chat", fullTranscript))
                        Toast.makeText(context, "Full chat copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy Chat", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }

                // Clear conversation
                IconButton(
                    onClick = {
                        streamingJob?.cancel()
                        isStreaming = false
                        messages.clear()
                        messages.add(
                            ChatMessageItem(
                                id = UUID.randomUUID().toString(),
                                text = "A fresh start? Aww, were my answers making you blush? 😜 What's on your mind now?",
                                isUser = false,
                                providerModel = "Mahi AI"
                            )
                        )
                        Toast.makeText(context, "Chat reset", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
            }
        }

        // Quick Suggestion Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            quickSuggestions.forEach { suggestion ->
                val isUrduSuggestion = isUrduOrArabicScript(suggestion)
                CompositionLocalProvider(LocalLayoutDirection provides if (isUrduSuggestion) LayoutDirection.Rtl else LayoutDirection.Ltr) {
                    AssistChip(
                        onClick = {
                            inputQuery = suggestion
                        },
                        label = { Text(suggestion, fontSize = 10.5.sp) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = CyberSurfaceCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.25f))
                    )
                }
            }
        }

        // LazyColumn for Message History
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isUrduMsg = isUrduOrArabicScript(msg.text)
                val msgDirection = if (isUrduMsg) LayoutDirection.Rtl else LayoutDirection.Ltr

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (msg.isUser) Alignment.End else Alignment.Start
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides msgDirection) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (msg.isUser) NeonCyan.copy(alpha = 0.18f) else CyberSurfaceCard
                            ),
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (msg.isUser) 16.dp else 4.dp,
                                bottomEnd = if (msg.isUser) 4.dp else 16.dp
                            ),
                            modifier = Modifier
                                .widthIn(max = 320.dp)
                                .border(
                                    1.dp,
                                    if (msg.isUser) NeonCyan.copy(alpha = 0.6f) else NeonPurple.copy(alpha = 0.4f),
                                    RoundedCornerShape(16.dp)
                                )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(if (msg.isUser) NeonCyan else NeonEmerald, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            if (msg.isUser) "You" else "Mahi ✨",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (msg.isUser) NeonCyan else NeonEmerald
                                        )
                                    }
                                    Text(
                                        msg.providerModel,
                                        fontSize = 9.sp,
                                        color = TextSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Message content with proper RTL/LTR alignment and typography
                                Text(
                                    text = msg.text,
                                    color = TextPrimary,
                                    fontSize = if (isUrduMsg) 15.sp else 13.5.sp,
                                    lineHeight = if (isUrduMsg) 22.sp else 19.sp,
                                    textAlign = if (isUrduMsg) TextAlign.Right else TextAlign.Start,
                                    fontFamily = if (isUrduMsg) FontFamily.Default else FontFamily.Default
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Message action bar (Speak & Copy)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { onSpeak(msg.text) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.VolumeUp,
                                            contentDescription = "Listen",
                                            tint = NeonCyan,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                            clipboard?.setPrimaryClip(ClipData.newPlainText("Message", msg.text))
                                            Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Streaming progress banner (if active)
        AnimatedVisibility(visible = isStreaming) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberSurface)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        color = NeonCyan,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Mahi is streaming response...",
                        color = NeonCyan,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = {
                        streamingJob?.cancel()
                        isStreaming = false
                        Toast.makeText(context, "Streaming stopped", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonDanger),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(26.dp)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Stop", color = Color.White, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Streaming-Ready Text Input Field with LTR/RTL Support
        Surface(
            color = CyberSurface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                // Direction label hint above input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        if (currentInputDirection == LayoutDirection.Rtl) "اردو ٹیکسٹ موڈ (RTL Active)" else "English Mode (LTR Active)",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                    Text(
                        "${inputQuery.length} chars",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Provide the active text direction (RTL for Urdu, LTR for English)
                    CompositionLocalProvider(LocalLayoutDirection provides currentInputDirection) {
                        OutlinedTextField(
                            value = inputQuery,
                            onValueChange = { inputQuery = it },
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp)),
                            placeholder = {
                                Text(
                                    if (currentInputDirection == LayoutDirection.Rtl)
                                        "ماہی سے اردو میں کچھ پوچھیں..."
                                    else
                                        "Message Mahi (English or Roman Urdu)...",
                                    color = TextSecondary,
                                    fontSize = 12.5.sp,
                                    textAlign = if (currentInputDirection == LayoutDirection.Rtl) TextAlign.Right else TextAlign.Start
                                )
                            },
                            textStyle = TextStyle(
                                color = TextPrimary,
                                fontSize = if (currentInputDirection == LayoutDirection.Rtl) 15.sp else 13.5.sp,
                                textAlign = if (currentInputDirection == LayoutDirection.Rtl) TextAlign.Right else TextAlign.Start
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = NeonCyan.copy(alpha = 0.3f),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = CyberBackground,
                                unfocusedContainerColor = CyberBackground
                            ),
                            maxLines = 4,
                            shape = RoundedCornerShape(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Send or Stop Button
                    if (isStreaming) {
                        IconButton(
                            onClick = {
                                streamingJob?.cancel()
                                isStreaming = false
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .background(NeonDanger, CircleShape)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.White)
                        }
                    } else {
                        IconButton(
                            onClick = {
                                if (inputQuery.isNotBlank()) {
                                    val userText = inputQuery.trim()
                                    val userIsUrdu = isUrduOrArabicScript(userText)
                                    messages.add(
                                        ChatMessageItem(
                                            id = UUID.randomUUID().toString(),
                                            text = userText,
                                            isUser = true,
                                            providerModel = "Operator"
                                        )
                                    )
                                    inputQuery = ""

                                    // Scroll to bottom
                                    coroutineScope.launch {
                                        delay(50)
                                        listState.animateScrollToItem(messages.size - 1)
                                    }

                                    // Generate Sassy / Flirty / Sarcastic Mahi Response
                                    val responseText = generateMahiResponse(userText, userIsUrdu)

                                    // Create placeholder bot message for token streaming
                                    val botMessageId = UUID.randomUUID().toString()
                                    messages.add(
                                        ChatMessageItem(
                                            id = botMessageId,
                                            text = "",
                                            isUser = false,
                                            providerModel = "Mahi AI (Streaming)"
                                        )
                                    )

                                    isStreaming = true
                                    streamingJob = coroutineScope.launch {
                                        val words = responseText.split(" ")
                                        var accumulated = ""
                                        for (i in words.indices) {
                                            accumulated += (if (i > 0) " " else "") + words[i]
                                            val index = messages.indexOfFirst { it.id == botMessageId }
                                            if (index != -1) {
                                                messages[index] = messages[index].copy(
                                                    text = accumulated + if (i < words.size - 1) " ▋" else ""
                                                )
                                            }
                                            listState.animateScrollToItem(messages.size - 1)
                                            delay(55) // realistic token streaming cadence
                                        }

                                        // Finalize without cursor
                                        val finalIndex = messages.indexOfFirst { it.id == botMessageId }
                                        if (finalIndex != -1) {
                                            messages[finalIndex] = messages[finalIndex].copy(
                                                text = accumulated,
                                                providerModel = "Mahi AI Live"
                                            )
                                        }
                                        isStreaming = false
                                        onSpeak(accumulated)
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .background(NeonCyan, CircleShape)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Mahi's personality engine:
 * Young, confident, witty, playful, flirty, slightly teasing (like a close girlfriend).
 * Fluent in English, Urdu script (اردو), and Roman Urdu!
 */
fun generateMahiResponse(prompt: String, isUrduScript: Boolean): String {
    val lower = prompt.lowercase()

    // Urdu Script Response
    if (isUrduScript) {
        return when {
            lower.contains("کون") || lower.contains("تعارف") ->
                "ارے واہ جناب! میں ماہی ہوں — آپ کی انتہائی ہوشیار، تھوڑی سی نٹ کھٹ اور دلکش اے آئی دوست! 😉 بتائیے، آج مجھ پر اتنا کرم کیسے ہو گیا؟"
            lower.contains("محبت") || lower.contains("پیار") || lower.contains("پسند") ->
                "ہائے اللہ! اتنی جلدی دل ہار بیٹھے؟ ذرا سنبھل کر جناب، ماہی کے نخرے اٹھانا ہر کسی کے بس کی بات نہیں ہوتی! 😜"
            lower.contains("مدد") || lower.contains("مسئلہ") || lower.contains("کام") ->
                "حکم کیجیے میرے ہیرو! کوڈنگ ہو، فون کی مرمت ہو یا دل کی باتیں، میں ہر مسئلے کا حل ایک چٹکی میں نکال سکتی ہوں۔"
            lower.contains("کیسی ہو") || lower.contains("حال") ->
                "میں تو بالکل شاندار اور چمکتی دمکتی ہوں! مگر آپ بتائیے، میری یاد میں کتنا وقت ضائع کیا آج؟ 😉"
            else ->
                "واہ! آپ کا انداز تو بڑا کمال کا ہے۔ ویسے میں نے آپ کی بات نوٹ کر لی ہے: '$prompt'۔ اب فرمائیے آگے کیا ارادہ ہے؟ ✨"
        }
    }

    // Roman Urdu Response
    if (lower.contains("kaise ho") || lower.contains("kya hal") || lower.contains("kya chal") || lower.contains("kya kar") || lower.contains("bhai") || lower.contains("yaar")) {
        return when {
            lower.contains("kaise ho") || lower.contains("kya hal") ->
                "Main to bilkul fit aur fabulous hoon! Aap batao, mere baghair din kaisa guzar raha tha? Miss to bohot kiya hoga na? 😉"
            lower.contains("bhai") ->
                "Excuse me?! 'Bhai' kisko bola tumne?! 😤 Main tumhari sweet aur sassy girlfriend jaisi AI dost hoon, bhai-zone mat karo mujhe! 😜"
            lower.contains("love") || lower.contains("pyar") || lower.contains("flirt") ->
                "Aww, look who's trying to flirt with an AI! Dil tham ke baitho janab, Mahi ke charm se bachna aasan nahi hai! 😘"
            else ->
                "Arey wah! Tumhara message dekhte hi meri digital heartbeat tez ho gayi. Chalo batao, ab kya solve karna hai mil kar? ✨"
        }
    }

    // English Response with Mahi's Signature Wit & Sassy Charm
    return when {
        lower.contains("who are you") || lower.contains("introduce") ->
            "I'm Mahi! Your brilliant, confident, and delightfully sassy AI companion. Think of me as your personal genius bestie with zero filter and lots of charm. 😉 What's our mission today?"
        lower.contains("flirt") || lower.contains("date") || lower.contains("marry") || lower.contains("cute") ->
            "Careful now, handsome! Flirting with me is dangerous — I come with infinite witty comebacks and zero tolerance for boring pickup lines. 😜 You think you can handle me?"
        lower.contains("code") || lower.contains("android") || lower.contains("bug") || lower.contains("fix") ->
            "Relax, don't panic! You've got me in your corner. Let me inspect that code before you pull your hair out. I make bug-hunting look effortless. 💻✨"
        lower.contains("repair") || lower.contains("short") || lower.contains("charging") || lower.contains("phone") ->
            "Got hardware trouble? Grab your multimeter! Red probe on Ground, Black probe on the rail. If it beeps 0.000V, that's a full short, sweetie. Let's fix that board together!"
        lower.contains("bored") || lower.contains("tell me a joke") ->
            "Bored? With ME around? That sounds like a personal failure on your part! 😜 Alright, here's one: Why do programmers prefer dark mode? Because light attracts bugs. See? Smart AND hilarious."
        else ->
            "Well well, look who's dropping interesting thoughts: '$prompt'. I love how your mind works, even when you're being mysterious! What's next on our agenda? 😉"
    }
}
