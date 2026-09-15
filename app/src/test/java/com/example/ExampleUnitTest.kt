package com.example

import com.example.ui.MainViewModel
import com.example.util.ConnectivityObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeConnectivityObserver(
    var connectedState: Boolean = true
) : ConnectivityObserver {
    val flow = MutableSharedFlow<Boolean>()

    override val isConnectedFlow: Flow<Boolean> = flow
    override fun isCurrentlyConnected(): Boolean = connectedState
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state reflects connected status`() = runTest {
        val fakeObserver = FakeConnectivityObserver(connectedState = true)
        val viewModel = MainViewModel(fakeObserver)

        assertTrue(viewModel.uiState.value.isConnected)
    }

    @Test
    fun `initial state reflects offline status`() = runTest {
        val fakeObserver = FakeConnectivityObserver(connectedState = false)
        val viewModel = MainViewModel(fakeObserver)

        assertFalse(viewModel.uiState.value.isConnected)
    }

    @Test
    fun `retry connection updates state`() = runTest {
        val fakeObserver = FakeConnectivityObserver(connectedState = false)
        val viewModel = MainViewModel(fakeObserver)
        assertFalse(viewModel.uiState.value.isConnected)

        fakeObserver.connectedState = true
        viewModel.retryConnection()

        assertTrue(viewModel.uiState.value.isConnected)
    }

    @Test
    fun `clear captured image resets bitmap to null`() = runTest {
        val fakeObserver = FakeConnectivityObserver()
        val viewModel = MainViewModel(fakeObserver)

        viewModel.clearCapturedImage()
        assertNull(viewModel.uiState.value.capturedBitmap)
    }

    @Test
    fun `clear selected file resets file to null`() = runTest {
        val fakeObserver = FakeConnectivityObserver()
        val viewModel = MainViewModel(fakeObserver)

        viewModel.clearSelectedFile()
        assertNull(viewModel.uiState.value.selectedFile)
    }
}

