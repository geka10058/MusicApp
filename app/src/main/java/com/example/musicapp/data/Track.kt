package com.example.musicapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Track (
    val title: String,
    val artist: String,
    val bitmapUri: String,
    val trackUri: String,
    val duration: Int
    ):Parcelable{

}