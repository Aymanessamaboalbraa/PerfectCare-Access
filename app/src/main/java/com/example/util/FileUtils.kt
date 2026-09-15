package com.example.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.model.SelectedFileInfo
import java.text.DecimalFormat

object FileUtils {

    fun getFileInfo(context: Context, uri: Uri): SelectedFileInfo {
        var name = "Selected Document"
        var size: Long = -1
        val contentResolver = context.contentResolver

        try {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1 && !cursor.isNull(nameIndex)) {
                        name = cursor.getString(nameIndex)
                    }
                    if (sizeIndex != -1 && !cursor.isNull(sizeIndex)) {
                        size = cursor.getLong(sizeIndex)
                    }
                }
            }
        } catch (e: Exception) {
            val lastSegment = uri.lastPathSegment
            if (!lastSegment.isNullOrBlank()) {
                name = lastSegment
            }
        }

        if (name.isBlank() && uri.lastPathSegment != null) {
            name = uri.lastPathSegment ?: "Selected File"
        }

        val mimeType = try {
            contentResolver.getType(uri)
        } catch (e: Exception) {
            null
        }

        return SelectedFileInfo(
            uri = uri,
            name = name,
            size = size,
            formattedSize = formatFileSize(size),
            mimeType = mimeType
        )
    }

    private fun formatFileSize(size: Long): String {
        if (size <= 0) return "Size unavailable"
        if (size < 1024) return "$size B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        val format = DecimalFormat("#,##0.#")
        return "${format.format(size / Math.pow(1024.0, digitGroups.toDouble()))} ${units[digitGroups]}"
    }
}
