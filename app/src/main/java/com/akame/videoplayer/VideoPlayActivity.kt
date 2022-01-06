package com.akame.videoplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.akame.videoplayer.databinding.ActivityVideoPlayBinding

class VideoPlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoPlayBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.videoPlay.setup(lifecycleScope, MediaType.StringType("https://mirror.aarnet.edu.au/pub/TED-talks/911Mothers_2010W-480p.mp4"))
    }
}