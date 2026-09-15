package com.example.model

import android.net.Uri

data class SelectedFileInfo(
    val uri: Uri,
    val name: String,
    val size: Long,
    val formattedSize: String,
    val mimeType: String? = null
)
