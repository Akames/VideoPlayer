package com.akame.videoplayer.layer

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.akame.videoplayer.R
import com.akame.videoplayer.databinding.AkLayoutBufferLoadBinding
import com.akame.videoplayer.utils.VideoPlayStatus

/**
 * 缓冲加载层
 */
class BufferLoadLayer(context: Context) : FrameLayout(context) {
    private lateinit var binding: AkLayoutBufferLoadBinding

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return if (visibility == View.VISIBLE) {
            true
        } else {
            super.dispatchTouchEvent(ev)
        }
    }

    init {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val bufferLoadView = LayoutInflater.from(context).inflate(R.layout.ak_layout_buffer_load, null)
        binding = AkLayoutBufferLoadBinding.bind(bufferLoadView)
        binding.root.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        addView(binding.root)
    }

    fun onPlaybackStateChanged(videoPlayStatus: VideoPlayStatus) {
        when (videoPlayStatus) {
            VideoPlayStatus.STATE_BUFFERING -> {
                visibility = View.VISIBLE
            }
            VideoPlayStatus.STATE_READY -> {
                visibility = View.GONE
            }
            else -> {

            }
        }
    }

    fun onPlayerError() {
        visibility = View.VISIBLE
    }
}