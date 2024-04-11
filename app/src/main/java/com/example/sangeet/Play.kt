package com.example.sangeet

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sangeet.databinding.ActivityPlayBinding
import java.io.File
import java.io.IOException
import java.util.Locale

class Play : AppCompatActivity() {
    private lateinit var binding: ActivityPlayBinding
//    Null safety
    private var mediaPlayer: MediaPlayer? = null
    private var currentSongIndex = 0
    private var songPath : String? = null
    private var songList : ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityPlayBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        songPath = intent.getStringExtra("songPath")
        val songTitle = intent.getStringExtra("songTitle")
        songList = intent.getStringArrayListExtra("songList")
        currentSongIndex = intent.getIntExtra("currentSongIndex",0)

        binding.sgName.text = songTitle

        startMarqueeAnimation(binding.sgName)
//        Initializing mediaPlayer
        mediaPlayer = MediaPlayer()

//        Setting data source and preparing mediaPlayer
//        using try and catch to confirm songPath is not empty

        try {
            songPath?.let {
                mediaPlayer?.setDataSource(it)
                mediaPlayer?.prepare()
            }
        }
        catch (e:IOException){
            e.printStackTrace()
        }
        binding.nxt.setOnClickListener {
                playNextSong()
        }
        binding.pre.setOnClickListener {
                playPreSong()
        }

        binding.seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                    binding.playerTime.text = formatTime(progress)
                    binding.seekBar?.progress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // No specific actions needed when the user starts tracking the touch
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Optional: Perform any actions when the user stops tracking the touch on the seek bar
            }
        })

        mediaPlayer?.setOnPreparedListener {
            binding.seekBar?.max = mediaPlayer?.duration?:0
            val totalDuration = mediaPlayer?.duration?:0
            binding.playerTimee?.text = formatTime(totalDuration)
            updateSeekBar()
        }

//        Start playing
        mediaPlayer?.start()

        binding.playpause.setOnClickListener {
            if (mediaPlayer?.isPlaying==true){
                mediaPlayer?.pause()
                binding.playpause.setImageResource(R.drawable.play)
            }
            else {
                mediaPlayer?.start()
                binding.playpause.setImageResource(R.drawable.pause)
            }
        }
        binding.tenSecForward.setOnClickListener {
            mediaPlayer?.seekTo(mediaPlayer?.currentPosition?.plus(10000) ?:0)
        }
        binding.tenSecBack.setOnClickListener {
            mediaPlayer?.seekTo(mediaPlayer?.currentPosition?.minus(10000) ?:0)
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    private fun startMarqueeAnimation(textView: TextView) {
        textView.isSelected = true // Enable marquee effect
    }
    private fun updateSeekBar() {
        mediaPlayer?.let {
            val currentPosition = it.currentPosition
            binding.seekBar?.progress = currentPosition
            binding.playerTime?.text = formatTime(currentPosition)
            Handler(Looper.getMainLooper()).postDelayed({
                updateSeekBar()
            }, 1000)
        }
    }

    private fun formatTime(ms: Int): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun playNextSong()
    {
        if (currentSongIndex<songList!!.size-1) {
            currentSongIndex++
        }
        else{
            currentSongIndex=0
        }
            val nextSongPath = songList!![currentSongIndex]
            val title = File(nextSongPath).nameWithoutExtension
            playNewSong(nextSongPath,title)


    }
    private fun playPreSong()
    {
        if (currentSongIndex>0) {
            currentSongIndex--
        }
        else {
            currentSongIndex=songList!!.size-1
        }
            val preSongPath = songList!![currentSongIndex]
            val titlePre = File(preSongPath).nameWithoutExtension
            playNewSong(preSongPath, titlePre)

    }

    fun playNewSong(songPath:String,songTitle:String)
    {
        mediaPlayer?.apply {
            stop()
            reset()
            try {
                setDataSource(songPath)
                binding.sgName.text = songTitle
                prepareAsync()
                setOnPreparedListener {
                    binding.seekBar?.max = duration
                    val totalDuration = duration
                    binding.playerTimee.text = formatTime(totalDuration)
                    start()
                }
            }
            catch (e:IOException)
            {
                e.printStackTrace()
            }
        }
    }

}