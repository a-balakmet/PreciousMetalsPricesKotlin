package com.metals.precious.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.metals.precious.R


class StartScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.SplashTheme)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}