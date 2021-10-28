package com.example.musicapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.musicapp.R
import com.example.musicapp.data.Track
import kotlinx.android.synthetic.main.item_mini.view.*
import javax.inject.Inject

class SongAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var tracks: List<Track>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_mini,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val track = tracks[position]
        holder.itemView.apply {
            text_view_title.text = track.title
            text_view_artist.text = track.artist
            glide.load(track.bitmapUri).into(album_image_view)

            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(track)
                }
            }
        }
    }

    private var onItemClickListener: ((Track) -> Unit)? = null
    fun setOnItemClickListener(listener: (Track) -> Unit){
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}