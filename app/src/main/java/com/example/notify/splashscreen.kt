package com.example.notify

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class splashscreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        // Delay for 3 seconds and then navigate to MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            // Intent to navigate to MainActivity
            val intent = Intent(this@splashscreen, login::class.java)
            startActivity(intent)
            finish() // Close the SplashActivity so it's removed from the back stack
        }, 3000) // 3000 milliseconds = 3 seconds
    }
}