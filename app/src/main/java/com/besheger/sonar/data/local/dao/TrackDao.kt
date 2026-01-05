package com.besheger.sonar.data.local.dao

import androidx.room.*
import com.besheger.sonar.data.local.entity.SonarTrackEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks ORDER BY title ASC")
    fun getAllTracks(): Flow<List<SonarTrackEntity>>

    @Query("SELECT * FROM tracks WHERE category = :category")
    fun getTracksByCategory(category: String): Flow<List<SonarTrackEntity>>

    @Query("SELECT * FROM tracks WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%'")
    fun searchTracks(query: String): Flow<List<SonarTrackEntity>>

    // --- NEW FUNCTIONALITY ---

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTracks(tracks: List<SonarTrackEntity>)


    // 2. Edit the category of a specific track
    // Use uriString because that is your Primary Key
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: SonarTrackEntity)
    @Query("UPDATE tracks SET category = :newCategory WHERE uriString = :uri")
    suspend fun updateCategory(uri: String, newCategory: String)

    @Query("UPDATE tracks SET category = :newCategory WHERE uriString = :uri")
    suspend fun updateTrackCategory(uri: String, newCategory: String)
    // Dashboard Statistics
    @Query("SELECT COUNT(*) FROM tracks")
    fun getTrackCount(): Flow<Int>
    @Delete
    suspend fun deleteTrack(track: SonarTrackEntity)
    @Query("SELECT category, COUNT(*) as count FROM tracks GROUP BY category")
    fun getCategoryStats(): Flow<List<CategoryStat>>



}
// Data class for Dashboard stats
data class CategoryStat(val category: String, val count: Int)