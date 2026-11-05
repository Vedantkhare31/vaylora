package com.vaylora.app.core.config

object AppConfig {
    // ML thresholds
    const val DETECTOR_SCORE_THRESHOLD = 0.35f
    const val CLASSIFIER_SCORE_THRESHOLD = 0.15f
    const val LIVING_BEING_CLASSIFIER_THRESHOLD = 0.25f
    const val MAX_DETECTOR_RESULTS = 5
    const val MAX_CLASSIFIER_RESULTS = 8
    const val LEVENSHTEIN_MAX_DISTANCE = 2

    // Model file names
    const val OBJECT_DETECTOR_MODEL = "efficientdet_lite0.tflite"
    const val IMAGE_CLASSIFIER_MODEL = "efficientnet_lite0_classifier.tflite"
    const val LLM_MODEL_FILENAME = "vaylora_model.task"
    const val WASTE_RULES_ASSET = "waste_rules.json"

    // Firestore collection names
    const val COLLECTION_USERS = "users"
    const val COLLECTION_SCAN_HISTORY = "scanHistory"
    const val COLLECTION_CHALLENGES = "challenges"

    // DataStore keys
    const val PREFS_THEME = "theme_dark"
    const val PREFS_NOTIFICATIONS = "notifications_enabled"
    const val PREFS_ONBOARDING_DONE = "onboarding_done"

    // EcoScore
    const val DEFAULT_CARBON_SAVED_KG = 0.25f
}
