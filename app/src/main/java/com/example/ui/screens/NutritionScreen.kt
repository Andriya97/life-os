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
import com.example.data.model.MealLog
import com.example.ui.MainViewModel
import com.example.ui.components.EmptyState
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val meals by viewModel.mealLogs.collectAsState()
    val totalWater by viewModel.totalWaterMl.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val foodItems by viewModel.foodItems.collectAsState()

    var showAddMealDialog by remember { mutableStateOf(false) }

    val totalCalories = meals.sumOf { it.calories }
    val targetCalories = profile?.targetCalories ?: 2200

    val totalProtein = meals.sumOf { it.protein.toDouble() }.toFloat()
    val targetProtein = (profile?.targetProteinGrams ?: 140).toFloat()

    val totalCarbs = meals.sumOf { it.carbs.toDouble() }.toFloat()
    val targetCarbs = (profile?.targetCarbsGrams ?: 250).toFloat()

    val totalFat = meals.sumOf { it.fat.toDouble() }.toFloat()
    val targetFat = (profile?.targetFatGrams ?: 70).toFloat()

    val targetWater = profile?.targetWaterMl ?: 3000

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddMealDialog = true },
                modifier = Modifier
                    .padding(bottom = 72.dp)
                    .testTag("nutrition_fab"),
                containerColor = ColorNutrition,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Log Meal")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("nutrition_screen_lazy_column"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Calorie & Macro Target Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ColorNutrition.copy(alpha = 0.1f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ColorNutrition.copy(alpha = 0.3f)))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "CALORIC TARGET",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = ColorNutrition)
                                )
                                Text(
                                    text = "$totalCalories / $targetCalories kcal",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Text(
                                text = "${targetCalories - totalCalories} kcal remaining",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { (totalCalories.toFloat() / targetCalories).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = ColorNutrition,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Macros breakdown
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MacroPill(label = "Protein", current = totalProtein.toInt(), target = targetProtein.toInt(), color = ColorFitness, modifier = Modifier.weight(1f))
                            MacroPill(label = "Carbs", current = totalCarbs.toInt(), target = targetCarbs.toInt(), color = ColorAcademics, modifier = Modifier.weight(1f))
                            MacroPill(label = "Fats", current = totalFat.toInt(), target = targetFat.toInt(), color = BrandTertiary, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Hydration Tracker
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(ColorHabits.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.WaterDrop, contentDescription = null, tint = ColorHabits, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Water Hydration", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    Text("${String.format(Locale.US, "%.1f", totalWater / 1000f)}L of ${String.format(Locale.US, "%.1f", targetWater / 1000f)}L goal", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                }
                            }

                            Text(
                                text = "${((totalWater.toFloat() / targetWater) * 100).toInt()}%",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = ColorHabits)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { (totalWater.toFloat() / targetWater).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = ColorHabits,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        // Quick Add Water Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(250, 500, 750).forEach { amount ->
                                OutlinedButton(
                                    onClick = { viewModel.logWater(amount) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("${amount}ml", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Meal Logs List
            item {
                SectionHeader(
                    title = "Today's Meals",
                    actionLabel = "+ Log Meal",
                    onActionClick = { showAddMealDialog = true }
                )
            }

            if (meals.isEmpty()) {
                item {
                    EmptyState(
                        title = "No meals logged today",
                        description = "Log breakfast, lunch, dinner, or snacks to track macro targets.",
                        buttonText = "Log First Meal",
                        icon = Icons.Default.Restaurant,
                        onButtonClick = { showAddMealDialog = true }
                    )
                }
            } else {
                items(meals) { meal ->
                    MealItemCard(
                        meal = meal,
                        onDelete = { viewModel.deleteMealLog(meal) }
                    )
                }
            }
        }
    }

    // Add Meal Dialog
    if (showAddMealDialog) {
        var foodName by remember { mutableStateOf("") }
        var mealType by remember { mutableStateOf("Lunch") }
        var portion by remember { mutableStateOf("1 serving") }
        var calories by remember { mutableStateOf("450") }
        var protein by remember { mutableStateOf("30") }
        var carbs by remember { mutableStateOf("50") }
        var fat by remember { mutableStateOf("12") }

        AlertDialog(
            onDismissRequest = { showAddMealDialog = false },
            title = { Text("Log Meal / Food") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Meal Category", style = MaterialTheme.typography.labelMedium)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Breakfast", "Lunch", "Dinner", "Snacks").forEach { cat ->
                            AssistChip(
                                onClick = { mealType = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (mealType == cat) ColorNutrition.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    }

                    OutlinedTextField(value = foodName, onValueChange = { foodName = it }, label = { Text("Food / Meal Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = portion, onValueChange = { portion = it }, label = { Text("Portion (e.g. 1 bowl, 300g)") }, modifier = Modifier.fillMaxWidth())

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = calories, onValueChange = { calories = it }, label = { Text("Calories") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = protein, onValueChange = { protein = it }, label = { Text("Protein (g)") }, modifier = Modifier.weight(1f))
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = carbs, onValueChange = { carbs = it }, label = { Text("Carbs (g)") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = fat, onValueChange = { fat = it }, label = { Text("Fat (g)") }, modifier = Modifier.weight(1f))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (foodName.isNotBlank()) {
                            viewModel.logMeal(
                                mealType = mealType,
                                foodName = foodName,
                                portion = portion,
                                calories = calories.toIntOrNull() ?: 400,
                                protein = protein.toFloatOrNull() ?: 20f,
                                carbs = carbs.toFloatOrNull() ?: 40f,
                                fat = fat.toFloatOrNull() ?: 10f,
                                fiber = 4f
                            )
                            showAddMealDialog = false
                        }
                    }
                ) {
                    Text("Save Meal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMealDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun MacroPill(label: String, current: Int, target: Int, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant))
            Text("${current}g / ${target}g", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = color))
        }
    }
}

@Composable
fun MealItemCard(meal: MealLog, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                    .background(ColorNutrition.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Restaurant, contentDescription = null, tint = ColorNutrition, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(meal.foodName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                Text("${meal.mealType} • ${meal.portion} • ${meal.calories} kcal", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                Text("P: ${meal.protein.toInt()}g | C: ${meal.carbs.toInt()}g | F: ${meal.fat.toInt()}g", style = MaterialTheme.typography.labelSmall.copy(color = ColorNutrition))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline)
            }
        }
    }
}
