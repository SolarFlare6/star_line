package com.example.starline

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.starline.ui.main.BottomTab

class NavigationViewModel : ViewModel() {

    var appState by mutableStateOf(AppState())
        private set

    fun updateState(newState: AppState) {
        appState = newState
    }


}
