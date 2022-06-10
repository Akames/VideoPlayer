package com.akame.videoplayer

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.akame.videoplayer.databinding.ActivityVideoPlayBinding
import com.akame.videoplayer.impl.IVideoPlayListener
import com.akame.videoplayer.utils.MediaType

class VideoPlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoPlayBinding

    //    private val videoPath = "https://mirror.aarnet.edu.au/pub/TED-talks/911Mothers_2010W-480p.mp4"

    private val videoPath1 =
        "http://gxapp-vedio.oss-cn-qingdao.aliyuncs.com/xiaotiyun/minprogram/sports-video/001%20%E4%BB%B0%E5%8D%A7%E8%B5%B7%E5%9D%90.mp4"

    private val videoPath2 =
        "https://gxapp-vedio.oss-cn-qingdao.aliyuncs.com/xiaotiyun/app/sports-clock-video/2021-12-16/ee2c165e94fe1b2679b887b01bb31b23.mp4"
    private var isTure = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        StatusBarUtil.init(this, true, false, binding.tvTitle) { k, s ->

        }
        binding.videoPlay.bindLifecycle(lifecycle)
        val videoMediaType = MediaType.StringType(videoPath1)
        binding.videoPlay.setup(lifecycleScope, videoMediaType, "你的选择没有错", isAutoPlay = false)

        binding.tvTitle.setOnClickListener {
            val videpath = if (isTure) videoPath1 else videoPath2
            isTure = !isTure
            val videoMediaType = MediaType.StringType(videpath)
            binding.videoPlay.setup(lifecycleScope, videoMediaType, "你的选择没有错", isAutoPlay = false)
        }
        binding.videoPlay.setVideoPlayListener(object : IVideoPlayListener {
            override fun enterFullScreen() {
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.videoPlay, InputMethodManager.SHOW_FORCED)
                imm.hideSoftInputFromWindow(binding.videoPlay.windowToken, 0) //强制隐藏键盘
                binding.videoPlay.requestFocus()
            }

            override fun exitFullScreen() {

            }
        })
    }

    override fun onBackPressed() {
        if (binding.videoPlay.onBackPressed()) {
            super.onBackPressed()
        }
    }
}