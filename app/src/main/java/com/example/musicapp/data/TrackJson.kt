package com.example.musicapp.data

import android.content.Context
import com.example.musicapp.R
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class TrackJson(private val context: Context): TrackList {
    override fun getTrackList(): List<Track>? {
        val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val type = Types.newParameterizedType(List::class.java, Track::class.java)
        val adapter:JsonAdapter<List<Track>> = moshi.adapter(type)
        val jsonList = context.resources.openRawResource(R.raw.songs).bufferedReader().use {
            it.readText()
        }
        return adapter.fromJson(jsonList)
    }
}