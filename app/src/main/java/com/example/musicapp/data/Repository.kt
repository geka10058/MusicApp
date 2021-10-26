package com.example.musicapp.data

class Repository(private val trackList: TrackList) {
    fun getTrackList() = trackList.getTrackList()
}