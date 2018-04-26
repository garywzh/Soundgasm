package com.garywzh.soundgasm.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.garywzh.soundgasm.R
import com.garywzh.soundgasm.model.SoundRepo
import kotlinx.android.synthetic.main.activity_artist_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

class ArtistListActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    private var artists: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_list)
        title = "All Artists"
        loadData()
    }

    private fun loadData() {
        SoundRepo.sounds?.let {
            doAsync {
                artists = it.distinctBy { it.artist }.map { it.artist }
                uiThread {
                    listView.adapter = ArrayAdapter<String>(this@ArtistListActivity, android.R.layout.simple_list_item_1, artists)
                    listView.onItemClickListener = this@ArtistListActivity
                }
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        artists?.get(position)?.let {
            startActivity<ArtistActivity>(
                    "artist" to it
            )
        }
    }
}
