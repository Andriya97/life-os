package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary

@Composable
fun OnboardingScreen(
    viewModel: MainViewModel,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(1) }

    // Step 1: Personal info
    var name by remember { mutableStateOf("Alex Vance") }
    var age by remember { mutableStateOf("23") }
    var occupation by remember { mutableStateOf("Computer Science Student & Engineer") }
    var height by remember { mutableStateOf("178") }
    var weight by remember { mutableStateOf("72.5") }

    // Step 2: Lifestyle
    var wakeTime by remember { mutableStateOf("06:30") }
    var sleepTime by remember { mutableStateOf("23:00") }
    var studyHours by remember { mutableStateOf("4.0") }

    // Step 3: Targets
    var calories by remember { mutableStateOf("2200") }
    var protein by remember { mutableStateOf("140") }
    var waterMl by remember { mutableStateOf("3000") }
    var steps by remember { mutableStateOf("10000") }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("onboarding_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header & Step Indicator
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "LIFEOS ONBOARDING",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = BrandPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = when (currentStep) {
                        1 -> "Your Profile"
                        2 -> "Lifestyle & Routine"
                        3 -> "Health & Study Targets"
                        else -> "Setup Complete!"
                    },
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { currentStep / 4f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = BrandPrimary
                )
            }

            // Step Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (currentStep) {
                    1 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Tell us a bit about yourself to personalize your life metrics.", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = occupation, onValueChange = { occupation = it }, label = { Text("Primary Focus / Occupation") }, modifier = Modifier.fillMaxWidth())
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Height (cm)") }, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (kg)") }, modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    2 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Configure your circadian schedule and deep work blocks.", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            OutlinedTextField(value = wakeTime, onValueChange = { wakeTime = it }, label = { Text("Target Wake Time (e.g. 06:30)") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = sleepTime, onValueChange = { sleepTime = it }, label = { Text("Target Sleep Time (e.g. 23:00)") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = studyHours, onValueChange = { studyHours = it }, label = { Text("Available Study / Deep Work Hours/Day") }, modifier = Modifier.fillMaxWidth())
                        }
                    }

                    3 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Define your baseline daily nutrition and wellness goals.", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = calories, onValueChange = { calories = it }, label = { Text("Calories (kcal)") }, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = protein, onValueChange = { protein = it }, label = { Text("Protein (g)") }, modifier = Modifier.weight(1f))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = waterMl, onValueChange = { waterMl = it }, label = { Text("Water (ml)") }, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = steps, onValueChange = { steps = it }, label = { Text("Steps") }, modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    4 -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(72.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("You're all set to master your life!", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Your personalized Life Score, schedule blocks, and habit matrices are primed.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Navigation Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep > 1) {
                    OutlinedButton(
                        onClick = { currentStep -= 1 },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Back")
                    }
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Button(
                    onClick = {
                        if (currentStep < 4) {
                            currentStep += 1
                        } else {
                            viewModel.completeOnboarding(
                                name = name,
                                age = age.toIntOrNull() ?: 23,
                                gender = "Other",
                                heightCm = height.toFloatOrNull() ?: 178f,
                                weightKg = weight.toFloatOrNull() ?: 72.5f,
                                wakeTime = wakeTime,
                                sleepTime = sleepTime,
                                calories = calories.toIntOrNull() ?: 2200,
                                protein = protein.toIntOrNull() ?: 140,
                                waterMl = waterMl.toIntOrNull() ?: 3000,
                                steps = steps.toIntOrNull() ?: 10000,
                                studyHours = studyHours.toFloatOrNull() ?: 4f
                            )
                            onComplete()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text(if (currentStep < 4) "Next" else "Launch LifeOS")
                }
            }
        }
    }
}
