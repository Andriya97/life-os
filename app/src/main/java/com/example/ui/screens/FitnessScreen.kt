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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Workout
import com.example.ui.MainViewModel
import com.example.ui.components.EmptyState
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val workouts by viewModel.workouts.collectAsState()
    val allWorkouts by viewModel.allWorkouts.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    var showAddWorkoutDialog by remember { mutableStateOf(false) }

    val totalDurationToday = workouts.sumOf { it.durationMinutes }
    val totalCaloriesBurnedToday = workouts.sumOf { it.caloriesBurned }
    val targetExerciseMinutes = profile?.targetExerciseMinutes ?: 45

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddWorkoutDialog = true },
                modifier = Modifier
                    .padding(bottom = 72.dp)
                    .testTag("fitness_fab"),
                containerColor = ColorFitness,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Log Workout")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("fitness_screen_lazy_column"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Fitness Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ColorFitness.copy(alpha = 0.1f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ColorFitness.copy(alpha = 0.3f)))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "TODAY'S ACTIVITY",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = ColorFitness
                                    )
                                )
                                Text(
                                    text = "$totalDurationToday min / $targetExerciseMinutes min",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = ColorFitness.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "$totalCaloriesBurnedToday kcal",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ColorFitness
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { (totalDurationToday.toFloat() / targetExerciseMinutes).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = ColorFitness,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Estimated Steps: 8,421 / 10,000", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            Text("84% Daily Goal", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ColorFitness))
                        }
                    }
                }
            }

            // Today's Workouts
            item {
                SectionHeader(
                    title = "Today's Workouts",
                    actionLabel = "+ Log Workout",
                    onActionClick = { showAddWorkoutDialog = true }
                )
            }

            if (workouts.isEmpty()) {
                item {
                    EmptyState(
                        title = "No workouts logged today",
                        description = "Track strength training, runs, HIIT, or mobility sessions.",
                        buttonText = "Log Workout",
                        icon = Icons.Default.FitnessCenter,
                        onButtonClick = { showAddWorkoutDialog = true }
                    )
                }
            } else {
                items(workouts) { workout ->
                    WorkoutItemCard(
                        workout = workout,
                        onDelete = { viewModel.deleteWorkout(workout) }
                    )
                }
            }

            // Recent Workout History
            if (allWorkouts.isNotEmpty()) {
                item {
                    SectionHeader(title = "Workout History")
                }
                items(allWorkouts.take(5)) { workout ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ColorFitness.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = ColorFitness, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(workout.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                                Text("${workout.dateString} • ${workout.durationMinutes} mins • ${workout.caloriesBurned} kcal", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Workout Dialog
    if (showAddWorkoutDialog) {
        var name by remember { mutableStateOf("Hypertrophy Training") }
        var type by remember { mutableStateOf("Strength") }
        var duration by remember { mutableStateOf("45") }
        var calories by remember { mutableStateOf("320") }
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddWorkoutDialog = false },
            title = { Text("Log Workout") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Workout Name / Routine") }, modifier = Modifier.fillMaxWidth())

                    Text("Workout Type", style = MaterialTheme.typography.labelMedium)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Strength", "Running", "HIIT", "Yoga").forEach { t ->
                            AssistChip(
                                onClick = { type = t },
                                label = { Text(t, fontSize = 11.sp) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (type == t) ColorFitness.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (min)") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = calories, onValueChange = { calories = it }, label = { Text("Est. Calories") }, modifier = Modifier.weight(1f))
                    }

                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Sets / Notes / RPE") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addWorkout(
                                name = name,
                                type = type,
                                durationMins = duration.toIntOrNull() ?: 45,
                                caloriesBurned = calories.toIntOrNull() ?: 300,
                                notes = notes
                            )
                            showAddWorkoutDialog = false
                        }
                    }
                ) {
                    Text("Log Workout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddWorkoutDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun WorkoutItemCard(
    workout: Workout,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ColorFitness.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = ColorFitness, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(workout.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("${workout.workoutType} • ${workout.durationMinutes} mins • ${workout.caloriesBurned} kcal", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline)
                }
            }

            if (workout.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = workout.notes,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
