package com.example.musicapp.ui.fragments

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.example.musicapp.R
import com.example.musicapp.adapters.SongAdapter
import com.example.musicapp.adapters.SwipeSongAdapter
import com.example.musicapp.data.Track
import com.example.musicapp.databinding.FragmentGaleryBinding
import com.example.musicapp.exoplayer.isPlaying
import com.example.musicapp.exoplayer.toTrack
import com.example.musicapp.other.Status
import com.example.musicapp.ui.vm.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_galery.recycler

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_galery) {

    private var _binding: FragmentGaleryBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var currentPayingSong: Track? = null
    private var playbackState: PlaybackStateCompat? = null

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter

    @Inject
    lateinit var songAdapter: SongAdapter

    @Inject
    lateinit var glide: RequestManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGaleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        setupRecyclerView()
        subscribeToObservers()


        binding.vpPayer.adapter = swipeSongAdapter

        binding.vpPayer.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (playbackState?.isPlaying == true) {
                    mainViewModel.playOrToggleSong(swipeSongAdapter.tracks[position])
                } else currentPayingSong = swipeSongAdapter.tracks[position]
            }
        })

        binding.playerButtonPlayPause.setOnClickListener {
            currentPayingSong?.let {
                mainViewModel.playOrToggleSong(it,true)
            }
        }

        songAdapter.setItemClickListener {
            mainViewModel.playOrToggleSong(it) //TODO (it)
            //findNavController().navigate(R.id.action_galleryFragment_to_playerFragment)
        }

        swipeSongAdapter.setItemClickListener {
            findNavController().navigate(R.id.action_galleryFragment_to_playerFragment)
        }

    }

    private fun setupRecyclerView() = recycler.apply {
        adapter = songAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun switchViewPagerToCurrentTrack(track: Track, smoothScroll: Boolean = false) {
        val newItemIndex = swipeSongAdapter.tracks.indexOf(track)
        if (newItemIndex != -1) {
            binding.vpPayer.setCurrentItem(newItemIndex, smoothScroll)
            currentPayingSong = track
        }
    }

    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(viewLifecycleOwner){
                it?.let { result ->
                    when (result.status) {
                        Status.SUCCESS -> {
                            binding.allSongsProgressBar.isVisible = false
                            binding.bottomBar.isVisible = true
                            binding.vpPayer.isVisible = true
                            result.data?.let { tracks ->
                                songAdapter.tracks = tracks
                                ///////////////// новый блок для вьюпэйджера
                                swipeSongAdapter.tracks = tracks
                                if (tracks.isNotEmpty()) {
                                    glide.load((currentPayingSong?: tracks[0]).bitmapUri).into(binding.playerAlbumImage)
                                }
                                switchViewPagerToCurrentTrack(currentPayingSong ?: return@observe)

                            }
                        }
                        Status.ERROR -> Unit
                        Status.LOADING -> {
                            binding.allSongsProgressBar.isVisible = true
                            //binding.recycler.isVisible = true
                            binding.bottomBar.isVisible = false
                        }
                    } }
        }

        //приложение не крашится, но VP2 не работает
       /* mainViewModel.mediaItems.observe(viewLifecycleOwner,{
                result ->
            when (result.status) {
                Status.SUCCESS -> {
                    binding.allSongsProgressBar.isVisible = false
                    *//*binding.bottomBar.isVisible = true*//*
                    binding.vpPayer.isVisible = true
                    result.data?.let { tracks ->
                        songAdapter.tracks = tracks

                    }
                }
                Status.ERROR -> Unit
                Status.LOADING -> {
                    binding.allSongsProgressBar.isVisible = true
                    //binding.recycler.isVisible = true
                    *//*binding.bottomBar.isVisible = false*//*
                }
            }
        })*/

        mainViewModel.currentPlayingSong.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            val firstTime = currentPayingSong == null
            currentPayingSong = it.toTrack()
            glide.load(currentPayingSong?.bitmapUri).into(binding.playerAlbumImage)
            switchViewPagerToCurrentTrack(currentPayingSong ?: return@observe, firstTime)
        }

        mainViewModel.playbackState.observe(viewLifecycleOwner){
            playbackState = it
            binding.playerButtonPlayPause.setImageResource(
                if (playbackState?.isPlaying == true) R.drawable.ic_pause_black else R.drawable.ic_play_arrow_black
            )
        }

        mainViewModel.isConnected.observe(viewLifecycleOwner) {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.ERROR -> Snackbar.make(
                        binding.root,
                        result.message ?: "Connection failed",
                        Snackbar.LENGTH_LONG
                    ).show()
                    else -> Unit
                }
            }
        }

        mainViewModel.networkError.observe(viewLifecycleOwner) {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.ERROR -> Snackbar.make(
                        binding.root,
                        result.message ?: "Internet connection failed, turn on the internet on the device",
                        Snackbar.LENGTH_LONG
                    ).show()
                    else -> Unit
                }
            }
        }

    }
}