package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LifeOsTheme {
                LifeOsApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeOsApp(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var showMoreMenuSheet by remember { mutableStateOf(false) }
    var showQuickAddSheet by remember { mutableStateOf(false) }
    var showMorningPlanner by remember { mutableStateOf(false) }
    var showEveningReview by remember { mutableStateOf(false) }
    var showFocusTimer by remember { mutableStateOf(false) }
    var isForceOnboarding by remember { mutableStateOf(false) }

    // If onboarding is not completed, display OnboardingScreen
    val needsOnboarding = profile != null && (!profile!!.isOnboardingCompleted || isForceOnboarding)

    if (needsOnboarding) {
        OnboardingScreen(
            viewModel = viewModel,
            onComplete = { isForceOnboarding = false }
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(BrandPrimary, BrandSecondary))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "L",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "LifeOS",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            )
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showFocusTimer = true },
                        modifier = Modifier.testTag("topbar_timer_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Focus Timer",
                            tint = BrandPrimary
                        )
                    }
                    IconButton(
                        onClick = { showMoreMenuSheet = true },
                        modifier = Modifier.testTag("topbar_more_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = "More Modules",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("lifeos_bottom_navigation")
            ) {
                Screen.primaryMobileItems.forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandPrimary,
                            selectedTextColor = BrandPrimary,
                            indicatorColor = BrandPrimary.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentScreen, label = "screenTransition") { screen ->
                when (screen) {
                    Screen.HOME -> HomeScreen(
                        viewModel = viewModel,
                        onNavigate = { currentScreen = it },
                        onOpenQuickAdd = { showQuickAddSheet = true },
                        onOpenMorningPlanner = { showMorningPlanner = true },
                        onOpenEveningReview = { showEveningReview = true },
                        onOpenFocusTimer = { showFocusTimer = true }
                    )
                    Screen.TODAY -> TodayScreen(viewModel = viewModel)
                    Screen.ACADEMICS -> AcademicsScreen(viewModel = viewModel, onOpenFocusTimer = { showFocusTimer = true })
                    Screen.FITNESS -> FitnessScreen(viewModel = viewModel)
                    Screen.NUTRITION -> NutritionScreen(viewModel = viewModel)
                    Screen.UPSKILLING -> UpskillingScreen(viewModel = viewModel, onOpenFocusTimer = { showFocusTimer = true })
                    Screen.HABITS -> HabitsScreen(viewModel = viewModel)
                    Screen.GOALS -> GoalsTasksScreen(viewModel = viewModel)
                    Screen.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
                    Screen.PROFILE -> ProfileSettingsScreen(viewModel = viewModel, onResetOnboarding = { isForceOnboarding = true })
                }
            }
        }
    }

    // Modal Sheets & Dialogs
    if (showQuickAddSheet) {
        QuickAddSheet(
            viewModel = viewModel,
            onNavigate = { target ->
                currentScreen = target
                showQuickAddSheet = false
            },
            onDismiss = { showQuickAddSheet = false }
        )
    }

    if (showMorningPlanner) {
        MorningPlannerDialog(
            viewModel = viewModel,
            onDismiss = { showMorningPlanner = false }
        )
    }

    if (showEveningReview) {
        EveningReviewDialog(
            viewModel = viewModel,
            onDismiss = { showEveningReview = false }
        )
    }

    if (showFocusTimer) {
        FocusTimerDialog(
            viewModel = viewModel,
            onDismiss = { showFocusTimer = false }
        )
    }

    if (showMoreMenuSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMoreMenuSheet = false },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "All LifeOS Modules",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Screen.entries.chunked(3).forEach { rowScreens ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        rowScreens.forEach { screen ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(90.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        currentScreen = screen
                                        showMoreMenuSheet = false
                                    }
                                    .padding(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(if (currentScreen == screen) BrandPrimary else MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = screen.selectedIcon,
                                        contentDescription = screen.title,
                                        tint = if (currentScreen == screen) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = screen.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (currentScreen == screen) FontWeight.Bold else FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
