package com.udacity.aelzohry.loadapp.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Parcelable
import androidx.core.app.NotificationCompat
import com.udacity.aelzohry.loadapp.R
import com.udacity.aelzohry.loadapp.models.DownloadFile
import com.udacity.aelzohry.loadapp.models.DownloadStatus
import com.udacity.aelzohry.loadapp.ui.details.DetailsActivity

private const val REQUEST_CODE = 0

fun NotificationManager.sendDownloadCompleteNotification(
    applicationContext: Context,
    downloadId: Long,
    downloadFile: DownloadFile,
    downloadStatus: DownloadStatus,
) {
    val notificationId = downloadId.toInt()

    val fileDownloadedIntent = Intent(applicationContext, DetailsActivity::class.java)
    fileDownloadedIntent.putExtra(DetailsActivity.NOTIFICATION_ID_KEY, notificationId)
    fileDownloadedIntent.putExtra(DetailsActivity.DOWNLOAD_FILE_KEY, downloadFile as Parcelable)
    fileDownloadedIntent.putExtra(DetailsActivity.DOWNLOAD_STATUS_KEY, downloadStatus as Parcelable)

    val fileDownloadedPendingIntent =
        PendingIntent.getActivity(
            applicationContext,
            REQUEST_CODE,
            fileDownloadedIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

    val builder = NotificationCompat
        .Builder(
            applicationContext,
            applicationContext.getString(R.string.notification_channel_id)
        )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(downloadStatus.title)
        .setContentText(downloadFile.title)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.notification_action_button),
            fileDownloadedPendingIntent
        )

    notify(notificationId, builder.build())
}

// Create notification channel for later Android versions
fun NotificationManager.createChannel(
    channelId: String,
    channelName: String,
    channelDescription: String,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setShowBadge(false)
        }

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = channelDescription

        createNotificationChannel(notificationChannel)
    }
}