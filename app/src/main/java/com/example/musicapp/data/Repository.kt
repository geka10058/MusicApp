package com.example.musicapp.data

import android.content.res.AssetManager
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Provides
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Inject


class Repository@Inject constructor(
    private val assetManager: AssetManager
) {

    private var _catalog: List<Track>? = null
    val catalog: List<Track> get() = requireNotNull(_catalog)

    init {
        initPlaylistFromJson()
    }

    private fun initPlaylistFromJson() {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val arrayType = Types.newParameterizedType(List::class.java, Track::class.java)
        val adapter: JsonAdapter<List<Track>> = moshi.adapter(arrayType)
        val myJson = assetManager.open(FILE_NAME).bufferedReader().use { it.readText() }

        _catalog = adapter.fromJson(myJson)
    }

    companion object {
        const val FILE_NAME = "Tracks.json"
    }
}
/*
class Repository(private val trackList: TrackList) {
    fun getTrackList() = trackList.getTrackList()
}*/
