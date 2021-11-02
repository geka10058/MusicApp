package com.example.musicapp.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import com.example.musicapp.R
import com.example.musicapp.data.Track
import kotlinx.android.synthetic.main.fragment_vp.view.*
import javax.inject.Inject

class SwipeSongAdapter @Inject constructor() : BaseSongAdapter(R.layout.fragment_vp) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val track = tracks[position]
        holder.itemView.apply {
            vp_tw_title.text = track.title
            vp_tw_artist.text = track.artist

            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(track)
                }
            }
        }
    }
}