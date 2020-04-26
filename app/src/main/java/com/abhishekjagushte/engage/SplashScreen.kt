package com.abhishekjagushte.engage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.abhishekjagushte.engage.ui.setup.SetupActivity

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val intent = Intent(this, SetupActivity::class.java)
        startActivity(intent)
        finish()
    }
}
