package com.rybnickov.taxidrivers.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rybnickov.taxidrivers.R

class FragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
    }
}