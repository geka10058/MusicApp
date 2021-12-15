package com.example.musicapp.adapters

import android.util.Log
import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import com.example.musicapp.R
import javax.inject.Inject
import kotlinx.android.synthetic.main.item_mini.view.album_image_view
import kotlinx.android.synthetic.main.item_mini.view.text_view_artist
import kotlinx.android.synthetic.main.item_mini.view.text_view_title

class SongAdapter @Inject constructor(
    private val glide: RequestManager
) : BaseSongAdapter(R.layout.item_mini) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val track = tracks[position]
        // Why not to use Android Bindings here instead of kotlin syntetics?
        holder.itemView.apply {
            text_view_title.text = track.title
            text_view_artist.text = track.artist
            glide.load(track.bitmapUri)
                .centerCrop()
                .into(album_image_view)

            setOnClickListener {
                onItemClickListener?.let { click ->
                    //notifyDataSetChanged()
                    click(track)
                    Log.d("AppDebug", "track kicked")
                }
            }
        }
    }
}