package com.example.musicapp.data

import dagger.Provides
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Inject

class Repository(private val trackList: TrackList) {
    fun getTrackList() = trackList.getTrackList()
}