package com.akame.videoplayer.layer

import android.app.Activity
import android.content.pm.ActivityInfo
import android.media.MediaMetadataRetriever
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.akame.videoplayer.*
import com.akame.videoplayer.core.IVideoPlayerControl
import com.akame.videoplayer.MMSS
import com.akame.videoplayer.VideoPlayView
import com.akame.videoplayer.databinding.AkLayoutVideoPlayControlBinding
import com.akame.videoplayer.utils.AutoRotationScreenManager
import com.akame.videoplayer.utils.MediaType
import com.akame.videoplayer.utils.ScreenUtils
import com.akame.videoplayer.utils.VideoPlayStatus
import kotlinx.coroutines.*
import java.lang.Runnable
import java.net.HttpURLConnection
import java.net.URLConnection
import javax.net.ssl.HttpsURLConnection

class VideoPlayControlLayer(
    private val videoPlayView: VideoPlayView,
    private val videoPlay: IVideoPlayerControl
) {
    private lateinit var controlBinding: AkLayoutVideoPlayControlBinding
    private val goneRunnable = Runnable { controlBinding.root.visibility = FrameLayout.GONE }
    private var originalWidth = 0
    private var originalHeight = 0
    private var videoTitle: String = ""
    private val autoRotationManager by lazy {
        AutoRotationScreenManager(videoPlayView.context)
    }

    init {
        setAutoRotationListener()
    }

    fun injectView(): View {
        val view = LayoutInflater
            .from(videoPlayView.context)
            .inflate(R.layout.ak_layout_video_play_control, null)
        controlBinding = AkLayoutVideoPlayControlBinding.bind(view)
        controlBinding.changeVisibility(goneRunnable)
        initListener()
        initData()
        return controlBinding.root
    }

    fun showDelayGone() {
        controlBinding.showDelayGone(goneRunnable)
    }

    fun changeVisibility() {
        controlBinding.changeVisibility(goneRunnable)
    }

    fun setVideoTitle(videoTitle: String) {
        this.videoTitle = videoTitle
    }

    fun onIsPlayingChanged(isPlaying: Boolean) {
        val playStatusImageRes = if (isPlaying) R.mipmap.ak_ic_pause else R.mipmap.ak_ic_play
        controlBinding.ivPlayStatus.setImageResource(playStatusImageRes)
    }

    fun onPlaybackStateChanged(videoPlayStatus: VideoPlayStatus) {
        when (videoPlayStatus) {
            VideoPlayStatus.STATE_BUFFERING -> {
                controlBinding.loadingBar.visibility = View.VISIBLE
            }
            VideoPlayStatus.STATE_READY -> {
                controlBinding.tvEndTime.text = videoPlay.getDuration().MMSS
                controlBinding.loadingBar.visibility = View.GONE
            }

            VideoPlayStatus.STATE_IDLE ->{

            }

            VideoPlayStatus.STATE_ENDED ->{

            }
        }
    }

    fun onPlayingCurrentDuration(currentDuration: Long) {
        controlBinding.tvStartTime.text = currentDuration.MMSS
        controlBinding.seekBar.updateProgress((currentDuration * 1f / videoPlay.getDuration() * 100).toInt())
    }

    fun release() {
        autoRotationManager.release()
    }

    fun onBackPressed(): Boolean {
        val orientation = videoPlayView.context.resources.configuration.orientation
        val co = videoPlayView.context
        if (orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && co is Activity) {
            outFullScreen(co, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            return false
        }
        return true
    }

    private fun initData() {
        controlBinding.seekBar.max = 100
        originalWidth = videoPlayView.width
        originalHeight = videoPlayView.height
        controlBinding.tvTitle?.text = videoTitle
        onIsPlayingChanged(videoPlay.isPlaying())
        onPlayingCurrentDuration(videoPlay.getCurrentDuration())
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
            onPlayingCurrentDuration(seekToProcess)
        }
        //全屏按钮进入全屏
        controlBinding.ivFullScreen?.setOnClickListener {
            val context = videoPlayView.context
            if (context is Activity) {
                enterFullScreen(context, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            }
        }
        //返回按钮退出全屏
        controlBinding.ivBack?.setOnClickListener {
            val context = videoPlayView.context
            if (context is Activity) {
                outFullScreen(context, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
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
        val cOrientation = videoPlayView.context.resources.configuration.orientation
        if (cOrientation == orientation || (isAutoFullScreen && !videoPlay.isPlaying())) {
            return
        }
        ScreenUtils.setFullLandscape(activity, orientation)
        val layoutParams = videoPlayView.layoutParams
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    /**
     * 退出全屏
     */
    private fun outFullScreen(activity: Activity, orientation: Int) {
        val cOrientation = videoPlayView.context.resources.configuration.orientation
        if (cOrientation == orientation) {
            return
        }
        ScreenUtils.setOrientationPortrait(activity, orientation)
        val layoutParams = videoPlayView.layoutParams
        layoutParams.width = originalWidth
        layoutParams.height = originalHeight
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
            val context = videoPlayView.context
            if (context is Activity) {
                enterFullScreen(context, orientation, true)
            }
        }
    }
}