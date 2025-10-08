package dev.mforcen.marshgallery

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    private val handlerStartGallery = Handler(Looper.myLooper()!!)
    var timer = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener {
            Log.d("marshGallery", "Starting settings activity")
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.galleryButton).setOnClickListener {
            val intent = Intent(this, PhotoActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.exitAppButton).setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        timer = 0
        startTimer()
    }

    override fun onStop() {
        super.onStop()
        handlerStartGallery.removeCallbacksAndMessages(null)
    }

    fun startTimer() {
        handlerStartGallery.postDelayed(Runnable {
            findViewById<TextView>(R.id.autostartTextView).text = getString(R.string.autostart_text).format(10-timer)
            timer += 1
            if(timer > 10) {
                val intent = Intent(this, PhotoActivity::class.java)
                startActivity(intent)
            } else {
                startTimer()
            }
        }, 1000)
    }
}