package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.CoachInsights
import com.example.ai.LifeCoachService
import com.example.data.db.LifeOsDatabase
import com.example.data.model.*
import com.example.data.repository.LifeOsRepository
import com.example.domain.LifeScoreBreakdown
import com.example.domain.LifeScoreEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = LifeOsDatabase.getDatabase(application, viewModelScope)
    private val repository = LifeOsRepository(database.lifeOsDao())

    val userProfile = repository.userProfile.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val scheduleEvents = repository.getScheduleEvents().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val subjects = repository.allSubjects.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val studySessions = repository.getStudySessionsForDate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allStudySessions = repository.daoGetAllStudySessions().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val assignments = repository.allAssignments.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val exams = repository.allExams.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val workouts = repository.getWorkoutsForDate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allWorkouts = repository.allWorkouts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val foodItems = repository.foodItems.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val mealLogs = repository.getMealLogsForDate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val waterLogs = repository.getWaterLogsForDate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val totalWaterMl = repository.getTotalWaterForDate().map { it ?: 0 }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val sleepLogs = repository.recentSleepLogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val todaySleepLog = repository.getSleepLogForDate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val todayMoodLog = repository.getMoodLogForDate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val learningGoals = repository.allLearningGoals.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val courses = repository.allCourses.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val learningSessions = repository.getLearningSessionsForDate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val habits = repository.allHabits.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val habitCompletions = repository.getHabitCompletionsForDate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val goals = repository.allGoals.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val tasks = repository.allTasks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val recentScores = repository.recentDailyScores.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val dailyReview = repository.getDailyReviewForDate().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Derived Real-Time Life Score
    val lifeScoreBreakdown: StateFlow<LifeScoreBreakdown> = combine(
        userProfile,
        studySessions,
        workouts,
        mealLogs,
        totalWaterMl,
        todaySleepLog,
        learningSessions,
        habits,
        habitCompletions,
        tasks
    ) { params ->
        val profile = params[0] as? UserProfile ?: UserProfile()
        @Suppress("UNCHECKED_CAST")
        val studies = params[1] as List<StudySession>
        @Suppress("UNCHECKED_CAST")
        val wOuts = params[2] as List<Workout>
        @Suppress("UNCHECKED_CAST")
        val meals = params[3] as List<MealLog>
        val water = params[4] as Int
        val sleep = params[5] as? SleepLog
        @Suppress("UNCHECKED_CAST")
        val lSessions = params[6] as List<LearningSession>
        @Suppress("UNCHECKED_CAST")
        val hList = params[7] as List<Habit>
        @Suppress("UNCHECKED_CAST")
        val hComps = params[8] as List<HabitCompletion>
        @Suppress("UNCHECKED_CAST")
        val tList = params[9] as List<TaskItem>

        LifeScoreEngine.calculateScore(
            profile = profile,
            studySessions = studies,
            workouts = wOuts,
            meals = meals,
            waterTotalMl = water,
            sleepLog = sleep,
            learningSessions = lSessions,
            habits = hList,
            habitCompletions = hComps,
            tasks = tList
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        LifeScoreBreakdown(82, 80, 85, 80, 75, 75, 88, 80, 1850)
    )

    // AI Coach Insights
    private val _coachInsights = MutableStateFlow<CoachInsights?>(null)
    val coachInsights: StateFlow<CoachInsights?> = _coachInsights.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // Global Search State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Focus Timer State
    private val _timerSeconds = MutableStateFlow(0)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _timerTargetName = MutableStateFlow("Deep Work Focus")
    val timerTargetName: StateFlow<String> = _timerTargetName.asStateFlow()

    private var timerJob: Job? = null

    init {
        refreshAiCoach()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun refreshAiCoach() {
        viewModelScope.launch {
            _isAiLoading.value = true
            val profile = userProfile.value ?: UserProfile()
            val breakdown = lifeScoreBreakdown.value
            val insights = LifeCoachService.generateDailyInsights(
                profile = profile,
                breakdown = breakdown,
                studySessions = studySessions.value,
                workouts = workouts.value,
                meals = mealLogs.value,
                habits = habits.value,
                habitCompletions = habitCompletions.value,
                tasks = tasks.value
            )
            _coachInsights.value = insights
            _isAiLoading.value = false
        }
    }

    // Focus Timer
    fun startTimer(targetName: String = "Deep Work Focus") {
        _timerTargetName.value = targetName
        _isTimerRunning.value = true
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value) {
                delay(1000)
                _timerSeconds.value += 1
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        _timerSeconds.value = 0
    }

    fun saveTimerAsStudy(subjectName: String, focusRating: Int = 4, notes: String = "") {
        val durationMins = maxOf(1, _timerSeconds.value / 60)
        resetTimer()
        viewModelScope.launch {
            repository.addStudySession(
                StudySession(
                    subjectName = subjectName,
                    dateString = repository.getTodayString(),
                    durationMinutes = durationMins,
                    studyMethod = "Timer Focus",
                    focusRating = focusRating,
                    notes = notes
                )
            )
            rewardXp(durationMins * 5)
        }
    }

    fun saveTimerAsLearning(courseTitle: String, topic: String, notes: String = "") {
        val durationMins = maxOf(1, _timerSeconds.value / 60)
        resetTimer()
        viewModelScope.launch {
            repository.logLearningSession(
                LearningSession(
                    courseTitle = courseTitle,
                    topic = topic,
                    durationMinutes = durationMins,
                    dateString = repository.getTodayString(),
                    notes = notes
                )
            )
            rewardXp(durationMins * 5)
        }
    }

    // Schedule Mutations
    fun addScheduleEvent(title: String, category: String, startTime: String, endTime: String) {
        viewModelScope.launch {
            repository.addScheduleEvent(
                ScheduleEvent(
                    title = title,
                    category = category,
                    startTime = startTime,
                    endTime = endTime,
                    dateString = repository.getTodayString()
                )
            )
        }
    }

    fun toggleScheduleEvent(event: ScheduleEvent) {
        viewModelScope.launch {
            repository.toggleScheduleEvent(event.id, event.isCompleted)
            if (!event.isCompleted) rewardXp(25)
        }
    }

    fun deleteScheduleEvent(event: ScheduleEvent) {
        viewModelScope.launch { repository.deleteScheduleEvent(event) }
    }

    // Academics Mutations
    fun addSubject(name: String, code: String, professor: String, semester: String, targetGrade: String, colorHex: String) {
        viewModelScope.launch {
            repository.addSubject(Subject(name = name, code = code, professor = professor, semester = semester, targetGrade = targetGrade, colorHex = colorHex))
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch { repository.deleteSubject(subject) }
    }

    fun addStudySession(subjectName: String, durationMinutes: Int, method: String, focusRating: Int, notes: String) {
        viewModelScope.launch {
            repository.addStudySession(
                StudySession(
                    subjectName = subjectName,
                    dateString = repository.getTodayString(),
                    durationMinutes = durationMinutes,
                    studyMethod = method,
                    focusRating = focusRating,
                    notes = notes
                )
            )
            rewardXp(durationMinutes * 4)
        }
    }

    fun addAssignment(title: String, subjectName: String, dueDate: String, priority: String, estimatedMins: Int) {
        viewModelScope.launch {
            repository.addAssignment(
                Assignment(
                    title = title,
                    subjectName = subjectName,
                    dueDateString = dueDate,
                    priority = priority,
                    estimatedMinutes = estimatedMins,
                    status = "Not started"
                )
            )
        }
    }

    fun toggleAssignmentStatus(assignment: Assignment) {
        val newStatus = if (assignment.status == "Completed") "In progress" else "Completed"
        viewModelScope.launch {
            repository.updateAssignment(assignment.copy(status = newStatus))
            if (newStatus == "Completed") rewardXp(50)
        }
    }

    fun deleteAssignment(assignment: Assignment) {
        viewModelScope.launch { repository.deleteAssignment(assignment) }
    }

    fun addExam(subjectName: String, examName: String, dateString: String, syllabus: String, prepPercent: Int, expectedGrade: String) {
        viewModelScope.launch {
            repository.addExam(
                Exam(
                    subjectName = subjectName,
                    examName = examName,
                    examDateString = dateString,
                    syllabus = syllabus,
                    prepPercentage = prepPercent,
                    expectedGrade = expectedGrade
                )
            )
        }
    }

    fun deleteExam(exam: Exam) {
        viewModelScope.launch { repository.deleteExam(exam) }
    }

    // Fitness Mutations
    fun addWorkout(name: String, type: String, durationMins: Int, caloriesBurned: Int, notes: String, exercises: List<WorkoutExercise> = emptyList()) {
        viewModelScope.launch {
            repository.addWorkout(
                Workout(
                    name = name,
                    workoutType = type,
                    dateString = repository.getTodayString(),
                    durationMinutes = durationMins,
                    caloriesBurned = caloriesBurned,
                    notes = notes
                ),
                exercises
            )
            rewardXp(durationMins * 5)
        }
    }

    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch { repository.deleteWorkout(workout) }
    }

    // Nutrition & Hydration
    fun addFoodItem(foodItem: FoodItem) {
        viewModelScope.launch { repository.addFoodItem(foodItem) }
    }

    fun logMeal(mealType: String, foodName: String, portion: String, calories: Int, protein: Float, carbs: Float, fat: Float, fiber: Float) {
        viewModelScope.launch {
            repository.logMeal(
                MealLog(
                    dateString = repository.getTodayString(),
                    mealType = mealType,
                    foodName = foodName,
                    portion = portion,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    fiber = fiber
                )
            )
            rewardXp(20)
        }
    }

    fun deleteMealLog(meal: MealLog) {
        viewModelScope.launch { repository.deleteMealLog(meal) }
    }

    fun logWater(amountMl: Int) {
        viewModelScope.launch {
            repository.logWater(amountMl)
            rewardXp(10)
        }
    }

    // Sleep & Mood
    fun logSleep(bedtime: String, wakeTime: String, durationMinutes: Int, quality: Int, notes: String) {
        viewModelScope.launch {
            repository.logSleep(
                SleepLog(
                    dateString = repository.getTodayString(),
                    bedtime = bedtime,
                    wakeTime = wakeTime,
                    durationMinutes = durationMinutes,
                    qualityRating = quality,
                    notes = notes
                )
            )
            rewardXp(30)
        }
    }

    fun logMood(moodScore: Int, energyScore: Int, stressScore: Int, biggestWin: String, improvementArea: String, gratitude: String) {
        viewModelScope.launch {
            repository.logMood(
                MoodLog(
                    dateString = repository.getTodayString(),
                    moodScore = moodScore,
                    energyScore = energyScore,
                    stressScore = stressScore,
                    biggestWin = biggestWin,
                    improvementArea = improvementArea,
                    gratitude = gratitude
                )
            )
            rewardXp(25)
        }
    }

    // Upskilling
    fun addLearningGoal(title: String, targetTrack: String) {
        viewModelScope.launch {
            repository.addLearningGoal(LearningGoal(title = title, targetTrack = targetTrack))
        }
    }

    fun addCourse(learningGoalId: Long, title: String, platform: String, topicsCount: Int) {
        viewModelScope.launch {
            repository.addCourse(Course(learningGoalId = learningGoalId, title = title, platform = platform, topicsCount = topicsCount, completedTopics = 0, progressPercent = 0))
        }
    }

    fun logLearningSession(courseTitle: String, topic: String, durationMinutes: Int, notes: String) {
        viewModelScope.launch {
            repository.logLearningSession(
                LearningSession(
                    courseTitle = courseTitle,
                    topic = topic,
                    durationMinutes = durationMinutes,
                    dateString = repository.getTodayString(),
                    notes = notes
                )
            )
            rewardXp(durationMinutes * 4)
        }
    }

    // Habits
    fun addHabit(name: String, icon: String, category: String, frequency: String, reminder: String, colorHex: String) {
        viewModelScope.launch {
            repository.addHabit(Habit(name = name, iconName = icon, category = category, frequency = frequency, reminderTime = reminder, colorHex = colorHex))
        }
    }

    fun toggleHabit(habitId: Long) {
        viewModelScope.launch {
            val isAlreadyDone = habitCompletions.value.any { it.habitId == habitId }
            repository.toggleHabit(habitId, isAlreadyDone)
            if (!isAlreadyDone) rewardXp(20)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch { repository.deleteHabit(habit) }
    }

    // Goals & Tasks
    fun addGoal(title: String, level: String, category: String, description: String, targetDate: String, priority: String) {
        viewModelScope.launch {
            repository.addGoal(
                Goal(
                    title = title,
                    level = level,
                    category = category,
                    description = description,
                    startDateString = repository.getTodayString(),
                    targetDateString = targetDate,
                    priority = priority,
                    progressPercent = 0,
                    status = "Active"
                )
            )
        }
    }

    fun updateGoalProgress(goal: Goal, newProgress: Int) {
        viewModelScope.launch {
            repository.updateGoal(goal.copy(progressPercent = newProgress.coerceIn(0, 100), status = if (newProgress >= 100) "Completed" else "Active"))
            if (newProgress >= 100) rewardXp(100)
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch { repository.deleteGoal(goal) }
    }

    fun addTask(title: String, description: String, category: String, priority: String, dueDate: String, estimatedMins: Int) {
        viewModelScope.launch {
            repository.addTask(
                TaskItem(
                    title = title,
                    description = description,
                    category = category,
                    priority = priority,
                    dueDateString = dueDate,
                    estimatedMinutes = estimatedMins,
                    isCompleted = false
                )
            )
        }
    }

    fun toggleTask(task: TaskItem) {
        viewModelScope.launch {
            repository.toggleTask(task)
            if (!task.isCompleted) rewardXp(25)
        }
    }

    fun deleteTask(task: TaskItem) {
        viewModelScope.launch { repository.deleteTask(task) }
    }

    // Daily Evening Review
    fun saveDailyReview(biggestWin: String, improvementGoal: String, gratitudeNotes: String) {
        viewModelScope.launch {
            val score = lifeScoreBreakdown.value.overallScore
            val today = repository.getTodayString()
            repository.saveDailyReview(
                DailyReview(
                    dateString = today,
                    lifeScore = score,
                    biggestWin = biggestWin,
                    improvementGoal = improvementGoal,
                    gratitudeNotes = gratitudeNotes
                )
            )
            repository.saveDailyScore(
                DailyScore(
                    dateString = today,
                    totalScore = score,
                    academicsScore = lifeScoreBreakdown.value.academicsScore,
                    fitnessScore = lifeScoreBreakdown.value.fitnessScore,
                    nutritionScore = lifeScoreBreakdown.value.nutritionScore,
                    upskillingScore = lifeScoreBreakdown.value.upskillingScore,
                    habitsScore = lifeScoreBreakdown.value.habitsScore,
                    sleepScore = lifeScoreBreakdown.value.sleepScore,
                    productivityScore = lifeScoreBreakdown.value.productivityScore
                )
            )
            rewardXp(150)
        }
    }

    // Profile & Onboarding
    fun updateUserProfile(profile: UserProfile) {
        viewModelScope.launch { repository.updateUserProfile(profile) }
    }

    fun completeOnboarding(
        name: String,
        age: Int,
        gender: String,
        heightCm: Float,
        weightKg: Float,
        wakeTime: String,
        sleepTime: String,
        calories: Int,
        protein: Int,
        waterMl: Int,
        steps: Int,
        studyHours: Float
    ) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfile()
            val updated = current.copy(
                name = name,
                age = age,
                gender = gender,
                heightCm = heightCm,
                weightKg = weightKg,
                wakeTime = wakeTime,
                sleepTime = sleepTime,
                targetCalories = calories,
                targetProteinGrams = protein,
                targetWaterMl = waterMl,
                targetSteps = steps,
                targetStudyMinutes = (studyHours * 60).toInt(),
                isOnboardingCompleted = true
            )
            repository.updateUserProfile(updated)
            rewardXp(300)
        }
    }

    private suspend fun rewardXp(amount: Int) {
        val current = userProfile.value ?: return
        val newXp = current.xp + amount
        val newLevel = (newXp / 350) + 1
        repository.updateUserProfile(current.copy(xp = newXp, level = newLevel))
    }

    // Data Export (Requirement 35)
    fun generateJsonExport(): String {
        val root = JSONObject()
        root.put("exportDate", repository.getTodayString())
        root.put("profile", JSONObject().apply {
            val p = userProfile.value ?: UserProfile()
            put("name", p.name)
            put("email", p.email)
            put("level", p.level)
            put("xp", p.xp)
            put("streak", p.currentStreak)
        })
        root.put("habits", JSONArray().apply {
            for (h in habits.value) {
                put(JSONObject().apply {
                    put("name", h.name)
                    put("streak", h.currentStreak)
                    put("frequency", h.frequency)
                })
            }
        })
        root.put("tasks", JSONArray().apply {
            for (t in tasks.value) {
                put(JSONObject().apply {
                    put("title", t.title)
                    put("completed", t.isCompleted)
                    put("priority", t.priority)
                })
            }
        })
        return root.toString(2)
    }

    fun generateCsvExport(): String {
        val sb = StringBuilder()
        sb.append("Module,ItemName,StatusOrValue,Date\n")
        val today = repository.getTodayString()
        for (h in habits.value) {
            val done = habitCompletions.value.any { it.habitId == h.id }
            sb.append("Habit,\"${h.name}\",${if (done) "Completed" else "Pending"},$today\n")
        }
        for (s in studySessions.value) {
            sb.append("Study,\"${s.subjectName}\",${s.durationMinutes} mins,$today\n")
        }
        for (w in workouts.value) {
            sb.append("Fitness,\"${w.name}\",${w.durationMinutes} mins,$today\n")
        }
        for (m in mealLogs.value) {
            sb.append("Nutrition,\"${m.foodName}\",${m.calories} kcal,$today\n")
        }
        return sb.toString()
    }
}
