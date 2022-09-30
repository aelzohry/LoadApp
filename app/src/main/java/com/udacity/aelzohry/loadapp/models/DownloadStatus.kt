package com.udacity.aelzohry.loadapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class DownloadStatus : Parcelable {
    Succeed,
    Failed;

    val title: String
        get() = when (this) {
            Succeed -> "Succeed"
            Failed -> "Failed"
        }
}