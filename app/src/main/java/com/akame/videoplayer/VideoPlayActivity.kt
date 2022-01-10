package com.akame.videoplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.akame.videoplayer.databinding.ActivityVideoPlayBinding
import com.akame.videoplayer.utils.MediaType

class VideoPlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoPlayBinding

    //    private val videoPath = "https://mirror.aarnet.edu.au/pub/TED-talks/911Mothers_2010W-480p.mp4"
    private val videoPath =
        "http://gxapp-vedio.oss-cn-qingdao.aliyuncs.com/xiaotiyun/minprogram/sports-video/001%20%E4%BB%B0%E5%8D%A7%E8%B5%B7%E5%9D%90.mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycle.addObserver(binding.videoPlay)
        val videoMediaType = MediaType.StringType(videoPath)
        binding.videoPlay.setup(lifecycleScope, videoMediaType, "你的选择没有错", isAutoPlay = false)
    }

    override fun onBackPressed() {
        if (binding.videoPlay.onBackPressed()) {
            super.onBackPressed()
        }
    }
}