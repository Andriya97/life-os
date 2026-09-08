package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.LifeOsDao
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        UserProfile::class,
        Subject::class,
        StudySession::class,
        Assignment::class,
        Exam::class,
        Workout::class,
        WorkoutExercise::class,
        FoodItem::class,
        MealLog::class,
        WaterLog::class,
        SleepLog::class,
        MoodLog::class,
        LearningGoal::class,
        Course::class,
        LearningSession::class,
        Habit::class,
        HabitCompletion::class,
        Goal::class,
        TaskItem::class,
        ScheduleEvent::class,
        DailyReview::class,
        DailyScore::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LifeOsDatabase : RoomDatabase() {
    abstract fun lifeOsDao(): LifeOsDao

    companion object {
        @Volatile
        private var INSTANCE: LifeOsDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): LifeOsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeOsDatabase::class.java,
                    "lifeos_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.lifeOsDao())
                    }
                }
            }
        }

        private suspend fun populateInitialData(dao: LifeOsDao) {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // 1. User Profile
            dao.insertOrUpdateUserProfile(
                UserProfile(
                    id = 1,
                    name = "Alex Vance",
                    email = "alex@lifeos.app",
                    age = 23,
                    gender = "Other",
                    heightCm = 178f,
                    weightKg = 72.5f,
                    country = "United States",
                    timezone = "UTC-7",
                    wakeTime = "06:30",
                    sleepTime = "23:00",
                    availableStudyHours = 4.0f,
                    activityLevel = "Active",
                    occupationStatus = "Computer Science Student & Engineer",
                    targetCalories = 2200,
                    targetProteinGrams = 140,
                    targetCarbsGrams = 250,
                    targetFatGrams = 70,
                    targetFiberGrams = 30,
                    targetWaterMl = 3000,
                    targetSteps = 10000,
                    targetSleepHours = 8.0f,
                    targetStudyMinutes = 240,
                    targetExerciseMinutes = 45,
                    targetLearningMinutes = 60,
                    xp = 2450,
                    level = 8,
                    currentStreak = 14,
                    longestStreak = 28,
                    isOnboardingCompleted = true
                )
            )

            // 2. Schedule Events (Timeline)
            val events = listOf(
                ScheduleEvent(title = "Morning Wake & Hydration", category = "Wake", startTime = "06:30", endTime = "07:00", dateString = todayStr, isCompleted = true, sortOrder = 1),
                ScheduleEvent(title = "High Protein Breakfast", category = "Meal", startTime = "07:00", endTime = "07:45", dateString = todayStr, isCompleted = true, sortOrder = 2),
                ScheduleEvent(title = "Distributed Systems Lecture", category = "College", startTime = "08:00", endTime = "10:00", dateString = todayStr, isCompleted = true, sortOrder = 3),
                ScheduleEvent(title = "Algorithms & Deep Work Session", category = "Study", startTime = "10:30", endTime = "12:30", dateString = todayStr, isCompleted = true, sortOrder = 4),
                ScheduleEvent(title = "Fuel & Recovery Lunch", category = "Meal", startTime = "13:00", endTime = "13:45", dateString = todayStr, isCompleted = true, sortOrder = 5),
                ScheduleEvent(title = "Strength Hypertrophy Session", category = "Workout", startTime = "17:00", endTime = "18:00", dateString = todayStr, isCompleted = true, sortOrder = 6),
                ScheduleEvent(title = "Full-Stack System Architecture Coding", category = "Learning", startTime = "19:00", endTime = "20:00", dateString = todayStr, isCompleted = false, sortOrder = 7),
                ScheduleEvent(title = "Dinner & Family Time", category = "Meal", startTime = "20:30", endTime = "21:30", dateString = todayStr, isCompleted = false, sortOrder = 8),
                ScheduleEvent(title = "Reading & Wind-Down Protocol", category = "Personal", startTime = "22:00", endTime = "22:45", dateString = todayStr, isCompleted = false, sortOrder = 9),
                ScheduleEvent(title = "Sleep & Optimal Recovery", category = "Sleep", startTime = "23:00", endTime = "06:30", dateString = todayStr, isCompleted = false, sortOrder = 10)
            )
            for (ev in events) dao.insertScheduleEvent(ev)

            // 3. Subjects & Academics
            dao.insertSubject(Subject(name = "Distributed Computing", code = "CS-441", professor = "Dr. Katherine Shaw", semester = "Fall 2026", colorHex = "#3B82F6", targetGrade = "A"))
            dao.insertSubject(Subject(name = "Machine Learning Systems", code = "CS-482", professor = "Prof. Marcus Thorne", semester = "Fall 2026", colorHex = "#8B5CF6", targetGrade = "A"))
            dao.insertSubject(Subject(name = "Advanced Database Engines", code = "CS-415", professor = "Dr. Elena Rostova", semester = "Fall 2026", colorHex = "#10B981", targetGrade = "A-"))
            dao.insertSubject(Subject(name = "Cybersecurity & Cryptography", code = "CS-460", professor = "Prof. David Hayes", semester = "Fall 2026", colorHex = "#F59E0B", targetGrade = "A"))

            // Study sessions today
            dao.insertStudySession(StudySession(subjectName = "Distributed Computing", dateString = todayStr, durationMinutes = 110, studyMethod = "Pomodoro", focusRating = 5, notes = "Raft Consensus leader election analysis"))
            dao.insertStudySession(StudySession(subjectName = "Machine Learning Systems", dateString = todayStr, durationMinutes = 50, studyMethod = "Active Recall", focusRating = 4, notes = "Transformer multi-head attention forward pass derivation"))

            // Assignments
            dao.insertAssignment(Assignment(title = "Raft Algorithm Cluster Implementation", subjectName = "Distributed Computing", description = "Implement election timeouts & heartbeats in Go/Kotlin", dueDateString = todayStr, priority = "High", status = "In progress", estimatedMinutes = 180, actualMinutes = 90))
            dao.insertAssignment(Assignment(title = "Fine-Tuning LoRA on Vision Embeddings", subjectName = "Machine Learning Systems", description = "Evaluate cross-entropy loss vs prompt length", dueDateString = "2026-09-12", priority = "Critical", status = "Not started", estimatedMinutes = 240))
            dao.insertAssignment(Assignment(title = "B+ Tree Disk Page Split Benchmark", subjectName = "Advanced Database Engines", description = "Optimize page size to 4KB with latch concurrency", dueDateString = "2026-09-15", priority = "Medium", status = "Not started", estimatedMinutes = 120))

            // Exams
            dao.insertExam(Exam(subjectName = "Distributed Computing", examName = "Midterm Examination", examDateString = "2026-09-24", syllabus = "Consistency models, Paxos, Raft, Vector Clocks", prepPercentage = 75, expectedGrade = "A"))
            dao.insertExam(Exam(subjectName = "Machine Learning Systems", examName = "System Design & Modeling Exam", examDateString = "2026-09-29", syllabus = "Backprop math, Attention, KV Cache optimization", prepPercentage = 55, expectedGrade = "A"))

            // 4. Fitness
            val workoutId = dao.insertWorkout(Workout(name = "Hypertrophy Upper Body & Core", workoutType = "Strength", dateString = todayStr, durationMinutes = 55, caloriesBurned = 410, notes = "Felt great power output on bench press sets!"))
            dao.insertWorkoutExercise(WorkoutExercise(workoutId = workoutId, exerciseName = "Barbell Bench Press", setsCount = 3, repsDetails = "10, 8, 8", weightDetails = "60kg, 65kg, 65kg", restSeconds = 90, rpe = 8))
            dao.insertWorkoutExercise(WorkoutExercise(workoutId = workoutId, exerciseName = "Incline Dumbbell Press", setsCount = 3, repsDetails = "10, 10, 8", weightDetails = "24kg, 24kg, 26kg", restSeconds = 75, rpe = 8))
            dao.insertWorkoutExercise(WorkoutExercise(workoutId = workoutId, exerciseName = "Weighted Pull-Ups", setsCount = 3, repsDetails = "8, 8, 7", weightDetails = "+10kg", restSeconds = 90, rpe = 9))

            // 5. Food library & Meal logs
            dao.insertFoodItem(FoodItem(name = "Oatmeal with Whey & Berries", category = "Breakfast", servingSize = "1 bowl", calories = 520, proteinGrams = 42f, carbsGrams = 68f, fatGrams = 9f, fiberGrams = 8f, isFavorite = true))
            dao.insertFoodItem(FoodItem(name = "Chicken Breast Rice Bowl", category = "Lunch", servingSize = "450g", calories = 650, proteinGrams = 48f, carbsGrams = 78f, fatGrams = 12f, fiberGrams = 6f, isFavorite = true))
            dao.insertFoodItem(FoodItem(name = "Greek Yogurt with Honey & Almonds", category = "Snacks", servingSize = "200g", calories = 280, proteinGrams = 22f, carbsGrams = 24f, fatGrams = 10f, fiberGrams = 2f, isFavorite = true))
            dao.insertFoodItem(FoodItem(name = "Salmon, Quinoa & Steamed Broccoli", category = "Dinner", servingSize = "400g", calories = 580, proteinGrams = 40f, carbsGrams = 45f, fatGrams = 22f, fiberGrams = 7f, isFavorite = true))

            dao.insertMealLog(MealLog(dateString = todayStr, mealType = "Breakfast", foodName = "Oatmeal with Whey & Berries", calories = 520, protein = 42f, carbs = 68f, fat = 9f, fiber = 8f))
            dao.insertMealLog(MealLog(dateString = todayStr, mealType = "Lunch", foodName = "Chicken Breast Rice Bowl", calories = 650, protein = 48f, carbs = 78f, fat = 12f, fiber = 6f))
            dao.insertMealLog(MealLog(dateString = todayStr, mealType = "Snacks", foodName = "Greek Yogurt with Honey & Almonds", calories = 280, protein = 22f, carbs = 24f, fat = 10f, fiber = 2f))

            // 6. Water logs (2400 ml logged today)
            dao.insertWaterLog(WaterLog(dateString = todayStr, amountMl = 500))
            dao.insertWaterLog(WaterLog(dateString = todayStr, amountMl = 500))
            dao.insertWaterLog(WaterLog(dateString = todayStr, amountMl = 500))
            dao.insertWaterLog(WaterLog(dateString = todayStr, amountMl = 500))
            dao.insertWaterLog(WaterLog(dateString = todayStr, amountMl = 400))

            // 7. Sleep
            dao.insertSleepLog(SleepLog(dateString = todayStr, bedtime = "23:10", wakeTime = "06:42", durationMinutes = 452, qualityRating = 4, notes = "Deep sleep ratio was high, wake refreshed."))

            // 8. Upskilling
            val lGoalId = dao.insertLearningGoal(LearningGoal(title = "Master Cloud & Full Stack Architecture", targetTrack = "Software Engineering", totalCourses = 4, completedCourses = 2, progressPercent = 65))
            dao.insertCourse(Course(learningGoalId = lGoalId, title = "Modern Kotlin & Jetpack Compose", platform = "Android Developer Track", topicsCount = 14, completedTopics = 11, progressPercent = 78))
            dao.insertCourse(Course(learningGoalId = lGoalId, title = "System Design & Distributed Scalability", platform = "ByteByteGo & Papers", topicsCount = 10, completedTopics = 6, progressPercent = 60))
            dao.insertLearningSession(LearningSession(courseTitle = "Modern Kotlin & Jetpack Compose", topic = "Coroutines, Room & Flow reactivity", durationMinutes = 45, dateString = todayStr, notes = "Studied StateFlow vs SharedFlow and lifecycle collectors"))

            // 9. Habits
            val h1 = dao.insertHabit(Habit(name = "Wake up before 7:00 AM", iconName = "Alarm", category = "Productivity", frequency = "Daily", colorHex = "#3B82F6", currentStreak = 14, bestStreak = 28))
            val h2 = dao.insertHabit(Habit(name = "Read 20+ pages", iconName = "Book", category = "Growth", frequency = "Daily", colorHex = "#8B5CF6", currentStreak = 8, bestStreak = 15))
            val h3 = dao.insertHabit(Habit(name = "Daily Hypertrophy or HIIT", iconName = "FitnessCenter", category = "Health", frequency = "Daily", colorHex = "#F97316", currentStreak = 5, bestStreak = 12))
            val h4 = dao.insertHabit(Habit(name = "No refined junk food", iconName = "NoFood", category = "Health", frequency = "Daily", colorHex = "#10B981", currentStreak = 9, bestStreak = 21))
            val h5 = dao.insertHabit(Habit(name = "Drink 3.0L Water", iconName = "WaterDrop", category = "Health", frequency = "Daily", colorHex = "#06B6D4", currentStreak = 12, bestStreak = 20))
            val h6 = dao.insertHabit(Habit(name = "Practice 1h Clean Coding", iconName = "Code", category = "Skill", frequency = "Daily", colorHex = "#EC4899", currentStreak = 6, bestStreak = 14))

            dao.insertHabitCompletion(HabitCompletion(habitId = h1, dateString = todayStr, isCompleted = true))
            dao.insertHabitCompletion(HabitCompletion(habitId = h3, dateString = todayStr, isCompleted = true))
            dao.insertHabitCompletion(HabitCompletion(habitId = h4, dateString = todayStr, isCompleted = true))

            // 10. Goals
            dao.insertGoal(Goal(title = "Graduate with High Honors (GPA > 3.85)", level = "Yearly", category = "Academics", description = "Ace senior thesis and advanced systems track", startDateString = "2026-01-01", targetDateString = "2026-12-15", progressPercent = 78, priority = "High", status = "Active"))
            dao.insertGoal(Goal(title = "Achieve 12% Body Fat & 80kg Lean Mass", level = "Yearly", category = "Fitness", description = "Progressive overload on compound lifts & 140g protein daily", startDateString = "2026-01-01", targetDateString = "2026-12-31", progressPercent = 65, priority = "High", status = "Active"))
            dao.insertGoal(Goal(title = "Publish Open-Source Systems Project", level = "Monthly", category = "Upskilling", description = "Production-grade Raft consensus distributed key-value store", startDateString = "2026-09-01", targetDateString = "2026-09-30", progressPercent = 40, priority = "Critical", status = "Active"))

            // 11. Tasks
            dao.insertTask(TaskItem(title = "Submit CS-441 Raft Benchmark Report", description = "Include graphs on latency under network partition", category = "Academics", priority = "High", dueDateString = todayStr, estimatedMinutes = 45, actualMinutes = 40, isCompleted = true))
            dao.insertTask(TaskItem(title = "Review Linear Attention Paper notes", description = "Verify flash attention GPU memory footprint", category = "Academics", priority = "Medium", dueDateString = todayStr, estimatedMinutes = 30, isCompleted = false))
            dao.insertTask(TaskItem(title = "Meal prep chicken & sweet potatoes for tomorrow", description = "Season with paprika, garlic, bake at 400F", category = "Health", priority = "Medium", dueDateString = todayStr, estimatedMinutes = 40, isCompleted = false))
            dao.insertTask(TaskItem(title = "Configure PostgreSQL connection pool index", description = "Test max connection limits with pgbench", category = "Dev", priority = "Low", dueDateString = "2026-09-09", estimatedMinutes = 30, isCompleted = false))

            // 12. Pre-populate past scores
            dao.insertDailyScore(DailyScore(dateString = "2026-09-05", totalScore = 79, academicsScore = 75, fitnessScore = 80, nutritionScore = 82, upskillingScore = 70, habitsScore = 80, sleepScore = 85, productivityScore = 78))
            dao.insertDailyScore(DailyScore(dateString = "2026-09-06", totalScore = 85, academicsScore = 88, fitnessScore = 90, nutritionScore = 80, upskillingScore = 80, habitsScore = 85, sleepScore = 88, productivityScore = 82))
            dao.insertDailyScore(DailyScore(dateString = todayStr, totalScore = 84, academicsScore = 82, fitnessScore = 92, nutritionScore = 85, upskillingScore = 75, habitsScore = 70, sleepScore = 90, productivityScore = 80))
        }
    }
}
