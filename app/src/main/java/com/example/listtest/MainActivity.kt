package com.example.listtest


import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.listtest.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        blinkText(binding.taptext, 1000, 500)

        supportActionBar?.hide()
    }

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                val intent = Intent(this, SecondActivity::class.java)
                startActivity(intent)

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
            MotionEvent.ACTION_UP -> {}
            MotionEvent.ACTION_MOVE -> {}
            MotionEvent.ACTION_CANCEL -> {}
        }
        return false
    }

    private fun blinkText(txtView: TextView, duration: Long, offset: Long) {
        val anm: Animation = AlphaAnimation(0.0f, 1.0f)
        anm.duration = duration
        anm.startOffset = offset
        anm.repeatMode = Animation.REVERSE
        anm.repeatCount = Animation.INFINITE
        txtView.startAnimation(anm)
    }
}