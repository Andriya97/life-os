package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.ScoreBreakdownItem
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun AnalyticsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val breakdown by viewModel.lifeScoreBreakdown.collectAsState()
    val recentScores by viewModel.recentScores.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    var showExportDialog by remember { mutableStateOf(false) }
    var exportContent by remember { mutableStateOf("") }
    var exportTitle by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("analytics_screen_lazy_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Overall Performance Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ANALYTICS & TRENDS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp, color = BrandPrimary)
                            )
                            Text(
                                text = "Average Score: 84 / 100",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BrandSecondary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "+8% vs last week",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = BrandSecondary),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 7-day Bar Visualizer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val days = listOf("Mon" to 78, "Tue" to 82, "Wed" to 79, "Thu" to 85, "Fri" to 88, "Sat" to 81, "Sun" to breakdown.overallScore)
                        days.forEach { (day, score) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.height(100.dp)
                            ) {
                                Text("$score", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .height(((score / 100f) * 65).dp)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(if (day == "Sun") BrandPrimary else BrandPrimaryLight.copy(alpha = 0.5f))
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(day, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant))
                            }
                        }
                    }
                }
            }
        }

        // Dimension Breakdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Pillar Adherence Breakdown", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

                    val p = profile ?: com.example.data.model.UserProfile()
                    ScoreBreakdownItem(title = "Academics", score = breakdown.academicsScore, weight = p.weightAcademics, accentColor = ColorAcademics)
                    ScoreBreakdownItem(title = "Fitness", score = breakdown.fitnessScore, weight = p.weightFitness, accentColor = ColorFitness)
                    ScoreBreakdownItem(title = "Nutrition", score = breakdown.nutritionScore, weight = p.weightNutrition, accentColor = ColorNutrition)
                    ScoreBreakdownItem(title = "Upskilling", score = breakdown.upskillingScore, weight = p.weightUpskilling, accentColor = ColorUpskilling)
                    ScoreBreakdownItem(title = "Habits", score = breakdown.habitsScore, weight = p.weightHabits, accentColor = ColorHabits)
                    ScoreBreakdownItem(title = "Sleep & Recovery", score = breakdown.sleepScore, weight = p.weightSleep, accentColor = ColorSleep)
                    ScoreBreakdownItem(title = "Productivity", score = breakdown.productivityScore, weight = p.weightProductivity, accentColor = ColorProductivity)
                }
            }
        }

        // Behavioral Correlation Insights
        item {
            SectionHeader(title = "Behavioral Correlative Insights")
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CorrelationItem(
                        icon = Icons.Default.Bedtime,
                        title = "Sleep vs. Deep Work Focus",
                        description = "On nights with 7.5+ hours of sleep, your academic focus rating averages 4.6/5 (+24%)."
                    )
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                    CorrelationItem(
                        icon = Icons.Default.FitnessCenter,
                        title = "Workout vs. Energy Levels",
                        description = "Days with morning or afternoon training show a 32% reduction in self-reported stress."
                    )
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                    CorrelationItem(
                        icon = Icons.Default.LocalFireDepartment,
                        title = "Habit Stacking Consistency",
                        description = "Your 14-day streak on morning wake triggers higher compliance on subsequent tasks."
                    )
                }
            }
        }

        // Data Export Buttons
        item {
            SectionHeader(title = "Data Management & Portability")
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        exportTitle = "LifeOS Data Export (JSON)"
                        exportContent = viewModel.generateJsonExport()
                        showExportDialog = true
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export JSON")
                }

                OutlinedButton(
                    onClick = {
                        exportTitle = "LifeOS Activity Export (CSV)"
                        exportContent = viewModel.generateCsvExport()
                        showExportDialog = true
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export CSV")
                }
            }
        }
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text(exportTitle) },
            text = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = exportContent,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showExportDialog = false }) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
fun CorrelationItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(BrandPrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            Text(description, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        }
    }
}
