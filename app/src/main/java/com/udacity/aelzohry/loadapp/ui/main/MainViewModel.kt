package com.udacity.aelzohry.loadapp.ui.main

import android.app.Application
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.aelzohry.loadapp.R
import com.udacity.aelzohry.loadapp.helpers.Event
import com.udacity.aelzohry.loadapp.helpers.createChannel
import com.udacity.aelzohry.loadapp.helpers.sendDownloadCompleteNotification
import com.udacity.aelzohry.loadapp.models.DownloadFile
import com.udacity.aelzohry.loadapp.models.DownloadStatus
import com.udacity.aelzohry.loadapp.ui.loading_button.LoadingState

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val context: Context
        get() = getApplication<Application>().applicationContext

    private val notificationManager: NotificationManager
        get() = context.getSystemService(NotificationManager::class.java)

    private val downloadManager: DownloadManager
        get() = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState>
        get() = _loadingState

    private val _downloadFile = MutableLiveData<DownloadFile>()
    val downloadFile: LiveData<DownloadFile>
        get() = _downloadFile

    private var downloadingFile: DownloadFile? = null

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = _message

    private val downloadCompleteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (id == downloadID) {
                _loadingState.postValue(LoadingState.Completed)

                val downloadingFile = downloadingFile ?: return

                val query = DownloadManager.Query().setFilterById(id)
                val cursor = downloadManager.query(query)
                if (cursor != null && cursor.moveToFirst()) {
                    val downloadStatusInt: Int? = try {
                        cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                    } catch (e: Exception) {
                        null
                    }

                    cursor.close()

                    val downloadStatus = when (downloadStatusInt) {
                        DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.Succeed
                        else -> DownloadStatus.Failed
                    }

                    notificationManager.sendDownloadCompleteNotification(
                        context, downloadID, downloadingFile, downloadStatus
                    )
                }
            }
        }
    }

    private var downloadID: Long = 0

    init {
        _loadingState.value = LoadingState.Completed

        context.registerReceiver(downloadCompleteReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createNotificationChannel()
    }

    fun setDownloadFile(file: DownloadFile) {
        _downloadFile.value = file
    }

    fun onDownload() {
        if (loadingState.value == LoadingState.Loading) return

        val downloadFile = downloadFile.value

        if (downloadFile == null) {
            _message.value = Event(context.getString(R.string.select_file_message))
            return
        }

        downloadingFile = downloadFile

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

        val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        downloadID =
            downloadManager.enqueue(request) // enqueue puts the download request in the queue.
    }

    private fun createNotificationChannel() {
        notificationManager.createChannel(
            context.getString(R.string.notification_channel_id),
            context.getString(R.string.notification_channel_name),
            context.getString(R.string.notification_channel_description)
        )
    }

    override fun onCleared() {
        context.unregisterReceiver(downloadCompleteReceiver)
        super.onCleared()
    }

}