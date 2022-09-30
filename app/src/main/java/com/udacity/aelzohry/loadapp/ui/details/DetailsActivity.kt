package com.udacity.aelzohry.loadapp.ui.details

import android.app.NotificationManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.udacity.aelzohry.loadapp.R
import com.udacity.aelzohry.loadapp.databinding.ActivityDetailsBinding
import com.udacity.aelzohry.loadapp.models.DownloadFile
import com.udacity.aelzohry.loadapp.models.DownloadStatus

class DetailsActivity : AppCompatActivity() {

    companion object {
        const val NOTIFICATION_ID_KEY = "notification_id"
        const val DOWNLOAD_FILE_KEY = "download_file"
        const val DOWNLOAD_STATUS_KEY = "download_status"
    }

    private lateinit var binding: ActivityDetailsBinding

    private var notificationId: Int? = null
    private var downloadFile: DownloadFile? = null
    private var downloadStatus: DownloadStatus? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details)

        notificationId = intent.getIntExtra(NOTIFICATION_ID_KEY, -1)
        downloadFile = intent.getParcelableExtra(DOWNLOAD_FILE_KEY)
        downloadStatus = intent.getParcelableExtra(DOWNLOAD_STATUS_KEY)

        cancelNotification()
        fillData()

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun cancelNotification() {
        val notificationId = notificationId ?: return
        if (notificationId == -1) return

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancel(notificationId)
    }

    private fun fillData() {
        val downloadFile = downloadFile ?: return
        val downloadStatus = downloadStatus ?: return

        binding.apply {
            titleTextView.text = downloadFile.title
            urlTextView.text = downloadFile.url
            statusTextView.text = downloadStatus.title

            statusTextView.setTextColor(
                if (downloadStatus == DownloadStatus.Succeed)
                    Color.GREEN
                else
                    Color.RED
            )
        }
    }

}