package com.akame.videoplayer.layer

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.akame.videoplayer.R
import com.akame.videoplayer.core.IVideoPlayerControl
import com.akame.videoplayer.databinding.AkLayoutPlayCompleteBinding
import com.akame.videoplayer.utils.VideoPlayStatus

/**
 * 播放完成层
 */
class PlayCompleteLayer(context: Context, private val videoPlay: IVideoPlayerControl) : FrameLayout(context) {
    private lateinit var binding: AkLayoutPlayCompleteBinding
    private var videoTitle: String = ""

    init {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        initCompleteView()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val boo = super.dispatchTouchEvent(ev)
        if (!boo) {
            return true
        }
        return boo
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        removeView(binding.root)
        initCompleteView()
    }

    fun onPlaybackStateChanged(videoPlayStatus: VideoPlayStatus) {
        visibility = when (videoPlayStatus) {
            VideoPlayStatus.STATE_ENDED -> {
                View.VISIBLE
            }

            else -> {
                View.GONE
            }
        }
    }

    fun setUp(title: String) {
        videoTitle = title
    }

    var onBackClickListener: (() -> Unit)? = null

    private fun initCompleteView() {
        val completeView = LayoutInflater.from(context).inflate(R.layout.ak_layout_play_complete, null)
        completeView.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        binding = AkLayoutPlayCompleteBinding.bind(completeView)
        addView(binding.root)
        binding.tvReplay.setOnClickListener {
            videoPlay.seekTo(0)
        }
        binding.tvTitle?.text = videoTitle
        binding.ivBack?.setOnClickListener {
            onBackClickListener?.invoke()
        }
        visibility = if (videoPlay.getCurrentPlayStatus() == VideoPlayStatus.STATE_ENDED) View.VISIBLE else View.GONE
    }
}