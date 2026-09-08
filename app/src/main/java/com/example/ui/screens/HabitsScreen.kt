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
import androidx.compose.material.icons.outlined.Circle
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
import com.example.data.model.Habit
import com.example.ui.MainViewModel
import com.example.ui.components.EmptyState
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val habits by viewModel.habits.collectAsState()
    val completions by viewModel.habitCompletions.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    val completedCount = habits.count { h -> completions.any { it.habitId == h.id } }
    val totalCount = habits.size
    val completionFraction = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .padding(bottom = 72.dp)
                    .testTag("habits_fab"),
                containerColor = ColorHabits,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Habit")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("habits_screen_lazy_column"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Habit Progress Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ColorHabits.copy(alpha = 0.1f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ColorHabits.copy(alpha = 0.3f)))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "HABIT CONSISTENCY",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = ColorHabits)
                                )
                                Text(
                                    text = "$completedCount of $totalCount habits done",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Text(
                                text = "${(completionFraction * 100).toInt()}%",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, color = ColorHabits)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { completionFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = ColorHabits,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }

            // Habits List
            item {
                SectionHeader(
                    title = "Daily Habits",
                    actionLabel = "+ Add Habit",
                    onActionClick = { showAddDialog = true }
                )
            }

            if (habits.isEmpty()) {
                item {
                    EmptyState(
                        title = "No habits created",
                        description = "Atomic habits compound into life-changing mastery over time.",
                        buttonText = "Create Habit",
                        icon = Icons.Default.CheckCircle,
                        onButtonClick = { showAddDialog = true }
                    )
                }
            } else {
                items(habits) { habit ->
                    val isDone = completions.any { it.habitId == habit.id }
                    HabitCard(
                        habit = habit,
                        isDone = isDone,
                        onToggle = { viewModel.toggleHabit(habit.id) },
                        onDelete = { viewModel.deleteHabit(habit) }
                    )
                }
            }
        }
    }

    // Add Habit Dialog
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Health") }
        var frequency by remember { mutableStateOf("Daily") }
        var reminder by remember { mutableStateOf("07:00") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Create Atomic Habit") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Habit Name (e.g. Read 20 pages)") }, modifier = Modifier.fillMaxWidth())

                    Text("Category", style = MaterialTheme.typography.labelMedium)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Health", "Productivity", "Growth", "Skill").forEach { cat ->
                            AssistChip(
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (category == cat) ColorHabits.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    }

                    OutlinedTextField(value = reminder, onValueChange = { reminder = it }, label = { Text("Reminder Time (e.g. 07:00)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addHabit(name, "Check", category, frequency, reminder, "#06B6D4")
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Save Habit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun HabitCard(
    habit: Habit,
    isDone: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) BrandSecondary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isDone) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(BrandSecondary, BrandSecondary))) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                contentDescription = null,
                tint = if (isDone) BrandSecondary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(26.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "${habit.category} • ${habit.frequency} • ${habit.reminderTime}",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = ColorFitness.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = ColorFitness, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${habit.currentStreak}d",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ColorFitness)
                    )
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
            }
        }
    }
}
