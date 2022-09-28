package com.udacity.aelzohry.loadapp.ui.loading_button

sealed class LoadingState {
    object Loading : LoadingState()
    object Completed : LoadingState()
}