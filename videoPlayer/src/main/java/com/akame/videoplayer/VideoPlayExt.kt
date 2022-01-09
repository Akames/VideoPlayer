package com.akame.videoplayer

import android.view.View
import com.akame.videoplayer.databinding.AkLayoutVideoPlayControlBinding
import java.text.SimpleDateFormat
import java.util.*

fun AkLayoutVideoPlayControlBinding.changeVisibility(goneRunnable: Runnable) {
    if (this.root.visibility == View.VISIBLE) {
        this.root.visibility = View.GONE
    } else {
        showDelayGone(goneRunnable)
        this.root.visibility = View.VISIBLE
    }
}

fun AkLayoutVideoPlayControlBinding.showDelayGone(goneRunnable: Runnable, delayTime: Long = 3000) {
    this.root.removeCallbacks(goneRunnable)
    this.root.postDelayed(goneRunnable, delayTime)
}

internal val Long.MMSS get() = SimpleDateFormat("mm:ss", Locale.getDefault()).format(this)
