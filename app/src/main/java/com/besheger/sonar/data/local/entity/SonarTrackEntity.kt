package com.besheger.sonar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class SonarTrackEntity(
    @PrimaryKey val uriString: String,
    val title: String,
    val artist: String,
    val durationText: String,
    val filePath: String,
    val category: String = "Music", // Default category
    val isRemote: Boolean = false
)