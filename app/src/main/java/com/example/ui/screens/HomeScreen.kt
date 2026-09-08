package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.data.model.*
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigate: (Screen) -> Unit,
    onOpenQuickAdd: () -> Unit,
    onOpenMorningPlanner: () -> Unit,
    onOpenEveningReview: () -> Unit,
    onOpenFocusTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val scoreBreakdown by viewModel.lifeScoreBreakdown.collectAsState()
    val scheduleEvents by viewModel.scheduleEvents.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val habitCompletions by viewModel.habitCompletions.collectAsState()
    val studySessions by viewModel.studySessions.collectAsState()
    val workouts by viewModel.workouts.collectAsState()
    val meals by viewModel.mealLogs.collectAsState()
    val totalWater by viewModel.totalWaterMl.collectAsState()
    val learningSessions by viewModel.learningSessions.collectAsState()
    val sleepLog by viewModel.todaySleepLog.collectAsState()
    val coachInsights by viewModel.coachInsights.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    var showScoreWeightsDialog by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }

    val dayFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    val userName = profile?.name?.split(" ")?.firstOrNull() ?: "Alex"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header & Greeting
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$greeting, $userName 👋",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = dayFormat,
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    StreakPill(streakDays = profile?.currentStreak ?: 14)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary)
                            .clickable { onNavigate(Screen.PROFILE) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.take(1).uppercase(),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // 2. Daily Life Score Gauge
        item {
            LifeScoreGauge(
                score = scoreBreakdown.overallScore,
                onClick = { showScoreWeightsDialog = true }
            )
        }

        // 3. AI Life Coach Insight Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_coach_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BrandPrimary.copy(alpha = 0.08f)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(BrandPrimaryLight, ColorUpskilling))
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(BrandPrimaryLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Coach",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "AI LIFE COACH",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = BrandPrimary
                                )
                            )
                        }

                        IconButton(
                            onClick = { viewModel.refreshAiCoach() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            if (isAiLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh Insights",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = coachInsights?.dailyHeadline ?: "Synthesizing your daily behavioral momentum...",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = coachInsights?.dailySummary ?: "Logging metrics across academics, fitness and nutrition helps tailor actionable insights.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    if (coachInsights?.recommendedFocus?.isNotBlank() == true) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = ColorWarning,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = coachInsights!!.recommendedFocus,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Quick Actions: Morning Plan & Evening Review & Focus Timer
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onOpenMorningPlanner,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_morning_plan"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = ColorWarning,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Plan Day",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Button(
                    onClick = onOpenFocusTimer,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_focus_timer"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary.copy(alpha = 0.15f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Focus",
                        color = BrandPrimary,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Button(
                    onClick = onOpenEveningReview,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_evening_review"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NightsStay,
                        contentDescription = null,
                        tint = ColorSleep,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Review",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // 5. Today's Progress Highlights
        item {
            SectionHeader(
                title = "Today's Progress",
                actionLabel = "View Analytics",
                onActionClick = { onNavigate(Screen.ANALYTICS) }
            )
        }

        item {
            val totalStudyMins = studySessions.sumOf { it.durationMinutes }
            val studyHoursStr = "${totalStudyMins / 60}h ${totalStudyMins % 60}m"
            val targetStudyStr = "${(profile?.targetStudyMinutes ?: 240) / 60}h"

            val totalCalories = meals.sumOf { it.calories }
            val targetCalories = profile?.targetCalories ?: 2200

            val totalProtein = meals.sumOf { it.protein.toDouble() }.toInt()
            val targetProtein = profile?.targetProteinGrams ?: 140

            val waterLitres = String.format(Locale.US, "%.1f", totalWater / 1000f)
            val targetWaterLitres = String.format(Locale.US, "%.1f", (profile?.targetWaterMl ?: 3000) / 1000f)

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricSummaryCard(
                        title = "Academics",
                        valueText = studyHoursStr,
                        targetText = targetStudyStr,
                        progressFraction = totalStudyMins.toFloat() / (profile?.targetStudyMinutes ?: 240),
                        accentColor = ColorAcademics,
                        icon = Icons.Default.School,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.ACADEMICS) }
                    )

                    MetricSummaryCard(
                        title = "Calories",
                        valueText = "$totalCalories",
                        targetText = "${targetCalories}k",
                        progressFraction = totalCalories.toFloat() / targetCalories,
                        accentColor = ColorNutrition,
                        icon = Icons.Default.LocalDining,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.NUTRITION) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricSummaryCard(
                        title = "Protein",
                        valueText = "${totalProtein}g",
                        targetText = "${targetProtein}g",
                        progressFraction = totalProtein.toFloat() / targetProtein,
                        accentColor = ColorFitness,
                        icon = Icons.Default.FitnessCenter,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.NUTRITION) }
                    )

                    MetricSummaryCard(
                        title = "Hydration",
                        valueText = "${waterLitres}L",
                        targetText = "${targetWaterLitres}L",
                        progressFraction = totalWater.toFloat() / (profile?.targetWaterMl ?: 3000),
                        accentColor = ColorHabits,
                        icon = Icons.Default.WaterDrop,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.NUTRITION) }
                    )
                }
            }
        }

        // 6. Today's Chronological Schedule Snippet
        item {
            SectionHeader(
                title = "Today's Schedule",
                actionLabel = "Full Timeline",
                onActionClick = { onNavigate(Screen.TODAY) }
            )
        }

        if (scheduleEvents.isEmpty()) {
            item {
                EmptyState(
                    title = "No schedule items for today",
                    description = "Plan your classes, deep work, workouts, and meals.",
                    buttonText = "Add Schedule Event",
                    icon = Icons.Default.CalendarToday,
                    onButtonClick = onOpenQuickAdd
                )
            }
        } else {
            items(scheduleEvents.take(4)) { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .testTag("schedule_item_${event.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = event.isCompleted,
                            onCheckedChange = { viewModel.toggleScheduleEvent(event) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    textDecoration = if (event.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                ),
                                color = if (event.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${event.startTime} - ${event.endTime} • ${event.category}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }
            }
        }

        // 7. Today's Habits Quick Row
        item {
            SectionHeader(
                title = "Daily Habits",
                actionLabel = "All Habits",
                onActionClick = { onNavigate(Screen.HABITS) }
            )
        }

        item {
            if (habits.isEmpty()) {
                EmptyState(
                    title = "No active habits",
                    description = "Form strong atomic habits to drive daily compound growth.",
                    buttonText = "Create Habit",
                    icon = Icons.Default.CheckCircle,
                    onButtonClick = onOpenQuickAdd
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    habits.take(4).forEach { habit ->
                        val isDone = habitCompletions.any { it.habitId == habit.id }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("home_habit_${habit.id}")
                                .clickable { viewModel.toggleHabit(habit.id) },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDone) BrandSecondary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isDone) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(BrandSecondary, BrandSecondary))) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                                    contentDescription = null,
                                    tint = if (isDone) BrandSecondary else MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = habit.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${habit.currentStreak}d 🔥",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ColorFitness)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Score Weights Dialog
    if (showScoreWeightsDialog) {
        AlertDialog(
            onDismissRequest = { showScoreWeightsDialog = false },
            title = { Text("Life Score Breakdown & Weights") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val p = profile ?: UserProfile()
                    ScoreBreakdownItem(title = "Academics", score = scoreBreakdown.academicsScore, weight = p.weightAcademics, accentColor = ColorAcademics)
                    ScoreBreakdownItem(title = "Fitness", score = scoreBreakdown.fitnessScore, weight = p.weightFitness, accentColor = ColorFitness)
                    ScoreBreakdownItem(title = "Nutrition", score = scoreBreakdown.nutritionScore, weight = p.weightNutrition, accentColor = ColorNutrition)
                    ScoreBreakdownItem(title = "Upskilling", score = scoreBreakdown.upskillingScore, weight = p.weightUpskilling, accentColor = ColorUpskilling)
                    ScoreBreakdownItem(title = "Habits", score = scoreBreakdown.habitsScore, weight = p.weightHabits, accentColor = ColorHabits)
                    ScoreBreakdownItem(title = "Sleep", score = scoreBreakdown.sleepScore, weight = p.weightSleep, accentColor = ColorSleep)
                    ScoreBreakdownItem(title = "Productivity", score = scoreBreakdown.productivityScore, weight = p.weightProductivity, accentColor = ColorProductivity)
                }
            },
            confirmButton = {
                TextButton(onClick = { showScoreWeightsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
