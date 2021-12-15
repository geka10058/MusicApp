package com.example.musicapp.data

import android.content.res.AssetManager
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Inject


@ServiceScoped
class TracksRepository @Inject constructor(
    private val assetManager: AssetManager,
    private val moshi: Moshi,
) {

    private var _catalog: List<Track>? = null
    val catalog: List<Track>
        get() = requireNotNull(_catalog)

    init {
        initPlaylistFromJson()
    }

    private fun initPlaylistFromJson() {
        val myJson = assetManager.open(FILE_NAME).bufferedReader().use { it.readText() }
        val arrayType = Types.newParameterizedType(List::class.java, Track::class.java)
        val adapter: JsonAdapter<List<Track>> = moshi.adapter(arrayType)

        _catalog = adapter.fromJson(myJson)
    }

    private companion object {
        const val FILE_NAME = "Tracks.json"
    }
}

