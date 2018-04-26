package com.garywzh.soundgasm.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.garywzh.soundgasm.R
import com.garywzh.soundgasm.model.SoundRepo
import kotlinx.android.synthetic.main.activity_artist.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ArtistActivity : AppCompatActivity() {
    private lateinit var artist: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist)
        artist = intent.getStringExtra("artist")
        title = artist
        loadData()
    }

    private fun loadData() {
        SoundRepo.sounds?.let {
            doAsync {
                val list = it.filter { it.artist == artist }
                uiThread {
                    (fragment as SoundListFragment).setData(list)
                }
            }
        }
    }
}
