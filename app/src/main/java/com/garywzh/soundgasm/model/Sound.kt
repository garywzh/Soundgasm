package com.garywzh.soundgasm.model

data class Sound(
        val id: Int,
        val artist: String,
        val title: String,
        val url: String,
        val description: String,
        val playCount: Int
)