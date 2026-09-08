package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Alex Vance",
    val email: String = "alex@lifeos.app",
    val age: Int = 22,
    val gender: String = "Other",
    val heightCm: Float = 178f,
    val weightKg: Float = 72.5f,
    val country: String = "United States",
    val timezone: String = "UTC-7",
    // Lifestyle
    val wakeTime: String = "06:30",
    val sleepTime: String = "23:00",
    val availableStudyHours: Float = 4.0f,
    val activityLevel: String = "Moderate",
    val occupationStatus: String = "Student & Developer",
    // Targets
    val targetCalories: Int = 2200,
    val targetProteinGrams: Int = 140,
    val targetCarbsGrams: Int = 250,
    val targetFatGrams: Int = 70,
    val targetFiberGrams: Int = 30,
    val targetWaterMl: Int = 3000,
    val targetSteps: Int = 10000,
    val targetSleepHours: Float = 8.0f,
    val targetStudyMinutes: Int = 240, // 4 hours
    val targetExerciseMinutes: Int = 45,
    val targetLearningMinutes: Int = 60, // 1 hour
    // Life Score Weights (Sum to 100)
    val weightAcademics: Int = 20,
    val weightFitness: Int = 15,
    val weightNutrition: Int = 15,
    val weightUpskilling: Int = 15,
    val weightHabits: Int = 10,
    val weightSleep: Int = 15,
    val weightProductivity: Int = 10,
    // Gamification & State
    val xp: Int = 1850,
    val level: Int = 7,
    val currentStreak: Int = 14,
    val longestStreak: Int = 32,
    val isOnboardingCompleted: Boolean = true,
    val aiCoachEnabled: Boolean = true
)

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val code: String,
    val professor: String,
    val semester: String,
    val colorHex: String = "#3B82F6",
    val targetGrade: String = "A"
)

@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectName: String,
    val dateString: String, // YYYY-MM-DD
    val durationMinutes: Int,
    val studyMethod: String = "Pomodoro",
    val focusRating: Int = 4, // 1 - 5
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "assignments")
data class Assignment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subjectName: String,
    val description: String = "",
    val dueDateString: String, // YYYY-MM-DD
    val priority: String = "Medium", // Low, Medium, High, Critical
    val status: String = "In progress", // Not started, In progress, Completed, Overdue
    val estimatedMinutes: Int = 60,
    val actualMinutes: Int = 0
)

@Entity(tableName = "exams")
data class Exam(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectName: String,
    val examName: String,
    val examDateString: String, // YYYY-MM-DD
    val syllabus: String = "",
    val prepPercentage: Int = 60, // 0 - 100
    val expectedGrade: String = "A",
    val actualGrade: String = ""
)

@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val workoutType: String = "Strength", // Strength, Running, Walking, Cycling, HIIT, Yoga, Sports, Custom
    val dateString: String, // YYYY-MM-DD
    val durationMinutes: Int = 45,
    val caloriesBurned: Int = 320,
    val notes: String = ""
)

@Entity(tableName = "workout_exercises")
data class WorkoutExercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long,
    val exerciseName: String,
    val setsCount: Int = 3,
    val repsDetails: String = "10, 8, 8",
    val weightDetails: String = "60kg, 65kg, 65kg",
    val restSeconds: Int = 90,
    val rpe: Int = 8 // Rate of Perceived Exertion (1-10)
)

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String = "Lunch", // Breakfast, Lunch, Dinner, Snacks
    val servingSize: String = "100g",
    val calories: Int,
    val proteinGrams: Float,
    val carbsGrams: Float,
    val fatGrams: Float,
    val fiberGrams: Float = 0f,
    val isFavorite: Boolean = false
)

@Entity(tableName = "meal_logs")
data class MealLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String, // YYYY-MM-DD
    val mealType: String, // Breakfast, Lunch, Dinner, Snacks
    val foodName: String,
    val portion: String = "1 serving",
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val fiber: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "water_logs")
data class WaterLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String, // YYYY-MM-DD
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "sleep_logs")
data class SleepLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String, // YYYY-MM-DD
    val bedtime: String = "23:15",
    val wakeTime: String = "06:45",
    val durationMinutes: Int = 450, // 7.5 hrs
    val qualityRating: Int = 4, // 1 - 5
    val notes: String = "Felt well rested and energized"
)

@Entity(tableName = "mood_logs")
data class MoodLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String, // YYYY-MM-DD
    val moodScore: Int = 8, // 1 - 10
    val energyScore: Int = 8, // 1 - 10
    val stressScore: Int = 3, // 1 - 10
    val biggestWin: String = "",
    val improvementArea: String = "",
    val gratitude: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "learning_goals")
data class LearningGoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetTrack: String,
    val totalCourses: Int = 4,
    val completedCourses: Int = 2,
    val progressPercent: Int = 50
)

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val learningGoalId: Long = 1,
    val title: String,
    val platform: String = "Self-Paced",
    val topicsCount: Int = 12,
    val completedTopics: Int = 7,
    val progressPercent: Int = 58
)

@Entity(tableName = "learning_sessions")
data class LearningSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseTitle: String,
    val topic: String,
    val durationMinutes: Int,
    val dateString: String, // YYYY-MM-DD
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconName: String = "Check",
    val category: String = "Health",
    val frequency: String = "Daily", // Daily, Weekdays, Weekly
    val reminderTime: String = "07:00",
    val colorHex: String = "#06B6D4",
    val currentStreak: Int = 5,
    val bestStreak: Int = 18
)

@Entity(tableName = "habit_completions")
data class HabitCompletion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val dateString: String, // YYYY-MM-DD
    val isCompleted: Boolean = true
)

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val level: String = "Monthly", // Yearly, Monthly, Weekly, DailyAction
    val parentGoalId: Long? = null,
    val category: String = "Productivity",
    val description: String = "",
    val startDateString: String = "",
    val targetDateString: String = "",
    val progressPercent: Int = 35,
    val priority: String = "High",
    val status: String = "Active" // Not started, Active, Completed, Paused
)

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "General",
    val priority: String = "Medium", // Low, Medium, High, Critical
    val dueDateString: String,
    val estimatedMinutes: Int = 30,
    val actualMinutes: Int = 0,
    val isCompleted: Boolean = false,
    val recurringType: String = "None" // None, Daily, Weekly
)

@Entity(tableName = "schedule_events")
data class ScheduleEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String = "Study", // Wake, Meal, College, Study, Workout, Learning, Sleep, Personal
    val startTime: String, // e.g. "08:00"
    val endTime: String,   // e.g. "09:30"
    val dateString: String, // YYYY-MM-DD
    val isCompleted: Boolean = false,
    val sortOrder: Int = 0
)

@Entity(tableName = "daily_reviews")
data class DailyReview(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String, // YYYY-MM-DD
    val lifeScore: Int,
    val biggestWin: String,
    val improvementGoal: String,
    val gratitudeNotes: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_scores")
data class DailyScore(
    @PrimaryKey val dateString: String, // YYYY-MM-DD
    val totalScore: Int,
    val academicsScore: Int,
    val fitnessScore: Int,
    val nutritionScore: Int,
    val upskillingScore: Int,
    val habitsScore: Int,
    val sleepScore: Int,
    val productivityScore: Int
)
