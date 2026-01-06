package com.besheger.sonar.ui.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.besheger.sonar.data.local.entity.SonarTrackEntity
@Composable
fun TrackListItem(
    track: SonarTrackEntity,
    activeTrack: SonarTrackEntity?,
    onPlayRequest: (SonarTrackEntity) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit // NEW PARAMETER

) {
    val isSelected = activeTrack?.uriString == track.uriString

    Surface(
        onClick = { onPlayRequest(track) },
        // INCREASED OPACITY: Changed from 0.04f to 0.15f/0.3f for better visibility
        color = if (isSelected) Color.Cyan.copy(0.2f) else Color.White.copy(0.1f),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp), // Added spacing between items
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color.Cyan) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp), // Increased padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.MusicNote,
                contentDescription = null,
                tint = if(isSelected) Color.Cyan else Color.White.copy(0.7f),
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    color = Color.White,
                    fontSize = 16.sp, // Slightly larger
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = track.artist,
                    color = Color.White.copy(0.6f), // Brighter than standard Gray
                    fontSize = 13.sp,
                    maxLines = 1
                )
            }

            // --- ACTION BUTTONS ---
            // --- ACTION BUTTONS ---
            IconButton(onClick = onShareClick) { // SHARE BUTTON
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color.Cyan.copy(0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(onClick = {
                android.util.Log.d("UI_DEBUG", "Edit clicked for ${track.title}")
                onEditClick()
            }) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.Cyan,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red.copy(0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}