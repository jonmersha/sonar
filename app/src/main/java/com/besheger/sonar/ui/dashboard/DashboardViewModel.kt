package com.besheger.sonar.ui.dashboard

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.besheger.sonar.data.local.dao.CategoryStat
import com.besheger.sonar.data.local.entity.SonarTrackEntity
import com.besheger.sonar.data.repository.TrackRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: TrackRepository) : ViewModel() {
    // Inside DashboardViewModel.kt
    fun onEditCategory(track: SonarTrackEntity, newCategory: String) {
        viewModelScope.launch {
            // This calls the repository to update the specific track
            repository.updateTrackCategory(track.uriString, newCategory)
        }
    }
    // Inside your ViewModel
    val totalCount: StateFlow<Int> = repository.totalTracksCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val categories: StateFlow<List<CategoryStat>> = repository.categoryStats
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    fun deleteTrack(track: SonarTrackEntity) {
        viewModelScope.launch { repository.removeTrack(track) }
    }

    fun renameCategory(track: SonarTrackEntity, newName: String) {
        viewModelScope.launch { repository.updateCategory(track.uriString, newName) }
    }
    // --- 1. SEARCH & CATEGORY STATE ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    // Combined Flow: Reacts to Database changes, Search, and Category filters
    val tracks: StateFlow<List<SonarTrackEntity>> = combine(
        _searchQuery,
        _selectedCategory,
        repository.allTracks
    ) { query, category, allTracks ->
        allTracks.filter { track ->
            val matchesSearch = track.title.contains(query, ignoreCase = true) ||
                    track.artist.contains(query, ignoreCase = true)
            val matchesCategory = if (category == "All") true else track.category == category

            matchesSearch && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- 2. UI VIEW STATE ---
    var isGridView by mutableStateOf(true)
    var activeTrack by mutableStateOf<SonarTrackEntity?>(null)
    var localIsPlaying by mutableStateOf(false)
    var sliderPos by mutableFloatStateOf(0f)
    var currentPositionMs by mutableLongStateOf(0L)
    var totalDurationMs by mutableLongStateOf(1L)
    val magnitudes = mutableStateListOf(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f)

    // --- 3. CORE ACTIONS ---
//    fun onSearch(query: String) { _searchQuery.value = query }

    fun onCategorySelect(category: String) { _selectedCategory.value = category }

    fun toggleLayout() { isGridView = !isGridView }

    // --- 4. ENHANCED ACTIONS: REMOTE & SHARING ---


    // Requirement: Sharing audio file
    fun shareTrack(context: Context, track: SonarTrackEntity) {
        repository.shareAudioFile(context, track)
    }

    // --- 5. PLAYER LOGIC ---
    private var isDragging = false // Prevents slider from jumping while user is moving it

    fun onSliderDragging(value: Float) {
        isDragging = true
        sliderPos = value
    }

    fun onSliderFinished() {
        isDragging = false
    }

    fun updatePlaybackState(playing: Boolean, position: Long, duration: Long) {
        localIsPlaying = playing
        currentPositionMs = position
        totalDurationMs = duration.coerceAtLeast(1L)

        // Only update slider automatically if user isn't currently dragging it
        if (!isDragging) {
            sliderPos = position.toFloat() / totalDurationMs.toFloat()
        }
    }

    fun updateMagnitudes(data: ByteArray) {
        for (i in 0..7) {
            val sample = data[i * (data.size / 8)].toInt()
            val value = ((sample + 128).toFloat() / 255f)
            magnitudes[i] = (value * 1.2f).coerceIn(0.1f, 1f)
        }
    }


    // Call this when the user confirms a deletion
    fun onDeleteTrack(track: SonarTrackEntity) {
        viewModelScope.launch {
            repository.deleteTrack(track)
        }
    }

    // Call this for a "Create New" form
    fun onAddManualTrack(title: String, artist: String, category: String, path: String) {
        viewModelScope.launch {
            repository.addTrackManually(title, artist, category, path)
        }
    }
}