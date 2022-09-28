package com.udacity.aelzohry.loadapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.udacity.aelzohry.loadapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.downloadButton.setOnClickListener { onDownload() }
    }

    private fun onDownload() {
        val downloadButton = binding.downloadButton
        downloadButton.loadingState = when (downloadButton.loadingState) {
            LoadingState.Completed -> LoadingState.Loading
            LoadingState.Loading -> LoadingState.Completed
        }
    }
}