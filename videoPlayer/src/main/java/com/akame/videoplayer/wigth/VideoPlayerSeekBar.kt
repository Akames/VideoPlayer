package com.akame.videoplayer.wigth

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar

class VideoPlayerSeekBar(context: Context, attributeSet: AttributeSet) : AppCompatSeekBar(context, attributeSet), SeekBar.OnSeekBarChangeListener {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setOnSeekBarChangeListener(this)
    }

    private var isTrackingTouch = false

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        isTrackingTouch = true
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        isTrackingTouch = false
        onSeekPosition.invoke(progress * 1f / max)
    }

    var onSeekPosition: (progress: Float) -> Unit = {}

    fun updateProgress(newProcess: Int, bufferProcess: Int) {
        if (!isTrackingTouch) {
            progress = newProcess
            secondaryProgress = bufferProcess
        }
    }
}