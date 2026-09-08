package com.example.data.repository

import com.example.data.dao.LifeOsDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LifeOsRepository(private val dao: LifeOsDao) {

    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    val allSubjects: Flow<List<Subject>> = dao.getAllSubjects()
    val allAssignments: Flow<List<Assignment>> = dao.getAllAssignments()
    val allExams: Flow<List<Exam>> = dao.getAllExams()
    val allWorkouts: Flow<List<Workout>> = dao.getAllWorkouts()
    val allHabits: Flow<List<Habit>> = dao.getAllHabits()
    val allGoals: Flow<List<Goal>> = dao.getAllGoals()
    val allTasks: Flow<List<TaskItem>> = dao.getAllTasks()
    val allCourses: Flow<List<Course>> = dao.getAllCourses()
    val allLearningGoals: Flow<List<LearningGoal>> = dao.getAllLearningGoals()
    val foodItems: Flow<List<FoodItem>> = dao.getAllFoodItems()
    val recentSleepLogs: Flow<List<SleepLog>> = dao.getRecentSleepLogs()
    val recentDailyScores: Flow<List<DailyScore>> = dao.getRecentDailyScores()

    fun daoGetAllStudySessions(): Flow<List<StudySession>> = dao.getAllStudySessions()

    fun getTodayString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun getScheduleEvents(date: String = getTodayString()): Flow<List<ScheduleEvent>> = dao.getScheduleEvents(date)
    fun getStudySessionsForDate(date: String = getTodayString()): Flow<List<StudySession>> = dao.getStudySessionsForDate(date)
    fun getWorkoutsForDate(date: String = getTodayString()): Flow<List<Workout>> = dao.getWorkoutsForDate(date)
    fun getMealLogsForDate(date: String = getTodayString()): Flow<List<MealLog>> = dao.getMealLogsForDate(date)
    fun getWaterLogsForDate(date: String = getTodayString()): Flow<List<WaterLog>> = dao.getWaterLogsForDate(date)
    fun getTotalWaterForDate(date: String = getTodayString()): Flow<Int?> = dao.getTotalWaterForDate(date)
    fun getSleepLogForDate(date: String = getTodayString()): Flow<SleepLog?> = dao.getSleepLogForDate(date)
    fun getMoodLogForDate(date: String = getTodayString()): Flow<MoodLog?> = dao.getMoodLogForDate(date)
    fun getLearningSessionsForDate(date: String = getTodayString()): Flow<List<LearningSession>> = dao.getLearningSessionsForDate(date)
    fun getHabitCompletionsForDate(date: String = getTodayString()): Flow<List<HabitCompletion>> = dao.getCompletionsForDate(date)
    fun getDailyReviewForDate(date: String = getTodayString()): Flow<DailyReview?> = dao.getDailyReviewForDate(date)

    // Mutations
    suspend fun updateUserProfile(profile: UserProfile) = dao.insertOrUpdateUserProfile(profile)

    suspend fun addScheduleEvent(event: ScheduleEvent) = dao.insertScheduleEvent(event)
    suspend fun toggleScheduleEvent(id: Long, currentCompleted: Boolean) = dao.setScheduleEventCompleted(id, !currentCompleted)
    suspend fun deleteScheduleEvent(event: ScheduleEvent) = dao.deleteScheduleEvent(event)

    suspend fun addSubject(subject: Subject) = dao.insertSubject(subject)
    suspend fun deleteSubject(subject: Subject) = dao.deleteSubject(subject)

    suspend fun addStudySession(session: StudySession) = dao.insertStudySession(session)

    suspend fun addAssignment(assignment: Assignment) = dao.insertAssignment(assignment)
    suspend fun updateAssignment(assignment: Assignment) = dao.updateAssignment(assignment)
    suspend fun deleteAssignment(assignment: Assignment) = dao.deleteAssignment(assignment)

    suspend fun addExam(exam: Exam) = dao.insertExam(exam)
    suspend fun updateExam(exam: Exam) = dao.updateExam(exam)
    suspend fun deleteExam(exam: Exam) = dao.deleteExam(exam)

    suspend fun addWorkout(workout: Workout, exercises: List<WorkoutExercise> = emptyList()): Long {
        val id = dao.insertWorkout(workout)
        for (ex in exercises) {
            dao.insertWorkoutExercise(ex.copy(workoutId = id))
        }
        return id
    }
    suspend fun deleteWorkout(workout: Workout) = dao.deleteWorkout(workout)

    suspend fun addFoodItem(food: FoodItem) = dao.insertFoodItem(food)
    suspend fun logMeal(meal: MealLog) = dao.insertMealLog(meal)
    suspend fun deleteMealLog(meal: MealLog) = dao.deleteMealLog(meal)

    suspend fun logWater(amountMl: Int, date: String = getTodayString()) {
        dao.insertWaterLog(WaterLog(dateString = date, amountMl = amountMl))
    }

    suspend fun logSleep(sleep: SleepLog) = dao.insertSleepLog(sleep)
    suspend fun logMood(mood: MoodLog) = dao.insertMoodLog(mood)

    suspend fun addLearningGoal(goal: LearningGoal) = dao.insertLearningGoal(goal)
    suspend fun addCourse(course: Course) = dao.insertCourse(course)
    suspend fun logLearningSession(session: LearningSession) = dao.insertLearningSession(session)

    suspend fun addHabit(habit: Habit) = dao.insertHabit(habit)
    suspend fun updateHabit(habit: Habit) = dao.updateHabit(habit)
    suspend fun deleteHabit(habit: Habit) = dao.deleteHabit(habit)

    suspend fun toggleHabit(habitId: Long, isDone: Boolean, date: String = getTodayString()) {
        if (isDone) {
            dao.removeHabitCompletion(habitId, date)
        } else {
            dao.insertHabitCompletion(HabitCompletion(habitId = habitId, dateString = date, isCompleted = true))
        }
    }

    suspend fun addGoal(goal: Goal) = dao.insertGoal(goal)
    suspend fun updateGoal(goal: Goal) = dao.updateGoal(goal)
    suspend fun deleteGoal(goal: Goal) = dao.deleteGoal(goal)

    suspend fun addTask(task: TaskItem) = dao.insertTask(task)
    suspend fun toggleTask(task: TaskItem) = dao.updateTask(task.copy(isCompleted = !task.isCompleted))
    suspend fun deleteTask(task: TaskItem) = dao.deleteTask(task)

    suspend fun saveDailyReview(review: DailyReview) = dao.insertDailyReview(review)
    suspend fun saveDailyScore(score: DailyScore) = dao.insertDailyScore(score)
}
