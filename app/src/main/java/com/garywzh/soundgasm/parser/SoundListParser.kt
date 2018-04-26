package com.garywzh.soundgasm.parser

import com.garywzh.soundgasm.model.Sound
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * Created by garywzh on 2018/3/4.
 */
object SoundListParser {

    fun parseDocForSoundList(doc: Document): List<Sound> {
        val list = doc.select(".sound-details")
        val result = ArrayList<Sound>(list.size)
        for (ele in list) {
            parseDivForSound(ele)?.let {
                result.add(it)
            }
        }
        return result
    }

    private fun parseDivForSound(e: Element): Sound? {
        val a = e.child(0)
        val title = a.text()
        val url = a.attr("href")
        val id = url.hashCode()
        val artist = url.split("/")[4]
        val description = e.child(2).text()
        val playCount = e.select(".playCount")[0].text().replace("Play Count:", "").trim().toInt()
        return Sound(id, artist, title, url, description, playCount)
    }
}