package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LifeOsDao {
    // User Profile
    @Query("SELECT * FROM user_profiles WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserProfile(profile: UserProfile)

    // Schedule Events (Timeline)
    @Query("SELECT * FROM schedule_events WHERE dateString = :date ORDER BY startTime ASC")
    fun getScheduleEvents(date: String): Flow<List<ScheduleEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleEvent(event: ScheduleEvent): Long

    @Update
    suspend fun updateScheduleEvent(event: ScheduleEvent)

    @Delete
    suspend fun deleteScheduleEvent(event: ScheduleEvent)

    @Query("UPDATE schedule_events SET isCompleted = :completed WHERE id = :id")
    suspend fun setScheduleEventCompleted(id: Long, completed: Boolean)

    // Subjects & Academics
    @Query("SELECT * FROM subjects ORDER BY name ASC")
    fun getAllSubjects(): Flow<List<Subject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject): Long

    @Delete
    suspend fun deleteSubject(subject: Subject)

    // Study Sessions
    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC")
    fun getAllStudySessions(): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions WHERE dateString = :date ORDER BY timestamp DESC")
    fun getStudySessionsForDate(date: String): Flow<List<StudySession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySession(session: StudySession): Long

    // Assignments
    @Query("SELECT * FROM assignments ORDER BY dueDateString ASC")
    fun getAllAssignments(): Flow<List<Assignment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: Assignment): Long

    @Update
    suspend fun updateAssignment(assignment: Assignment)

    @Delete
    suspend fun deleteAssignment(assignment: Assignment)

    // Exams
    @Query("SELECT * FROM exams ORDER BY examDateString ASC")
    fun getAllExams(): Flow<List<Exam>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: Exam): Long

    @Update
    suspend fun updateExam(exam: Exam)

    @Delete
    suspend fun deleteExam(exam: Exam)

    // Workouts & Exercises
    @Query("SELECT * FROM workouts ORDER BY dateString DESC, id DESC")
    fun getAllWorkouts(): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE dateString = :date")
    fun getWorkoutsForDate(date: String): Flow<List<Workout>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout): Long

    @Delete
    suspend fun deleteWorkout(workout: Workout)

    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId")
    fun getExercisesForWorkout(workoutId: Long): Flow<List<WorkoutExercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExercise(exercise: WorkoutExercise): Long

    // Nutrition & Foods
    @Query("SELECT * FROM food_items ORDER BY isFavorite DESC, name ASC")
    fun getAllFoodItems(): Flow<List<FoodItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItem(foodItem: FoodItem): Long

    @Query("SELECT * FROM meal_logs WHERE dateString = :date ORDER BY timestamp ASC")
    fun getMealLogsForDate(date: String): Flow<List<MealLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealLog(mealLog: MealLog): Long

    @Delete
    suspend fun deleteMealLog(mealLog: MealLog)

    // Hydration
    @Query("SELECT * FROM water_logs WHERE dateString = :date ORDER BY timestamp ASC")
    fun getWaterLogsForDate(date: String): Flow<List<WaterLog>>

    @Query("SELECT SUM(amountMl) FROM water_logs WHERE dateString = :date")
    fun getTotalWaterForDate(date: String): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(waterLog: WaterLog): Long

    // Sleep
    @Query("SELECT * FROM sleep_logs ORDER BY dateString DESC LIMIT 14")
    fun getRecentSleepLogs(): Flow<List<SleepLog>>

    @Query("SELECT * FROM sleep_logs WHERE dateString = :date LIMIT 1")
    fun getSleepLogForDate(date: String): Flow<SleepLog?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepLog(sleepLog: SleepLog): Long

    // Mood & Reflections
    @Query("SELECT * FROM mood_logs WHERE dateString = :date LIMIT 1")
    fun getMoodLogForDate(date: String): Flow<MoodLog?>

    @Query("SELECT * FROM mood_logs ORDER BY timestamp DESC LIMIT 30")
    fun getRecentMoodLogs(): Flow<List<MoodLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodLog(moodLog: MoodLog): Long

    // Upskilling & Learning
    @Query("SELECT * FROM learning_goals ORDER BY id ASC")
    fun getAllLearningGoals(): Flow<List<LearningGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLearningGoal(goal: LearningGoal): Long

    @Query("SELECT * FROM courses ORDER BY progressPercent DESC")
    fun getAllCourses(): Flow<List<Course>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course): Long

    @Query("SELECT * FROM learning_sessions WHERE dateString = :date ORDER BY timestamp DESC")
    fun getLearningSessionsForDate(date: String): Flow<List<LearningSession>>

    @Query("SELECT * FROM learning_sessions ORDER BY timestamp DESC")
    fun getAllLearningSessions(): Flow<List<LearningSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLearningSession(session: LearningSession): Long

    // Habits & Completions
    @Query("SELECT * FROM habits ORDER BY id ASC")
    fun getAllHabits(): Flow<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit): Long

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("SELECT * FROM habit_completions WHERE dateString = :date")
    fun getCompletionsForDate(date: String): Flow<List<HabitCompletion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitCompletion(completion: HabitCompletion): Long

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND dateString = :date")
    suspend fun removeHabitCompletion(habitId: Long, date: String)

    // Goals
    @Query("SELECT * FROM goals ORDER BY level ASC, targetDateString ASC")
    fun getAllGoals(): Flow<List<Goal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal): Long

    @Update
    suspend fun updateGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)

    // Tasks
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDateString ASC, priority DESC")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem): Long

    @Update
    suspend fun updateTask(task: TaskItem)

    @Delete
    suspend fun deleteTask(task: TaskItem)

    // Reviews & Daily Scores
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyReview(review: DailyReview): Long

    @Query("SELECT * FROM daily_reviews WHERE dateString = :date LIMIT 1")
    fun getDailyReviewForDate(date: String): Flow<DailyReview?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyScore(score: DailyScore)

    @Query("SELECT * FROM daily_scores ORDER BY dateString DESC LIMIT 30")
    fun getRecentDailyScores(): Flow<List<DailyScore>>
}
