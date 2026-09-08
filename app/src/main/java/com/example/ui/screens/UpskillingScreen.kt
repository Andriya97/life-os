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
import com.example.data.model.Course
import com.example.ui.MainViewModel
import com.example.ui.components.EmptyState
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpskillingScreen(
    viewModel: MainViewModel,
    onOpenFocusTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val courses by viewModel.courses.collectAsState()
    val learningGoals by viewModel.learningGoals.collectAsState()
    val learningSessions by viewModel.learningSessions.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    var showAddCourseDialog by remember { mutableStateOf(false) }
    var showLogSessionDialog by remember { mutableStateOf(false) }

    val totalMins = learningSessions.sumOf { it.durationMinutes }
    val targetMins = profile?.targetLearningMinutes ?: 60

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showLogSessionDialog = true },
                modifier = Modifier
                    .padding(bottom = 72.dp)
                    .testTag("upskilling_fab"),
                containerColor = ColorUpskilling,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Log Learning")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("upskilling_screen_lazy_column"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ColorUpskilling.copy(alpha = 0.1f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ColorUpskilling.copy(alpha = 0.3f)))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "UPSKILLING ROADMAP",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = ColorUpskilling)
                                )
                                Text(
                                    text = "$totalMins min / $targetMins min today",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Button(
                                onClick = onOpenFocusTimer,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ColorUpskilling)
                            ) {
                                Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Focus")
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { (totalMins.toFloat() / targetMins).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = ColorUpskilling,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${courses.size} Active Courses • Consistent coding & technical study",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }

            // Active Courses
            item {
                SectionHeader(
                    title = "Active Tracks & Courses",
                    actionLabel = "+ Add Course",
                    onActionClick = { showAddCourseDialog = true }
                )
            }

            if (courses.isEmpty()) {
                item {
                    EmptyState(
                        title = "No courses added",
                        description = "Add skills, technical courses, or certifications you are pursuing.",
                        buttonText = "Add Course",
                        icon = Icons.Default.LaptopMac,
                        onButtonClick = { showAddCourseDialog = true }
                    )
                }
            } else {
                items(courses) { course ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                    Text(course.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                    Text("${course.platform} • ${course.completedTopics} of ${course.topicsCount} modules", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                }
                                Text("${course.progressPercent}%", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = ColorUpskilling))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { course.progressPercent / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = ColorUpskilling
                            )
                        }
                    }
                }
            }

            // Learning Sessions
            item {
                SectionHeader(
                    title = "Today's Learning Sessions",
                    actionLabel = "+ Log Session",
                    onActionClick = { showLogSessionDialog = true }
                )
            }

            if (learningSessions.isEmpty()) {
                item {
                    EmptyState(
                        title = "No learning sessions logged today",
                        description = "Log a practice coding session or course module.",
                        buttonText = "Log Session",
                        icon = Icons.Default.MenuBook,
                        onButtonClick = { showLogSessionDialog = true }
                    )
                }
            } else {
                items(learningSessions) { session ->
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
                                    .background(ColorUpskilling.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Code, contentDescription = null, tint = ColorUpskilling, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(session.courseTitle, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                                Text("${session.topic} • ${session.durationMinutes} mins", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                if (session.notes.isNotBlank()) {
                                    Text(session.notes, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Course Dialog
    if (showAddCourseDialog) {
        var title by remember { mutableStateOf("") }
        var platform by remember { mutableStateOf("Self-Paced") }
        var topicsCount by remember { mutableStateOf("10") }

        AlertDialog(
            onDismissRequest = { showAddCourseDialog = false },
            title = { Text("Add Course or Track") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Course Title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = platform, onValueChange = { platform = it }, label = { Text("Platform / Resource") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = topicsCount, onValueChange = { topicsCount = it }, label = { Text("Total Modules / Topics") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (title.isNotBlank()) {
                        viewModel.addCourse(1, title, platform, topicsCount.toIntOrNull() ?: 10)
                        showAddCourseDialog = false
                    }
                }) {
                    Text("Add Track")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCourseDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Log Session Dialog
    if (showLogSessionDialog) {
        var courseTitle by remember { mutableStateOf(courses.firstOrNull()?.title ?: "Modern Kotlin & Compose") }
        var topic by remember { mutableStateOf("") }
        var duration by remember { mutableStateOf("45") }
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showLogSessionDialog = false },
            title = { Text("Log Learning Session") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = courseTitle, onValueChange = { courseTitle = it }, label = { Text("Course / Track") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = topic, onValueChange = { topic = it }, label = { Text("Topic / Practical Work") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (mins)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Key Learnings / Notes") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (topic.isNotBlank()) {
                        viewModel.logLearningSession(courseTitle, topic, duration.toIntOrNull() ?: 45, notes)
                        showLogSessionDialog = false
                    }
                }) {
                    Text("Save Session")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogSessionDialog = false }) { Text("Cancel") }
            }
        )
    }
}
