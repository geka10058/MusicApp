package com.example.musicapp.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Track (
    @field:Json(name = "title") val title: String,
    @field:Json(name = "artist") val artist: String,
    @field:Json(name = "bitmapUri") val bitmapUri: String,
    @field:Json(name = "trackUri") val trackUri: String,
    //@field:Json(name = "duration") val duration: Int
)