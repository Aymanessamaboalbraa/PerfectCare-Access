package com.example.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.model.SelectedFileInfo
import com.example.util.ConnectivityObserver
import com.example.util.FileUtils
import com.example.util.NetworkConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainUiState(
    val isConnected: Boolean = true,
    val isCheckingConnection: Boolean = false,
    val capturedBitmap: Bitmap? = null,
    val selectedFile: SelectedFileInfo? = null,
    val showCameraPermissionRationale: Boolean = false,
    val statusMessage: String? = null
)

class MainViewModel(
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MainUiState(
            isConnected = connectivityObserver.isCurrentlyConnected()
        )
    )
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        observeNetworkConnectivity()
    }

    private fun observeNetworkConnectivity() {
        viewModelScope.launch {
            connectivityObserver.isConnectedFlow.collect { connected ->
                _uiState.value = _uiState.value.copy(
                    isConnected = connected,
                    isCheckingConnection = false
                )
            }
        }
    }

    fun onImageCaptured(bitmap: Bitmap?) {
        if (bitmap != null) {
            _uiState.value = _uiState.value.copy(
                capturedBitmap = bitmap,
                statusMessage = "Photo captured successfully"
            )
        }
    }

    fun onFileSelected(context: Context, uri: Uri?) {
        if (uri != null) {
            val fileInfo = FileUtils.getFileInfo(context, uri)
            _uiState.value = _uiState.value.copy(
                selectedFile = fileInfo,
                statusMessage = "File loaded: ${fileInfo.name}"
            )
        }
    }

    fun retryConnection() {
        _uiState.value = _uiState.value.copy(isCheckingConnection = true)
        val connected = connectivityObserver.isCurrentlyConnected()
        _uiState.value = _uiState.value.copy(
            isConnected = connected,
            isCheckingConnection = false
        )
    }

    fun clearCapturedImage() {
        _uiState.value = _uiState.value.copy(capturedBitmap = null)
    }

    fun clearSelectedFile() {
        _uiState.value = _uiState.value.copy(selectedFile = null)
    }

    fun showCameraPermissionRationale(show: Boolean) {
        _uiState.value = _uiState.value.copy(showCameraPermissionRationale = show)
    }

    fun clearStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainViewModel(NetworkConnectivityObserver(context)) as T
                }
            }
    }
}
