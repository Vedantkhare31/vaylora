package com.vaylora.app.rag

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RuleDao {
    @Query("SELECT * FROM waste_rules")
    suspend fun getAllRules(): List<RuleEntity>

    @Query("SELECT * FROM waste_rules WHERE material LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR instruction LIKE '%' || :query || '%'")
    suspend fun searchRules(query: String): List<RuleEntity>

    @Query("SELECT * FROM waste_rules WHERE category = :category")
    suspend fun getRulesByCategory(category: String): List<RuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rules: List<RuleEntity>)
}
