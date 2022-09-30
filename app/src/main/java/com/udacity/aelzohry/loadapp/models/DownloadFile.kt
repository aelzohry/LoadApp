package com.udacity.aelzohry.loadapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class DownloadFile(val title: String, val url: String): Parcelable {
    Glide("Glide", "https://github.com/bumptech/glide"),
    LoadApp("Udacity C3 Project", "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"),
    Retrofit("Retrofit", "https://github.com/square/retrofit"),
    Other("Other", "")
}