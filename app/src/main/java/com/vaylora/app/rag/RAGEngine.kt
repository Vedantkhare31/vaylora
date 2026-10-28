package com.vaylora.app.rag

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.vaylora.app.ml.WasteTaxonomy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.File

class RAGEngine(private val context: Context) {

    private var llmInference: LlmInference? = null
    private var cachedRules: List<RuleEntity> = emptyList()

    suspend fun initialize() = withContext(Dispatchers.IO) {
        loadRulesIntoDatabase()
        loadLlmModel()
    }

    private suspend fun loadRulesIntoDatabase() {
        val db = VayloraDatabase.getInstance(context)
        val existing = db.ruleDao().getAllRules()
        if (existing.size < 30) {
            val json = context.assets.open("waste_rules.json").bufferedReader().use { it.readText() }
            val array = JSONArray(json)
            val loadedRules = mutableListOf<RuleEntity>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                loadedRules.add(
                    RuleEntity(
                        id = obj.getInt("id"),
                        material = obj.getString("material"),
                        category = obj.optString("category", "General"),
                        binType = obj.optString("binType", "Recycling Bin"),
                        binColorHex = obj.optString("binColorHex", "#2196F3"),
                        instruction = obj.getString("instruction"),
                        preparationSteps = obj.optString("preparationSteps", "Rinse and clean"),
                        upcyclingIdea = obj.optString("upcyclingIdea", "Explore creative reuse"),
                        carbonSavedKg = obj.optDouble("carbonSavedKg", 0.25).toFloat(),
                        isDisposable = obj.optBoolean("isDisposable", true)
                    )
                )
            }
            db.ruleDao().deleteAll()
            db.ruleDao().insertAll(loadedRules)
        }
        cachedRules = db.ruleDao().getAllRules()
    }

    private fun loadLlmModel() {
        val modelFile = File(context.getExternalFilesDir(null), "vaylora_model.task")
        if (!modelFile.exists()) {
            return
        }
        try {
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelFile.absolutePath)
                .setMaxTokens(512)
                .build()
            llmInference = LlmInference.createFromOptions(context, options)
        } catch (_: Exception) {
            // Model file corrupt or unsupported hardware, graceful fallback
        }
    }

    fun getAllRules(): List<RuleEntity> = cachedRules

    suspend fun searchRules(query: String): List<RuleEntity> = withContext(Dispatchers.IO) {
        val db = VayloraDatabase.getInstance(context)
        if (query.isBlank()) {
            db.ruleDao().getAllRules()
        } else {
            db.ruleDao().searchRules(query)
        }
    }

    fun findBestRule(detectedMaterial: String): RuleEntity? {
        if (cachedRules.isEmpty()) return null
        val lowerMaterial = detectedMaterial.lowercase().trim()

        // 1. Safety Tripwire: Living beings are NEVER disposable waste
        if (WasteTaxonomy.isLivingBeing(lowerMaterial) || lowerMaterial == "person") {
            return cachedRules.firstOrNull { it.material == "person" || !it.isDisposable }
        }

        // 2. Direct equality match
        val exactMatch = cachedRules.firstOrNull { rule ->
            rule.material.equals(lowerMaterial, ignoreCase = true)
        }
        if (exactMatch != null) return exactMatch

        // 3. Substring contains match (minimum length 3 to prevent greedy false positives)
        val containsMatch = cachedRules.firstOrNull { rule ->
            val ruleMat = rule.material.lowercase()
            if (ruleMat.length >= 3 && lowerMaterial.length >= 3) {
                lowerMaterial.contains(ruleMat) || ruleMat.contains(lowerMaterial)
            } else {
                lowerMaterial == ruleMat
            }
        }
        if (containsMatch != null) return containsMatch

        // 4. Bounded Levenshtein distance fallback (max edit distance of 2, disposable rules only)
        val closest = cachedRules
            .filter { it.isDisposable }
            .map { rule -> rule to levenshteinDistance(rule.material.lowercase(), lowerMaterial) }
            .filter { it.second <= 2 }
            .minByOrNull { it.second }
            ?.first

        return closest
    }

    suspend fun getDisposalInstruction(detectedMaterial: String): String =
        withContext(Dispatchers.Default) {
            val matchedRule = findBestRule(detectedMaterial)
                ?: return@withContext "Unclassified item. Inspect packaging for resin code (#1-#7) or consult municipal disposal guidelines."

            if (!matchedRule.isDisposable) {
                return@withContext matchedRule.instruction
            }

            val llm = llmInference
            if (llm != null) {
                val prompt = """
                    You are Vaylora, an AI waste sorting assistant.
                    The user is holding: $detectedMaterial.
                    The local municipal rule says: ${matchedRule.instruction}
                    Bin type: ${matchedRule.binType}
                    Give a short, friendly, one-sentence instruction.
                """.trimIndent()
                try {
                    val response = llm.generateResponse(prompt)
                    if (!response.isNullOrBlank()) {
                        return@withContext response
                    }
                } catch (_: Exception) {
                    // Fall through to database rule
                }
            }

            return@withContext "${matchedRule.binType}: ${matchedRule.instruction}"
        }

    private fun levenshteinDistance(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j
        for (i in 1..a.length) {
            for (j in 1..b.length) {
                dp[i][j] = if (a[i - 1] == b[j - 1]) {
                    dp[i - 1][j - 1]
                } else {
                    minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1]) + 1
                }
            }
        }
        return dp[a.length][b.length]
    }
}
