package com.example.musicapp.data

import dagger.Provides

interface TrackList {
    fun getTrackList(): List<Track>?
}