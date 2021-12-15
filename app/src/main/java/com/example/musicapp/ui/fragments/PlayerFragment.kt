package com.example.musicapp.ui.fragments

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.RequestManager
import com.example.musicapp.R
import com.example.musicapp.data.Track
import com.example.musicapp.databinding.FragmentPlayerItemBinding
import com.example.musicapp.exoplayer.isPlaying
import com.example.musicapp.exoplayer.toTimeFormat
import com.example.musicapp.exoplayer.toTrack
import com.example.musicapp.other.Status
import com.example.musicapp.ui.vm.MainViewModel
import com.example.musicapp.ui.vm.PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_player_item.seekBar

@AndroidEntryPoint
class PlayerFragment : Fragment(R.layout.fragment_player_item) {

    @Inject
    lateinit var glide: RequestManager

    private lateinit var mainViewModel: MainViewModel
    private val trackViewModel: PlayerViewModel by viewModels()
    private val binding: FragmentPlayerItemBinding by viewBinding()

    private var currentPlayingTrack: Track? = null
    private var playbackState: PlaybackStateCompat? = null
    private var shouldUpdateSeekbar = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        subscribeToObservers()

        with(binding){

            fabNext.setOnClickListener {
                mainViewModel.skipToNextSong()
            }

            fabPlayPause.setOnClickListener {
                currentPlayingTrack?.let {
                    mainViewModel.playOrToggleSong(it, true)
                }
            }

            fabPrevious.setOnClickListener{
                mainViewModel.skipToPreviousSong()
            }

            seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        binding.currentDuration.text = progress.toLong().toTimeFormat()
                    } else {
                        binding.seekBar?.progress =
                            trackViewModel.currentPlayerPosition.value?.toInt() ?: 0
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    shouldUpdateSeekbar = false
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar?.let {
                    mainViewModel.seekTo(it.progress.toLong())
                    }
                    shouldUpdateSeekbar = true
                }
            })
        }
    }

    private fun updateData(track: Track) {
        binding.textViewTitle.text = track.title
        binding.textViewArtist.text = track.artist
        glide.load(track.bitmapUri).into(binding.albumImageView)
    }

    private fun subscribeToObservers(){
        mainViewModel.mediaItems.observe(viewLifecycleOwner) {
            it?.let{ result ->
                when(result.status) {
                    Status.SUCCESS -> {
                        result.data?.let { tracks ->
                            if (currentPlayingTrack == null && tracks.isNotEmpty()) {
                                currentPlayingTrack = tracks[0]
                                updateData(tracks[0])
                            }
                        }
                    } else -> Unit
                }
            }
        }

        mainViewModel.currentPlayingSong.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            currentPlayingTrack = it.toTrack()
            updateData(currentPlayingTrack!!)
        }

        mainViewModel.playbackState.observe(viewLifecycleOwner) {
            playbackState = it
            binding.fabPlayPause.setImageResource(
                if (playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play_arrow
            )
            binding.seekBar?.progress = it?.position?.toInt() ?: 0
        }

        trackViewModel.currentSongDuration.observe(viewLifecycleOwner){
            seekBar?.max = it.toInt()
            val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
            binding.fullDuration.text = dateFormat.format(it)
        }

        trackViewModel.currentPlayerPosition.observe(viewLifecycleOwner) {
            if (shouldUpdateSeekbar) {
                seekBar?.progress = it.toInt()
                setCurrentPlayerTimeToTextView(it)
            }
        }
    }

    private fun setCurrentPlayerTimeToTextView(ms: Long) {
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        binding.currentDuration.text = dateFormat.format(ms)
    }
}