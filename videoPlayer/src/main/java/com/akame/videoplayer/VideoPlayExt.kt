package com.akame.videoplayer

import android.view.View
import android.view.ViewGroup
import com.akame.videoplayer.layer.VideoPlayControlLayer
import java.text.SimpleDateFormat
import java.util.*

fun VideoPlayControlLayer.changeVisibility(goneRunnable: Runnable) {
    if (this.visibility == View.VISIBLE) {
        this.visibility = View.GONE
    } else {
        showDelayGone(goneRunnable)
        this.visibility = View.VISIBLE
    }
}

fun VideoPlayControlLayer.showDelayGone(goneRunnable: Runnable, delayTime: Long = 3000) {
    this.removeCallbacks(goneRunnable)
    this.postDelayed(goneRunnable, delayTime)
}

internal val Long.MMSS get() = SimpleDateFormat("mm:ss", Locale.getDefault()).format(this)

internal fun View.cleanParent():View{
    this.parent?.let {
        if (it is ViewGroup) {
            it.removeView(this)
        }
    }
    return this
}
