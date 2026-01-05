package com.besheger.sonar.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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

@Composable
fun AddTrackDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, artist: String, category: String, path: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Music") }
    var path by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A), // Matches your dashboard theme
        title = {
            Text("Add Track Manually", color = Color.Cyan, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CustomDialogTextField(value = title, label = "Title", onValueChange = { title = it })
                CustomDialogTextField(value = artist, label = "Artist", onValueChange = { artist = it })
                CustomDialogTextField(value = category, label = "Category", onValueChange = { category = it })
                CustomDialogTextField(value = path, label = "File Path", onValueChange = { path = it })
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && path.isNotBlank()) {
                        onConfirm(title, artist, category, path)
                    }
                }
            ) {
                Text("ADD", color = Color.Cyan, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = Color.Gray)
            }
        }
    )
}

@Composable
fun CustomDialogTextField(value: String, label: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.Cyan,
            unfocusedBorderColor = Color.Gray
        )
    )
}