package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.MainViewModel
import com.example.ui.components.LevelBadge
import com.example.ui.components.SectionHeader
import com.example.ui.components.StreakPill
import com.example.ui.theme.*

@Composable
fun ProfileSettingsScreen(
    viewModel: MainViewModel,
    onResetOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val p = profile ?: UserProfile()

    var name by remember(p) { mutableStateOf(p.name) }
    var email by remember(p) { mutableStateOf(p.email) }
    var calories by remember(p) { mutableStateOf(p.targetCalories.toString()) }
    var protein by remember(p) { mutableStateOf(p.targetProteinGrams.toString()) }
    var water by remember(p) { mutableStateOf(p.targetWaterMl.toString()) }
    var studyMins by remember(p) { mutableStateOf(p.targetStudyMinutes.toString()) }
    var aiCoachEnabled by remember(p) { mutableStateOf(p.aiCoachEnabled) }

    var isSavedSnackbar by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen_lazy_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Identity Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = p.name.take(1).uppercase(),
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(p.name, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Text(p.occupationStatus, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StreakPill(streakDays = p.currentStreak)
                        LevelBadge(level = p.level, xp = p.xp)
                    }
                }
            }
        }

        // Daily Targets Configuration
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Daily Targets & Goals", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = calories,
                            onValueChange = { calories = it },
                            label = { Text("Target Calories") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = protein,
                            onValueChange = { protein = it },
                            label = { Text("Target Protein (g)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = water,
                            onValueChange = { water = it },
                            label = { Text("Water Goal (ml)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = studyMins,
                            onValueChange = { studyMins = it },
                            label = { Text("Study Target (min)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.updateUserProfile(
                                p.copy(
                                    targetCalories = calories.toIntOrNull() ?: p.targetCalories,
                                    targetProteinGrams = protein.toIntOrNull() ?: p.targetProteinGrams,
                                    targetWaterMl = water.toIntOrNull() ?: p.targetWaterMl,
                                    targetStudyMinutes = studyMins.toIntOrNull() ?: p.targetStudyMinutes,
                                    aiCoachEnabled = aiCoachEnabled
                                )
                            )
                            isSavedSnackbar = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Targets")
                    }
                }
            }
        }

        // AI Life Coach Toggle
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("AI Life Coach", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text(
                            "Synthesize multi-dimensional habit patterns and smart daily schedules.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Switch(
                        checked = aiCoachEnabled,
                        onCheckedChange = {
                            aiCoachEnabled = it
                            viewModel.updateUserProfile(p.copy(aiCoachEnabled = it))
                        }
                    )
                }
            }
        }

        // Onboarding / Re-configuration
        item {
            OutlinedButton(
                onClick = onResetOnboarding,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Launch Setup & Onboarding Wizard")
            }
        }
    }
}
