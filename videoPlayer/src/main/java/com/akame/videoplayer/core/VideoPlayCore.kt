package com.akame.videoplayer.core

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.SurfaceView
import android.view.TextureView
import android.view.ViewGroup
import android.widget.FrameLayout
import com.akame.videoplayer.utils.MediaType
import kotlinx.coroutines.CoroutineScope

abstract class VideoPlayCore(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {
    private val textureView by lazy {
        TextureView(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ).apply {
                gravity = Gravity.CENTER
            }
        }
    }

    val videoPlay: IVideoPlayerControl by lazy {
        ExoVideoPlayerControl(context, textureView)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        textureView.parent?.let {
            if (it is ViewGroup) {
                it.removeView(textureView)
            }
        }
        addView(textureView)
    }

    open fun setup(externalScope: CoroutineScope, mediaType: MediaType, isAutoPlay: Boolean) {
        videoPlay.setUp(externalScope, mediaType, isAutoPlay)
    }
}