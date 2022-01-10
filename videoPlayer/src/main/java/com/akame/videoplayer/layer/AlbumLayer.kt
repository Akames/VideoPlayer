package com.akame.videoplayer.layer

import android.content.Context
import android.media.MediaMetadataRetriever
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.akame.videoplayer.utils.MediaType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
/**
 * 封面层
 */
class AlbumLayer(context: Context) : FrameLayout(context) {
    private var albumImageView: ImageView = ImageView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addView(albumImageView)
    }

    fun setUp(albumPath: String? = null) {

    }

    fun onIsPlayingChanged(isPlaying: Boolean) {
        if (isPlaying) {
            albumImageView.visibility = View.GONE
        }
    }
}