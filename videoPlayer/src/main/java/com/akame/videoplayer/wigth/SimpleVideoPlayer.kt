package com.akame.videoplayer.wigth

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import com.akame.videoplayer.utils.MediaType
import kotlinx.coroutines.CoroutineScope

class SimpleVideoPlayer(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {
    private val videoView by lazy {
        VideoPlayView(context, attributeSet)
    }

    init {
        addView(videoView)
    }

    fun bindLifecycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(videoView)
    }

    fun onBackPressed(): Boolean {
        return videoView.onBackPressed()
    }

    fun setup(
        externalScope: CoroutineScope,
        mediaType: MediaType,
        videoTitle: String,
        albumPath: String? = null,
        isAutoPlay: Boolean = true
    ) {
        videoView.setup(externalScope, mediaType, videoTitle, albumPath, isAutoPlay)
    }
}