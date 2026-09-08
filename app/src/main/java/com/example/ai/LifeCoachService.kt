package com.example.ai

import com.example.BuildConfig
import com.example.data.model.*
import com.example.domain.LifeScoreBreakdown
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class CoachInsights(
    val dailyHeadline: String,
    val dailySummary: String,
    val keyWin: String,
    val recommendedFocus: String,
    val tomorrowSuggestions: List<String>
)

object LifeCoachService {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun generateDailyInsights(
        profile: UserProfile,
        breakdown: LifeScoreBreakdown,
        studySessions: List<StudySession>,
        workouts: List<Workout>,
        meals: List<MealLog>,
        habits: List<Habit>,
        habitCompletions: List<HabitCompletion>,
        tasks: List<TaskItem>
    ): CoachInsights = withContext(Dispatchers.IO) {
        val apiKey = try {
            val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
            field.get(null) as? String ?: ""
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                return@withContext callGeminiAi(apiKey, profile, breakdown, studySessions, workouts, meals, habits, habitCompletions)
            } catch (_: Exception) {
                // Fallback smoothly to rule-based engine
            }
        }

        return@withContext generateHeuristicInsights(profile, breakdown, studySessions, workouts, meals, habits, habitCompletions, tasks)
    }

    private fun generateHeuristicInsights(
        profile: UserProfile,
        breakdown: LifeScoreBreakdown,
        studySessions: List<StudySession>,
        workouts: List<Workout>,
        meals: List<MealLog>,
        habits: List<Habit>,
        habitCompletions: List<HabitCompletion>,
        tasks: List<TaskItem>
    ): CoachInsights {
        val score = breakdown.overallScore
        val headline = when {
            score >= 85 -> "Outstanding execution today! You're operating at peak momentum."
            score >= 70 -> "Solid progress across your core pillars with high consistency."
            else -> "A steady foundation today. Let's optimize key focus blocks tomorrow."
        }

        val completedHabitsCount = habitCompletions.size
        val totalHabits = habits.size
        val studyMinutes = studySessions.sumOf { it.durationMinutes }
        val workoutDone = workouts.isNotEmpty()

        val keyWin = when {
            workoutDone && studyMinutes >= 120 -> "Dual mastery: Crushed high-intensity training and over 2 hours of focused deep work."
            workoutDone -> "Consistent physical discipline with a full completed workout."
            studyMinutes >= 120 -> "Exceptional academic stamina: Logged ${studyMinutes} minutes of focused study."
            completedHabitsCount >= 3 -> "Habit momentum: Checked off $completedHabitsCount daily micro-habits."
            else -> "Maintained steady tracking and daily awareness across your life pillars."
        }

        val lowestScoreCategory = listOf(
            "Academics" to breakdown.academicsScore,
            "Fitness" to breakdown.fitnessScore,
            "Nutrition" to breakdown.nutritionScore,
            "Learning" to breakdown.upskillingScore,
            "Habits" to breakdown.habitsScore,
            "Sleep" to breakdown.sleepScore
        ).minByOrNull { it.second }?.first ?: "Nutrition"

        val recommendedFocus = when (lowestScoreCategory) {
            "Academics" -> "Allocate an undisturbed 90-minute Pomodoro block before noon for your core syllabus."
            "Fitness" -> "Schedule a brisk 30-minute workout or zone 2 cardio session to boost physical resilience."
            "Nutrition" -> "Prioritize reaching your ${profile.targetProteinGrams}g protein goal with whole foods and hydration."
            "Learning" -> "Dedicate 45 minutes of quiet evening time to project development or skill tutorials."
            "Habits" -> "Stack your remaining habit triggers right after morning coffee or evening meals."
            else -> "Ensure a wind-down protocol 45 minutes prior to ${profile.sleepTime} for deep sleep recovery."
        }

        val summary = "Your Life Score is ${score}/100. You completed $completedHabitsCount of $totalHabits habits, recorded ${studyMinutes}m study, and hit ${breakdown.fitnessScore}% of your daily fitness benchmark."

        val tomorrowSuggestions = listOf(
            "07:15 - Drink 500ml water and complete 10m morning mobility",
            "09:30 - Deep work study session on highest priority assignment",
            "17:00 - Targeted workout session: Progressive overload or cardio",
            "22:15 - Evening reflection, device screen off for optimal REM sleep"
        )

        return CoachInsights(
            dailyHeadline = headline,
            dailySummary = summary,
            keyWin = keyWin,
            recommendedFocus = recommendedFocus,
            tomorrowSuggestions = tomorrowSuggestions
        )
    }

    private fun callGeminiAi(
        apiKey: String,
        profile: UserProfile,
        breakdown: LifeScoreBreakdown,
        studySessions: List<StudySession>,
        workouts: List<Workout>,
        meals: List<MealLog>,
        habits: List<Habit>,
        habitCompletions: List<HabitCompletion>
    ): CoachInsights {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        val prompt = """
            Analyze the user's day in LifeOS:
            User: ${profile.name}, Life Score: ${breakdown.overallScore}/100.
            Academics Score: ${breakdown.academicsScore}/100 (Study: ${studySessions.sumOf { it.durationMinutes }} min)
            Fitness Score: ${breakdown.fitnessScore}/100 (${workouts.size} workouts)
            Nutrition Score: ${breakdown.nutritionScore}/100 (${meals.sumOf { it.calories }} kcal)
            Habits Score: ${breakdown.habitsScore}/100 (${habitCompletions.size}/${habits.size} habits done)
            Sleep Score: ${breakdown.sleepScore}/100.
            Provide output strictly in JSON format with fields:
            headline (string, 1 punchy sentence),
            summary (string, 2 sentences),
            keyWin (string, 1 sentence highlighting their best win),
            recommendedFocus (string, 1 actionable recommendation),
            tomorrowPlan (array of 4 strings with timestamped suggested schedule)
            Notice: Informational guidance only, not medical advice.
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                    })
                })
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("HTTP ${response.code}")
            val respString = response.body?.string() ?: throw Exception("Empty response")
            val root = JSONObject(respString)
            val text = root.getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            val cleanJson = text.substringAfter("{").substringBeforeLast("}")
            val parsed = JSONObject("{$cleanJson}")

            val suggestions = mutableListOf<String>()
            val planArr = parsed.optJSONArray("tomorrowPlan")
            if (planArr != null) {
                for (i in 0 until planArr.length()) {
                    suggestions.add(planArr.getString(i))
                }
            }
            if (suggestions.isEmpty()) {
                suggestions.addAll(listOf("08:00 - Priority study block", "17:30 - Physical training", "22:00 - Rest & recovery"))
            }

            return CoachInsights(
                dailyHeadline = parsed.optString("headline", "Great progress today!"),
                dailySummary = parsed.optString("summary", "You kept strong consistency."),
                keyWin = parsed.optString("keyWin", "Strong adherence to your core habits."),
                recommendedFocus = parsed.optString("recommendedFocus", "Prioritize balanced nutrition and adequate sleep tonight."),
                tomorrowSuggestions = suggestions
            )
        }
    }
}
