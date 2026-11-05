package com.vaylora.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.vaylora.app.core.config.AppConfig
import com.vaylora.app.core.result.VayloraResult
import com.vaylora.app.domain.model.ScanRecord
import com.vaylora.app.domain.model.UserProfile
import kotlinx.coroutines.tasks.await

/**
 * Repository for user profile and scan history stored in Firestore.
 */
class UserRepository {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Create or update user profile in Firestore.
     */
    suspend fun saveUserProfile(profile: UserProfile): VayloraResult<Unit> {
        return try {
            val data = mapOf(
                "uid" to profile.uid,
                "name" to profile.name,
                "email" to profile.email,
                "photoUrl" to profile.photoUrl,
                "totalItemsScanned" to profile.totalItemsScanned,
                "totalCarbonSavedKg" to profile.totalCarbonSavedKg,
                "ecoScore" to profile.ecoScore,
                "createdAt" to profile.createdAt
            )
            db.collection(AppConfig.COLLECTION_USERS)
                .document(profile.uid)
                .set(data, SetOptions.merge())
                .await()
            VayloraResult.Success(Unit)
        } catch (e: Exception) {
            VayloraResult.Error(e.message ?: "Failed to save profile", e)
        }
    }

    /**
     * Load user profile from Firestore.
     */
    suspend fun getUserProfile(uid: String): VayloraResult<UserProfile> {
        return try {
            val snapshot = db.collection(AppConfig.COLLECTION_USERS)
                .document(uid)
                .get()
                .await()
            if (!snapshot.exists()) {
                return VayloraResult.Error("Profile not found")
            }
            val profile = UserProfile(
                uid = snapshot.getString("uid") ?: uid,
                name = snapshot.getString("name") ?: "",
                email = snapshot.getString("email") ?: "",
                photoUrl = snapshot.getString("photoUrl") ?: "",
                totalItemsScanned = (snapshot.getLong("totalItemsScanned") ?: 0L).toInt(),
                totalCarbonSavedKg = (snapshot.getDouble("totalCarbonSavedKg") ?: 0.0).toFloat(),
                ecoScore = (snapshot.getLong("ecoScore") ?: 0L).toInt(),
                createdAt = snapshot.getLong("createdAt") ?: System.currentTimeMillis()
            )
            VayloraResult.Success(profile)
        } catch (e: Exception) {
            VayloraResult.Error(e.message ?: "Failed to load profile", e)
        }
    }

    /**
     * Save a scan record to Firestore scan history.
     */
    suspend fun saveScanRecord(record: ScanRecord): VayloraResult<Unit> {
        return try {
            val data = mapOf(
                "uid" to record.uid,
                "materialLabel" to record.materialLabel,
                "normalizedMaterial" to record.normalizedMaterial,
                "binType" to record.binType,
                "binColorHex" to record.binColorHex,
                "instruction" to record.instruction,
                "carbonSavedKg" to record.carbonSavedKg,
                "confidence" to record.confidence,
                "timestamp" to record.timestamp
            )
            db.collection(AppConfig.COLLECTION_USERS)
                .document(record.uid)
                .collection(AppConfig.COLLECTION_SCAN_HISTORY)
                .add(data)
                .await()
            VayloraResult.Success(Unit)
        } catch (e: Exception) {
            VayloraResult.Error(e.message ?: "Failed to save scan", e)
        }
    }

    /**
     * Get the last N scan records for a user.
     */
    suspend fun getScanHistory(uid: String, limit: Long = 20): VayloraResult<List<ScanRecord>> {
        return try {
            val snapshot = db.collection(AppConfig.COLLECTION_USERS)
                .document(uid)
                .collection(AppConfig.COLLECTION_SCAN_HISTORY)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()
            val records = snapshot.documents.map { doc ->
                ScanRecord(
                    id = doc.id,
                    uid = doc.getString("uid") ?: uid,
                    materialLabel = doc.getString("materialLabel") ?: "",
                    normalizedMaterial = doc.getString("normalizedMaterial") ?: "",
                    binType = doc.getString("binType") ?: "",
                    binColorHex = doc.getString("binColorHex") ?: "#2196F3",
                    instruction = doc.getString("instruction") ?: "",
                    carbonSavedKg = (doc.getDouble("carbonSavedKg") ?: 0.0).toFloat(),
                    confidence = (doc.getDouble("confidence") ?: 0.0).toFloat(),
                    timestamp = doc.getLong("timestamp") ?: 0L
                )
            }
            VayloraResult.Success(records)
        } catch (e: Exception) {
            VayloraResult.Error(e.message ?: "Failed to load history", e)
        }
    }

    /**
     * Increment scan stats atomically for a user.
     */
    suspend fun incrementUserStats(uid: String, carbonSavedKg: Float): VayloraResult<Unit> {
        return try {
            db.collection(AppConfig.COLLECTION_USERS)
                .document(uid)
                .update(
                    mapOf(
                        "totalItemsScanned" to com.google.firebase.firestore.FieldValue.increment(1),
                        "totalCarbonSavedKg" to com.google.firebase.firestore.FieldValue.increment(carbonSavedKg.toDouble()),
                        "ecoScore" to com.google.firebase.firestore.FieldValue.increment(10)
                    )
                )
                .await()
            VayloraResult.Success(Unit)
        } catch (e: Exception) {
            VayloraResult.Error(e.message ?: "Failed to update stats", e)
        }
    }
}
