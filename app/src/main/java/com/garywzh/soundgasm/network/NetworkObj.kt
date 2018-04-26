package com.garywzh.soundgasm.network

import android.util.Log
import com.garywzh.soundgasm.model.Sound
import com.garywzh.soundgasm.parser.Parser
import com.garywzh.soundgasm.parser.SoundListParser
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Created by garywzh on 2018/4/22.
 */
object NetworkObj {
    private const val SOUND_LIST_PREFIX: String = "https://soundgasm.net/u/"

    private val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor
            .Logger { message -> Log.d("Http info: ", message) })
            .setLevel(HttpLoggingInterceptor.Level.BASIC)

    private val client = OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()

    fun getSoundList(artist: String): List<Sound>? {
        val request = Request.Builder().url(SOUND_LIST_PREFIX + artist).build()
        val response = client.newCall(request).execute()
        var soundList: List<Sound>? = null
        response.body()?.let {
            val html = it.string()
            val doc = Parser.toDoc(html)
            soundList = SoundListParser.parseDocForSoundList(doc)
        }
        return soundList
    }
}