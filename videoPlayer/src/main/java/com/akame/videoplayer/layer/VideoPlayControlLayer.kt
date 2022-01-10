package com.akame.videoplayer.layer

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.akame.videoplayer.*
import com.akame.videoplayer.core.IVideoPlayerControl
import com.akame.videoplayer.MMSS
import com.akame.videoplayer.VideoPlayView
import com.akame.videoplayer.databinding.AkLayoutVideoPlayControlBinding
import com.akame.videoplayer.utils.AutoRotationScreenManager
import com.akame.videoplayer.utils.ScreenUtils
import com.akame.videoplayer.utils.VideoPlayStatus
import java.lang.Runnable

class VideoPlayControlLayer(
    private val mContext: Context,
    private val videoPlayView: VideoPlayView,
    private val videoPlay: IVideoPlayerControl
) : FrameLayout(mContext) {
    private lateinit var controlBinding: AkLayoutVideoPlayControlBinding
    private val goneRunnable = Runnable { visibility = View.GONE }
    private var originalWidth = 0
    private var originalHeight = 0
    private var videoTitle: String = ""
    private var isLanderVideo = false //是否是竖的视频
    private val autoRotationManager by lazy {
        AutoRotationScreenManager(context)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setAutoRotationListener()
        injectView()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        removeView(controlBinding.root)
        injectView()
    }

    private fun injectView() {
        val view = LayoutInflater
            .from(context)
            .inflate(R.layout.ak_layout_video_play_control, null)
        controlBinding = AkLayoutVideoPlayControlBinding.bind(view)
        addView(controlBinding.root)
        initListener()
        initData()
    }

    fun showDelayGone() {
        showDelayGone(goneRunnable)
    }

    fun changeVisibility() {
        changeVisibility(goneRunnable)
    }

    fun setUp(videoTitle: String) {
        this.videoTitle = videoTitle
    }

    fun onIsPlayingChanged(isPlaying: Boolean) {
        val playStatusImageRes = if (isPlaying) R.mipmap.ak_ic_pause else R.mipmap.ak_ic_play
        controlBinding.ivPlayStatus.setImageResource(playStatusImageRes)
    }

    fun onPlaybackStateChanged(videoPlayStatus: VideoPlayStatus) {
        when (videoPlayStatus) {
            VideoPlayStatus.STATE_BUFFERING -> {

            }
            VideoPlayStatus.STATE_READY -> {
                showDelayGone()
                controlBinding.tvEndTime.text = videoPlay.getDuration().MMSS
            }

            VideoPlayStatus.STATE_IDLE -> {

            }

            VideoPlayStatus.STATE_ENDED -> {
                visibility = View.GONE
            }
        }
    }

    fun onPlayingCurrentDuration(currentDuration: Long, bufferDuration: Long) {
        controlBinding.tvStartTime.text = currentDuration.MMSS
        controlBinding.seekBar.updateProgress(
            (currentDuration * 1f / videoPlay.getDuration() * 100).toInt(),
            (bufferDuration * 1f / videoPlay.getDuration() * 100).toInt()
        )
    }

    fun onVideoSizeChanged(videoWidth: Int, videoHeight: Int) {
        isLanderVideo = videoWidth >= videoHeight
    }

    fun release() {
        autoRotationManager.release()
    }

    fun onBackPressed(): Boolean {
        val co = context
        if (isEnterFullScreen() && co is Activity) {
            exitFullScreen(co, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            return false
        }
        return true
    }

    fun onPlayerError() {
        visibility = View.GONE
    }

    private fun initData() {
        controlBinding.seekBar.max = 100
        originalWidth = videoPlayView.width
        originalHeight = videoPlayView.height
        controlBinding.tvTitle?.text = videoTitle
        onIsPlayingChanged(videoPlay.isPlaying())
        onPlayingCurrentDuration(videoPlay.getCurrentDuration(), videoPlay.getBufferDuration())
        controlBinding.tvEndTime.text = videoPlay.getDuration().MMSS
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
            onPlayingCurrentDuration(seekToProcess, videoPlay.getBufferDuration())
        }
        //全屏按钮进入全屏
        controlBinding.ivFullScreen?.setOnClickListener {
            val context = context
            if (context is Activity) {
                if (isEnterFullScreen()) {
                    exitFullScreen(context, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                } else {
                    enterFullScreen(context, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                }
            }
        }
        //返回按钮退出全屏
        controlBinding.ivBack?.setOnClickListener {
            val context = context
            if (context is Activity) {
                exitFullScreen(context, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            }
        }
    }

    /**
     * 进入全屏
     */
    private fun enterFullScreen(
        activity: Activity,
        orientation: Int,
        isAutoFullScreen: Boolean = false
    ) {
        if ((isAutoFullScreen && !videoPlay.isPlaying())) {
            return
        }
        ScreenUtils.setFullLandscape(activity, isLanderVideo, orientation)
        val layoutParams = videoPlayView.layoutParams
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        controlBinding.space?.visibility = View.VISIBLE
        requestLayout()
    }

    /**
     * 退出全屏
     */
    private fun exitFullScreen(activity: Activity, orientation: Int) {
        ScreenUtils.setOrientationPortrait(activity, isLanderVideo, orientation)
        val layoutParams = videoPlayView.layoutParams
        layoutParams.width = originalWidth
        layoutParams.height = originalHeight
        controlBinding.space?.visibility = View.GONE
        requestLayout()
    }

    /**
     * 设置自动旋转监听
     */
    private fun setAutoRotationListener() {
        autoRotationManager.enterFullScreen = {
            val orientation = if (it) {
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            val context = context
            if (context is Activity) {
                enterFullScreen(context, orientation, true)
            }
        }
    }

    private fun isEnterFullScreen(): Boolean {
        val layoutParams = videoPlayView.layoutParams
        return layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT
                && layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT
    }
}