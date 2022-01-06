package com.akame.videoplayer

import android.view.View
import com.akame.videoplayer.databinding.LayoutVideoControlBinding
import java.text.SimpleDateFormat
import java.util.*

fun LayoutVideoControlBinding.changeVisibility(goneRunnable: Runnable) {
    if (this.root.visibility == View.VISIBLE) {
        this.root.visibility = View.GONE
    } else {
        showDelayGone(goneRunnable)
        this.root.visibility = View.VISIBLE
    }
}

fun LayoutVideoControlBinding.showDelayGone(goneRunnable: Runnable, delayTime: Long = 3000) {
    this.root.removeCallbacks(goneRunnable)
    this.root.postDelayed(goneRunnable, delayTime)
}

val Long.MMSS get() = SimpleDateFormat("mm:ss", Locale.getDefault()).format(this)
