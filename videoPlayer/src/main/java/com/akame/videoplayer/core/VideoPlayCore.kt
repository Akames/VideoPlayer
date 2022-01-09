package com.akame.videoplayer.core

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.FrameLayout
import com.akame.videoplayer.utils.MediaType
import kotlinx.coroutines.CoroutineScope

abstract class VideoPlayCore(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {
    private val surfaceView by lazy {
        SurfaceView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }
    val videoPlay: IVideoPlayerControl by lazy {
        ExoVideoPlayerControl(context, surfaceView)
    }
    var isAutoPlay = true

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
        videoPlay.setUp(externalScope, mediaType, isAutoPlay)
    }
}