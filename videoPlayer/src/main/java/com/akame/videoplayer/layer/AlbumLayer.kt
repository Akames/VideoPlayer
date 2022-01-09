package com.akame.videoplayer.layer

import android.content.Context
import android.media.MediaMetadataRetriever
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.akame.videoplayer.utils.MediaType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 封面层
 */
class AlbumLayer {
    private lateinit var albumImageView: ImageView
    private val frameAtTime = 3 * 1000 * 1000L //5微秒

    fun injectView(context: Context): ImageView {
        albumImageView = ImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        return albumImageView
    }

    fun setAlbum(externalScope: CoroutineScope, mediaType: MediaType) {
        externalScope.launch(Dispatchers.Main) {
            val bitmap = withContext(Dispatchers.Default) {
                val retriever = MediaMetadataRetriever()
                when (mediaType) {
                    is MediaType.StringType -> retriever.setDataSource(mediaType.mediaPath)
                    is MediaType.UriType -> retriever.setDataSource(
                        albumImageView.context,
                        mediaType.mediaPath
                    )
                }
                retriever.getFrameAtTime(frameAtTime)
            }
            albumImageView.setImageBitmap(bitmap)
        }
    }

    fun onIsPlayingChanged(isPlaying: Boolean) {
        if (isPlaying) {
            albumImageView.visibility = View.GONE
        }
    }
}