package com.akame.videoplayer

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.*
import androidx.appcompat.app.AlertDialog
import com.akame.videoplayer.core.VideoPlayCore
import com.akame.videoplayer.core.VideoPlayListener
import com.akame.videoplayer.databinding.LayoutVideoPlayControlBinding
import kotlinx.coroutines.CoroutineScope

class VideoPlayView(context: Context, attributeSet: AttributeSet) : VideoPlayCore(context, attributeSet), VideoPlayListener {
    private lateinit var controlBinding: LayoutVideoPlayControlBinding
    private val goneRunnable = Runnable { controlBinding.root.visibility = GONE }
    private var originalWidth = 0
    private var originalHeight = 0
    private var videoTitle: String = ""
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initView()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        controlBinding.showDelayGone(goneRunnable)
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        controlBinding.changeVisibility(goneRunnable)
        return super.onTouchEvent(event)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        val playStatusImageRes = if (isPlaying) R.mipmap.ic_pause else R.mipmap.ic_play
        controlBinding.ivPlayStatus.setImageResource(playStatusImageRes)
    }

    override fun onPlayError(error: String) {

    }

    override fun onPlaybackStateChanged(videoPlayStatus: VideoPlayStatus) {
        when (videoPlayStatus) {
            VideoPlayStatus.STATE_BUFFERING -> {
                controlBinding.loadingBar.visibility = View.VISIBLE
            }

            VideoPlayStatus.STATE_IDLE -> {

            }

            VideoPlayStatus.STATE_READY -> {
                controlBinding.tvEndTime.text = videoPlay.getDuration().MMSS
                controlBinding.loadingBar.visibility = View.GONE
            }

            VideoPlayStatus.STATE_ENDED -> {

            }
        }
    }

    override fun onPlayingCurrentDuration(currentDuration: Long) {
        controlBinding.tvStartTime.text = currentDuration.MMSS
        controlBinding.seekBar.updateProgress((currentDuration * 1f / videoPlay.getDuration() * 100).toInt())
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        removeView(controlBinding.root)
        initView()
    }

    private fun initView() {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_video_play_control, null)
        controlBinding = LayoutVideoPlayControlBinding.bind(view)
        addView(controlBinding.root)
        controlBinding.changeVisibility(goneRunnable)
        initListener()
        initData()
    }

    private fun initListener() {
        //播放按钮监听
        controlBinding.ivPlayStatus.setOnClickListener {
            if (videoPlay.isPlaying()) {
                videoPlay.pause()
            } else {
                videoPlay.play()
            }
        }
        //进度条拖动
        controlBinding.seekBar.onSeekPosition = {
            val seekToProcess = (videoPlay.getDuration() * it).toLong()
            videoPlay.seekTo(seekToProcess)
            onPlayingCurrentDuration(seekToProcess)
        }
        //全屏按钮进入全屏
        controlBinding.ivFullScreen?.setOnClickListener {
            if (context is Activity) {
                ScreenUtils.setFullLandscape(context as Activity)
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }
        //返回按钮退出全屏
        controlBinding.ivBack?.setOnClickListener {
            if (context is Activity) {
                ScreenUtils.setOrientationUnspecified(context as Activity)
                layoutParams.width = originalWidth
                layoutParams.height = originalHeight
            }
        }
    }

    private fun initData() {
        controlBinding.seekBar.max = 100
        originalWidth = width
        originalHeight = height
        videoPlay.setPlayListener(this)
        controlBinding.tvTitle?.text = videoTitle
        onIsPlayingChanged(videoPlay.isPlaying())
        onPlayingCurrentDuration(videoPlay.getCurrentDuration())
        controlBinding.tvEndTime.text = videoPlay.getDuration().MMSS
    }

    fun setup(externalScope: CoroutineScope, mediaType: MediaType, videoTitle: String) {
        super.setup(externalScope, mediaType)
        this.videoTitle = videoTitle
    }
}