package com.example.musicapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.musicapp.R
import com.example.musicapp.data.Track
import com.example.musicapp.databinding.FragmentPlayerItemBinding
import com.example.musicapp.databinding.ItemMiniBinding
import kotlinx.android.synthetic.main.item_mini.view.*
import javax.inject.Inject

class SongAdapter @Inject constructor(
    private val context: Context,
    private val glide: RequestManager
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    inner class SongViewHolder(
        private val binding: ItemMiniBinding
    ) : RecyclerView.ViewHolder(binding.root){
        val tvTitle = binding.textViewTitle
        val tvArtist = binding.textViewArtist
        val ivAlbumImage = binding.albumImageView
    }




    private val differ = AsyncListDiffer(this, diffCallback)

    var tracks: List<Track>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMiniBinding.inflate(layoutInflater,parent,false)
        return SongViewHolder(binding)

    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        glide.load(tracks[position].bitmapUri).into(holder.ivAlbumImage)
        holder.tvTitle.text = tracks[position].title
        holder.tvArtist.text = tracks[position].artist
        holder.itemView.run {
            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(tracks[position])
                }
            }
        }
    /*val track = tracks[position]
        holder.itemView.apply {
            text_view_title.text = track.title
            text_view_artist.text = track.artist
            glide.load(track.bitmapUri).into(album_image_view)

            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(track)
                }
            }
        }*/
    }

    private var onItemClickListener: ((Track) -> Unit)? = null
    fun setOnItemClickListener(listener: (Track) -> Unit){
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    companion object{
        private val diffCallback = object : DiffUtil.ItemCallback<Track>() {
            override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }
    }
}