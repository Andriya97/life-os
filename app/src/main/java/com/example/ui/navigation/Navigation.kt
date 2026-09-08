package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    TODAY("Today", Icons.Filled.CalendarToday, Icons.Outlined.CalendarToday),
    ACADEMICS("Academics", Icons.Filled.School, Icons.Outlined.School),
    FITNESS("Fitness", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter),
    NUTRITION("Nutrition", Icons.Filled.Restaurant, Icons.Outlined.Restaurant),
    UPSKILLING("Upskilling", Icons.Filled.Code, Icons.Outlined.Code),
    HABITS("Habits", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircleOutline),
    GOALS("Goals", Icons.Filled.Flag, Icons.Outlined.Flag),
    ANALYTICS("Analytics", Icons.Filled.BarChart, Icons.Outlined.BarChart),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person);

    companion object {
        // Bottom bar primary items for mobile (keeps it clean, other items accessible from quick drawer/more)
        val primaryMobileItems = listOf(HOME, TODAY, ACADEMICS, FITNESS, NUTRITION)
        val secondaryMobileItems = listOf(UPSKILLING, HABITS, GOALS, ANALYTICS, PROFILE)
    }
}
