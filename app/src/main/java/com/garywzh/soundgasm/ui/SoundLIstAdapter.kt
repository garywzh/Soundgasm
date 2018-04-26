package com.garywzh.soundgasm.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.garywzh.soundgasm.R
import com.garywzh.soundgasm.model.Sound
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.sound_list_item.*

/**
 * Created by garywzh on 2018/3/4.
 */
class SoundLIstAdapter(private val listener: (v: View?) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: List<Sound>? = null

    override fun getItemCount(): Int = data?.size ?: 0

    override fun getItemViewType(position: Int): Int = TYPE_SOUND

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_SOUND) {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.sound_list_item, parent, false)
            view.setOnClickListener(listener)
            return SoundViewHolder(view)
        } else {
            throw RuntimeException("wrong view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        data?.get(position)?.let {
            (holder as? SoundViewHolder)?.fillData(position, it)
        }
    }

    override fun getItemId(position: Int): Long = data?.get(position)?.id?.toLong() ?: 1234567

    internal class SoundViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun fillData(i: Int, sound: Sound) {
            index.text = i.toString()
            title.text = sound.title
            artist.text = sound.artist
            playCount.text = "${sound.playCount} views"
            containerView.tag = sound
        }
    }

    companion object {
        const val TYPE_SOUND = 0x01
    }
}