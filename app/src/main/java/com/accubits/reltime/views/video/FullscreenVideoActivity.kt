package com.accubits.reltime.views.video

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.accubits.reltime.R
import com.accubits.reltime.activity.v2.home.HomeV2Fragment
import com.accubits.reltime.databinding.ActivityFullscreenVideoBinding

class FullscreenVideoActivity : AppCompatActivity(), MediaPlayer.OnCompletionListener {

    private lateinit var binding: ActivityFullscreenVideoBinding
    private val TAG = "MainActivity"

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullscreenVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
                binding.apply {
                    myvideoview.setOnCompletionListener(this@FullscreenVideoActivity);
                    myvideoview.setVideoURI(Uri.parse("android.resource://" + packageName.toString() + "/" +
                            if(intent.getIntExtra(HomeV2Fragment.VIDEO_TYPE,1)==1)R.raw.nagra else
                                R.raw.nagra))
                    val mediaController = MediaController(this@FullscreenVideoActivity)
                    myvideoview.setMediaController(mediaController);
                    myvideoview.start();
                }


    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
    override fun onCompletion(mp: MediaPlayer?) {
        finish();
    }


}