package com.garywzh.soundgasm.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.garywzh.soundgasm.R
import com.garywzh.soundgasm.model.Sound
import org.jetbrains.anko.browse

class SoundListFragment : Fragment() {
    private lateinit var adapter: SoundLIstAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sound_list, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = SoundLIstAdapter {
            val sound = it?.tag as Sound
            val url = sound.url
            activity?.browse(url)
//            CustomTabsIntent.Builder().build().launchUrl(activity, Uri.parse(url))
        }
        recyclerView.adapter = adapter
        return view
    }

    fun setData(list: List<Sound>) {
        adapter.data = list
        adapter.notifyDataSetChanged()
        recyclerView.smoothScrollToPosition(0)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SoundListFragment()
    }
}
