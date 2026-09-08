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
import com.example.data.model.Assignment
import com.example.data.model.Exam
import com.example.data.model.Subject
import com.example.ui.MainViewModel
import com.example.ui.components.EmptyState
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicsScreen(
    viewModel: MainViewModel,
    onOpenFocusTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.subjects.collectAsState()
    val studySessions by viewModel.studySessions.collectAsState()
    val assignments by viewModel.assignments.collectAsState()
    val exams by viewModel.exams.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Overview & Study, 1: Assignments, 2: Exams, 3: Subjects
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showAddAssignmentDialog by remember { mutableStateOf(false) }
    var showAddExamDialog by remember { mutableStateOf(false) }
    var showLogStudyDialog by remember { mutableStateOf(false) }

    val totalStudyMinutes = studySessions.sumOf { it.durationMinutes }
    val avgFocus = if (studySessions.isNotEmpty()) studySessions.map { it.focusRating }.average() else 4.0

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    when (selectedTab) {
                        0 -> showLogStudyDialog = true
                        1 -> showAddAssignmentDialog = true
                        2 -> showAddExamDialog = true
                        3 -> showAddSubjectDialog = true
                    }
                },
                modifier = Modifier
                    .padding(bottom = 72.dp)
                    .testTag("academics_fab"),
                containerColor = ColorAcademics,
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
            // Tabs
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Study") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Tasks (${assignments.count { it.status != "Completed" }})") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Exams (${exams.size})") }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Courses") }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("academics_lazy_column"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // Study Overview Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = ColorAcademics.copy(alpha = 0.1f)),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ColorAcademics.copy(alpha = 0.3f)))
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "TODAY'S STUDY METRICS",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = ColorAcademics)
                                            )
                                            Text(
                                                text = "${totalStudyMinutes / 60}h ${totalStudyMinutes % 60}m Focused",
                                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }

                                        Button(
                                            onClick = onOpenFocusTimer,
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = ColorAcademics)
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Timer")
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Avg Focus: ★ ${String.format(java.util.Locale.US, "%.1f", avgFocus)}/5.0",
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        )
                                        Text(
                                            text = "${studySessions.size} deep work sessions logged",
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            SectionHeader(
                                title = "Study Sessions",
                                actionLabel = "+ Log Session",
                                onActionClick = { showLogStudyDialog = true }
                            )
                        }

                        if (studySessions.isEmpty()) {
                            item {
                                EmptyState(
                                    title = "No study sessions yet",
                                    description = "Start your focus timer or log a completed study block.",
                                    buttonText = "Log Study Session",
                                    icon = Icons.Default.MenuBook,
                                    onButtonClick = { showLogStudyDialog = true }
                                )
                            }
                        } else {
                            items(studySessions) { session ->
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
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(ColorAcademics.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.School, contentDescription = null, tint = ColorAcademics, modifier = Modifier.size(20.dp))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = session.subjectName,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "${session.durationMinutes} mins • ${session.studyMethod} • Focus: ★${session.focusRating}",
                                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            )
                                            if (session.notes.isNotBlank()) {
                                                Text(
                                                    text = session.notes,
                                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp),
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Assignments Tab
                        if (assignments.isEmpty()) {
                            item {
                                EmptyState(
                                    title = "No assignments yet",
                                    description = "Add projects, lab assignments, and term papers to track deadlines.",
                                    buttonText = "Add Assignment",
                                    icon = Icons.Default.Assignment,
                                    onButtonClick = { showAddAssignmentDialog = true }
                                )
                            }
                        } else {
                            items(assignments) { assignment ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (assignment.status == "Completed") MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = assignment.status == "Completed",
                                            onCheckedChange = { viewModel.toggleAssignmentStatus(assignment) }
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = assignment.title,
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.SemiBold,
                                                    textDecoration = if (assignment.status == "Completed") androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                                )
                                            )
                                            Text(
                                                text = "${assignment.subjectName} • Due: ${assignment.dueDateString} • ${assignment.priority}",
                                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            )
                                        }
                                        IconButton(onClick = { viewModel.deleteAssignment(assignment) }) {
                                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Exams Tab
                        if (exams.isEmpty()) {
                            item {
                                EmptyState(
                                    title = "No upcoming exams",
                                    description = "Keep syllabus coverage and expected grades on track for finals.",
                                    buttonText = "Add Exam",
                                    icon = Icons.Default.Quiz,
                                    onButtonClick = { showAddExamDialog = true }
                                )
                            }
                        } else {
                            items(exams) { exam ->
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
                                            Column {
                                                Text(exam.examName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                                Text("${exam.subjectName} • Exam Date: ${exam.examDateString}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = ColorAcademics.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = "Target: ${exam.expectedGrade}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ColorAcademics),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Syllabus Preparation", style = MaterialTheme.typography.labelSmall)
                                            Text("${exam.prepPercentage}%", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress = { exam.prepPercentage / 100f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(CircleShape),
                                            color = ColorAcademics
                                        )
                                        if (exam.syllabus.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Syllabus: ${exam.syllabus}",
                                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        // Subjects Tab
                        if (subjects.isEmpty()) {
                            item {
                                EmptyState(
                                    title = "No subjects configured",
                                    description = "Add your academic courses, professors, and target grades.",
                                    buttonText = "Add Subject",
                                    icon = Icons.Default.School,
                                    onButtonClick = { showAddSubjectDialog = true }
                                )
                            }
                        } else {
                            items(subjects) { subject ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(Color(android.graphics.Color.parseColor(subject.colorHex)))
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(subject.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                            Text("${subject.code} • ${subject.professor}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                            Text("Semester: ${subject.semester} • Target: ${subject.targetGrade}", style = MaterialTheme.typography.labelSmall.copy(color = ColorAcademics))
                                        }
                                        IconButton(onClick = { viewModel.deleteSubject(subject) }) {
                                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Subject Dialog
    if (showAddSubjectDialog) {
        var name by remember { mutableStateOf("") }
        var code by remember { mutableStateOf("") }
        var prof by remember { mutableStateOf("") }
        var sem by remember { mutableStateOf("Fall 2026") }
        var grade by remember { mutableStateOf("A") }

        AlertDialog(
            onDismissRequest = { showAddSubjectDialog = false },
            title = { Text("Add Subject / Course") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Subject Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Course Code (e.g. CS-441)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = prof, onValueChange = { prof = it }, label = { Text("Professor / Teacher") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = sem, onValueChange = { sem = it }, label = { Text("Semester") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = grade, onValueChange = { grade = it }, label = { Text("Target Grade") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (name.isNotBlank()) {
                        viewModel.addSubject(name, code, prof, sem, grade, "#3B82F6")
                        showAddSubjectDialog = false
                    }
                }) {
                    Text("Save Subject")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSubjectDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Add Assignment Dialog
    if (showAddAssignmentDialog) {
        var title by remember { mutableStateOf("") }
        var subjectName by remember { mutableStateOf(subjects.firstOrNull()?.name ?: "General") }
        var dueDate by remember { mutableStateOf("2026-09-15") }
        var priority by remember { mutableStateOf("Medium") }
        var estMins by remember { mutableStateOf("60") }

        AlertDialog(
            onDismissRequest = { showAddAssignmentDialog = false },
            title = { Text("Add Assignment") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Assignment Title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = subjectName, onValueChange = { subjectName = it }, label = { Text("Subject Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Due Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = estMins, onValueChange = { estMins = it }, label = { Text("Estimated Minutes") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (title.isNotBlank()) {
                        viewModel.addAssignment(title, subjectName, dueDate, priority, estMins.toIntOrNull() ?: 60)
                        showAddAssignmentDialog = false
                    }
                }) {
                    Text("Add Assignment")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAssignmentDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Add Exam Dialog
    if (showAddExamDialog) {
        var examName by remember { mutableStateOf("") }
        var subName by remember { mutableStateOf(subjects.firstOrNull()?.name ?: "General") }
        var examDate by remember { mutableStateOf("2026-09-30") }
        var syllabus by remember { mutableStateOf("") }
        var prepPercent by remember { mutableStateOf("50") }

        AlertDialog(
            onDismissRequest = { showAddExamDialog = false },
            title = { Text("Add Exam") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = examName, onValueChange = { examName = it }, label = { Text("Exam Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = subName, onValueChange = { subName = it }, label = { Text("Subject") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = examDate, onValueChange = { examDate = it }, label = { Text("Exam Date") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = syllabus, onValueChange = { syllabus = it }, label = { Text("Syllabus Topics") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = prepPercent, onValueChange = { prepPercent = it }, label = { Text("Prep % (0-100)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (examName.isNotBlank()) {
                        viewModel.addExam(subName, examName, examDate, syllabus, prepPercent.toIntOrNull() ?: 50, "A")
                        showAddExamDialog = false
                    }
                }) {
                    Text("Save Exam")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddExamDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Log Study Dialog
    if (showLogStudyDialog) {
        var subName by remember { mutableStateOf(subjects.firstOrNull()?.name ?: "Distributed Computing") }
        var duration by remember { mutableStateOf("60") }
        var method by remember { mutableStateOf("Pomodoro") }
        var focusRating by remember { mutableIntStateOf(4) }
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showLogStudyDialog = false },
            title = { Text("Log Study Session") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = subName, onValueChange = { subName = it }, label = { Text("Subject") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (minutes)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = method, onValueChange = { method = it }, label = { Text("Method (e.g. Pomodoro, Recall)") }, modifier = Modifier.fillMaxWidth())
                    Text("Focus Quality: ★ $focusRating/5", style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = focusRating.toFloat(),
                        onValueChange = { focusRating = it.toInt() },
                        valueRange = 1f..5f,
                        steps = 3
                    )
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes / Concepts covered") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.addStudySession(subName, duration.toIntOrNull() ?: 60, method, focusRating, notes)
                    showLogStudyDialog = false
                }) {
                    Text("Save Session")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogStudyDialog = false }) { Text("Cancel") }
            }
        )
    }
}
