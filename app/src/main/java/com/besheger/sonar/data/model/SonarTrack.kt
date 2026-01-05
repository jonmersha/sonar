package com.besheger.sonar.data.model

import android.net.Uri

data class SonarTrack(
    val title: String,
    val artist: String,
    val uri: Uri,
    val durationText: String
)
fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}