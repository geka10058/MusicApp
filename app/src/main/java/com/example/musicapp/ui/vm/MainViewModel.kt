package com.example.musicapp.ui.vm

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicapp.data.Track
import com.example.musicapp.exoplayer.MusicServiceConnection
import com.example.musicapp.exoplayer.isPlayEnabled
import com.example.musicapp.exoplayer.isPrepared
import com.example.musicapp.other.Constants.MEDIA_ROOT_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.musicapp.other.Resource

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {
    private val _mediaItems = MutableLiveData<Resource<List<Track>>>()
    val mediaItems: LiveData<Resource<List<Track>>> = _mediaItems

    val isConnected = musicServiceConnection.isConnected
    val networkError = musicServiceConnection.networkError
    val currentPlayingSong = musicServiceConnection.currentPlayingSong
    val playbackState = musicServiceConnection.playbackState

    init{
        _mediaItems.postValue(Resource.loading(null))
        musicServiceConnection.subscribe(MEDIA_ROOT_ID, object : MediaBrowserCompat.SubscriptionCallback(){
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
                val items = children.map{
                    Track(
                        it.mediaId!!.toInt(),
                        it.description.title.toString(),
                        it.description.subtitle.toString(),
                        it.description.iconUri.toString(),
                        it.description.mediaUri.toString()
                    )
                }
                _mediaItems.postValue(Resource.success(items))
            }
        })

    }

    fun skipToNextSong() {
        musicServiceConnection.transportControls.skipToNext()
    }

    fun skipToPreviousSong() {
        musicServiceConnection.transportControls.skipToPrevious()
    }

    fun seekTo(position: Long) {
        musicServiceConnection.transportControls.seekTo(position)
    }

    //TODO если не работает что-то, то заменить на mediaitem.mediaID
    fun playOrToggleSong(mediaItem: Track, toggle: Boolean = false) {
        val isPrepared = playbackState.value?.isPrepared ?: false
        if(isPrepared && mediaItem.title == currentPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)) {
            playbackState.value?.let{ playbackState ->
                when {
                    playbackState.isPrepared -> if (toggle) musicServiceConnection.transportControls.pause()
                    playbackState.isPlayEnabled -> musicServiceConnection.transportControls.play()
                    else -> Unit
                }
            }
        } else {
            musicServiceConnection.transportControls.playFromMediaId(mediaItem.title, null)      //здесь тоже заменить на mediaID
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(
            MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {})
    }
}