package com.waleed.galbums.ui.fragments.albums.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.waleed.galbums.databinding.ItemAlbumGridBinding
import com.waleed.galbums.databinding.ItemAlbumListBinding
import com.waleed.galbums.models.Album
import com.waleed.galbums.utils.extensions.toFolderDrawable


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

    private var isGrid = true

    inner class AlbumViewHolder(private val binding: ViewBinding) :
        ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(album: Album, type: ViewType) = with(binding) {
            if (type == ViewType.GRID) {
                (binding as ItemAlbumGridBinding).apply {
                    albumName.text = album.name
                    albumCount.text = "Total ${album.count} items"

                    Glide.with(root.context).load(album.uri)
                        .override(200, 200)
                        .into(albumImage)
                }
            }
            if (type == ViewType.LIST) {
                (binding as ItemAlbumListBinding).apply {
                    albumName.text = album.name
                    albumCount.text = "Total ${album.count} items"
                    ivFolder.setImageDrawable(album.name.toFolderDrawable(root.context))
                }
            }


            Log.d("AlbumsAdapter", "Albums: bind: file $album. medias -> ${album.count} ")

            root.setOnClickListener {
                onAlbumClick.invoke(album)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {

        return AlbumViewHolder(
            if (ViewType.entries.toTypedArray()[viewType] == ViewType.GRID)
                ItemAlbumGridBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ) else ItemAlbumListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(getItem(position), if (isGrid) ViewType.GRID else ViewType.LIST)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isGrid) ViewType.GRID.ordinal else ViewType.LIST.ordinal
    }

    fun switchView() {
        isGrid = !isGrid
    }

    fun isGrid() = isGrid

    enum class ViewType {
        GRID, LIST
    }
}