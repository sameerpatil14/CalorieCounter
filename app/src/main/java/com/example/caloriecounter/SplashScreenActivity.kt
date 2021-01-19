package com.example.caloriecounter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val imageView = findViewById<ImageView>(R.id.imageView)

        var animZoomIn = AnimationUtils.loadAnimation(
            getApplicationContext(),
            R.anim.zoom_in
        );

        imageView.startAnimation(animZoomIn)

        Handler().postDelayed({
            val intent = Intent(this, SignInSignUpActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)

    }
}