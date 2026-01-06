@file:kotlin.OptIn(ExperimentalMaterial3Api::class)

package com.besheger.sonar.ui.dashboard

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import com.besheger.sonar.data.local.entity.SonarTrackEntity
import kotlinx.coroutines.delay

// Local project imports

import com.besheger.sonar.data.model.formatDuration
import com.besheger.sonar.ui.components.PlaybackControls
import com.besheger.sonar.ui.components.RealtimeBars
import com.besheger.sonar.ui.components.TrackGridItem
import com.besheger.sonar.ui.components.TrackListItem


//
//@UnstableApi
//@OptIn(UnstableApi::class)
//@Composable
//fun SonarDashboard(
//    tracks: List<SonarTrackEntity>,
//    controller: MediaController?,
//    onPlayRequest: (SonarTrackEntity) -> Unit,
//    viewModel: DashboardViewModel
//) {
//    var trackToEdit by remember { mutableStateOf<SonarTrackEntity?>(null) }
//    var editCategoryName by remember { mutableStateOf("") }
//    var showAddDialog by remember { mutableStateOf(false) }
//    // 1. SYNC PLAYER TO VIEWMODEL
//    LaunchedEffect(controller) {
//        while (true) {
//            controller?.let {
//                viewModel.updatePlaybackState(
//                    playing = it.isPlaying,
//                    position = it.currentPosition,
//                    duration = it.duration
//                )
//
//                // Sync Metadata
//                val metadata = it.mediaMetadata
//                if (metadata.title != null && viewModel.activeTrack?.title != metadata.title.toString()) {
//                    viewModel.activeTrack = tracks.find { t -> t.title == metadata.title.toString() }
//                }
//            }
//            delay(500)
//        }
//    }
//
//    // 2. AUDIO VISUALIZER ENGINE
//    DisposableEffect(viewModel.localIsPlaying, controller) {
//        var visualizer: android.media.audiofx.Visualizer? = null
//
//        if (viewModel.localIsPlaying && controller != null) {
//            try {
//                val sessionId = controller.audioSessionId.takeIf { it != 0 } ?: 0
//                visualizer = android.media.audiofx.Visualizer(sessionId).apply {
//                    captureSize = android.media.audiofx.Visualizer.getCaptureSizeRange()[1]
//                    setDataCaptureListener(object : android.media.audiofx.Visualizer.OnDataCaptureListener {
//                        override fun onWaveFormDataCapture(v: android.media.audiofx.Visualizer?, data: ByteArray?, samplingRate: Int) {
//                            data?.let { viewModel.updateMagnitudes(it) }
//                        }
//                        override fun onFftDataCapture(v: android.media.audiofx.Visualizer?, data: ByteArray?, samplingRate: Int) {}
//                    }, android.media.audiofx.Visualizer.getMaxCaptureRate() / 2, true, false)
//                    enabled = true
//                }
//            } catch (e: Exception) {
//                android.util.Log.e("VisualizerError", "${e.message}")
//            }
//        }
//        onDispose {
//            visualizer?.release()
//        }
//    }
//
//    // --- MAIN LAYOUT ---
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Brush.verticalGradient(listOf(Color(0xFF0A1A1E), Color(0xFF071013))))
//            .padding(top = 40.dp)
//    ) {
//        // --- VISUALIZER AREA ---
//        Box(
//            modifier = Modifier.fillMaxWidth().height(150.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            if (viewModel.localIsPlaying) {
//                RealtimeBars(magnitudes = viewModel.magnitudes)
//            } else {
//                Text(
//                    text = "READY TO SCAN",
//                    color = Color.Cyan.copy(0.2f),
//                    fontSize = 12.sp,
//                    letterSpacing = 2.sp
//                )
//            }
//        }
//
//        // --- TITLE & ARTIST ---
//        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
//            Text(viewModel.activeTrack?.title ?: "Select Music", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, maxLines = 1)
//            Text(viewModel.activeTrack?.artist ?: "Local Files", color = Color.Gray, fontSize = 14.sp)
//        }
//
//        PlaybackControls(
//            isPlaying = viewModel.localIsPlaying,
//            sliderPos = viewModel.sliderPos,
//            currentPosText = formatDuration(viewModel.currentPositionMs),
//            durationText = formatDuration(viewModel.totalDurationMs),
//            onValueChange = { viewModel.onSliderDragging(it) },
//            onValueChangeFinished = {
//                viewModel.onSliderFinished()
//                controller?.seekTo((viewModel.sliderPos * viewModel.totalDurationMs).toLong())
//            },
//            onPlayPause = { if (viewModel.localIsPlaying) controller?.pause() else controller?.play() },
//            onNext = { controller?.seekForward() },
//            onPrevious = { controller?.seekBack() }
//        )
//
//
//
//        val categories = listOf("All", "Music", "Spiritual", "Motivation", "Other")
//        val selectedCat by viewModel.selectedCategory.collectAsState()
//
//        LazyRow(
//            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            items(categories) { cat ->
//                FilterChip(
//                    selected = (selectedCat == cat),
//                    onClick = { viewModel.onCategorySelect(cat) },
//                    label = { Text(cat, color = Color.White) },
//                    colors = FilterChipDefaults.filterChipColors(
//                        selectedContainerColor = Color.Cyan.copy(0.3f)
//                    )
//                )
//            }
//        }
//
//
//        // --- LIBRARY HEADER ---
//        Row(
//            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text("Library", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
//            IconButton(onClick = { viewModel.toggleLayout() }) {
//                Icon(
//                    imageVector = if (viewModel.isGridView) Icons.Default.List else Icons.Default.GridView,
//                    contentDescription = null,
//                    tint = Color.Cyan
//                )
//            }
//        }
//
//        // --- CONTENT SWITCHER ---
//        Box(modifier = Modifier.weight(1f)) {
//            if (viewModel.isGridView) {
//                LazyVerticalGrid(
//                    columns = GridCells.Fixed(2),
//                    modifier = Modifier.padding(horizontal = 20.dp),
//                    horizontalArrangement = Arrangement.spacedBy(12.dp),
//                    verticalArrangement = Arrangement.spacedBy(12.dp),
//                    contentPadding = PaddingValues(bottom = 24.dp)
//                ) {
//                    items(tracks) { track ->
//                        TrackGridItem(track, viewModel.activeTrack, onPlayRequest)
//                    }
//                }
//            } else {
//                LazyColumn(
//                    modifier = Modifier.padding(horizontal = 20.dp),
//                    verticalArrangement = Arrangement.spacedBy(8.dp),
//                    contentPadding = PaddingValues(bottom = 24.dp)
//                ) {
//                    items(tracks) { track ->
//                        TrackListItem(track, viewModel.activeTrack, onPlayRequest,onEditClick = {
//                            // This opens the edit dialog we built earlier
//                            trackToEdit = track
//                            editCategoryName = track.category
//                        },
//                            onDeleteClick = {
//                                // This calls the delete logic in your ViewModel
//                                viewModel.onDeleteTrack(track)
//                            })
//                    }
//                }
//            }
//        }
//    }
//}
@UnstableApi
@OptIn(UnstableApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun SonarDashboard(
    tracks: List<SonarTrackEntity>,
    controller: MediaController?,
    onPlayRequest: (SonarTrackEntity) -> Unit,
    viewModel: DashboardViewModel
) {
    // --- 1. STATE MANAGEMENT ---
    var trackToEdit by remember { mutableStateOf<SonarTrackEntity?>(null) }
    var editCategoryName by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) } // Dropdown state

    val categoryOptions = listOf("Music", "Spiritual", "Motivation", "Other")

    // Filter tracks by Search and selected Category
    val selectedCat by viewModel.selectedCategory.collectAsState()
    val filteredTracks = tracks.filter { track ->
        val matchesSearch = track.title.contains(searchQuery, ignoreCase = true) ||
                track.artist.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCat == "All" || track.category == selectedCat
        matchesSearch && matchesCategory
    }

    // --- 2. PLAYER & VISUALIZER SYNC ---
    LaunchedEffect(controller) {
        while (true) {
            controller?.let {
                viewModel.updatePlaybackState(it.isPlaying, it.currentPosition, it.duration)
                val metadata = it.mediaMetadata
                if (metadata.title != null && viewModel.activeTrack?.title != metadata.title.toString()) {
                    viewModel.activeTrack = tracks.find { t -> t.title == metadata.title.toString() }
                }
            }
            delay(500)
        }
    }

    // --- 3. UI LAYOUT ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0A1A1E), Color(0xFF071013))))
            .padding(top = 40.dp)
    ) {
        // TOP SEARCH BAR
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            placeholder = { Text("Search title or artist...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Cyan.copy(0.6f)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.Gray)
                    }
                }
            },
            singleLine = true,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.White.copy(0.05f),
                unfocusedContainerColor = Color.White.copy(0.05f),
                focusedBorderColor = Color.Cyan.copy(0.5f),
                unfocusedBorderColor = Color.Transparent
            )
        )

        // VISUALIZER
        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
            if (viewModel.localIsPlaying) RealtimeBars(magnitudes = viewModel.magnitudes)
            else Text("READY TO SCAN", color = Color.Cyan.copy(0.2f), fontSize = 12.sp, letterSpacing = 2.sp)
        }

        // PLAYBACK CONTROLS
        PlaybackControls(
            isPlaying = viewModel.localIsPlaying,
            sliderPos = viewModel.sliderPos,
            currentPosText = formatDuration(viewModel.currentPositionMs),
            durationText = formatDuration(viewModel.totalDurationMs),
            onValueChange = { viewModel.onSliderDragging(it) },
            onValueChangeFinished = {
                viewModel.onSliderFinished()
                controller?.seekTo((viewModel.sliderPos * viewModel.totalDurationMs).toLong())
            },
            onPlayPause = { if (viewModel.localIsPlaying) controller?.pause() else controller?.play() },
            onNext = { controller?.seekForward() },
            onPrevious = { controller?.seekBack() }
        )

        // CATEGORY FILTER CHIPS
        LazyRow(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("All") + categoryOptions) { cat ->
                FilterChip(
                    selected = (selectedCat == cat),
                    onClick = { viewModel.onCategorySelect(cat) },
                    label = { Text(cat, color = Color.White) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color.Cyan.copy(0.3f))
                )
            }
        }

        // LIBRARY HEADER
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Library", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            IconButton(onClick = { viewModel.toggleLayout() }) {
                Icon(
                    imageVector = if (viewModel.isGridView) Icons.Default.List else Icons.Default.GridView,
                    contentDescription = null, tint = Color.Cyan
                )
            }
        }

        // --- 4. LIST / GRID CONTENT ---
        val context = androidx.compose.ui.platform.LocalContext.current
        Box(modifier = Modifier.weight(1f)) {
            if (viewModel.isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTracks) { track ->
                        TrackGridItem(track, viewModel.activeTrack, onPlayRequest)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredTracks) { track ->
                        TrackListItem(
                            track = track,
                            activeTrack = viewModel.activeTrack,
                            onPlayRequest = onPlayRequest,
                            onEditClick = {
                                trackToEdit = track
                                editCategoryName = track.category
                            },
                            onDeleteClick = { viewModel.onDeleteTrack(track) },
                            onShareClick = {
                                // CALL THE VIEWMODEL/REPOSITORY HERE
                                viewModel.shareTrack(context, track)
                            }
                        )
                    }
                }
            }
        }
    }

    // --- 5. EDIT DIALOG WITH DROPDOWN ---
    if (trackToEdit != null) {
        AlertDialog(
            onDismissRequest = { trackToEdit = null },
            containerColor = Color(0xFF1A1A1A),
            title = { Text("Update Category", color = Color.White) },
            text = {
                Column {
                    Text("Track: ${trackToEdit?.title}", color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = editCategoryName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.Cyan
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            containerColor = Color(0xFF252525)
                        ) {
                            categoryOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, color = Color.White) },
                                    onClick = {
                                        editCategoryName = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    trackToEdit?.let { viewModel.onEditCategory(it, editCategoryName) }
                    trackToEdit = null
                }) { Text("SAVE", color = Color.Cyan, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { trackToEdit = null }) { Text("CANCEL", color = Color.Gray) }
            }
        )
    }
}