package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MobileRepairingDatabase
import com.example.data.RepairCase
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileRepairScreen(
    onSpeak: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var isUrduMode by remember { mutableStateOf(false) }

    val categories = listOf("All", "Dead/Short", "Charging", "Display", "Diode Values", "IC Compatibility")

    val filteredCases = remember(searchQuery, selectedCategory) {
        MobileRepairingDatabase.cases.filter { item ->
            val matchesCategory = selectedCategory == "All" || item.category.equals(selectedCategory, ignoreCase = true)
            val query = searchQuery.lowercase().trim()
            val matchesQuery = query.isEmpty() ||
                    item.title.lowercase().contains(query) ||
                    item.symptom.lowercase().contains(query) ||
                    item.solutionEnglish.lowercase().contains(query) ||
                    item.solutionUrdu.lowercase().contains(query) ||
                    item.keyICs.lowercase().contains(query)
            matchesCategory && matchesQuery
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "MOBILE REPAIRING AI HARDWARE EXPERT",
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        if (isUrduMode) "موبائل ہارڈ ویئر ریپیئرنگ، شارٹ سرکٹ اور ڈائیوڈ موڈ ٹربل شوٹنگ" else "Component-level fault diagnosis, jumpering & diode values",
                        color = TextSecondary,
                        fontSize = 11.5.sp
                    )
                }

                FilterChip(
                    selected = isUrduMode,
                    onClick = { isUrduMode = !isUrduMode },
                    label = { Text(if (isUrduMode) "اردو" else "English", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    leadingIcon = { Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberSurfaceCard,
                        selectedLabelColor = NeonEmerald
                    )
                )
            }
        }

        // Multimeter Pro-Tip Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                modifier = Modifier.border(1.dp, NeonEmerald.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BuildCircle, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (isUrduMode) "ملٹی میٹر ڈائیوڈ موڈ سیٹ اپ قاعدہ" else "Multimeter Diode-Mode Golden Rule",
                            color = NeonEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        if (isUrduMode)
                            "1. ریڈ پروب (Red Probe) کو ہمیشہ فون کے گراؤنڈ (GND) پر رکھیں۔\n2. بلیک پروب (Black Probe) سے لائن یا ٹیسٹ پوائنٹ کو چھوئیں۔\n3. اگر ریڈنگ 0.000V آئے تو فل شارٹ ہے۔ 0.350V - 0.700V نارمل لائن ریڈنگ ہے۔"
                        else
                            "1. Connect RED PROBE to phone PCB GROUND (Shield / GND).\n2. Probe target rail with BLACK PROBE (Reverse Diode Mode).\n3. Reading 0.000V = Short Circuit | 0.350V - 0.700V = Normal | OL = Open Line.",
                        color = TextPrimary,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(if (isUrduMode) "فالٹ یا آئی سی تلاش کریں (مثال: dead, charging, display)..." else "Search fault, symptom, or IC (e.g. charging, dead, BQ)...", color = TextSecondary, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = NeonCyan.copy(alpha = 0.3f),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.take(4).forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 10.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberSurfaceCard,
                            selectedLabelColor = NeonCyan
                        )
                    )
                }
            }
        }

        // Cases Count
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${filteredCases.size} Hardware Diagnosis Cases",
                    color = TextSecondary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
                AssistChip(
                    onClick = {
                        val summary = if (isUrduMode) {
                            "موبائل ریپیئرنگ اے آئی ڈیٹا بیس لوڈ ہے۔ تمام شارٹ سرکٹ اور چارجنگ ڈائیوڈ ویلیوز تصدیق شدہ ہیں۔"
                        } else {
                            "Nexus Mobile Hardware Engine ready. ${filteredCases.size} verified diagnostic cases loaded."
                        }
                        onSpeak(summary)
                    },
                    label = { Text(if (isUrduMode) "آواز میں سنیں" else "Listen Summary", fontSize = 11.sp, color = NeonCyan) },
                    leadingIcon = { Icon(Icons.Default.VolumeUp, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp)) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = CyberSurface)
                )
            }
        }

        // Cases
        items(filteredCases) { repairCase ->
            RepairCaseCard(
                item = repairCase,
                isUrdu = isUrduMode,
                onSpeak = { onSpeak(if (isUrduMode) repairCase.solutionUrdu else repairCase.solutionEnglish) }
            )
        }
    }
}

@Composable
fun RepairCaseCard(
    item: RepairCase,
    isUrdu: Boolean,
    onSpeak: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NeonCyan.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(CyberSurface, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(item.category.uppercase(), color = NeonPurple, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(NeonEmerald.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("VERIFIED HARDWARE", color = NeonEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onSpeak, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Read Aloud", tint = NeonCyan, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(item.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))

            Text("Symptom: ${item.symptom}", color = TextSecondary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(8.dp))

            // Diode value pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberBackground, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Diode Range: ${item.diodeModeRange}",
                        color = NeonCyan,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = CyberSurface)
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    if (isUrdu) "مکمل حل اور ٹربل شوٹنگ کے مراحل:" else "Step-by-Step Diagnostic Steps:",
                    color = NeonEmerald,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    if (isUrdu) item.solutionUrdu else item.solutionEnglish,
                    color = TextPrimary,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Key ICs & Components: ${item.keyICs}",
                    color = NeonPurple,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Tap card to expand full repair steps & schematics →", color = TextSecondary.copy(alpha = 0.6f), fontSize = 10.5.sp)
            }
        }
    }
}
