package com.akame.videoplayer

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.AttributeSet
import android.view.*
import com.akame.videoplayer.core.VideoPlayCore
import com.akame.videoplayer.core.VideoPlayListener
import com.akame.videoplayer.databinding.LayoutVideoControlBinding


class VideoPlayView(context: Context, attributeSet: AttributeSet) : VideoPlayCore(context, attributeSet), VideoPlayListener {
    private lateinit var controlBinding: LayoutVideoControlBinding
    private val goneRunnable = Runnable { controlBinding.root.visibility = GONE }
    private var originalWidth = 0
    private var originalHeight = 0
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val view = LayoutInflater.from(context).inflate(R.layout.layout_video_control, null)
        controlBinding = LayoutVideoControlBinding.bind(view)
        addView(controlBinding.root)
        controlBinding.changeVisibility(goneRunnable)
        videoPlay.setPlayListener(this)
        initListener()
        initData()
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

    private fun initListener() {
        controlBinding.ivPlayStatus.setOnClickListener {
            if (videoPlay.isPlaying()) {
                videoPlay.pause()
            } else {
                videoPlay.play()
            }
        }
        controlBinding.seekBar.onSeekPosition = {
            videoPlay.seekTo((videoPlay.getDuration() * it).toLong())
        }

        controlBinding.ivFullScreen.setOnClickListener {
            if (context is Activity) {
                if ((context as Activity).resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    (context as Activity).window.setFlags(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN
                    )
                    this.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                } else {
                    (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    (context as Activity).window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    this.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                    layoutParams.width = originalWidth
                    layoutParams.height = originalHeight
                }
            }
        }
    }

    private fun initData() {
        controlBinding.seekBar.max = 100
        originalWidth = width
        originalHeight = height
    }
}