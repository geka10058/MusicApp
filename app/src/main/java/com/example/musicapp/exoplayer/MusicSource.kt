package com.example.musicapp.exoplayer

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import com.example.musicapp.data.TracksRepository
import com.example.musicapp.exoplayer.State.STATE_CREATED
import com.example.musicapp.exoplayer.State.STATE_ERROR
import com.example.musicapp.exoplayer.State.STATE_INITIALIZED
import com.example.musicapp.exoplayer.State.STATE_INITIALIZING
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicSource @Inject constructor(
    private val tracksRepository: TracksRepository
) {

    var songs = emptyList<MediaMetadataCompat>()

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var state: State = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALIZED || value == STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach{ listener ->
                        listener(state == STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }

        }

    suspend fun fetchMediaData() = withContext(Dispatchers.IO) {
        state = STATE_INITIALIZING
        val allSongs = tracksRepository.catalog
        songs = allSongs.map { song ->
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.id.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, song.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, song.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, song.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, song.trackUri)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, song.bitmapUri)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, song.bitmapUri)
                //.putString(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration.toString())
                .build()
        }
        state = STATE_INITIALIZED
    }

    fun asMediaSource(dataSourceFactory: DefaultDataSourceFactory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach { song ->
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(
                    MediaItem.Builder()
                        .setUri(song.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI).toUri())
                        .build()
                )
            /*.createMediaSource(
                    song.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI).toUri()
                )*/
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItems() = songs.map { song ->
        val desc = MediaDescriptionCompat.Builder()
            .setMediaUri(song.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI).toUri())
            .setTitle(song.description.title)
            .setSubtitle(song.description.subtitle)
            .setMediaId(song.description.mediaId)
            .setIconUri(song.description.iconUri)
            .build()
        MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }.toMutableList()

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        return if(state == STATE_CREATED || state == STATE_INITIALIZING) {
            onReadyListeners += action
            false
        } else {
            action(state == STATE_INITIALIZED)
            true
        }
    }

}

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}