
package com.besheger.sonar.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Import the Entity
import com.besheger.sonar.data.local.entity.SonarTrackEntity

@Composable
fun TrackGridItem(
    track: SonarTrackEntity,             // Corrected
    activeTrack: SonarTrackEntity?,      // Corrected from SonarTrack?
    onPlayRequest: (SonarTrackEntity) -> Unit // Corrected callback type
) {
    // Compare URIs to check selection
    val isSelected = track.uriString == activeTrack?.uriString

    Surface(
        onClick = { onPlayRequest(track) }, // Now passing the Entity correctly
        color = Color.White.copy(if (isSelected) 0.12f else 0.04f),
        shape = RoundedCornerShape(16.dp),
        border = if (isSelected) BorderStroke(2.dp, Color.Cyan) else null,
        modifier = Modifier.aspectRatio(1f)
    ) {
        Box(Modifier.padding(12.dp)) {
            Text(
                text = track.durationText,
                color = if(isSelected) Color.Cyan else Color.Gray,
                fontSize = 9.sp,
                modifier = Modifier.align(Alignment.TopEnd)
            )
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = if(isSelected) Color.Cyan else Color.Gray,
                    modifier = Modifier.size(if(isSelected) 36.dp else 24.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = track.title,
                    color = Color.White,
                    fontSize = 12.sp,
                    maxLines = 1,
                    fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                // Optional: Show the category tag in the grid
                Text(
                    text = track.category,
                    color = Color.Cyan.copy(0.5f),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}
