package com.besheger.sonar.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.besheger.sonar.data.local.dao.TrackDao
import com.besheger.sonar.data.local.entity.SonarTrackEntity

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import com.besheger.sonar.data.remote.RetrofitClient

class TrackRepository(private val trackDao: TrackDao) {
    val allTracks = trackDao.getAllTracks()
    val totalTracksCount = trackDao.getTrackCount()
    val categoryStats = trackDao.getCategoryStats()

    fun shareAudioFile(context: Context, track: SonarTrackEntity) {
        try {
            // 1. Convert the saved string back to a system URI
            val trackUri = Uri.parse(track.uriString)

            // 2. Build the Intent
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "audio/*"
                putExtra(Intent.EXTRA_STREAM, trackUri)
                // 3. IMPORTANT: Grant temporary read permission to the receiving app
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // 4. Start the chooser
            context.startActivity(Intent.createChooser(shareIntent, "Share: ${track.title}"))

        } catch (e: Exception) {
            android.util.Log.e("SHARE_ERROR", "${e.message}")
            Toast.makeText(context, "Could not share this file", Toast.LENGTH_SHORT).show()
        }
    }

    // 1. Manual Add
    suspend fun addManualTrack(title: String, artist: String, category: String, path: String) {
        // 1. Define the object
        val newTrack = SonarTrackEntity(
            uriString = "manual_${System.currentTimeMillis()}",
            title = title,
            artist = artist,
            durationText = "0:00",
            filePath = path,
            category = category,
            isRemote = false // Added this to match your Entity definition
        )

        // 2. Use the SAME name here
        trackDao.insertTrack(newTrack)
    }

    // 2. Edit Category
    suspend fun updateCategory(uri: String, newCategory: String) {
        trackDao.updateTrackCategory(uri, newCategory)
    }

    // 3. Remove/Delete
    suspend fun removeTrack(track: SonarTrackEntity) {
        trackDao.deleteTrack(track)
    }
    fun search(query: String) = trackDao.searchTracks(query)

    fun getByCategory(cat: String) = trackDao.getTracksByCategory(cat)

    // 2. Sync Local Files to Database
    suspend fun refreshLocalTracks(context: Context) {
        val scannedFiles = AudioScanner.queryAudioFiles(context)

        val entities = scannedFiles.map { track ->
            // Logic for Category: If folder name contains 'Spirit', mark as Spiritual
            val detectedCategory = when {
                track.title.contains("Spirit", true) -> "Spiritual"
                track.artist.contains("Motivation", true) -> "Motivation"
                else -> "Music"
            }

            SonarTrackEntity(
                uriString = track.uri.toString(),
                title = track.title,
                artist = track.artist,
                durationText = track.durationText,
                filePath = track.uri.path ?: "",
                category = detectedCategory
            )
        }
        trackDao.insertTracks(entities)
    }
    // Inside TrackRepository class
    suspend fun uploadTrackToServer(track: SonarTrackEntity) {
        val file = File(track.filePath)
        if (!file.exists()) return

        val requestFile = file.asRequestBody("audio/mpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val title = track.title.toRequestBody("text/plain".toMediaTypeOrNull())
        val artist = track.artist.toRequestBody("text/plain".toMediaTypeOrNull())
        val category = track.category.toRequestBody("text/plain".toMediaTypeOrNull())

        try {
            val response = RetrofitClient.instance.uploadTrack(body, title, artist, category)
            if (response.isSuccessful) {
                // Update local DB to mark as 'synced' if you add that field
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    /**
     * 1. ADD MANUALLY
     * Used for adding a track that wasn't found by the AudioScanner.
     */
    suspend fun addTrackManually(title: String, artist: String, category: String, filePath: String) {
        val manualTrack = SonarTrackEntity(
            // We create a unique URI for manual entries to avoid primary key conflicts
            uriString = "manual_${System.currentTimeMillis()}",
            title = title,
            artist = artist,
            durationText = "0:00", // Manual entries might not have metadata yet
            filePath = filePath,
            category = category,
            isRemote = false
        )
        trackDao.insertTrack(manualTrack)
    }


    suspend fun updateTrackCategory(uri: String, newCategory: String) {
        trackDao.updateTrackCategory(uri, newCategory)
    }

    /**
     * 3. REMOVE MANUALLY
     * Deletes the track record from the local Room database.
     */
    suspend fun deleteTrack(track: SonarTrackEntity) {
        trackDao.deleteTrack(track)
    }

}