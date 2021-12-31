package com.akame.videoplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import com.akame.videoplayer.databinding.ActivityMainBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var player: ExoPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPlayer()
        initListener()
    }

    private fun initListener() {
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {
                    Player.STATE_IDLE -> {
                        Log.e("tag", "初始状态...")
                    }

                    Player.STATE_BUFFERING -> {
                        Log.e("tag", "加载中...")
                    }

                    Player.STATE_READY -> {
                        Log.e("tag", "准备播放...")
                    }

                    Player.STATE_ENDED -> {
                        Log.e("tag", "播放结束...")
                        player.seekTo(0)
                    }
                }
            }
        })
        binding.bStart.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var progress = 0
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                this.progress = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                progress = 0
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (progress > 0) {
                    player.seekTo((player.duration * (progress / 100f)).toLong())
                }
            }
        })
    }

    private fun initPlayer() {
        player = ExoPlayer.Builder(this).build().apply {
            val mediaItem = MediaItem.fromUri("https://media.w3.org/2010/05/sintel/trailer.mp4")
            setMediaItem(mediaItem)
            prepare()
            play()
        }
        player.setVideoSurfaceView(binding.videoPlay)
    }

}