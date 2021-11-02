package com.example.musicapp.exoplayer

import android.support.v4.media.MediaMetadataCompat
import com.example.musicapp.data.Track
import java.text.SimpleDateFormat
import java.util.*

fun MediaMetadataCompat.toTrack(): Track? {
    return description?.let {
        Track(
            it.mediaId?.toInt() ?: 0,
            it.title.toString(),
            it.subtitle.toString(),
            it.mediaId.toString(),
            it.iconBitmap.toString()
        )
    }
}

fun Long.toTimeFormat():String{
    val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
    return timeFormat.format(this)
}