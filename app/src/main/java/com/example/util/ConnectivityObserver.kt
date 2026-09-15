package com.example.util

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val isConnectedFlow: Flow<Boolean>
    fun isCurrentlyConnected(): Boolean
}
