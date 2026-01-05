//
//package com.besheger.sonar
//
//import SonarAppTheme
//import android.content.ComponentName
//import android.os.Bundle
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.activity.viewModels
//import androidx.annotation.OptIn
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.lifecycleScope
//import androidx.media3.common.MediaItem
//import androidx.media3.common.MediaMetadata
//import androidx.media3.common.util.UnstableApi
//import androidx.media3.session.MediaController
//import androidx.media3.session.SessionToken
//import com.besheger.sonar.data.local.SonarDatabase
//import com.besheger.sonar.data.model.SonarTrack
//import com.besheger.sonar.data.repository.AudioScanner
//import com.besheger.sonar.data.repository.TrackRepository
//import com.besheger.sonar.service.PlaybackService
//import com.besheger.sonar.ui.dashboard.DashboardViewModel
//import com.besheger.sonar.ui.dashboard.SonarDashboard
//
//import com.google.common.util.concurrent.ListenableFuture
//import com.google.common.util.concurrent.MoreExecutors
//import kotlinx.coroutines.launch
//
//
//@OptIn(UnstableApi::class)
//class MainActivity : ComponentActivity() {
//    private var mediaController by mutableStateOf<MediaController?>(null)
//    private var localTracks by mutableStateOf<List<SonarTrack>>(emptyList())
//    private lateinit var controllerFuture: ListenableFuture<MediaController>
//
//    // Inside MainActivity.kt
//    private val database by lazy { SonarDatabase.getDatabase(this) }
//    private val repository by lazy { TrackRepository(database.trackDao()) }
//
//    // Initialize ViewModel with the Repository
//    private val dashboardViewModel: DashboardViewModel by viewModels {
//        object : ViewModelProvider.Factory {
//            override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                return DashboardViewModel(repository) as T
//            }
//        }
//    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        // 3. Permissions Setup
//        // Sync the database with local files on launch
//        lifecycleScope.launch {
//            repository.refreshLocalTracks(this@MainActivity)
//        }
//
//        val permissionsToRequest = if (android.os.Build.VERSION.SDK_INT >= 33) {
//            arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO, android.Manifest.permission.RECORD_AUDIO)
//        } else {
//            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.RECORD_AUDIO)
//        }
//
//        val sonarRequestLauncher = registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        ) { results ->
//            val storageGranted = results[permissionsToRequest[0]] ?: false
//            val audioGranted = results[android.Manifest.permission.RECORD_AUDIO] ?: false
//
//            if (storageGranted) {
//                // Now using the separate Repository for a cleaner Activity
//                localTracks = AudioScanner.queryAudioFiles(this)
//            } else {
//                Toast.makeText(this, "Music permission required", Toast.LENGTH_SHORT).show()
//            }
//
//            if (!audioGranted) {
//                Toast.makeText(this, "Visualizer needs audio permission", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Auto-launch permission request
//        sonarRequestLauncher.launch(permissionsToRequest)
//
//        // 4. Connect to PlaybackService
//        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
//        controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
//        controllerFuture.addListener({
//            try {
//                mediaController = controllerFuture.get()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }, MoreExecutors.directExecutor())
//
//        // 5. Compose UI
//        setContent {
//            SonarAppTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    SonarDashboard(
//                        tracks = localTracks,
//                        controller = mediaController,
//                        viewModel = dashboardViewModel,
//                        onPlayRequest = { track -> playTrack(track) }
//                    )
//                }
//            }
//        }
//    }
//
//    /**
//     * Handles the actual playback command.
//     * Updated to sync with the ViewModel for instant UI feedback.
//     */
//    private fun playTrack(track: SonarTrack) {
//        val controller = mediaController ?: run {
//            Toast.makeText(this, "Player not ready", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val mediaItem = MediaItem.Builder()
//            .setMediaId(track.uri.toString())
//            .setUri(track.uri)
//            .setMediaMetadata(
//                MediaMetadata.Builder()
//                    .setTitle(track.title)
//                    .setArtist(track.artist)
//                    .setDisplayTitle(track.title)
//                    .build()
//            )
//            .build()
//
//        try {
//            controller.setMediaItem(mediaItem)
//            controller.prepare()
//            controller.play()
//
//            // Update ViewModel immediately so the UI highlights the song right away
//            dashboardViewModel.activeTrack = track
//        } catch (e: Exception) {
//            android.util.Log.e("SonarPlayback", "Error: ${e.message}")
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        // Clean up the future to prevent leaks
//        MediaController.releaseFuture(controllerFuture)
//    }
//}
package com.besheger.sonar

import SonarAppTheme
import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.besheger.sonar.data.local.SonarDatabase
import com.besheger.sonar.data.local.entity.SonarTrackEntity // Import Entity
import com.besheger.sonar.data.repository.TrackRepository
import com.besheger.sonar.service.PlaybackService
import com.besheger.sonar.ui.dashboard.DashboardViewModel
import com.besheger.sonar.ui.dashboard.SonarDashboard
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
class MainActivity : ComponentActivity() {
    private var mediaController by mutableStateOf<MediaController?>(null)
    private lateinit var controllerFuture: ListenableFuture<MediaController>

    private val database by lazy { SonarDatabase.getDatabase(this) }
    private val repository by lazy { TrackRepository(database.trackDao()) }

    private val dashboardViewModel: DashboardViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return DashboardViewModel(repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Sync Database with Storage
        lifecycleScope.launch {
            repository.refreshLocalTracks(this@MainActivity)
        }

        // --- Permissions Logic ---
        val permissionsToRequest = if (android.os.Build.VERSION.SDK_INT >= 33) {
            arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO, android.Manifest.permission.RECORD_AUDIO)
        } else {
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.RECORD_AUDIO)
        }

        val sonarRequestLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            val storageGranted = results[permissionsToRequest[0]] ?: false
            if (storageGranted) {
                // Trigger a refresh after permission is granted
                lifecycleScope.launch { repository.refreshLocalTracks(this@MainActivity) }
            } else {
                Toast.makeText(this, "Music permission required", Toast.LENGTH_SHORT).show()
            }
        }
        sonarRequestLauncher.launch(permissionsToRequest)

        // --- Media3 Setup ---
        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener({
            try {
                mediaController = controllerFuture.get()
            } catch (e: Exception) { e.printStackTrace() }
        }, MoreExecutors.directExecutor())

        // --- UI Rendering ---
        setContent {
            // We observe the "Live" data from the database via the ViewModel
            val databaseTracks by dashboardViewModel.tracks.collectAsState()

            SonarAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SonarDashboard(
                        tracks = databaseTracks, // Now using Flow from Room
                        controller = mediaController,
                        viewModel = dashboardViewModel,
                        onPlayRequest = { track -> playTrack(track) }
                    )
                }
            }
        }
    }

    /**
     * Updated to use SonarTrackEntity to match Dashboard
     */
    private fun playTrack(track: SonarTrackEntity) {
        val controller = mediaController ?: run {
            Toast.makeText(this, "Player not ready", Toast.LENGTH_SHORT).show()
            return
        }

        val trackUri = Uri.parse(track.uriString)

        val mediaItem = MediaItem.Builder()
            .setMediaId(track.uriString)
            .setUri(trackUri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(track.title)
                    .setArtist(track.artist)
                    .setDisplayTitle(track.title)
                    .build()
            )
            .build()

        try {
            controller.setMediaItem(mediaItem)
            controller.prepare()
            controller.play()

            // Update ViewModel immediately for UI highlighting
            dashboardViewModel.activeTrack = track
        } catch (e: Exception) {
            android.util.Log.e("SonarPlayback", "Error: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MediaController.releaseFuture(controllerFuture)
    }
}