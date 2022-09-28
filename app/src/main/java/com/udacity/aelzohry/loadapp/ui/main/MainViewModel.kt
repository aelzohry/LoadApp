package com.udacity.aelzohry.loadapp.ui.main

import android.app.Application
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.aelzohry.loadapp.R
import com.udacity.aelzohry.loadapp.helpers.Event
import com.udacity.aelzohry.loadapp.models.DownloadFile
import com.udacity.aelzohry.loadapp.ui.loading_button.LoadingState

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val context: Context
        get() = getApplication<Application>().applicationContext

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _downloadFile = MutableLiveData<DownloadFile>()
    val downloadFile: LiveData<DownloadFile>
        get() = _downloadFile

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = _message

    private val downloadCompleteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (id == downloadID) {
                _loadingState.postValue(LoadingState.Completed)
            }
        }
    }

    private var downloadID: Long = 0

    init {
        _loadingState.value = LoadingState.Completed

        context.registerReceiver(downloadCompleteReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    fun setDownloadFile(file: DownloadFile) {
        _downloadFile.value = file
    }

    fun onDownload() {
        if (loadingState.value == LoadingState.Loading) return

        val downloadFile = downloadFile.value

        if (downloadFile == null) {
            _message.value = Event("Select file to download")
            return
        }

        _loadingState.value = LoadingState.Loading
        download(downloadFile.url)
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(context.getString(R.string.app_name))
                .setDescription(context.getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager =
            context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager

        downloadID =
            downloadManager.enqueue(request) // enqueue puts the download request in the queue.
    }

    override fun onCleared() {
        context.unregisterReceiver(downloadCompleteReceiver)
        super.onCleared()
    }

}