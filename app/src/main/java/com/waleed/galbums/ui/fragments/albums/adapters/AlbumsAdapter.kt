package com.waleed.galbums.ui.fragments.albums.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.waleed.galbums.databinding.ItemAlbumBinding
import com.waleed.galbums.models.Album


class AlbumsAdapter(private val onAlbumClick: (album: Album) -> Unit) :
    ListAdapter<Album, AlbumsAdapter.AlbumViewHolder>(object :
        DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem.name == newItem.name
        }

    }) {


    inner class AlbumViewHolder(private val binding: ItemAlbumBinding) : ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(album: Album) = with(binding) {
            albumName.text = album.name
            albumCount.text = "Total ${album.count} items"

            Glide.with(root.context).load(album.uri)
                .override(200, 200)
                .into(albumImage)

            Log.d("AlbumsAdapter", "Albums: bind: file $album. medias -> ${album.count} ")

            root.setOnClickListener {
                onAlbumClick.invoke(album)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        return AlbumViewHolder(
            ItemAlbumBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}