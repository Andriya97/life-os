package com.example.domain

import com.example.data.model.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class LifeScoreBreakdown(
    val overallScore: Int,
    val academicsScore: Int,
    val fitnessScore: Int,
    val nutritionScore: Int,
    val upskillingScore: Int,
    val habitsScore: Int,
    val sleepScore: Int,
    val productivityScore: Int,
    val xpEarnedToday: Int
)

object LifeScoreEngine {

    fun calculateScore(
        profile: UserProfile,
        studySessions: List<StudySession>,
        workouts: List<Workout>,
        meals: List<MealLog>,
        waterTotalMl: Int,
        sleepLog: SleepLog?,
        learningSessions: List<LearningSession>,
        habits: List<Habit>,
        habitCompletions: List<HabitCompletion>,
        tasks: List<TaskItem>
    ): LifeScoreBreakdown {
        // 1. Academics Score (0 - 100)
        val totalStudyMinutes = studySessions.sumOf { it.durationMinutes }
        val targetStudy = max(1, profile.targetStudyMinutes)
        val academicsScore = min(100, (totalStudyMinutes * 100) / targetStudy)

        // 2. Fitness Score (0 - 100)
        val workoutDone = workouts.isNotEmpty()
        val totalWorkoutMinutes = workouts.sumOf { it.durationMinutes }
        val targetExercise = max(1, profile.targetExerciseMinutes)
        val workoutScorePart = if (workoutDone) min(60, (totalWorkoutMinutes * 60) / targetExercise) else 0
        // steps estimation/log
        val stepsRatioScore = 35 // Base healthy steps component
        val fitnessScore = min(100, workoutScorePart + stepsRatioScore + if (workoutDone) 5 else 0)

        // 3. Nutrition Score (0 - 100)
        val totalCalories = meals.sumOf { it.calories }
        val totalProtein = meals.sumOf { it.protein.toDouble() }.toFloat()
        val targetCal = max(1, profile.targetCalories)
        val targetProtein = max(1, profile.targetProteinGrams)

        val calDiffRatio = abs(totalCalories - targetCal).toFloat() / targetCal
        val calScore = max(0, (50 - (calDiffRatio * 50)).toInt())
        val proteinScore = min(50, ((totalProtein / targetProtein) * 50).toInt())
        val nutritionScore = min(100, max(0, calScore + proteinScore))

        // 4. Upskilling Score (0 - 100)
        val totalLearningMinutes = learningSessions.sumOf { it.durationMinutes }
        val targetLearning = max(1, profile.targetLearningMinutes)
        val upskillingScore = min(100, (totalLearningMinutes * 100) / targetLearning)

        // 5. Habits Score (0 - 100)
        val totalHabitsCount = max(1, habits.size)
        val completedHabitsCount = habitCompletions.size
        val habitsScore = min(100, (completedHabitsCount * 100) / totalHabitsCount)

        // 6. Sleep Score (0 - 100)
        val sleepMinutes = sleepLog?.durationMinutes ?: (7.5 * 60).toInt()
        val targetSleepMinutes = (profile.targetSleepHours * 60).toInt()
        val sleepScore = min(100, (sleepMinutes * 100) / targetSleepMinutes)

        // 7. Productivity Score (0 - 100)
        val plannedTasks = tasks.size
        val completedTasks = tasks.count { it.isCompleted }
        val productivityScore = if (plannedTasks > 0) {
            min(100, (completedTasks * 100) / plannedTasks)
        } else {
            80 // Sensible baseline when tasks are cleared
        }

        // Weighted Average
        val totalWeight = (profile.weightAcademics + profile.weightFitness + profile.weightNutrition +
                profile.weightUpskilling + profile.weightHabits + profile.weightSleep + profile.weightProductivity)
            .coerceAtLeast(1)

        val weightedSum = (
                academicsScore * profile.weightAcademics +
                        fitnessScore * profile.weightFitness +
                        nutritionScore * profile.weightNutrition +
                        upskillingScore * profile.weightUpskilling +
                        habitsScore * profile.weightHabits +
                        sleepScore * profile.weightSleep +
                        productivityScore * profile.weightProductivity
                )

        val overall = min(100, weightedSum / totalWeight)

        // XP earned from activities
        val xpEarned = (academicsScore * 2) + (fitnessScore * 2) + (habitsScore * 1) + (overall * 3)

        return LifeScoreBreakdown(
            overallScore = overall,
            academicsScore = academicsScore,
            fitnessScore = fitnessScore,
            nutritionScore = nutritionScore,
            upskillingScore = upskillingScore,
            habitsScore = habitsScore,
            sleepScore = sleepScore,
            productivityScore = productivityScore,
            xpEarnedToday = xpEarned
        )
    }
}
