package com.akame.videoplayer.core

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.FrameLayout
import com.akame.videoplayer.ExoPlayer
import com.akame.videoplayer.IVideoPlayerControl
import com.akame.videoplayer.MediaType
import kotlinx.coroutines.CoroutineScope

open class VideoPlayCore(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {
    lateinit var videoPlay: IVideoPlayerControl
    private val surfaceView by lazy {
        SurfaceView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        surfaceView.parent?.let {
            if (it is ViewGroup) {
                it.removeView(surfaceView)
            }
        }
        addView(surfaceView)
    }

   open fun setup(externalScope: CoroutineScope, mediaType: MediaType) {
        videoPlay = ExoPlayer(context, externalScope, mediaType, surfaceView)
    }
}