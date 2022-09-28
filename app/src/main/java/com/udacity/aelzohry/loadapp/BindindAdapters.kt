package com.udacity.aelzohry.loadapp

import androidx.databinding.BindingAdapter
import com.udacity.aelzohry.loadapp.ui.loading_button.LoadingButton
import com.udacity.aelzohry.loadapp.ui.loading_button.LoadingState

@BindingAdapter("loadingState")
fun LoadingButton.setLoadingState(state: LoadingState?) {
    this.loadingState = state ?: LoadingState.Completed
}