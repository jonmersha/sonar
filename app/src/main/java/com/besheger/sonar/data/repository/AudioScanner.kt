package com.besheger.sonar.data.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.besheger.sonar.data.model.SonarTrack

object AudioScanner {

    fun queryAudioFiles(context: Context): List<SonarTrack> {
        val trackList = mutableListOf<SonarTrack>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION
        )

        // Filter: Only music (not ringtones), and longer than 10 seconds
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= 10000"

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )

        cursor?.use { c ->
            val idColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (c.moveToNext()) {
                val id = c.getLong(idColumn)
                val title = c.getString(titleColumn) ?: "Unknown Title"
                val artist = c.getString(artistColumn) ?: "Unknown Artist"
                val duration = c.getLong(durationColumn).toString()

                val uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                trackList.add(
                    SonarTrack(
                        uri = uri,
                        title = title,
                        artist = artist,
                        durationText = duration
                    )
                )
            }
        }
        return trackList
    }
}