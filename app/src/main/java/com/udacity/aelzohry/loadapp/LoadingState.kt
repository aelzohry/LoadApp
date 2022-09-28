package com.udacity.aelzohry.loadapp

sealed class LoadingState {
    object Loading : LoadingState()
    object Completed : LoadingState()
}