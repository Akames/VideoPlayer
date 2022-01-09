package com.akame.videoplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.akame.videoplayer.databinding.ActivityVideoPlayBinding
import com.akame.videoplayer.utils.MediaType

class VideoPlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoPlayBinding
    private val videoPath = "https://mirror.aarnet.edu.au/pub/TED-talks/911Mothers_2010W-480p.mp4"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycle.addObserver(binding.videoPlay)
        val videoMediaType = MediaType.StringType(videoPath)
        binding.videoPlay.setup(lifecycleScope, videoMediaType, "你的选择没有错", false)
    }

    override fun onBackPressed() {
        if (binding.videoPlay.onBackPressed()) {
            super.onBackPressed()
        }
    }
}