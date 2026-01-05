import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.besheger.sonar.data.local.entity.SonarTrackEntity
import com.besheger.sonar.ui.components.TrackGridItem
import com.besheger.sonar.ui.components.TrackListItem

@Composable
fun LibraryContent(
    tracks: List<SonarTrackEntity>,
    activeTrack: SonarTrackEntity?,
    onPlayRequest: (SonarTrackEntity) -> Unit,
    isGridView: Boolean,
    onDeleteTrack: (SonarTrackEntity) -> Unit,
    onEditCategory: (SonarTrackEntity, String) -> Unit
) {
    // These hold the state for the dialog
    var trackToEdit by remember { mutableStateOf<SonarTrackEntity?>(null) }
    var editCategoryName by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isGridView) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp) // Space for Player
            ) {
                items(tracks) { track ->
                    TrackGridItem(track, activeTrack, onPlayRequest)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(tracks) { track ->
                    TrackListItem(
                        track = track,
                        activeTrack = activeTrack,
                        onPlayRequest = onPlayRequest,
                        onEditClick = {
                            // THIS TRIGGERS THE DIALOG
                            trackToEdit = track
                            editCategoryName = track.category
                        },
                        onDeleteClick = { onDeleteTrack(track) }
                    )
                }
            }
        }
    }

    // --- DIALOG WITH BETTER CONTRAST ---
    if (trackToEdit != null) {
        AlertDialog(
            onDismissRequest = { trackToEdit = null },
            title = {
                Text("Edit Category",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            containerColor = Color(0xFF252525), // Lighter dark for contrast
            text = {
                Column {
                    Text(
                        "Updating: ${trackToEdit?.title}",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = editCategoryName,
                        onValueChange = { editCategoryName = it },
                        label = { Text("Category (e.g. Music, Spiritual)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.Cyan,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Cyan
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onEditCategory(trackToEdit!!, editCategoryName)
                    trackToEdit = null
                }) {
                    Text("SAVE CHANGES", color = Color.Cyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { trackToEdit = null }) {
                    Text("CANCEL", color = Color.White.copy(0.6f))
                }
            }
        )
    }
}