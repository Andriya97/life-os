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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Goal
import com.example.data.model.TaskItem
import com.example.ui.MainViewModel
import com.example.ui.components.EmptyState
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsTasksScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val goals by viewModel.goals.collectAsState()
    val tasks by viewModel.tasks.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Goals, 1: Tasks
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) showAddGoalDialog = true else showAddTaskDialog = true
                },
                modifier = Modifier
                    .padding(bottom = 72.dp)
                    .testTag("goals_tasks_fab"),
                containerColor = BrandPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Long-Term Goals (${goals.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Daily Tasks (${tasks.count { !it.isCompleted }})") }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("goals_tasks_lazy_column"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (selectedTab == 0) {
                    // Goals Section
                    if (goals.isEmpty()) {
                        item {
                            EmptyState(
                                title = "No goals established",
                                description = "Set visionary Yearly, Monthly, and Weekly milestone goals.",
                                buttonText = "Create Goal",
                                icon = Icons.Default.Flag,
                                onButtonClick = { showAddGoalDialog = true }
                            )
                        }
                    } else {
                        items(goals) { goal ->
                            GoalCard(
                                goal = goal,
                                onProgressChange = { p -> viewModel.updateGoalProgress(goal, p) },
                                onDelete = { viewModel.deleteGoal(goal) }
                            )
                        }
                    }
                } else {
                    // Tasks Section
                    if (tasks.isEmpty()) {
                        item {
                            EmptyState(
                                title = "All tasks cleared",
                                description = "Maintain daily clarity by defining prioritized tasks.",
                                buttonText = "Add Task",
                                icon = Icons.Default.Checklist,
                                onButtonClick = { showAddTaskDialog = true }
                            )
                        }
                    } else {
                        items(tasks) { task ->
                            TaskCard(
                                task = task,
                                onToggle = { viewModel.toggleTask(task) },
                                onDelete = { viewModel.deleteTask(task) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Goal Dialog
    if (showAddGoalDialog) {
        var title by remember { mutableStateOf("") }
        var level by remember { mutableStateOf("Yearly") }
        var category by remember { mutableStateOf("Academics") }
        var desc by remember { mutableStateOf("") }
        var targetDate by remember { mutableStateOf("2026-12-31") }
        var priority by remember { mutableStateOf("High") }

        AlertDialog(
            onDismissRequest = { showAddGoalDialog = false },
            title = { Text("Create Visionary Goal") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Goal Title") }, modifier = Modifier.fillMaxWidth())

                    Text("Goal Horizon", style = MaterialTheme.typography.labelMedium)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Yearly", "Monthly", "Weekly").forEach { l ->
                            AssistChip(
                                onClick = { level = l },
                                label = { Text(l, fontSize = 11.sp) },
                                colors = AssistChipDefaults.assistChipColors(containerColor = if (level == l) BrandPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
                            )
                        }
                    }

                    OutlinedTextField(value = targetDate, onValueChange = { targetDate = it }, label = { Text("Target Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Key Milestones / Description") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (title.isNotBlank()) {
                        viewModel.addGoal(title, level, category, desc, targetDate, priority)
                        showAddGoalDialog = false
                    }
                }) {
                    Text("Save Goal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddGoalDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Add Task Dialog
    if (showAddTaskDialog) {
        var title by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("General") }
        var priority by remember { mutableStateOf("Medium") }
        var estMins by remember { mutableStateOf("30") }

        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text("Add Actionable Task") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Task Description") }, modifier = Modifier.fillMaxWidth())

                    Text("Priority Level", style = MaterialTheme.typography.labelMedium)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Low", "Medium", "High", "Critical").forEach { p ->
                            AssistChip(
                                onClick = { priority = p },
                                label = { Text(p, fontSize = 11.sp) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (priority == p) (if (p == "Critical") ColorDanger.copy(alpha = 0.2f) else BrandPrimary.copy(alpha = 0.2f)) else MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    }

                    OutlinedTextField(value = estMins, onValueChange = { estMins = it }, label = { Text("Estimated Duration (min)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (title.isNotBlank()) {
                        viewModel.addTask(title, "", category, priority, "Today", estMins.toIntOrNull() ?: 30)
                        showAddTaskDialog = false
                    }
                }) {
                    Text("Add Task")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun GoalCard(
    goal: Goal,
    onProgressChange: (Int) -> Unit,
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(goal.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("${goal.level} • Target: ${goal.targetDateString}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Progress", style = MaterialTheme.typography.labelSmall)
                Text("${goal.progressPercent}%", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = BrandPrimary))
            }
            Slider(
                value = goal.progressPercent.toFloat(),
                onValueChange = { onProgressChange(it.toInt()) },
                valueRange = 0f..100f
            )

            if (goal.description.isNotBlank()) {
                Text(goal.description, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp))
            }
        }
    }
}

@Composable
fun TaskCard(
    task: TaskItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val priorityColor = when (task.priority) {
        "Critical" -> ColorDanger
        "High" -> ColorFitness
        "Medium" -> BrandTertiary
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                    ),
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${task.priority} • ${task.estimatedMinutes} min • Due: ${task.dueDateString}",
                    style = MaterialTheme.typography.bodySmall.copy(color = priorityColor, fontWeight = FontWeight.Medium)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline)
            }
        }
    }
}
