package com.udacity.aelzohry.loadapp

sealed class ButtonState {
    object Loading : ButtonState()
    object Completed : ButtonState()
}