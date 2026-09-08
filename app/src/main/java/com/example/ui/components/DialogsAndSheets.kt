package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddSheet(
    viewModel: MainViewModel,
    onNavigate: (Screen) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Quick Log & Action",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionItem(
                    icon = Icons.Default.WaterDrop,
                    title = "+500ml Water",
                    color = ColorHabits,
                    onClick = {
                        viewModel.logWater(500)
                        onDismiss()
                    }
                )
                QuickActionItem(
                    icon = Icons.Default.School,
                    title = "Log Study",
                    color = ColorAcademics,
                    onClick = {
                        onDismiss()
                        onNavigate(Screen.ACADEMICS)
                    }
                )
                QuickActionItem(
                    icon = Icons.Default.FitnessCenter,
                    title = "Log Workout",
                    color = ColorFitness,
                    onClick = {
                        onDismiss()
                        onNavigate(Screen.FITNESS)
                    }
                )
                QuickActionItem(
                    icon = Icons.Default.Restaurant,
                    title = "Log Meal",
                    color = ColorNutrition,
                    onClick = {
                        onDismiss()
                        onNavigate(Screen.NUTRITION)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionItem(
                    icon = Icons.Default.Code,
                    title = "Upskilling",
                    color = ColorUpskilling,
                    onClick = {
                        onDismiss()
                        onNavigate(Screen.UPSKILLING)
                    }
                )
                QuickActionItem(
                    icon = Icons.Default.CheckCircle,
                    title = "Add Habit",
                    color = BrandSecondary,
                    onClick = {
                        onDismiss()
                        onNavigate(Screen.HABITS)
                    }
                )
                QuickActionItem(
                    icon = Icons.Default.AddCircleOutline,
                    title = "New Task",
                    color = ColorProductivity,
                    onClick = {
                        onDismiss()
                        onNavigate(Screen.GOALS)
                    }
                )
                QuickActionItem(
                    icon = Icons.Default.CalendarToday,
                    title = "Schedule",
                    color = BrandPrimary,
                    onClick = {
                        onDismiss()
                        onNavigate(Screen.TODAY)
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(76.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(26.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            ),
            maxLines = 2
        )
    }
}

@Composable
fun MorningPlannerDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    var priority1 by remember { mutableStateOf("Complete CS-441 Raft Consensus module") }
    var priority2 by remember { mutableStateOf("Hypertrophy Upper Body workout session") }
    var priority3 by remember { mutableStateOf("Read 25 pages of System Architecture") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.WbSunny, contentDescription = null, tint = ColorWarning)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Morning Daily Launchpad")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Clarify your 3 non-negotiable MITs (Most Important Tasks) for maximum daily leverage.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                OutlinedTextField(
                    value = priority1,
                    onValueChange = { priority1 = it },
                    label = { Text("Priority 1 (MIT)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = priority2,
                    onValueChange = { priority2 = it },
                    label = { Text("Priority 2") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = priority3,
                    onValueChange = { priority3 = it },
                    label = { Text("Priority 3") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (priority1.isNotBlank()) viewModel.addTask(priority1, "", "Priority", "Critical", "Today", 60)
                    if (priority2.isNotBlank()) viewModel.addTask(priority2, "", "Priority", "High", "Today", 45)
                    if (priority3.isNotBlank()) viewModel.addTask(priority3, "", "Priority", "Medium", "Today", 30)
                    onDismiss()
                }
            ) {
                Text("Set Focus")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Dismiss") }
        }
    )
}

@Composable
fun EveningReviewDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    var moodScore by remember { mutableIntStateOf(8) }
    var energyScore by remember { mutableIntStateOf(7) }
    var biggestWin by remember { mutableStateOf("") }
    var improvementGoal by remember { mutableStateOf("") }
    var gratitude by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.NightsStay, contentDescription = null, tint = ColorSleep)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Evening Reflection & Review")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Mood rating: $moodScore/10", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = moodScore.toFloat(),
                    onValueChange = { moodScore = it.toInt() },
                    valueRange = 1f..10f,
                    steps = 8
                )

                Text("Energy rating: $energyScore/10", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = energyScore.toFloat(),
                    onValueChange = { energyScore = it.toInt() },
                    valueRange = 1f..10f,
                    steps = 8
                )

                OutlinedTextField(
                    value = biggestWin,
                    onValueChange = { biggestWin = it },
                    label = { Text("Today's Biggest Win") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = improvementGoal,
                    onValueChange = { improvementGoal = it },
                    label = { Text("What could be improved tomorrow?") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = gratitude,
                    onValueChange = { gratitude = it },
                    label = { Text("Gratitude notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.logMood(moodScore, energyScore, 3, biggestWin, improvementGoal, gratitude)
                    viewModel.saveDailyReview(biggestWin, improvementGoal, gratitude)
                    onDismiss()
                }
            ) {
                Text("Complete Review (+150 XP)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun FocusTimerDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val seconds by viewModel.timerSeconds.collectAsState()
    val isRunning by viewModel.isTimerRunning.collectAsState()
    val targetName by viewModel.timerTargetName.collectAsState()

    var subjectName by remember { mutableStateOf("Distributed Computing") }
    var mode by remember { mutableStateOf("Academics") } // "Academics" or "Upskilling"

    val mins = seconds / 60
    val secs = seconds % 60
    val timeFormatted = String.format("%02d:%02d", mins, secs)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = BrandPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Deep Work Focus Timer")
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = mode == "Academics",
                        onClick = { mode = "Academics" },
                        label = { Text("Academics") }
                    )
                    FilterChip(
                        selected = mode == "Upskilling",
                        onClick = { mode = "Upskilling" },
                        label = { Text("Upskilling") }
                    )
                }

                OutlinedTextField(
                    value = subjectName,
                    onValueChange = { subjectName = it },
                    label = { Text(if (mode == "Academics") "Subject" else "Course / Track") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = timeFormatted,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = BrandPrimary,
                        fontSize = 54.sp
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (!isRunning) {
                        Button(
                            onClick = { viewModel.startTimer(subjectName) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Start")
                        }
                    } else {
                        Button(
                            onClick = { viewModel.pauseTimer() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ColorFitness)
                        ) {
                            Icon(Icons.Default.Pause, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pause")
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.resetTimer() },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (seconds > 0) {
                        if (mode == "Academics") {
                            viewModel.saveTimerAsStudy(subjectName, 5, "Deep work session")
                        } else {
                            viewModel.saveTimerAsLearning(subjectName, "Practical implementation", "Hands-on focus")
                        }
                    }
                    onDismiss()
                }
            ) {
                Text("Save & Close")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
